package com.team.arium.student.login;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/student")
public class StudentLogin {

	@GetMapping("/login")
	public String myPage(@RequestParam(name="error", required = false)String error) {
		System.out.println("에러발생시 :" + error);
		return "/student/student_login";	
	}
	
  	@PostMapping("/loginok.do")
  	public String loginok() {	//로그인 확인 페이지
  		return null;
  	}
	
	
	
}
