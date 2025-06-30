package com.team.arium.student.login;


import java.util.ArrayList;
import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import com.team.arium.domain.Empl_Info;
import com.team.arium.domain.User_Info;

//개발자 직접 로그인 및 로그아웃등 원하는 형태로 새롭게 구성하는 class
@SuppressWarnings("serial")
public class se_customuser implements UserDetails {
	public User_Info user;	//DTO null

	public se_customuser(User_Info user) {
		this.user = user;
	}
	
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		Collection<GrantedAuthority> collection = new ArrayList<>();
		collection.add(new GrantedAuthority() {
			@Override
			public String getAuthority() {
				return user.getUserRole();	//DB에 저장된 권한을 가져오는 역활
			}
		});
		return collection;
	}

	//로그인에 사용되는 패스워드
	@Override
	public String getPassword() {
		return this.user.getLoginPw();
	}

	//로그인에 사용되는 아이디
	@Override
	public String getUsername() {
		return this.user.getEmplNo();
	}	
}
