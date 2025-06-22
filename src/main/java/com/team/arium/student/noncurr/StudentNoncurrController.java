package com.team.arium.student.noncurr;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/student/noncurr")
public class StudentNoncurrController {
	
	@GetMapping("/list")
	public String listPage() {
		
		return "/student/noncurr/student_noncurr_list.html";	// 비교과 목록
	}
	
	@GetMapping("/detail")
	public String detailPage() {
		
		return "/student/noncurr/student_noncurr_detail.html";	// 비교과 상세
	}
	
	@GetMapping("/add")
	public String addPage() {
		
		return "/student/noncurr/student_noncurr_add.html";	// 비교과 신청
	}
	
	@GetMapping("/addcheck")
	public String addcheckPage() {
		
		return "/student/noncurr/student_noncurr_addcheck.html";	// 비교과 신청 내역
	}
	
	@GetMapping("/survey")
	public String surveyPage() {
		
		return "/student/noncurr/student_noncurr_survey.html";	// 만족도 조사
	}
}