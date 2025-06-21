package com.team.arium.student.mileage;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/student/mileage")
public class StudentMileageController {
	
	@GetMapping("/my")
	public String myPage() {
		
		return "/student/mileage/student_mileage_my.html";	// 나의 마일리지
	}
}