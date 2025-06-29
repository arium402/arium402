package com.team.arium.student.login;

import java.util.Collection;
import java.util.Collections;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.team.arium.domain.User_Info;

public class StudentUserDetails implements UserDetails{

	private final User_Info studentuser; //DB에서 조회된 사용자 정보
	
	public StudentUserDetails(User_Info user) { //생성자 정의
        this.studentuser = user;
    }
	
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return Collections.singletonList(
				new SimpleGrantedAuthority(studentuser.getUserRole())
		);
	}
	
	@Override
	public String getPassword() {
		return studentuser.getLoginPw(); //암호화된 비밀번호
	}
	@Override
	public String getUsername() {
		return studentuser.getEmplNo(); //로그인 ID로 사용할 필드(사번 = ID)
	}
	
	
}
