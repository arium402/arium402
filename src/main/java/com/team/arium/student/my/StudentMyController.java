package com.team.arium.student.my;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/student/my")
public class StudentMyController {
	
	@GetMapping("/mypage")
	public String mypagePage() {
		
		return "/student/my/student_my_mypage.html";	// 마이페이지
	}
}
