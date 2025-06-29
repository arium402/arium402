package com.team.arium.admin.login;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import com.team.arium.domain.User_Info;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

//로그인 성공 후 동작을 커스마이징 하기 위한 클래스
//AutenticationSuccessHandler를 구현하여 사용자의 상태를 판단하여 리다이렉트 동작 제어
public class LoginSuccess implements AuthenticationSuccessHandler{

	//로그인 한 사용자의 정보 가져올 수 있음
	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException { 

		//CustomerUserDetails에 담겨있던 실제 user_info 엔티티 가져옴
		//DB에 저장된 사용자의 상세 정보에 접근 할 수 있음 
		 AdminUserDetails userDetails = (AdminUserDetails) authentication.getPrincipal();
	        User_Info user = userDetails.getUser();

	        // 권한 리스트에서 ROLE_ADMIN 포함 여부 확인
	        boolean isAdmin = authentication.getAuthorities().stream()
	                        .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
	        
	        if (!isAdmin) {
	            // admin 권한 없으면 권한 없음 처리
	            response.sendRedirect("/admin/login?error=forbidden");
	            return;
	        }
	        
	        // admin인 경우 상태 체크
	        int emplStatCd = user.getEmplInfo().getEmplStatCd().getCodeId();

	        if (emplStatCd == 32 || emplStatCd == 33) {
	            // 휴직, 퇴사 상태 권한 없음 처리
	            response.sendRedirect("/admin/login?error=forbidden");
	        } else if (emplStatCd == 31) {
	            // 근무 중이면 정상 로그인
	            response.sendRedirect("/admin/dashboard");
	        } else {
	            // 그 외 상태도 권한 없음 처리
	        	// 근무, 휴직, 퇴사외의 다른 값이 들어왔을 때를 대비하여 권한 없음으로 처리하기 위함
	            response.sendRedirect("/admin/login?error=forbidden");
	        }
		
	        System.out.println("emplStatCd = " + emplStatCd);
	        System.out.println("User Roles = " + authentication.getAuthorities());

	}
	
}
