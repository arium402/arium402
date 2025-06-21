package com.team.arium.competence;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/student/competence")
public class StudentCompetenceController {
	
	@GetMapping("/info")
	public String infoPage() {
		
		return "/student/competence/student_competence_info.html";	// 핵심 역량 소개
	}
	
	@GetMapping("/test")
	public String testPage() {
		
		return "/student/competence/student_competence_test.html";	// 핵심 역량 진단
	}
	
	@GetMapping("/chart")
	public String chartPage() {
		
		return "/student/competence/student_competence_chart.html";	// 핵심 역량 결과
	}
}