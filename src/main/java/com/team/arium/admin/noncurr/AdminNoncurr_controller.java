package com.team.arium.admin.noncurr;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/admin")
public class AdminNoncurr_controller {
	
	//비교과 목록
	@GetMapping("/noncurr_list")
	public String noncurr_list() {
		
		
		return "/admin/admin_noncurr_list";
	}
	
	
	// 비교과 등록 페이지
	@GetMapping("/noncurr_add")
	public String noncurr_add() {
	    return "/admin/admin_noncurr_list_add";
	}
	
	
	// 비교과 상세 페이지
	@GetMapping("/noncurr_detail")
	public String noncurr_detail() {
			return "/admin/admin_noncurr_list_detail";	
	}
	
	// 비교과 상세 > 수정 페이지
	@GetMapping("/noncurr_edit")
	public String noncurr_edit() {
		return "/admin/admin_noncurr_list_edit";
	}

	// 비교과 통계
	@GetMapping("/noncurr_stat")
	public String noncurr_stat() {
		return "/admin/admin_noncurr_stat";	
	}
	
}
