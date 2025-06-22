package com.team.arium.student.counsel.add;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/student/counsel/add")
public class StudentCounselAddController {
	
	@GetMapping("/choose")
	public String choosePage() {
		
		return "/student/counsel/add/student_counsel_choose.html";	// 상담사 선택
	}
	

	@GetMapping("/addcheck")
	public String addcheckPage() {
		
		return "/student/counsel/add/student_counsel_addcheck.html";	// 상담 신청 내역
	}
}
