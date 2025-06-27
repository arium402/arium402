package com.team.arium.admin.login;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/*WEB Security 활성화 및 환경설정을 하기 위한 어노테이션*/
@Configuration
@EnableWebSecurity
public class securityconfig {

   //FilterChain: 인가(사용자 필터), Spring Security의 필터 역할로 해당 환경조건 및 접속 URL에 대한 권한을 설정
   @Bean
   public SecurityFilterChain filterch(HttpSecurity http) throws Exception{
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
	   http.authorizeHttpRequests((auth)->auth
				.requestMatchers("/**").permitAll()
				.requestMatchers("/login/admin").hasRole("ROLE_ADMIN") //ADMIN권한 가진 사용자만 접근 가능
				.anyRequest().authenticated() //그 외 요청은 인증된 사용자만 허용
		);
			   
	   return http.build(); //보안관련 설정값을 build로 생성하여 적용
	  
   }
   
   //spring security hash 암호화 기술
   //BCryptPasswordEncoder: spring security에서 제공된 암호화 모듈로 비밀번호를 암호화 함
   @Bean
   public BCryptPasswordEncoder bcrypass() {
      return new BCryptPasswordEncoder();
   }
   
   @Bean
	public BCryptPasswordEncoder bcrypass() {
		return new BCryptPasswordEncoder();
	}
}
