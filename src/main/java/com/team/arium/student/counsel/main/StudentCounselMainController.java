package com.team.arium.student.counsel.main;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/student/counsel/main")
public class StudentCounselMainController {
	
	@GetMapping("/psy")
	public String psyPage() {
		
		return "/student/counsel/main/student_counsel_psy.html";	// 심리 상담
	}

	@GetMapping("/ano")
	public String anoPage() {
		
		return "/student/counsel/main/student_counsel_ano.html";	// 익명 상담
	}
	
	@GetMapping("/cri")
	public String criPage() {
		
		return "/student/counsel/main/student_counsel_cri.html";	// 위기 상담
	}
	
	@GetMapping("/car")
	public String carPage() {
		
		return "/student/counsel/main/student_counsel_car.html";	// 진로/취업 상담
	}
	
	@GetMapping("/con")
	public String conPage() {
		
		return "/student/counsel/main/student_counsel_con.html";	// 학습 컨설팅
	}
}