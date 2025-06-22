package com.team.arium.student.counsel.before;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/student/counsel/before")
public class StudentCounselBeforeController {
	
	@GetMapping("/gen")
	public String genPage() {
		
		return "/student/counsel/before/student_counsel_bef_gen.html";	// 일반 상담 사전 검사
	}
	
	@GetMapping("/car")
	public String carPage() {
		
		return "/student/counsel/before/student_counsel_bef_car.html";	// 진로/취업 상담 사전 검사
	}
	
	@GetMapping("/con")
	public String conPage() {
		
		return "/student/counsel/before/student_counsel_bef_con.html";	// 학습 컨설팅 사전 검사
	}
}