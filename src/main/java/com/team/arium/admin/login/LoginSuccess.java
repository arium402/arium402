package com.team.arium.admin.login;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.team.arium.domain.User_Info;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
//로그인 성공 후 동작을 커스마이징 하기 위한 클래스
//AutenticationSuccessHandler를 구현하여 사용자의 상태를 판단하여 리다이렉트 동작 제어
public class LoginSuccess implements AuthenticationSuccessHandler{

	   @Override
	    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
	                                         Authentication authentication) throws IOException, ServletException {

	        Object principal = authentication.getPrincipal(); //로그인한 사용자의 상세 정보 꺼냄 
	        String loginPath = request.getServletPath(); // 예: /admin-login-process

	        //instanceof를 통해 사용자 타입이 관리자/상담사(EmplUserDetails), 학생(StuUserDetails)경우, 각자 알맞은 메서드로 처리됨
	        if (principal instanceof EmplUserDetails emplDetails) {
	            handleEmplLogin(emplDetails, loginPath, response, request);
	        }
	        //학생 로그인
	        else if (principal instanceof StuUserDetails stuDetails) {
	            handleStudentLogin(stuDetails, loginPath, response, request);
	        }
	        else {
	            response.sendRedirect("/student/login?error=forbidden"); //어떤 타입도 아닌 경우, 안전하게 로그인 페이지로 돌려보냄
	        }
	        
	        
	    }

	    // 관리자 및 상담사 처리
	    private void handleEmplLogin(EmplUserDetails userDetails, String loginPath, 
	    		HttpServletResponse response,  HttpServletRequest request) throws IOException {
	        User_Info user = userDetails.getUser(); //로그인한 사용자의 정보(사번, 역할, 상태코드) 가져옴
	        String role = user.getUserRole(); //권한(ROLE_ADMIN, ROLE_COUNSELOR)
	        int statusCode = user.getEmplInfo().getEmplStatCd().getCodeId(); //상태 코드(재직, 휴직, 퇴사)

	        if (statusCode != EmplStatusCode.WORKING) { //근무 중 아니면 로그인 실패 처리
	            response.sendRedirect("/student/login?error=forbidden");
	            return;
	        }

	        // 세션에 이름 저장
	        request.getSession().setAttribute("userName", user.getEmplInfo().getEmplName());  // 관리자/상담사 이름 세션 저장

	        
	        // 로그인 시도한 URL과 역할이 맞는지 검증
	        if (loginPath.equals("/admin-login-process") && !role.equals("ROLE_ADMIN")) {
	            response.sendRedirect("/admin/login?error=forbidden");
	            return;
	        }

	        if (loginPath.equals("/counselor-login-process") && !role.equals("ROLE_COUNSELOR")) {
	            response.sendRedirect("/student/login?error=forbidden");
	            return;
	        }

	        /*관리자면 /admin/dashboard
	         *상담사면 /counselor/dashboard로 이동
	         *그 외 역할은 다시 로그인 창으로 이동*/
	        switch (role) {
	            case "ROLE_ADMIN":
	                response.sendRedirect("/admin/dashboard");
	                break;
	            case "ROLE_COUNSELOR":
	                response.sendRedirect("/counselor/dashboard");
	                break;
	            default:
	                response.sendRedirect("/student/login?error=forbidden");
	                break;
	        }

	        System.out.println("Empl Login Success → role: " + role + ", statusCode: " + statusCode);
	    }

	    // 학생 처리
	    private void handleStudentLogin(StuUserDetails userDetails, String loginPath, 
	    		HttpServletResponse response,  HttpServletRequest request) throws IOException {
	        User_Info user = userDetails.getUser(); //로그인한 사용자의 정보(학번, 역할, 상태코드) 가져옴
	        String role = user.getUserRole(); //권한(ROLE_STUDENT)
	        int statusCode = user.getStdInfo().getStdStatCd().getCodeId(); //상태 코드(재학, 휴학, 자퇴, 졸업)

	        // 로그인 시도한 URL과 역할이 맞는지 검증
	        if (loginPath.equals("/student-login-process") && !role.equals("ROLE_STUDENT")) {
	            response.sendRedirect("/student/login?error=forbidden");
	            return;
	        }
	        
	        //재학, 휴학 중이면 /student/main으로 이동
	        //졸업, 자퇴의 경우는 로그인 거부
	        if (statusCode == StuStatusCode.INSCHOOL || statusCode == StuStatusCode.SEMESTEROFF) {
	        	// 세션에 학생 이름 저장
	            request.getSession().setAttribute("userName", user.getStdInfo().getStdNm());
	        	
	        	response.sendRedirect("/student/main");
	        } else {
	            response.sendRedirect("/student/login?error=forbidden");
	        }

	        System.out.println("Student Login Success → statusCode: " + statusCode);
	    }
	    
	    // 역할별 로그인 페이지로 이동 (근무 상태 아님일 때)
	    private void redirectToLoginByRolze(String role, HttpServletResponse response) throws IOException {
	        switch (role) {
	            case "ROLE_ADMIN":
	                response.sendRedirect("/admin/login?error=forbidden");
	                break;
	            case "ROLE_COUNSELOR":
	                response.sendRedirect("/student/login?error=forbidden");
	                break;
	            default:
	                response.sendRedirect("/student/login?error=forbidden");
	                break;
	        }
	   }
}