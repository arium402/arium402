package com.team.arium.competence;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.team.arium.domain.Core_CptEval;

@Repository
public interface StudentCompetenceEvalRepository extends JpaRepository<Core_CptEval, Integer> {

	List<Core_CptEval> findByEvalId(String evalId);	// 모든 답변 조회
}