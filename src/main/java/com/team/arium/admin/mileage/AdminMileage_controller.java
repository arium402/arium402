package com.team.arium.admin.mileage;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/admin")
public class AdminMileage_controller {

	//마일리지 지급 리스트
	@GetMapping("/admin_mileage_payment")
	public String admin_mileage_payment(HttpServletResponse res)  {
		
		
		return "/admin/admin_mileage_payment";
	}
	//마일리지 지급 / 상세
	@GetMapping("/admin_mileage_payment_add")
	public String admin_mileage_payment_add(HttpServletResponse res)  {
		
		
		return "/admin/admin_mileage_payment_add";
	}
	
	
	//마일리지 전환 
	@GetMapping("/admin_mileage_to_money")
	public String admin_mileage_to_money(HttpServletResponse res) {
		
		
		return "/admin/admin_mileage_to_money";
	}
	
}
