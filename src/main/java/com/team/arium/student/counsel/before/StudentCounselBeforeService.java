package com.team.arium.student.counsel.before;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.team.arium.domain.Cnsl_PreInfo;
import com.team.arium.domain.Cnsl_PreQst;
import com.team.arium.domain.Cnsl_PreQstOpt;

@Service
public class StudentCounselBeforeService {
	
	@Autowired
	private StudentCounselBeforeRepository pr;
	
	// 일반상담 사전검사 문항들 조회
	public List<StudentCounselBeforeDTO> getQuestionsByType(Integer preSurveyId) {
		List<Cnsl_PreQst> questions = this.pr.findQuestionsByPreSurveyId(preSurveyId);
		List<StudentCounselBeforeDTO> result = new ArrayList<>();
		
		for (Cnsl_PreQst question : questions) {
			if (question.getOptions() != null && !question.getOptions().isEmpty()) {	// 옵션 있는 문항
				for (Cnsl_PreQstOpt option : question.getOptions()) {
					StudentCounselBeforeDTO dto = new StudentCounselBeforeDTO();
					dto.setPreQstId(question.getPreQstId());
					dto.setPreQstContent(question.getPreQstContent());
					dto.setPreQstOrd(question.getPreQstOrd());
					dto.setPreQstType(question.getPreQstType().getCodeId());
					dto.setPreOptId(option.getPreOptId());
					dto.setOptContent(option.getOptContent());
					dto.setOptOrd(option.getOptOrd());
					result.add(dto);
				}
			}
			else {	// 옵션 없는 문항
				StudentCounselBeforeDTO dto = new StudentCounselBeforeDTO();
				dto.setPreQstId(question.getPreQstId());
				dto.setPreQstContent(question.getPreQstContent());
				dto.setPreQstOrd(question.getPreQstOrd());
				dto.setPreQstType(question.getPreQstType().getCodeId());
				result.add(dto);
			}
		}
		
		return result;
	}
	
	// 답변 저장
	@Transactional
	public String saveAnswers(Integer stdId, Integer preSurveyId, Map<String, String[]> answers) {
		// pre_eval_id 생성 (중복 체크)
		long ctime = System.currentTimeMillis();
		String preEvalId = "E" + String.valueOf(ctime).substring(5);
		
		// 마스터 정보 저장 (누가, 언제, 어떤 검사를 했는지)
		this.pr.insertEvalMaster(preEvalId, stdId, preSurveyId);
		
		// 답변 저장 (각 문항별로)
		for (Map.Entry<String, String[]> entry : answers.entrySet()) {
			String key = entry.getKey();
			String[] values = entry.getValue();
			
			if (key.startsWith("question")) {
				Integer questionId = Integer.parseInt(key.replace("question", ""));
				
				for (String value : values) {
					if (!value.trim().isEmpty()) {
						if (isNumeric(value)) {
							// 객관식 답변 (숫자 = 옵션 ID)
							this.pr.insertEvalAnswer(preEvalId, questionId, Integer.parseInt(value), null);
						}
						else {
							// 주관식 답변 (문자 = 텍스트) -> 113은 주관식 더미 pre_opt_id 값
							this.pr.insertEvalAnswer(preEvalId, questionId, 113, value);
						}
					}
				}
			}
		}
		return preEvalId;
	}
	
	// 숫자인지 문자인지 확인 (객관식 vs 주관식)
	private boolean isNumeric(String str) {
		try {
			Integer.parseInt(str);
			return true; 	// 숫자면 객관식
		}
		catch (NumberFormatException e) {
			return false;	// 문자면 주관식
		}
	}
}