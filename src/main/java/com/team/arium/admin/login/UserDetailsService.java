package com.team.arium.admin.login;

import org.springframework.security.core.userdetails.UsernameNotFoundException;

//사용자 정보를 로딩하는 서비스 인터페이스(DB 접근 등)
public interface UserDetailsService {
	
    UserDetails loadUserByUsername(String username) throws UsernameNotFoundException;
}
