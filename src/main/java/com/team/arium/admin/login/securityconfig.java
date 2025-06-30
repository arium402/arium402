package com.team.arium.admin.login; // 패키지명은 프로젝트에 맞게 변경하세요

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.team.arium.student.login.loginck;
import com.team.arium.student.login.logins;

@Configuration
@EnableWebSecurity
public class securityconfig {

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    @Order(1) // 이 필터 체인이 먼저 적용됩니다.
    public SecurityFilterChain adminSecurityFilterChain(HttpSecurity http) throws Exception {
        http
            .securityMatcher(new AntPathRequestMatcher("/admin/**")) // 이 필터 체인이 /admin/** 경로에만 적용되도록 설정
            .csrf(csrf -> csrf.disable()) // 예시로 CSRF 비활성화, 실제 환경에서는 보안 고려
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/**").permitAll() // 관리자 로그인 페이지는 모두 접근 가능
                .requestMatchers("/admin/**").hasRole("ADMIN") // /admin 경로 아래는 ADMIN 역할만 접근 가능
                .anyRequest().authenticated() // 그 외 /admin 경로에 대한 모든 요청은 인증 필요
            )
            .formLogin(form -> form
                .loginPage("/admin/login") // 관리자 로그인 페이지 경로 설정
                .loginProcessingUrl("/admin/login") // 로그인 폼 제출 URL
                .defaultSuccessUrl("/admin/admin_dashboard", true) // 로그인 성공 시 이동할 URL
                .failureUrl("/admin/login?error=true") // 로그인 실패 시 이동할 URL
                .usernameParameter("username") // 사용자명 파라미터명 (기본값 username)
                .passwordParameter("password") // 비밀번호 파라미터명 (기본값 password)
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/admin/logout") // 로그아웃 처리 URL
                .logoutSuccessUrl("/admin/login?logout=true") // 로그아웃 성공 시 이동할 URL
                .permitAll()
            );
        

        return http.build();
    }
    
    @Bean
	public loginck idchecks(){
	    return new loginck();
	}
    
	@Bean
	public logins sessionlogin() {
		return new logins();
	}
	
    @Bean
    @Order(2) // 두 번째로 적용됩니다.
    public SecurityFilterChain userSecurityFilterChain(HttpSecurity http) throws Exception {
    	
		final AuthenticationFailureHandler loginck;
    	http
            .csrf(csrf -> csrf.disable()) // 예시로 CSRF 비활성화
            .authorizeHttpRequests(authorize -> authorize
            	.requestMatchers("/**").permitAll()
                .requestMatchers("/", "/user/login", "/css/**", "/js/**").permitAll() // 메인, 사용자 로그인, 정적 리소스는 모두 접근 가능
                .requestMatchers("/counselor/**").hasRole("COUNSELOR") // /admin 경로 아래는 ADMIN 역할만 접근 가능
                .anyRequest().authenticated() // 그 외 모든 요청은 인증 필요
            )
            .formLogin(form -> form
                .loginPage("/student/login") // 사용자 로그인 페이지 경로 설정
                .loginProcessingUrl("/student/loginok.do")
                /*
                .defaultSuccessUrl("/user/dashboard", true) // 로그인 성공 시 이동할 URL
                .failureUrl("/user/login?error=true") // 로그인 실패 시 이동할 URL
                */
                .usernameParameter("username")
                .passwordParameter("password")
				.successHandler(sessionlogin())
				.defaultSuccessUrl("/counselor/dashboard", true) // 로그인 성공 시 이동할 URL
				.failureHandler(idchecks()).permitAll()
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/student/logout") // 로그아웃 처리 URL
                .logoutSuccessUrl("/student/logout?logout=true") // 로그아웃 성공 시 이동할 URL
                .permitAll()
            );
    	http.sessionManagement((auth)->auth.maximumSessions(1)
				.maxSessionsPreventsLogin(true));
		
		http.sessionManagement((auth) -> auth
                .sessionFixation().changeSessionId());
        return http.build();
    }
}