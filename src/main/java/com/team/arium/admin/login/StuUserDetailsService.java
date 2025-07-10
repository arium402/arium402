package com.team.arium.admin.login;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.team.arium.domain.User_Info;

@Service
public class StuUserDetailsService implements UserDetailsService{

	//DB에서 user_info 엔티티를 조회하는 인터페이스, 생성자를 통해 주입받아 사용
	private final LoginRepository logRepository;
		
	@Autowired
	public StuUserDetailsService(LoginRepository logRepository) {
	    this.logRepository = logRepository;
	}
		
	//로그인시 입력한 사용자 ID(학번)가 username으로 들어옴
	//사용자의 인증 처리를 위함
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		  //학번으로 user_info 테이블에서 사용자 정보 조회
		  //결과는 Optional로 감싸서 반환 됨
		  Optional<User_Info> userOpt = logRepository.findByStdNo(username);
	        if (userOpt.isEmpty()) { //사용자 정보 없으면 예외 발생, spring security는 로그인 실패 처리함
	        	
	        	if (logRepository.existsByEmplNo(username)) {
	                // LoginFail에서 forbidden으로 인식하게 하기 위해 명시적으로 메시지 지정
	                throw new org.springframework.security.access.AccessDeniedException("forbidden");
	            }
	        	
	            throw new UsernameNotFoundException("해당 학번의 사용자가 존재하지 않습니다: " + username);
	        }

	        //조회된 사용자 정보를 StuUserDetails 클래스에 넘겨줌
	        return new StuUserDetails(userOpt.get());
	}
}
