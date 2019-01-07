/*
 * Copyright 2002-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package demo.config;

import demo.filter.MyTokenAuthenticationFilter;
import demo.provider.MyTokenProvider;
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
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * @author Joe Grandja
 */
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	private UserDetailsService MyUserDetailsService;

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


		http
			// 可以追加filter
			.addFilterBefore(getTokenAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
			.logout().logoutUrl("/logout").logoutSuccessUrl("/").and()
			.formLogin().loginPage("/login").permitAll().defaultSuccessUrl("/").failureUrl("/login-error").and()
			.authorizeRequests()
			.antMatchers("/css/**").permitAll()
			.antMatchers("/user/**").hasRole("USER").anyRequest().authenticated()
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
		//ignore
		web.ignoring().antMatchers("/info","/health","/hystrix.stream");
	}


	/**
	 * 用户验证
	 * @param auth
	 */
	@Override
		public void configure(AuthenticationManagerBuilder auth) throws Exception {
//			super.configure(auth);
			auth.authenticationProvider(myTokenProvider());
			auth.authenticationProvider(daoAuthenticationProvider());
		}


	@Bean
	public AccessDecisionManager getAccessDecisionManager() {
		return new UrlMatchAccessDecisionManager();
	}




	@Bean
	public DaoAuthenticationProvider daoAuthenticationProvider(){
		DaoAuthenticationProvider provider1 = new DaoAuthenticationProvider();
		// 设置userDetailsService
		provider1.setUserDetailsService(MyUserDetailsService);
		// 禁止隐藏用户未找到异常
		provider1.setHideUserNotFoundExceptions(false);
		// 使用BCrypt进行密码的hash
//		provider1.setPasswordEncoder(myEncoder());
		return provider1;
	}


	@Bean
	public MyTokenProvider myTokenProvider() {
		return new MyTokenProvider();
	}

//	@Bean
	public BCryptPasswordEncoder myEncoder(){
		return new BCryptPasswordEncoder(6);
	}

	/**
	 * token登录过滤器。
	 * @return
	 */
	@Bean
	public MyTokenAuthenticationFilter getTokenAuthenticationFilter() {
		MyTokenAuthenticationFilter filter = new MyTokenAuthenticationFilter();
		try {
			filter.setAuthenticationManager(this.authenticationManagerBean());
		} catch (Exception e) {
			e.printStackTrace();
		}
//		filter.setAuthenticationSuccessHandler(new MyLoginAuthSuccessHandler());
		filter.setAuthenticationFailureHandler(new SimpleUrlAuthenticationFailureHandler("/login-error2"));
		return filter;
	}


//
//	// @formatter:off
//	@Autowired
//	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
//		auth
//			.inMemoryAuthentication()
//				.withUser("user").password("password").roles("USER");
//	}
//	// @formatter:on


}