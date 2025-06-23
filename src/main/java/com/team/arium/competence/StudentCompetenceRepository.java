package com.team.arium.competence;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.team.arium.domain.Core_CptInfo;

@Repository
public interface StudentCompetenceRepository extends JpaRepository<Core_CptInfo, Long>{

	List<Core_CptInfo> findByUpCclIdIsNotNullOrderByUpCclIdAscCclCdAsc();	// 하위 역량만 조회
	
	Core_CptInfo findByCclId(Long cclId);	// 상위 역량 조회
	
	List<Core_CptInfo> findByUpCclIdIsNullOrderByCclCdAsc();	// 상위 역량만 조회
}
