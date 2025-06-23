package com.team.arium.admin;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.team.arium.domain.Empl_Info;
import com.team.arium.model.generateNo;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/admin")
public class admin_controller {
	PrintWriter pw = null;
	
	@Autowired
	public admin_counselor_repo admin_cnsl_repo;

	@Autowired
	public admin_service admin_svc;
	
	@Resource(name="generateNo")
	public generateNo gen_no;
	
	
	List<String> list = null; 
	Map<String, String> map = null;
	String url = "";
	String msg = "";
	
	
	//관리자 메인화면 
	@GetMapping("/admin_dashboard")
	public String admin_dashboard(HttpServletResponse res) {
		
		
		return "/admin/admin_dashboard.html";
	}

	
//*******************************************************************************************//	
	
	
	
	//상담사 목록 
	@GetMapping("/admin_counselorList")
	public String admin_counselorList(HttpServletResponse res)  {

		return "/admin/admin_counselorList.html";
	}
	
	
	//상담사등록 페이지로 이동 
	@GetMapping("/admin_counselorList_add")
	public String admin_counselorList_add() {
		return "/admin/admin_counselorList_add.html";
	}
		
	//상담사 등록
	@PutMapping("/admin_counselorList_addOk")
	public ResponseEntity<String> admin_counselorList_addOk(@RequestBody admin_counselor_DTO emp_data, HttpServletResponse res) throws IOException {
		
		Empl_Info data_info = this.admin_svc.insert_counselor(emp_data);
		
		
		System.out.println(data_info);
		return null;
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
