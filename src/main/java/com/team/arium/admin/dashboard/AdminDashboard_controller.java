package com.team.arium.admin.dashboard;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/admin")
public class AdminDashboard_controller {
	
	//관리자 메인화면 
	@GetMapping("/admin_dashboard")
	public String admin_dashboard(HttpServletResponse res) {
		
		
		return "/admin/admin_dashboard";
	}
}
