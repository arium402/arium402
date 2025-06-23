package com.team.arium.competence;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.team.arium.domain.Core_CptInfo;

@Service
public class StudentCompetenceService {

	@Autowired
	private StudentCompetenceRepository scr;
	
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
}