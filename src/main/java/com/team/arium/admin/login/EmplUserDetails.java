package com.team.arium.admin.login;

import java.util.Collection;
import java.util.Collections;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.team.arium.domain.User_Info;

//user_info 정보를 UserDetails 인터페이스 형태로 감싸서 spring security에 맞게 가공한 클래스
public class EmplUserDetails implements UserDetails {

private final User_Info user; //DB에서 조회된 사용자 정보
	
	public EmplUserDetails(User_Info user) { //생성자 정의
        this.user = user;
    }
	
	/*private final User_Info user가 있으나, private기 때문에 접근 어려움
	 * 따라서 외부에서 user 객체를 안전하게 꺼내 쓰기 위해 get 메소드 정의*/
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
		return user.getEmplNo(); //로그인 ID로 사용할 필드(사번 = ID)
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
	
	//계정이 활성화 되었는지 여부, 재직중인 경우만 true
	//여기서 현황(재직, 휴직, 퇴사) 상태 체크 가능
	@Override
	public boolean isEnabled() {
		 return user.getEmplInfo() != null &&
				user.getEmplInfo().getEmplStatCd() != null &&
				user.getEmplInfo().getEmplStatCd().getCodeId() == EmplStatusCode.WORKING;
	}
}