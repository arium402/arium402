package com.team.arium.counselor.counsel;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.team.arium.DTO.counselor_patient_DTO;
import com.team.arium.domain.Cnsl_Aply;
import com.team.arium.domain.Std_Info;

public interface counselor_repo extends JpaRepository<Cnsl_Aply, Long>{
	
	List<Cnsl_Aply> findAllByOrderByCnslAplyId(); 

}
