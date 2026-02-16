package com.team.arium.competence;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.team.arium.domain.Core_CptEval;
import com.team.arium.domain.Core_CptInfo;
import com.team.arium.domain.Core_CptQst;

@Service
public class StudentCompetenceService {

	@Autowired
	private StudentCompetenceRepository scr;
	
	// 핵심 역량 진단 소개 출력
	public Map<String, Object> getcompetence(){
		List<Core_CptInfo> upcom = this.scr.findByUpCclIdIsNullOrderByCclCdAsc();
		List<Core_CptInfo> subcom = this.scr.findByUpCclIdIsNotNullOrderByUpCclIdAscCclCdAsc();
		
		List<StudentCompetenceDTO> result = new ArrayList<>();
		
		for (Core_CptInfo up : upcom) {
			for (Core_CptInfo sub : subcom) {
				if(sub.getUpCclId().longValue() == up.getCclId()) {
					StudentCompetenceDTO dto = new StudentCompetenceDTO();
					dto.setCategoryName(up.getCclNm());
					dto.setCclNm(sub.getCclNm());
					dto.setCclDesc(sub.getCclDesc());
					
					result.add(dto);
				}
			}
		}

		Map<String, Object> data = new HashMap<>();
		data.put("result", result);
		data.put("upcom", upcom);
		
		return data;
	}
	
	// 핵심 역량 진단 문항 출력
	public Map<String, Object> getQuestions(){
		List<Core_CptQst> allQuestions = this.scr.findAllQuestionsOrderByQstOrdAsc();
		
		List<StudentCompetenceDTO> result = new ArrayList<>();
		
		for (Core_CptQst all : allQuestions) {
			Core_CptInfo subcom = all.getCoreCptInfo();
			Core_CptInfo upcom = scr.findByCclId(subcom.getUpCclId());
			
			if(upcom != null) {
				StudentCompetenceDTO dto = new StudentCompetenceDTO();
				dto.setCategoryName(upcom.getCclNm());
				dto.setCclNm(subcom.getCclNm());
				dto.setCclDesc(subcom.getCclDesc());
				dto.setQstOrd(all.getQstOrd());
				dto.setQstContent(all.getQstContent());
				dto.setQstId(all.getQstId().intValue());
				dto.setCclId(subcom.getCclId().intValue());
				
				result.add(dto);
			}
		}
		
		List<Core_CptInfo> up = this.scr.findByUpCclIdIsNullOrderByCclCdAsc();
		
		Map<String, Object> data = new HashMap<>();
		data.put("result", result);
		data.put("up", up);
		
		return data;
	}
	
	// 현재 로그인한 사용자의 std_id 조회
	public Integer getCurrentStdId() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		
		if (authentication == null || !authentication.isAuthenticated()) {
			throw new RuntimeException("인증되지 않은 사용자입니다.");
		}
		
		String stdNo = authentication.getName();	// 학번
		Integer stdId = this.scr.findStdIdByStdNo(stdNo);
		
		if (stdId == null) {
			throw new RuntimeException("학생 정보를 찾을 수 없습니다: " + stdNo);
		}
		
