package demo.simple;

import demo.simple.common.contants.SsoContants;
import demo.simple.common.service.MyUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.autoconfigure.ManagementServerProperties;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.security.KeyPair;

@Configuration
@ComponentScan
@EnableAutoConfiguration
@Controller
public class SimpleSsoApplication extends WebMvcConfigurerAdapter {

	public static void main(String[] args) {
		SpringApplication.run(SimpleSsoApplication.class, args);
	}


	@Override
	public void addViewControllers(ViewControllerRegistry registry) {
		registry.addViewController("/login").setViewName("login");
//		registry.addViewController("/oauth/confirm_access").setViewName("authorize");
	}



	@Configuration
	@Order(ManagementServerProperties.ACCESS_OVERRIDE_ORDER)
	//	@Order(-1) // 如果优先级在AuthorizationServerSecurity之前，则走不到AuthorizationServerSecurityfilter中。
	protected static class LoginConfig extends WebSecurityConfigurerAdapter {

		@Autowired
		private AuthenticationManager authenticationManager;


		@Override
		protected void configure(HttpSecurity http) throws Exception {
			http.formLogin().loginPage("/login")
				.permitAll().and()
				.authorizeRequests()
				.anyRequest().authenticated().and().cors().and().csrf().disable()
				.logout().logoutUrl("/logout");


		}


		/**
		 * 1、用户验证，指定多个AuthenticationProvider
		 * 实际执行时候根据provider的supports方法判断是否走逻辑
		 *
		 * 2、如果不覆盖，优先会获取AuthenticationProvider bean作为provider；
		 * 如果没有bean，默认提供DaoAuthenticationProvider
		 *
		 * @param auth
		 */
		@Override
		protected void configure(AuthenticationManagerBuilder auth) throws Exception {
			auth.parentAuthenticationManager(authenticationManager);
		}


		@Override
		public void configure(WebSecurity web) throws Exception {
			//忽略请求 不走security filters
			web.ignoring().antMatchers(HttpMethod.GET, "/login").antMatchers(HttpMethod.OPTIONS, "/oauth/**").antMatchers("/login-error2","/css/**","/info","/health","/hystrix.stream");
		}


		@Bean
		public MyUserDetailsService myUserDetailsService() {
			return new MyUserDetailsService();
		}

	}

	// 处理oauth2相关。
	@Configuration
	@EnableAuthorizationServer
	protected static class OAuth2Config extends AuthorizationServerConfigurerAdapter {

		@Autowired
		private AuthenticationManager authenticationManager;



		@Bean
		public JwtAccessTokenConverter jwtAccessTokenConverter() {
			// 自定义 jwt 加密的参数
			JwtAccessTokenConverter converter = new JwtAccessTokenConverter();

			KeyPair keyPair = new KeyStoreKeyFactory(
				new ClassPathResource("keystore.jks"), "foobar".toCharArray())
				.getKeyPair("test");
			converter.setKeyPair(keyPair);
			return converter;
		}

		@Override
		public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
			clients.inMemory()
					.withClient(SsoContants.DEFAULT_CLIENT_ID).autoApprove(true)
					.secret(SsoContants.DEFAULT_CLIENT_SECRET)
					.authorizedGrantTypes("implicit", "authorization_code", "refresh_token",
							"password").scopes("openid");
		}

		@Override
		public void configure(AuthorizationServerEndpointsConfigurer endpoints)
				throws Exception {
			endpoints.authenticationManager(authenticationManager)
				.accessTokenConverter(
					jwtAccessTokenConverter());
		}

		@Override
		public void configure(AuthorizationServerSecurityConfigurer oauthServer)
				throws Exception {
			oauthServer.tokenKeyAccess("permitAll()").checkTokenAccess(
					"isAuthenticated()");
		}

	}
}
