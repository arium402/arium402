package com.team.arium.student.counsel.before;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.team.arium.domain.Cnsl_PreInfo;
import com.team.arium.domain.Cnsl_PreQst;

@Repository
public interface StudentCounselBeforeRepository extends JpaRepository<Cnsl_PreInfo, Integer> {

	// 사전검사 문항 조회
	@Query("SELECT DISTINCT q FROM Cnsl_PreQst q LEFT JOIN FETCH q.options o WHERE q.cnslPreInfo.preSurveyId = :preSurveyId ORDER BY q.preQstOrd ASC, o.optOrd ASC")
	List<Cnsl_PreQst> findQuestionsByPreSurveyId(@Param("preSurveyId") Integer preSurveyId);
	
	// 사전검사 실시 마스터 정보 저장
	@Query(value = "INSERT INTO cnsl_pre_eval_master (pre_eval_id, std_id, pre_survey_id, reg_dt) " +
		"VALUES (:preEvalId, :stdId, :preSurveyId, NOW())", nativeQuery = true)
	@Modifying(clearAutomatically = true)
	@Transactional
	int insertEvalMaster(@Param("preEvalId") String preEvalId, 
		@Param("stdId") Integer stdId,
		@Param("preSurveyId") Integer preSurveyId);
	
	// 문항별 답변 저장
	@Query(value = "INSERT INTO cnsl_pre_eval (pre_eval_id, pre_qst_id, pre_opt_id, ans_text, reg_dt) " +
		"VALUES (:preEvalId, :preQstId, :preOptId, :ansText, NOW())", nativeQuery = true)
	@Modifying(clearAutomatically = true)
	@Transactional
	int insertEvalAnswer(@Param("preEvalId") String preEvalId, 
		@Param("preQstId") Integer preQstId, 
		@Param("preOptId") Integer preOptId, 
		@Param("ansText") String ansText);
}