		return stdId;
	}
	
	// 현재 사용자의 진단 완료 여부 및 evalId 반환
	public Map<String, Object> checkDiagnosisStatus() {
		Integer stdId = getCurrentStdId();
		
		Map<String, Object> result = new HashMap<>();
		String evalId = this.scr.findEvalIdByStdId(stdId);
		
		// evalId가 있으면 진단 완료, 없으면 미완료
		boolean hasCompleted = (evalId != null);
		
		result.put("hasCompleted", hasCompleted);
		result.put("evalId", evalId);
		
		return result;
	}

	// 답변 저장
	@Transactional
	public String saveAnswers(Integer stdId, List<StudentCompetenceDTO> answers) {
		String evalId;
		
		// 실시 ID 생성
		do {
			long ctime = System.currentTimeMillis();
			evalId = "E" + String.valueOf(ctime).substring(5);
		} while(this.scr.existsEvalByEvalId(evalId));
		
		// 현재 시간을 메서드 안에서 직접 생성
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String currentTime = sdf.format(new Date());
		
		for (StudentCompetenceDTO ans : answers) {
			this.scr.insertEvaluation(evalId, ans.getQstId(), stdId, ans.getAnsScore(), currentTime);
		}
		
		// 상위 역량 점수 저장
		saveMainCompetencyScores(stdId, evalId, answers);
		
		return evalId;
	}
	
	// 상위 역량 4개만 std_ccl_score에 저장
	private void saveMainCompetencyScores(Integer stdId, String evalId, List<StudentCompetenceDTO> answers) {
		// 상위 역량별로 점수 수집
		Map<String, List<Integer>> subCompetencyScores = new HashMap<>();
		Map<String, Integer> subToMainIdMapping = new HashMap<>();
		
		for (StudentCompetenceDTO ans : answers) {
			Core_CptQst question = this.scr.findQuestionById(ans.getQstId());
			Core_CptInfo subCompetency = question.getCoreCptInfo();
			
			String subKey = subCompetency.getCclNm();	// 하위 역량명
			Integer mainCclId = subCompetency.getUpCclId();	// 상위 역량 ID
			
			if (!subCompetencyScores.containsKey(subKey)) {
				subCompetencyScores.put(subKey, new ArrayList<>());
				subToMainIdMapping.put(subKey, mainCclId);
			}
			
			int score = ans.getAnsScore() * 20;
			subCompetencyScores.get(subKey).add(score);
		}
		
		// 상위 역량별로 하위 역량 평균들을 모으기
		Map<Integer, List<Integer>> mainCompetencyScores = new HashMap<>();
		
		for (String subKey : subCompetencyScores.keySet()) {
			Integer mainCclId = subToMainIdMapping.get(subKey);
			
			if (!mainCompetencyScores.containsKey(mainCclId)) {
				mainCompetencyScores.put(mainCclId, new ArrayList<>());
			}
			
			// 하위 역량의 평균 계산
			List<Integer> scores = subCompetencyScores.get(subKey);
			
			int total = 0;
			for (Integer score : scores) {
				total += score;
			}
			
			int subAvgScore = scores.size() > 0 ? total / scores.size() : 0;
			
			// 상위 역량에 하위 역량 평균 추가
			mainCompetencyScores.get(mainCclId).add(subAvgScore);
		}
		
		// 상위 역량별 평균 계산하고 저장
		for (Integer mainCclId : mainCompetencyScores.keySet()) {
			List<Integer> subAverages = mainCompetencyScores.get(mainCclId);
			
			// 하위 역량 평균들의 평균
			int total = 0;
			for (Integer avg : subAverages) {
				total += avg;
			}
			
			int finalAvgScore = subAverages.size() > 0 ? total / subAverages.size() : 0;
			
			this.scr.insertCompetencyScore(stdId, mainCclId, 41, evalId, finalAvgScore);
		}
	}
	
	// 진단 결과 조회
	public Map<String, Object> getCompetenceResult(String evalId) {
		List<Core_CptEval> evaluations = this.scr.findEvaluationsByEvalId(evalId);	// evalId로 해당 진단의 모든 답변 조회
		
		if (evaluations.isEmpty()) {
			return null;
		}
		
		// 역량별로 데이터 분류 및 점수 계산
		Map<String, List<Integer>> subCompetencyScores = new HashMap<>();
		Map<String, String> subToMainMapping = new HashMap<>();
		Map<String, String> mainCompetencyNames = new HashMap<>();
		
		for (Core_CptEval eval : evaluations) {
			Core_CptQst question = eval.getCoreCptQst();
			Core_CptInfo subCompetency = question.getCoreCptInfo();	// 세부 역량
			Core_CptInfo mainCompetency = this.scr.findByCclId(subCompetency.getUpCclId());	// 상위 역량
			
			String subKey = subCompetency.getCclNm(); // 하위 역량명			
			String mainKey = getCompetencyKey(mainCompetency.getCclNm());	// 역량명을 key로 변경
			
			// 하위 역량별로 점수 저장
			if (!subCompetencyScores.containsKey(subKey)) {
				subCompetencyScores.put(subKey, new ArrayList<>());
				subToMainMapping.put(subKey, mainKey);
				mainCompetencyNames.put(mainKey, mainCompetency.getCclNm());
			}
			
			// 점수는 1-5를 20점씩 곱해서 100점 만점으로 변환
			int score = eval.getAnsScore() * 20;
			subCompetencyScores.get(subKey).add(score);
		}

		Map<String, List<Integer>> competencyScores = new HashMap<>();
		
		for (String subKey : subCompetencyScores.keySet()) {
			String mainKey = subToMainMapping.get(subKey);
			
			if (!competencyScores.containsKey(mainKey)) {
				competencyScores.put(mainKey, new ArrayList<>());
			}
			
			// 하위 역량의 평균 점수 계산
			List<Integer> scores = subCompetencyScores.get(subKey);
			
			int total = 0;
			for (Integer score : scores) {
				total += score;
			}
			
			int avgScore = scores.size() > 0 ? total / scores.size() : 0;
			
			// 상위 역량에 하위 역량 평균 추가
			competencyScores.get(mainKey).add(avgScore);
		}
		
		Map<String, Object> result = new HashMap<>();
		
		String[] colors = {
				"rgba(75, 192, 192, 0.3)", "rgba(255, 99, 132, 0.3)",
				"rgba(255, 206, 86, 0.3)", "rgba(54, 162, 235, 0.3)"
		};
		String[] borderColors = {
				"rgba(75, 192, 192, 1)", "rgba(255, 99, 132, 1)",
				"rgba(255, 206, 86, 1)", "rgba(54, 162, 235, 1)"
		};
		
		int colorIndex = 0;
		
		for (String key : competencyScores.keySet()) {
			Map<String, Object> competencyData = new HashMap<>();
			List<Integer> scores = competencyScores.get(key);
			
			competencyData.put("name", mainCompetencyNames.get(key));
			competencyData.put("color", colors[colorIndex % colors.length]);
			competencyData.put("borderColor", borderColors[colorIndex % borderColors.length]);				
			competencyData.put("scores", scores);
			
			// 평균 점수 계산
			int total = 0;
			
			for (Integer score : scores) {
				total += score;
			}
			
			int avgScore = scores.size() > 0 ? total / scores.size() : 0;
			competencyData.put("avgScore", avgScore);
			
			result.put(key, competencyData);
			colorIndex++;
		}
		
		return result;
	}
	
	private String getCompetencyKey(String competencyName) {
		// 실제 역량명에 맞게 수정 필요
		if (competencyName.equals("글로컬 리더 역량")) {
			return "global";
		}
		else if (competencyName.equals("협력적 소통 역량")) {
			return "communication";
		}
		else if (competencyName.equals("창의적 혁신 역량")) {
			return "innovation";
		}
		else if (competencyName.equals("융합적 탐구 역량")) {
			return "inquiry";
		}
		
		return "unknown";
	}
}