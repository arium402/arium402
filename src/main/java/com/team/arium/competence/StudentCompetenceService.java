package com.team.arium.competence;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.team.arium.domain.Core_CptEval;
import com.team.arium.domain.Core_CptInfo;
import com.team.arium.domain.Core_CptQst;
import com.team.arium.domain.Std_Info;

@Service
public class StudentCompetenceService {

	@Autowired
	private StudentCompetenceRepository scr;
	
	@Autowired
	private StudentCompetenceEvalRepository scer;
	
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
			Core_CptEval eval = new Core_CptEval();
			eval.setEvalId(evalId);
			eval.setCoreCptQst(this.scr.findQuestionById(ans.getQstId()));
			
			Std_Info stdInfo = new Std_Info();
			stdInfo.setStdId(stdId);
			eval.setStdInfo(stdInfo);
			
			eval.setAnsScore(ans.getAnsScore());
			eval.setAnsDt(currentTime);
			
			this.scer.save(eval);
		}
		
		return evalId;
	}
	
	private String getCompetencyKey(String competencyName) {
		// 실제 역량명에 맞게 수정 필요
		if (competencyName.contains("글로컬") || competencyName.contains("리더")) {
			return "global";
		}
		else if (competencyName.contains("소통") || competencyName.contains("협력")) {
			return "communication";
		}
		else if (competencyName.contains("창의") || competencyName.contains("혁신")) {
			return "innovation";
		}
		else if (competencyName.contains("탐구") || competencyName.contains("융합")) {
			return "inquiry";
		}
		
		return "unknown";
	}
	
	// 진단 결과 조회
	public Map<String, Object> getCompetenceResult(String evalId) {
		List<Core_CptEval> evaluations = this.scer.findByEvalId(evalId);	// evalId로 해당 진단의 모든 답변 조회
		
		if (evaluations.isEmpty()) {
			return null;
		}
		
		// 역량별로 데이터 분류 및 점수 계산
		Map<String, List<Integer>> competencyScores = new HashMap<>();
		Map<String, String> competencyNames = new HashMap<>();
		
		for (Core_CptEval eval : evaluations) {
			Core_CptQst question = eval.getCoreCptQst();
			Core_CptInfo subCompetency = question.getCoreCptInfo();	// 세부 역량
			Core_CptInfo mainCompetency = this.scr.findByCclId(subCompetency.getUpCclId());	// 상위 역량
			
			String mainKey = getCompetencyKey(mainCompetency.getCclNm());	// 역량명을 key로 변경
			
			if (!competencyScores.containsKey(mainKey)) {
				competencyScores.put(mainKey, new ArrayList<>());
				competencyNames.put(mainKey, mainCompetency.getCclNm());
			}
			
			// 점수는 1-5를 20점씩 곱해서 100점 만점으로 변환
			int score = eval.getAnsScore() * 20;
			competencyScores.get(mainKey).add(score);
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
			
			competencyData.put("name", competencyNames.get(key));
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
}