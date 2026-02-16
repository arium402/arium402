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

public class SecurityConfig {

	@Autowired
	private EmplUserDetailsService EmplUserDetailsService;  // 커스텀 UserDetailsService 빈 자동 주입
	
	@Autowired
	private StuUserDetailsService StuUserDetailsService; 
	
	@Autowired
	private LoginSuccess loginSuccess;
	
	@Autowired
	private LoginFail loginFail;
	
	//spring security hash 암호화 기술
	//BCryptPasswordEncoder: spring security에서 제공된 암호화 모듈로 비밀번호를 암호화 함
	@Bean
	public BCryptPasswordEncoder bcrypass() {
	   return new BCryptPasswordEncoder();
	}
	
	  /* authorizeHttpRequests: URL 보안 인증 및 인가를 구현
	    * securityMatcher: 유형별로 보안 설정 분리할 때 사용
	    * requestMatchers: 컨트롤에 사용되는 요청 타입에 따라 페이지 지정, 권한 레벨에 맞춰 접근 설정 가능
	    * 
	    * 접근권한
	    * permitAll(): 모든 사용자가 로그인 없이 접근 가능
	    * hasRole(null): 관리자 권한 또는 일반 권한 등, 각 파트에 맞게 접근 가능
	    * denyAll(): 모든 사용자에게 접근 금지
	    * authenticated(): 로그인한 사용자만 접근 가능. 관리자/일반 사용자는 가능, guest 제외
	    * hasAnyRole(null): 여러 권한을 한번에 처리할 때 사용*/
	
   //FilterChain: 인가(사용자 필터), Spring Security의 필터 역할로 해당 환경조건 및 접속 URL에 대한 권한을 설정
	@Bean
	@Order(1)
	public SecurityFilterChain adminfilterch(HttpSecurity http) throws Exception{
		http
			.securityMatcher("/admin/**", "/admin-login-process", "/logout") //해당 경로에 securityfilterchain이 적용됨
			.csrf((auth)->auth.disable()) //CSRF 보호 기능 비활성화
			.authorizeHttpRequests(auth -> auth
		        .requestMatchers("/css/**", "/js/**", "/images/**").permitAll()  // 정적 자원 허용
		        .requestMatchers("/admin/login", "/admin-login-process").permitAll()   // 로그인 페이지 및 처리 URL은 허용
		        .requestMatchers("/admin/**").hasRole("ADMIN")                   // /admin 경로는 관리자만
		        .anyRequest().authenticated()                                    // 그 외는 인증 필요
		    )
		    .formLogin(form -> form
		        .loginPage("/admin/login")                      // 로그인 페이지
		        .loginProcessingUrl("/admin-login-process")  	//로그인 처리 요청
		        .successHandler(loginSuccess)					//로그인 성공 핸들러
		        .failureHandler(loginFail)						//로그인 실패 핸들러
		        //.defaultSuccessUrl("/admin/dashboard", true)  // 성공 시 이동할 페이지, true(항상 이 url로 이동!)
		        //.failureUrl("/admin/login?error")             // 실패 시 이동할 페이지
		        .permitAll()									//로그인 시도를 모두 허용
		    )
		    .logout(logout -> logout
		    	    .logoutUrl("/admin/logout")                 // 로그아웃 처리 URL
		    	    .logoutSuccessUrl("/admin/login?logout")    // 로그아웃 후 이동할 페이지
		    	    .invalidateHttpSession(true)                // 세션 무효화
		    	    .deleteCookies("JSESSIONID")                // JSESSIONID 쿠키 삭제
		    	    .permitAll()
		    )
		    .sessionManagement(auth -> auth
				    .sessionFixation().changeSessionId()
			)
		    .userDetailsService(EmplUserDetailsService);
		    return http.build();
   }
	@Bean
	@Order(2)
	public SecurityFilterChain CoFilterch(HttpSecurity http) throws Exception{
		
		http
			.securityMatcher("/counselor/**", "/counselor-login-process")
			.csrf((auth)->auth.disable())
			.authorizeHttpRequests(auth -> auth
					.requestMatchers("/css/**", "/js/**", "/images/**").permitAll()
					.requestMatchers("/student/login", "/counselor-login-process").permitAll()
					.requestMatchers("/counselor/find_*").permitAll()
					.requestMatchers("/counselor/**").hasRole("COUNSELOR")
					.anyRequest().authenticated()
			)
			.formLogin(form -> form
					.loginPage("/student/login")
					.loginProcessingUrl("/counselor-login-process")
					.successHandler(loginSuccess)			
			        .failureHandler(loginFail)			
					.permitAll()
			)
			.logout(logout -> logout
		    	    .logoutUrl("/counselor/logout")                                 
		    	    .logoutSuccessUrl("/student/login?logout")              
		    	    .invalidateHttpSession(true)                          
		    	    .deleteCookies("JSESSIONID")                         
		    	    .permitAll()
		    )
			.sessionManagement(auth -> auth
				    .sessionFixation().changeSessionId()
			)
			.userDetailsService(EmplUserDetailsService);
		return http.build();
	}

	@Bean
	@Order(3)
	public SecurityFilterChain SuFilterch(HttpSecurity http) throws Exception{
		
		http
			.securityMatcher("/student/**", "/student-login-process")
			.csrf((auth)->auth.disable())
			.authorizeHttpRequests(auth -> auth
					.requestMatchers("/css/**", "/js/**", "/images/**").permitAll()
					.requestMatchers("/student/login", "/student-login-process").permitAll()
					 
					//팝업 페이지들 허용 추가
					.requestMatchers("/student/find_*", "/student/change_password").permitAll()
	                
					.requestMatchers("/student/**").hasRole("STUDENT")
					.anyRequest().authenticated()
			)
			.formLogin(form -> form
					.loginPage("/student/login")
					.loginProcessingUrl("/student-login-process")
					.successHandler(loginSuccess)			
			        .failureHandler(loginFail)		
					.permitAll()
			)
			.logout(logout -> logout
		    	    .logoutUrl("/student/logout")                                 
		    	    .logoutSuccessUrl("/student/login?logout")              
		    	    .invalidateHttpSession(true)                          
		    	    .deleteCookies("JSESSIONID")                          
		    	    .permitAll()
		    )
			.sessionManagement(session -> session
					.sessionFixation().changeSessionId()	//sessionFixation(): 세선 고정 공격 방지, changeSessionId(): 기존 세션 유지하되, 세션 ID 새로 부여
					.maximumSessions(1) 					//한 계정당 동시에 유지 가능한 세션 수 1개
					.maxSessionsPreventsLogin(true)			//이미 로그인된 세션이 있으면, 새로운 로그인 시도 차단
			)
			.userDetailsService(StuUserDetailsService);
		return http.build();
	}
}
