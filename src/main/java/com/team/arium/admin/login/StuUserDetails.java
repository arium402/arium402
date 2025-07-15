package com.team.arium.admin.login;

import java.util.Collection;
import java.util.Collections;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.team.arium.domain.User_Info;

public class StuUserDetails implements UserDetails{

private final User_Info user; //DB에서 조회된 사용자 정보
	
	public StuUserDetails(User_Info user) { //생성자 정의
        this.user = user;
    }
	
	public User_Info getUser() {
	    return this.user;
	}

	//권한 목록
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return Collections.singletonList(
				new SimpleGrantedAuthority(user.getUserRole())
		);
	}
	
	//사용자 비밀번호 DB와 비교
	@Override
	public String getPassword() {
		return user.getLoginPw(); //암호화된 비밀번호
	}
	
	//사용자 ID
	@Override
	public String getUsername() {
		return user.getStdNo(); //로그인 ID로 사용할 필드(사번 = ID)
	}
	
	//계정이 만료되지 않았는지 여부
	@Override
	public boolean isAccountNonExpired() {
		return true;
	}
	
	//계정이 잠기지 않았는지 여부
	@Override
	public boolean isAccountNonLocked() {
		return true;
	}
	
	//비밀번호가 만료되지 않았는지 여부
	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}
	
	//계정이 활성화 되었는지 여부, 재학, 휴학중인 경우만 true
	//여기서 현황(재학, 휴학, 자퇴, 졸업) 상태 체크 가능
	@Override
	public boolean isEnabled() {
		 return user.getStdInfo() != null &&
				user.getStdInfo().getStdStatCd() != null &&(
				user.getStdInfo().getStdStatCd().getCodeId() == StuStatusCode.INSCHOOL ||
				user.getStdInfo().getStdStatCd().getCodeId() == StuStatusCode.SEMESTEROFF
		);
	}
}