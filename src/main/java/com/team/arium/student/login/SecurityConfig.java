package com.team.arium.student.login;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@Order(2)
public class SecurityConfig {

	@Bean
	public SecurityFilterChain filterchain(HttpSecurity http) throws Exception{
		
		http.csrf(csrf -> csrf.disable());

		http.authorizeHttpRequests((auth)->auth
	            .requestMatchers("/**").permitAll()
//	            .requestMatchers("/css/**", "/js/**", "/images/**").permitAll()
//	            .requestMatchers("/student/login", "/student-login-process").permitAll()
//	            .requestMatchers("/admin/**").hasAnyRole("STUDENT", "COUNSLER")
//	            .anyRequest().authenticated()  
	      ); 
		
		  http.formLogin(form -> form
			        .loginPage("/student/login")                      // 로그인 페이지
			        .loginProcessingUrl("/student-login-process")  	    //로그인 처리 요청
//			        .successHandler(new LoginSuccess())			//로그인 성공 핸들러
//			        .failureHandler(new LoginFail())			//로그인 실패 핸들러
			        .defaultSuccessUrl("/student/main", true)    // 성공 시 이동할 페이지, true(항상 이 url로 이동!)
			        .failureUrl("/student/login?error")               // 실패 시 이동할 페이지
			        .permitAll()							//로그인 시도를 모두 허용
			    );
		
		 return http.build();
	}
	
	   //spring security hash 암호화 기술
	   //BCryptPasswordEncoder: spring security에서 제공된 암호화 모듈로 비밀번호를 암호화 함
//	   @Bean
//	   public BCryptPasswordEncoder studentbcrypass() {
//	      return new BCryptPasswordEncoder();
//	   }
}
