package com.team.arium.admin.login;

import java.io.IOException;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

//로그인 실패 후 동작을 커스터마이징 하기 위한 클래스
public class LoginFail implements AuthenticationFailureHandler{

	//로그인 시도 했지만 실패했을 때 호출되는 메소드
	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException exception) throws IOException, ServletException {

		 String errorMessage = "unknown";

	        if (exception instanceof BadCredentialsException) {
	            errorMessage = "bad"; // 아이디/비번 오류
	        } else {
	            errorMessage = "forbidden"; // 그 외는 권한 없음 처리
	        }

	        //에러 메세지를 붙여 다시 로그인 페이지로 이동시킴
	        response.sendRedirect("/admin/login?error=" + errorMessage);
		
	}
}
