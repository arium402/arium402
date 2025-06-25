package com.team.arium.competence;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/student/competence")
public class StudentCompetenceController {
	
	@Autowired
	private StudentCompetenceService scs;
	
	@GetMapping("/info")
	public String infoPage(Model m) {
		Map<String, Object> data = this.scs.getcompetence();
		
		m.addAttribute("data", data);
		
		return "/student/competence/student_competence_info.html";	// 핵심 역량 소개
	}
	
	@GetMapping("/test")
	public String testPage(Model m) {
		Map<String, Object> data = this.scs.getQuestions();
		
		m.addAttribute("data", data);
		
		return "/student/competence/student_competence_test.html";	// 핵심 역량 진단
	}
	
	@GetMapping("/chart")
	public String chartPage() {
		
		return "/student/competence/student_competence_chart.html";	// 핵심 역량 결과
	}
}