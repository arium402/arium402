package com.team.arium.competence;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.team.arium.domain.Core_CptInfo;
import com.team.arium.domain.Core_CptQst;

@Repository
public interface StudentCompetenceRepository extends JpaRepository<Core_CptInfo, Long>{

	List<Core_CptInfo> findByUpCclIdIsNotNullOrderByUpCclIdAscCclCdAsc();	// 하위 역량만 조회
	
	Core_CptInfo findByCclId(Integer cclId);	// 상위 역량 조회
	
	List<Core_CptInfo> findByUpCclIdIsNullOrderByCclCdAsc();	// 상위 역량만 조회
	
	@Query("SELECT q FROM Core_CptQst q ORDER BY q.qstOrd ASC")
	List<Core_CptQst> findAllQuestionsOrderByQstOrdAsc();	// 문항 조회
	
	@Query("SELECT q FROM Core_CptQst q WHERE q.qstId = :qstId")
	Core_CptQst findQuestionById(@Param("qstId") Integer qstId);	// 특정 질문 조회
	
	@Query("SELECT COUNT(e) > 0 FROM Core_CptEval e WHERE e.evalId = :evalId")
	boolean existsEvalByEvalId(@Param("evalId") String evalId);	// 실시 id 조회
}