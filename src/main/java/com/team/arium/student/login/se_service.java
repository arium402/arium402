package com.team.arium.student.login;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.team.arium.domain.User_Info;

//jpa로 insert, select, update.. 모든것을 처리하여 결과값을 Controller에 전달

/*
 UserDetailsService : 사용자 정보를 로드하여 권한 및 역활 정보를 가져오는 interface 
 기본 메소드 인자값은 username 
 */
@Service
public class se_service implements UserDetailsService {

   
	@Autowired
	private se_repo repo;
	
	@Autowired
	private BCryptPasswordEncoder bcrypt;

	int result = 0;
	@Override
	public UserDetails loadUserByUsername(String userid) throws UsernameNotFoundException {
		System.out.println(userid);
		User_Info user = this.repo.findByEmplNo(userid);
		if(user != null) {		
			//해당 정보가 확인이 되었을 경우 새롭게 se_customuser호출 하여 로그인 정보를 반환함
			return new se_customuser(user);
		}
		/*
		System.out.println("체크1:" + user.getEmplNo());
		System.out.println("체크2:" + user.getLoginPw());
		System.out.println("체크3:" +user.getUserRole());
		*/
		return null;
	}
	
}