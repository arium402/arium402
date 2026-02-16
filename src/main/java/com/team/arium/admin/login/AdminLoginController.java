package com.team.arium.admin.login;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/admin")
public class AdminLoginController {

	//관리자 로그인 페이지
	@GetMapping("/login")
	public String adloginpage() {

		return "/admin/admin_login";
	}

	// 관리자 메인(대시보드) 페이지
	@GetMapping("/dashboard")
	public String adminDash() {

		return "/admin/admin_dashboard";
	}
	
//	//인가
//	@RequestMapping("/access-denied")
//    public String accessDenied(HttpServletRequest request) {
//        String referer = request.getHeader("Referer");
//        String typeParam = "student"; // 기본값
//
//        if (referer != null && referer.contains("/counselor")) {
//            typeParam = "counselor";
//        }
//
//        return "redirect:/student/login?error=forbidden&type=" + typeParam;
//    }
//	
}
