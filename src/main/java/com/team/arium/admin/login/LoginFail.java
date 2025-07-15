package com.team.arium.admin.login;

import java.io.IOException;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
//로그인 실패 후 동작을 커스터마이징 하기 위한 클래스
public class LoginFail implements AuthenticationFailureHandler{

	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
	                                     AuthenticationException exception) throws IOException, ServletException {

	    String loginPath = request.getServletPath();
	    String redirectBase; // 기본값

	    // 요청 경로에 따라 리다이렉트 기본 경로 설정
        switch (loginPath) {
            case "/admin-login-process":
                redirectBase = "/admin/login";
                break;
            case "/counselor-login-process":
            case "/student-login-process":
                redirectBase = "/student/login";
                break;
            default:
                redirectBase = "/student/login";
                break;
        }

     // 에러 타입에 따라 파라미터 설정
        String errorParam;
        if(exception instanceof BadCredentialsException) {
        	errorParam = "bad";
        }
        else if (exception instanceof DisabledException) {
            // 계정 비활성화 (재직 상태 아님)
            errorParam = "forbidden";
        }
        //예외메세지가 null이 아니고, 메세지 안에 forbidden이라는 단어가 들어있다면 권한 문제 처리
        else if (exception.getMessage() != null && exception.getMessage().contains("forbidden")){
        	errorParam = "forbidden";
        }
        else {
        	errorParam = "fail"; //id, pw / 권한 이외의 문제
        }
        response.sendRedirect(redirectBase + "?error=" + errorParam);
    }
}