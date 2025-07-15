package com.team.arium.admin.login;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.team.arium.domain.User_Info;



@Service //스프링 빈으로 자동 등록되도록 지정하는 어노테이션, spring security가 자동으로 이 클래스를 찾아서 로그인 과정에 사용

//UserDetailsService 구현
//사용자가 입력한 ID를 기반, DB에서 사용자 조회 후, UserDetails(CustomerUserDetails)객체로 반환
//spring security가 로그인 시 사용자 정보 조회할 때 인터페이스의 loadUserByUserName() 메소드 호출
public class EmplUserDetailsService implements UserDetailsService {

	//DB에서 user_info 엔티티를 조회하는 인터페이스, 생성자를 통해 주입받아 사용
	private final LoginRepository logRepository;
	
	@Autowired
	public EmplUserDetailsService(LoginRepository logRepository) {
	    this.logRepository = logRepository;
	}
	
	//로그인시 입력한 사용자 ID(사번)가 username으로 들어옴
	//사용자의 인증 처리를 위함
	  @Override
	    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
	      //사번으로 user_info 테이블에서 사용자 정보 조회
		  //결과는 Optional로 감싸서 반환 됨
		  Optional<User_Info> userOpt = logRepository.findByEmplNo(username);
	        if (userOpt.isEmpty()) { //사용자 정보 없으면 예외 발생, spring security는 로그인 실패 처리함
	        	
	        	 // ❗ 학생 계정으로 관리자 로그인 시도한 경우
	            if (logRepository.existsByStdNo(username)) {
	                // LoginFail에서 forbidden으로 인식하게 하기 위해 명시적으로 메시지 지정
	                throw new org.springframework.security.access.AccessDeniedException("forbidden");
	            }
	            
	            throw new UsernameNotFoundException("해당 사번의 사용자가 존재하지 않습니다: " + username);
	        }

	        //조회된 사용자 정보를 EmplUserDetails 클래스에 넘겨줌
	        return new EmplUserDetails(userOpt.get());
	    }
}