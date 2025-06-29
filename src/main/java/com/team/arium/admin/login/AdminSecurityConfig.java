package com.team.arium.admin.login;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/*WEB Security 활성화 및 환경설정을 하기 위한 어노테이션*/
@Configuration
@EnableWebSecurity
@Order(1)
public class AdminSecurityConfig {

	@Autowired
    private UserDetailsService userDetailsService;  // 커스텀 UserDetailsService 빈 자동 주입
	
   //FilterChain: 인가(사용자 필터), Spring Security의 필터 역할로 해당 환경조건 및 접속 URL에 대한 권한을 설정
	@Bean
	public SecurityFilterChain filterch(HttpSecurity http) throws Exception{
		http.securityMatcher("/admin/**", "/login-process", "/logout");
		http.csrf((auth)->auth.disable()); //CSRF 보호 기능 비활성화
	   
		   //ajax로 인하여 보안을 풀어놓은 상황
		   /*
		    * authorizeHttpRequests: URL 보안 인증 및 인가를 구현
		    * requestMatchers: 컨트롤에 사용되는 요청 타입에 따라 페이지 지정, 권한 레벨에 맞춰 접근 설정 가능
		    * 
		    * 접근권한
		    * permitAll(): 모든 사용자가 로그인 없이 접근 가능
		    * hasRole(null): 관리자 권한 또는 일반 권한 등, 각 파트에 맞게 접근 가능
		    * denyAll(): 모든 사용자에게 접근 금지
		    * authenticated(): 로그인한 사용자만 접근 가능. 관리자/일반 사용자는 가능, guest 제외
		    * hasAnyRole(null): 여러 권한을 한번에 처리할 때 사용*/
	   http.authorizeHttpRequests(auth -> auth
		        .requestMatchers("/css/**", "/js/**", "/images/**").permitAll()  // 정적 자원 허용
		        .requestMatchers("/admin/login", "/admin-login-process").permitAll()   // 로그인 페이지 및 처리 URL은 허용
		        .requestMatchers("/admin/**").hasRole("ADMIN")                   // /admin 경로는 관리자만
		        .anyRequest().authenticated()                                    // 그 외는 인증 필요
		    );

		    http.formLogin(form -> form
		        .loginPage("/admin/login")                      // 로그인 페이지
		        .loginProcessingUrl("/login-process")  	    //로그인 처리 요청
		        .successHandler(new LoginSuccess())			//로그인 성공 핸들러
		        .failureHandler(new LoginFail())			//로그인 실패 핸들러
		        //.defaultSuccessUrl("/admin/dashboard", true)    // 성공 시 이동할 페이지, true(항상 이 url로 이동!)
		        //.failureUrl("/admin/login?error")               // 실패 시 이동할 페이지
		        .permitAll()									  //로그인 시도를 모두 허용
		    );

		    http.logout(logout -> logout
		    	    .logoutUrl("/logout")                                 // 로그아웃 처리 URL
		    	    .logoutSuccessUrl("/admin/login?logout")              // 로그아웃 후 이동할 페이지
		    	    .invalidateHttpSession(true)                          // 세션 무효화
		    	    .deleteCookies("JSESSIONID")                          // JSESSIONID 쿠키 삭제
		    	    .permitAll()
		    	);

		    
		    return http.build();
		    
   }
   
   //spring security hash 암호화 기술
   //BCryptPasswordEncoder: spring security에서 제공된 암호화 모듈로 비밀번호를 암호화 함
   @Bean
   public BCryptPasswordEncoder bcrypass() {
      return new BCryptPasswordEncoder();
   }
}
