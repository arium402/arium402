package com.team.arium.student.counsel.before;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/student/counsel/before")
public class StudentCounselBeforeController {
	
	@Autowired
	private StudentCounselBeforeService scs;
	
	@GetMapping("/gen")
	public String genPage(@RequestParam("cnslCd") Integer cnslCd, Model m) {
		List<StudentCounselBeforeDTO> result = this.scs.getQuestionsByType(1);
		
		m.addAttribute("result", result);
		m.addAttribute("preSurveyId", 1);
		m.addAttribute("cnslCd", cnslCd);
		
		return "/student/counsel/before/student_counsel_bef_gen.html";	// 일반 상담 사전 검사
	}
	
	@GetMapping("/car")
	public String carPage(@RequestParam("cnslCd") Integer cnslCd, Model m) {
		List<StudentCounselBeforeDTO> result = this.scs.getQuestionsByType(2);
		
		m.addAttribute("result", result);
		m.addAttribute("preSurveyId", 2);
		m.addAttribute("cnslCd", cnslCd);
		
		return "/student/counsel/before/student_counsel_bef_car.html";	// 진로/취업 상담 사전 검사
	}
	
	@GetMapping("/con")
	public String conPage(@RequestParam("cnslCd") Integer cnslCd, Model m) {
		List<StudentCounselBeforeDTO> result = this.scs.getQuestionsByType(3);
		
		m.addAttribute("result", result);
		m.addAttribute("preSurveyId", 3);
		m.addAttribute("cnslCd", cnslCd);
		
		return "/student/counsel/before/student_counsel_bef_con.html";	// 학습 컨설팅 사전 검사
	}

	@PostMapping("/save")
	@ResponseBody
	public Map<String, Object> saveAnswers(@RequestParam("preSurveyId") Integer preSurveyId, @RequestParam("cnslCd") Integer cnslCd, HttpServletRequest request) {
		Map<String, Object> response = new HashMap<>();
		
		try{
			Map<String, String[]> answers = request.getParameterMap();
			
			Integer stdId = 1;
			
			String preEvalId = this.scs.saveAnswers(stdId, preSurveyId, answers);
			
			response.put("success", true);
			response.put("preEvalId", preEvalId);
			response.put("cnslCd", cnslCd);
			response.put("message", "답변이 성공적으로 저장되었습니다.");
		}
		catch (Exception e) {
			response.put("success", false);
			response.put("message", "저장 중 오류가 발생했습니다: " + e.getMessage());
		}
		
		return response;
	}
}