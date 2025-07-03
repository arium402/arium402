package com.team.arium.student.counsel.add;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/student/counsel/add")
public class StudentCounselAddController {
	
	@Autowired
	private StudentCounselAddService as;
	
	@GetMapping("/choose")
	public String choosePage(Model m, @RequestParam(value = "startDate", required = false) String startDate) {

		// Service에서 모든 처리 (날짜 계산 포함)
		Map<String, Object> scheduleData = this.as.getScheduleData(startDate);

		// 모델에 데이터 담아서 HTML로 전달
		m.addAttribute("scheduleData", scheduleData);

		return "/student/counsel/add/student_counsel_choose.html";	// 상담사 선택
	}
	

	@GetMapping("/addcheck")
	public String addcheckPage() {
		
		return "/student/counsel/add/student_counsel_addcheck.html";	// 상담 신청 내역
	}
}
