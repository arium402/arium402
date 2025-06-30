package com.team.arium.cmncode;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.team.arium.domain.Common_Code;

public interface commonCode_repo extends JpaRepository<Common_Code, Long> {
	
	 Optional<Common_Code> findByCodeTypeAndCodeId(String codeType, Integer codeId);

}
