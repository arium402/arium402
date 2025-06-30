package com.team.arium.admin.login;

import org.springframework.security.core.GrantedAuthority;
import java.util.Collection;

//사용자 정보를 담는 객체 인터페이스
public interface UserDetails {

    // 권한 목록 (예: ROLE_ADMIN, ROLE_USER)
    Collection<? extends GrantedAuthority> getAuthorities();

    // 사용자 비밀번호, 입력된 비밀번호와 비교
    String getPassword();

    // 사용자 ID (로그인에 사용할 사용자명)
    String getUsername();

    // 계정이 만료되지 않았는지 여부 (true면 사용 가능)
    boolean isAccountNonExpired();

    // 계정이 잠기지 않았는지 여부 (true면 사용 가능), 일정 횟수 이상 로그인 실패 시 잠금 처리 기능
    boolean isAccountNonLocked();

    // 비밀번호가 만료되지 않았는지 여부 (true면 사용 가능)
    boolean isCredentialsNonExpired();

    // 계정이 활성화 되었는지 여부 (true면 로그인 허용)
    boolean isEnabled();
}
