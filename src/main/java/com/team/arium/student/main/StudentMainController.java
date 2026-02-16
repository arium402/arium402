package com.team.arium.student.main;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/student")
public class StudentMainController {

	//학생 로그인 페이지
	@GetMapping("/login")
	public String stloginpage() {
		return "/student/student_login";
	}
	
	//학생 메인
	@GetMapping("/main")
	public String stmainpage() {
		return "/student/main/student_main";
	}
	
	//학번 찾기
	@GetMapping("/find_student_id")
    public String findStudentId() {
		return "/student/find_student_id";
    }
    
	//사번 찾기
    @GetMapping("/find_counselor_id")
    public String findCounselorId() {
    	return "/student/find_counselor_id";
    }
    
    //학생 비밀번호 찾기
    @GetMapping("/find_student_password")
    public String findStudentPassword() {
    	return "/student/find_student_password";
    }
    
    //상담사 비밀번호 찾기
    @GetMapping("/find_counselor_password")
    public String findCounselorPassword() {
    	return "/student/find_counselor_password";
    }
    
    //비밀번호 변경
    @GetMapping("/change_password")
    public String changePassword() {
    	return "/student/change_password";
    }
    
}
