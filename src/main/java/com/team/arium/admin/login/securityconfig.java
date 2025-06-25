//package com.team.arium.admin.login;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.core.userdetails.User;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.security.provisioning.InMemoryUserDetailsManager;
//import org.springframework.security.web.SecurityFilterChain;
//
///*WEB Security 활성화 및 환경설정을 하기 위한 어노테이션*/
//@Configuration
//@EnableWebSecurity
//@EnableMethodSecurity 
//public class securityconfig {
//
//   //FilterChain: 인가(사용자 필터), Spring Security의 필터 역할로 해당 환경조건 및 접속 URL에 대한 권한을 설정
//   @Bean
//   public SecurityFilterChain filterch(HttpSecurity http) throws Exception {
//      http.csrf(csrf -> csrf.disable())
//       .authorizeHttpRequests(auth -> auth
//           .requestMatchers("/admin/**").permitAll()
//           .requestMatchers("/css/**").permitAll()
//           .requestMatchers("/js/**").permitAll()
//           .anyRequest().authenticated()
//       )
//      .formLogin(login -> login
//            .loginPage("/admin/admin_dashboard")      // 커스텀 로그인 페이지
//            .permitAll()
//        )
//        .logout(logout -> logout
//            .logoutUrl("/logout")     // 로그아웃 URL
//            .logoutSuccessUrl("/admin/login") // 로그아웃 후 이동할 페이지
//            .invalidateHttpSession(true)
//            .deleteCookies("JSESSIONID")
//            .permitAll()
//        );
//      return http.build();
//   }
//   
//   
//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
//    }
//    
//   @Bean
//   public UserDetailsService userDetailsService() {
//       UserDetails admin = User.builder()
//           .username("admin")
//           .password(passwordEncoder().encode("1234"))
//           .roles("ADMIN")  
//           .build();
//       UserDetails user = User.builder()
//           .username("user")
//           .password(passwordEncoder().encode("1234"))
//           .roles("USER")  
//           .build();
//       return new InMemoryUserDetailsManager(admin, user);
//   }
//}

//package com.team.arium.admin.login;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.core.userdetails.User;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.security.provisioning.InMemoryUserDetailsManager;
//import org.springframework.security.web.SecurityFilterChain;
//
///*WEB Security 활성화 및 환경설정을 하기 위한 어노테이션*/
//@Configuration
//@EnableWebSecurity
//@EnableMethodSecurity 
//public class securityconfig {
//
//   //FilterChain: 인가(사용자 필터), Spring Security의 필터 역할로 해당 환경조건 및 접속 URL에 대한 권한을 설정
//   @Bean
//   public SecurityFilterChain filterch(HttpSecurity http) throws Exception {
//      http.csrf(csrf -> csrf.disable())
//       .authorizeHttpRequests(auth -> auth
//           .requestMatchers("/admin/**").permitAll()
//           .requestMatchers("/css/**").permitAll()
//           .requestMatchers("/js/**").permitAll()
//           .anyRequest().authenticated()
//       )
//      .formLogin(login -> login
//            .loginPage("/admin/admin_dashboard")      // 커스텀 로그인 페이지
//            .permitAll()
//        )
//        .logout(logout -> logout
//            .logoutUrl("/logout")     // 로그아웃 URL
//            .logoutSuccessUrl("/admin/login") // 로그아웃 후 이동할 페이지
//            .invalidateHttpSession(true)
//            .deleteCookies("JSESSIONID")
//            .permitAll()
//        );
//      return http.build();
//   }
//   
//   
//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
//    }
//    
//   @Bean
//   public UserDetailsService userDetailsService() {
//       UserDetails admin = User.builder()
//           .username("admin")
//           .password(passwordEncoder().encode("1234"))
//           .roles("ADMIN")  
//           .build();
//       UserDetails user = User.builder()
//           .username("user")
//           .password(passwordEncoder().encode("1234"))
//           .roles("USER")  
//           .build();
//       return new InMemoryUserDetailsManager(admin, user);
//   }
//}

package com.team.arium.admin.login;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

/*WEB Security 활성화 및 환경설정을 하기 위한 어노테이션*/
@Configuration
@EnableWebSecurity
public class securityconfig {

   //FilterChain: 인가(사용자 필터), Spring Security의 필터 역할로 해당 환경조건 및 접속 URL에 대한 권한을 설정
   @Bean
   public SecurityFilterChain filterch(HttpSecurity http) throws Exception{
	   http.csrf((auth)->auth.disable());
	   
	   //ajax로 인하여 보안을 풀어놓은 상황
	   http.authorizeHttpRequests((auth)->auth
				.requestMatchers("/**").permitAll()
		);
			   
	   return http.build();
   }
}
