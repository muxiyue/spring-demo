package demo.config;

import demo.filter.MyTokenAuthenticationFilter;
import demo.provider.MyTokenProvider;
import demo.service.UrlMatchVoter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.vote.AbstractAccessDecisionManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.ObjectPostProcessor;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	private UserDetailsService myUserDetailsService;

	// @formatter:off
	@Override
	protected void configure(HttpSecurity http) throws Exception {
//		http
//				.authorizeRequests()
//			.accessDecisionManager(getAccessDecisionManager())
//					.antMatchers("/css/**", "/index").permitAll()
//					.antMatchers("/user/**").hasRole("USER")
//					.and().logout().logoutUrl("/logout").logoutSuccessUrl("/").and()
//				.formLogin().loginPage("/login").defaultSuccessUrl("/").failureUrl("/login-error");

//        http
//            .authorizeRequests()
//		      // 覆盖默认的AffirmativeBased授权逻辑。
//            // .accessDecisionManager(getAccessDecisionManager())
//            // .access 方式 校验是否有权限。
//            .antMatchers("/user/**", "/").access("@myAuthService.canAccess(request,authentication)")
//            .and().logout().logoutUrl("/logout").logoutSuccessUrl("/").and()
//                .formLogin().loginPage("/login").defaultSuccessUrl("/").failureUrl("/login-error");


		http
			// 将tokenfilter追加进去，筛选出来tokenLogin逻辑。
			.addFilterBefore(getTokenAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
			.logout().logoutUrl("/logout").logoutSuccessUrl("/").and()
			.formLogin().loginPage("/login").defaultSuccessUrl("/").failureUrl("/login-error").permitAll().and()
			.authorizeRequests()
			.antMatchers(MyTokenAuthenticationFilter.SPRING_SECURITY_RESTFUL_LOGIN_URL).permitAll()
			.antMatchers("/admin/**").hasRole("ADMIN")
			.antMatchers("/user/**").hasRole("USER")
			.anyRequest().authenticated()
			// 修改授权相关逻辑
			.withObjectPostProcessor(new ObjectPostProcessor<FilterSecurityInterceptor>() {
				public <O extends FilterSecurityInterceptor> O postProcess(
					O fsi) {

//					// 覆盖SecurityMetadataSource
//					fsi.setSecurityMetadataSource(fsi.getSecurityMetadataSource());

//					// 覆盖AccessDecisionManager
//					fsi.setAccessDecisionManager(getAccessDecisionManager());

					// 增加投票项
					AccessDecisionManager accessDecisionManager = fsi.getAccessDecisionManager();
					if (accessDecisionManager instanceof AbstractAccessDecisionManager) {
						((AbstractAccessDecisionManager) accessDecisionManager).getDecisionVoters().add(new UrlMatchVoter());
					}

					return fsi;
				}
			});
	}
	// @formatter:on


	@Override
	public void configure(WebSecurity web) throws Exception {
		//忽略请求 不走security filters
		web.ignoring().antMatchers("/login-error2","/css/**","/info","/health","/hystrix.stream");
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
	public void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.authenticationProvider(myTokenProvider());
		// 未配置时候用户名密码默认登录provider
		auth.authenticationProvider(daoAuthenticationProvider());
	}


	/**
	 *
	 * @Description: 自定义授权AccessDecisionManager
	 *
	 * AffirmativeBased(spring security默认使用):
	 * 		只要有投通过（ACCESS_GRANTED）票，则直接判为通过。
	 * 		如果没有投通过票且反对（ACCESS_DENIED）票在1个及其以上的，则直接判为不通过。
	 * ConsensusBased(少数服从多数):
	 * 		通过的票数大于反对的票数则判为通过;通过的票数小于反对的票数则判为不通过;
	 * 		通过的票数和反对的票数相等，则可根据配置allowIfEqualGrantedDeniedDecisions
	 * 	   （默认为true）进行判断是否通过。
	 * UnanimousBased(反对票优先):
	 * 		无论多少投票者投了多少通过（ACCESS_GRANTED）票，只要有反对票（ACCESS_DENIED），
	 * 		那都判为不通过;如果没有反对票且有投票者投了通过票，那么就判为通过.
	 *
	 *
	 * @auther: csp
	 * @date:  2019/1/7 下午9:12
	 * @return: org.springframework.security.access.AccessDecisionManager
	 *
	 */
	@Bean
	public AccessDecisionManager getAccessDecisionManager() {
		return new UrlMatchAccessDecisionManager();
	}




	@Bean
	public DaoAuthenticationProvider daoAuthenticationProvider(){
		DaoAuthenticationProvider provider1 = new DaoAuthenticationProvider();
		// 设置userDetailsService
		provider1.setUserDetailsService(myUserDetailsService);
		// 禁止隐藏用户未找到异常
		provider1.setHideUserNotFoundExceptions(false);
		// 使用BCrypt进行密码的hash
//		provider1.setPasswordEncoder(myEncoder());
		return provider1;
	}


	/**
	 *
	 * @Description:  自定义token方式认证逻辑provider
	 *
	 * @auther: csp
	 * @date:  2019/1/7 下午9:18
	 * @return: demo.provider.MyTokenProvider
	 *
	 */
	@Bean
	public MyTokenProvider myTokenProvider() {
		return new MyTokenProvider(myUserDetailsService);
	}

//	@Bean
	public BCryptPasswordEncoder myEncoder(){
		return new BCryptPasswordEncoder(6);
	}

	/**
	 * token登录过滤器，用来筛选出来token登录方式。
	 */
	@Bean
	public MyTokenAuthenticationFilter getTokenAuthenticationFilter() {
		MyTokenAuthenticationFilter filter = new MyTokenAuthenticationFilter();
		try {
			// 使用的是默认的authenticationManager
			filter.setAuthenticationManager(this.authenticationManagerBean());
		} catch (Exception e) {
			e.printStackTrace();
		}
//		filter.setAuthenticationSuccessHandler(new MyLoginAuthSuccessHandler());
		filter.setAuthenticationSuccessHandler(new SimpleUrlAuthenticationSuccessHandler("/"));
		filter.setAuthenticationFailureHandler(new SimpleUrlAuthenticationFailureHandler("/login-error2"));
		return filter;
	}


//  直接指定用户名密码
//	// @formatter:off
//	@Autowired
//	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
//		auth
//			.inMemoryAuthentication()
//				.withUser("user").password("password").roles("USER");
//	}
//	// @formatter:on


}