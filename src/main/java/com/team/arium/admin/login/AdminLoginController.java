package com.team.arium.admin.login;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminLoginController {

	// 로그인 페이지
	@GetMapping("/login")
	public String loginpage() {

		return "/admin/admin_login";
	}

	// 관리자 메인(대시보드) 페이지
	@GetMapping("/dashboard")
	public String adminDash() {

		return "/admin/admin_dashboard";
	}

}
