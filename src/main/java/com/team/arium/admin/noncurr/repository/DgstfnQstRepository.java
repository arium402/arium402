package com.team.arium.admin.noncurr.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.team.arium.domain.Dgstfn_Qst;

//만족도 조사 문항 Repository  
@Repository
public interface DgstfnQstRepository extends JpaRepository<Dgstfn_Qst, Integer> {
 
 /**
  * 특정 조사의 문항들을 순서대로 조회
  */
 @Query("SELECT q FROM Dgstfn_Qst q WHERE q.dgstfnInfo.surveyId = :surveyId ORDER BY q.surOrd")
 List<Dgstfn_Qst> findBySurveyIdOrderBySurOrd(@Param("surveyId") Integer surveyId);
 
 /**
  * 모든 문항을 순서대로 조회 (기본 만족도 조사용)
  */
 @Query("SELECT q FROM Dgstfn_Qst q ORDER BY q.surOrd")
 List<Dgstfn_Qst> findAllOrderBySurOrd();
}