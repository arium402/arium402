package com.team.arium.student.login;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/student")
public class StudentLoginController {

	//로그인 페이지
	@GetMapping("/login")
	public String loginpage() {
		return "/student/student_login";
	}
	
	//학생 메인 페이지
	@GetMapping("/main")
	public String mainpage() {
		
		return "/student/main/student_main.html";
	}
	
//	@GetMapping("/my")
//	public String mypage() {
//		
//		return "/student/my/student_my_mypage.html";
//	}
}
