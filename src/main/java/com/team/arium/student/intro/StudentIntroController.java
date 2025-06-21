package com.team.arium.student.intro;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/student/intro")
public class StudentIntroController {
	
	@GetMapping("/center")
	public String centerPage() {
		
		return "/student/intro/student_intro_center.html";	// 센터 소개
	}
	
	@GetMapping("/process")
	public String processPage() {
		
		return "/student/intro/student_intro_process.html";	// 프로세스 소개
	}

	@GetMapping("/coming")
	public String comingPage() {
		
		return "/student/intro/student_intro_coming.html";	// 오시는 길
	}	
}