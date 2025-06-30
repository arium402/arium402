package com.team.arium.counselor.counsel;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.team.arium.DTO.admin_counselor_DTO;
import com.team.arium.DTO.counselor_patient_DTO;
import com.team.arium.domain.Cnsl_Aply;
import com.team.arium.domain.Empl_Info;
import com.team.arium.domain.Std_Info;

@Service
public class counselor_serviceImpl implements counselor_service {
	
	@Autowired
	public counselor_repo cns_repo;

	@Override
	public List<counselor_patient_DTO> allPatientList() {
		List<Cnsl_Aply> entityList = this.cns_repo.findAllByOrderByCnslAplyId();
		List<counselor_patient_DTO> dtoList = new ArrayList<>();
		 
		for (Cnsl_Aply e : entityList) {
			counselor_patient_DTO dto = new counselor_patient_DTO();
			
	        dto.setStdNo(e.getStdInfo().getStdNo());
	        dto.setStdNm(e.getStdInfo().getStdNm());
	        dto.setDeptName(e.getStdInfo().getDeptInfo().getDeptNm());
	        dto.setCns_regDt(e.getRegDt());
	        
	        dtoList.add(dto); // 리스트에 추가
	    }
		return dtoList;
	}
	
	
	
	

}
