package com.team.arium.student.counsel.add;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
	public String addcheckPage(@RequestParam("stdId") Integer stdId, 
			@RequestParam(value = "page", defaultValue = "0") Integer page, @RequestParam(value = "size", defaultValue = "10") Integer size, Model m) {
		// 페이징 객체 생성
		Pageable pageable = PageRequest.of(page, size);
		
		// 상담 신청 내역 조회
		Page<StudentCounselAddDTO> cnslList = this.as.getCnslAplyList(stdId, pageable);
		
		// Model에 데이터 담기
		m.addAttribute("cnslList", cnslList);
		m.addAttribute("stdId", stdId);
		
		return "/student/counsel/add/student_counsel_addcheck.html";	// 상담 신청 내역
	}
}
