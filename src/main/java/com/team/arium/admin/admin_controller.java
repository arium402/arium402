package com.team.arium.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.team.arium.domain.Empl_Info;

import jakarta.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/admin")
public class admin_controller {
	
	@Autowired
	public admin_repo admin_repo;


	
//*******************************************************************************************//	
	
	
	
	//상담사 목록 
	@GetMapping("/admin_counselorList")
	public String admin_counselorList(HttpServletResponse res)  {
		//Empl_Info 도메인
		
		
		
		return "/admin/admin_counselorList";
	}
	
	
	//상담사등록 
	@GetMapping("/admin_counselorList_add")
	public String admin_counselorList_add(HttpServletResponse res) {
		Empl_Info emp_insert = new Empl_Info();
		
		
		return "/admin/admin_counselorList_add";
	}
		
		
	//상담사관리 > 상담사 일정관리 
	@GetMapping("/admin_counselor_schedule")
	public String admin_counselor_schedule(HttpServletResponse res){
		
		
		return "/admin/admin_counselor_schedule";
	}
	
	
	//상담사 관리 > 상담사별 통계 
	@GetMapping("/admin_counselor_statistics")
	public String admin_counselor_statistics(HttpServletResponse res)  {
		
		
		return "/admin/admin_counselor_statistics";
	}
	
	
	
	
	
	//상담사 정보 
	@GetMapping("/admin_counselorList_detail")
	public String admin_counselorList_detail(HttpServletResponse res)  {
		
		
		return "/admin/admin_counselorList_detail";
	}
	
		
		
	//*******************************************************************************************//
	
	
	//상담사 관리 > 상담신청내역
	@GetMapping("/admin_counselor_studentApply")
	public String admin_counselor_studentApply(HttpServletResponse res)  {
		
		
		return "/admin/admin_counselor_studentApply";
	}
	
	
	//상담관리 > 상담 신청내역 
	@GetMapping("/admin_counselor_scheduleDetail")
	public String admin_counselor_scheduleDetail(HttpServletResponse res)  {
		
		
		return "/admin/admin_counselor_scheduleDetail";
	}
	
	
	//상담관리 > 상담 분야별 통계
	@GetMapping("/admin_counselingType_stats")
	public String admin_counselingType_stats(HttpServletResponse res)  {
		
		return "/admin/admin_counselingType_stats";
	}
	

	

}
