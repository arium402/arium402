package com.team.arium.competence;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/student/competence")
public class StudentCompetenceController {
	
	@Autowired
	private StudentCompetenceService scs;
	
	@GetMapping("/info")
	public String infoPage(Model m) {
		Map<String, Object> data = this.scs.getcompetence();
		
		m.addAttribute("data", data);
		
		return "/student/competence/student_competence_info.html";	// 핵심 역량 소개
	}
	
	@GetMapping("/test")
	public String testPage(Model m) {
		try {
			// 진단 완료 여부 체크
			Map<String, Object> diagnosisStatus = this.scs.checkDiagnosisStatus();
			boolean hasCompleted = (Boolean) diagnosisStatus.get("hasCompleted");
			String evalId = (String) diagnosisStatus.get("evalId");
			
			m.addAttribute("hasCompleted", hasCompleted);
			m.addAttribute("evalId", evalId);
			
			if (!hasCompleted) {
				// 진단을 하지 않은 경우에만 문항 데이터 로드
				Map<String, Object> data = this.scs.getQuestions();
				
				m.addAttribute("data", data);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			
			String error = "진단 페이지 로드 중 오류가 발생했습니다.";
			boolean hasCompleted = false;
			
			m.addAttribute("error", error);
			m.addAttribute("hasCompleted", hasCompleted);
		}

		return "/student/competence/student_competence_test.html";	// 핵심 역량 진단
	}
	
	// 답변 저장
	@PostMapping("/submit")
	@ResponseBody
	public String submitAnswers(@RequestBody List<StudentCompetenceDTO> answers, HttpSession session) {
		try {
			Integer stdId = this.scs.getCurrentStdId();
			
			String evalId = this.scs.saveAnswers(stdId, answers);
			
			return evalId;
		}
		catch(Exception e){
			e.printStackTrace();
			
			return "ERROR:" + e.getMessage();
		}
	}
	
	@GetMapping("/chart")
	public String chartPage(Model m) {
		try {
			// 현재 로그인한 사용자의 진단 결과 조회
			Map<String, Object> diagnosisStatus = this.scs.checkDiagnosisStatus();
			boolean hasCompleted = (Boolean) diagnosisStatus.get("hasCompleted");

			if (!hasCompleted) {
				String error = "진단을 먼저 완료해주세요.";
				m.addAttribute("error", error);
			}
			
			String evalId = (String) diagnosisStatus.get("evalId");
			
			// 진단 결과 데이터 조회
			Map<String, Object> competencyData = this.scs.getCompetenceResult(evalId);
			
			if (competencyData != null) {
				m.addAttribute("competencyData", competencyData);
			}
			else {
				String error = "진단 결과를 찾을 수 없습니다.";
				m.addAttribute("error", error);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			
			String error = "결과 조회 중 오류가 발생했습니다.";
			
			m.addAttribute("error", error);
		}
		
		return "/student/competence/student_competence_chart.html";	// 핵심 역량 결과
	}
}