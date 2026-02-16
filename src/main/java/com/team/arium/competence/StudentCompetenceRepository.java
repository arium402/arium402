package com.team.arium.competence;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.team.arium.domain.Core_CptEval;
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
	
	// 답변 저장 쿼리 추가
	@Query(value = "INSERT INTO core_cpt_eval (eval_id, qst_id, std_id, ans_score, ans_dt, reg_dt) " +
			"VALUES (:evalId, :qstId, :stdId, :ansScore, :ansDt, NOW())", nativeQuery = true)
	@Modifying(clearAutomatically = true)
	@Transactional
	int insertEvaluation(@Param("evalId") String evalId,
			@Param("qstId") Integer qstId,
			@Param("stdId") Integer stdId,
			@Param("ansScore") Integer ansScore,
			@Param("ansDt") String ansDt);
	
	// std_ccl_score 저장 쿼리 추가
	@Query(value = "INSERT INTO std_ccl_score (std_id, ccl_id, score_type, eval_id, score, reg_dt) " +
			"VALUES (:stdId, :cclId, :scoreType, :evalId, :score, NOW())", nativeQuery = true)
	@Modifying(clearAutomatically = true)
	@Transactional
	int insertCompetencyScore(@Param("stdId") Integer stdId,
			@Param("cclId") Integer cclId,
			@Param("scoreType") Integer scoreType,
			@Param("evalId") String evalId,
			@Param("score") Integer score);
	
	// 답변 조회 쿼리 추가
	@Query(value = "SELECT * FROM core_cpt_eval WHERE eval_id = :evalId", nativeQuery = true)
	List<Core_CptEval> findEvaluationsByEvalId(@Param("evalId") String evalId);
}