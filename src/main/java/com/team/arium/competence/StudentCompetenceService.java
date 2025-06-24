package com.team.arium.competence;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
	
	// 실시 ID 생성
	private String newEvalId() {
		long ctime = System.currentTimeMillis();
		
		String evalId = "E" + String.valueOf(ctime).substring(5);
		
		return evalId;
	}
	/*
	// 답변 저장
	@Transactional
	public String saveAnswers(Integer stdId, List<StudentCompetenceDTO> answers) {
		String evalId = newEvalId();	// 자동 생성
		
		while(this.scr.existsEvalByEvalId(evalId)) {	// 중복 체크
			evalId = newEvalId();
		}
		
		for (StudentCompetenceDTO ans : answers) {
			
		}
		
		return evalId;
	}
	*/
}