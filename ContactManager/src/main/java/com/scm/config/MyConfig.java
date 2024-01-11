package com.scm.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.SecurityFilterChain;

import jakarta.servlet.Filter;

@Configuration
@EnableWebSecurity
public class MyConfig {
	
	@Bean
	public UserDetailsService getUserDetailsService() {
		return new UserDetailsServiceImpl();
	}
	
	@Bean
	public BCryptPasswordEncoder bCryptPasswordEncoder() {
		return new BCryptPasswordEncoder();
	}	
	
	@Bean
	public DaoAuthenticationProvider daoauthenticationProvider() {
		DaoAuthenticationProvider daoAuthenticationProvider= new DaoAuthenticationProvider();
		
		daoAuthenticationProvider.setUserDetailsService(this.getUserDetailsService());
		daoAuthenticationProvider.setPasswordEncoder(this.bCryptPasswordEncoder());
		
		return daoAuthenticationProvider;
	}

	///main-important
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
		
		httpSecurity.authenticationProvider(daoauthenticationProvider());
		httpSecurity.csrf(csrf->csrf.disable())
					.authorizeHttpRequests(auth -> 
							auth.requestMatchers("/user/**").hasRole("USER")
								.requestMatchers("/**").permitAll())
					.formLogin((form) -> form.loginPage("/signin").permitAll()
									.loginProcessingUrl("/dologin")
									.defaultSuccessUrl("/user/index"));
		DefaultSecurityFilterChain defaultSecurityFilterChain =  httpSecurity.build();
		return defaultSecurityFilterChain;
	}
	

	
	@Bean
	public AuthenticationManager authenticationManagerBean(AuthenticationConfiguration 
			authenticationConfiguration) throws Exception {
		return authenticationConfiguration.getAuthenticationManager();
	}
	
	
	protected void configure(AuthenticationManagerBuilder authenticationManagerBuilder) {
		authenticationManagerBuilder.authenticationProvider(this.daoauthenticationProvider());
	}
	
	
}
