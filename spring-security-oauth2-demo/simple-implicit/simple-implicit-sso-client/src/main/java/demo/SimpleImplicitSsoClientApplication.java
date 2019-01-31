package demo;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.security.oauth2.client.EnableOAuth2Sso;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Configuration
@ComponentScan
@EnableAutoConfiguration
@RestController
@RequestMapping("/dashboard")
public class SimpleImplicitSsoClientApplication {

	@RequestMapping("/message")
	public Map<String, Object> dashboard() {
		return Collections.<String, Object> singletonMap("message", "Yay!");
	}

	@RequestMapping("/user")
	public Principal user(Principal user) {
		return user;
	}



	public static void main(String[] args) {
		ConfigurableApplicationContext ctx = SpringApplication.run(SimpleImplicitSsoClientApplication.class, args);
	}


	@Controller
	public static class LoginErrors {

		@RequestMapping("/dashboard/login")
		public String dashboard(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
			return "redirect:/#/";
		}

	}

	@Component
	@EnableOAuth2Sso
	public static class LoginConfigurer extends WebSecurityConfigurerAdapter {

		@Value("${security.oauth2.client.ssoLogoutUri}")
		private String ssoLogoutUrl;

		// 这个地方的url 判断是否登录 还是根据session会话保持来的（逻辑可见SecurityContextPersistenceFilter，
		// 可以通过重写SecurityContextRepository实现外部回话保持。）
		@Override
		public void configure(HttpSecurity http) throws Exception {
			// 拦截多个请求，放行其他的。
			List<RequestMatcher> matchers = new ArrayList<RequestMatcher>();
			matchers.add(new AntPathRequestMatcher("/dashboard/login"));
			// 退出逻辑，可以自定义处理。这里就简单清除掉token，跳转到sso登出接口
			matchers.add(new AntPathRequestMatcher("/dashboard/logout"));

			http.requestMatcher(new OrRequestMatcher(matchers)).authorizeRequests()
					.anyRequest().authenticated()
					.and()
					.csrf().disable()
					.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.NEVER).and()
                    .cors().and()
					.logout().logoutSuccessUrl(ssoLogoutUrl).deleteCookies("accessToken").logoutUrl("/dashboard/logout").permitAll();
		}


	}

}


