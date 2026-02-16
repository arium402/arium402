package com.team.arium.admin.noncurr.repository;

import com.team.arium.domain.Dgstfn_Eval;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DgstfnEvalRepository extends JpaRepository<Dgstfn_Eval, Integer> {
    
    /**
     * 특정 프로그램의 만족도 조사 응답 통계 조회
     * 각 문항별, 점수별 응답 개수와 비율을 계산
     */
    @Query(value = """
        SELECT 
            dq.sur_id,
            dq.sur_content,
            dq.sur_ord,
            de.ans_score,
            COUNT(*) as score_count,
            ROUND(COUNT(*) * 100.0 / (
                SELECT COUNT(*) 
                FROM dgstfn_eval de2 
                WHERE de2.prg_id = de.prg_id 
                AND de2.sur_id = de.sur_id
            ), 1) as percentage
        FROM dgstfn_eval de
        JOIN dgstfn_qst dq ON de.sur_id = dq.sur_id
        WHERE de.prg_id = :prgId
        GROUP BY dq.sur_id, dq.sur_content, dq.sur_ord, de.ans_score
        ORDER BY dq.sur_ord, de.ans_score
        """, nativeQuery = true)
    List<Object[]> findSurveyStatisticsByPrgId(@Param("prgId") Integer prgId);
    
    /**
     * 특정 프로그램의 각 문항별 응답자 수와 평균 점수
     */
    @Query(value = """
        SELECT 
            dq.sur_id,
            dq.sur_content,
            dq.sur_ord,
            COUNT(*) as total_responses,
            ROUND(AVG(de.ans_score), 1) as average_score
        FROM dgstfn_eval de
        JOIN dgstfn_qst dq ON de.sur_id = dq.sur_id  
        WHERE de.prg_id = :prgId
        GROUP BY dq.sur_id, dq.sur_content, dq.sur_ord
        ORDER BY dq.sur_ord
        """, nativeQuery = true)
    List<Object[]> findQuestionStatisticsByPrgId(@Param("prgId") Integer prgId);
    
    /**
     * 특정 프로그램의 섹션별 평균 점수 계산
     * (문항 순서로 섹션을 구분)
     */
    @Query(value = """
        SELECT 
            CASE 
                WHEN dq.sur_ord BETWEEN 1 AND 5 THEN '1. 프로그램 종합 만족도'
                WHEN dq.sur_ord BETWEEN 6 AND 8 THEN '2. 프로그램 내용' 
                WHEN dq.sur_ord BETWEEN 9 AND 11 THEN '3. 프로그램 강사'
                ELSE '기타'
            END as section_name,
            ROUND(AVG(de.ans_score), 1) as section_average,
            COUNT(DISTINCT de.std_id) as section_responders
        FROM dgstfn_eval de
        JOIN dgstfn_qst dq ON de.sur_id = dq.sur_id
        WHERE de.prg_id = :prgId
        GROUP BY section_name
        ORDER BY MIN(dq.sur_ord)
        """, nativeQuery = true)
    List<Object[]> findSectionStatisticsByPrgId(@Param("prgId") Integer prgId);
    
    /**
     * 특정 프로그램과 학생의 만족도 조사 응답 존재 여부 확인 (디버깅 버전)
     */
    @Query("SELECT COUNT(e) FROM Dgstfn_Eval e WHERE e.ncsPrgInfo.prgId = :prgId AND e.stdInfo.stdId = :stdId")
    Long countByPrgIdAndStdId(@Param("prgId") Integer prgId, @Param("stdId") Integer stdId);

    
    /**
     * 특정 프로그램과 학생의 만족도 조사 응답 존재 여부 확인
     */
    @Query("SELECT COUNT(e) > 0 FROM Dgstfn_Eval e WHERE e.ncsPrgInfo.prgId = :prgId AND e.stdInfo.stdId = :stdId")
    boolean existsByPrgIdAndStdId(@Param("prgId") Integer prgId, @Param("stdId") Integer stdId);
    
    
    /**
     * 특정 프로그램의 전체 응답자 수
     */
    @Query("SELECT COUNT(DISTINCT e.stdInfo.stdId) FROM Dgstfn_Eval e WHERE e.ncsPrgInfo.prgId = :prgId")
    Integer countTotalRespondersByPrgId(@Param("prgId") Integer prgId);
    
    /**
     * 특정 프로그램의 전체 평균 만족도
     */
    @Query("SELECT ROUND(AVG(e.ansScore), 1) FROM Dgstfn_Eval e WHERE e.ncsPrgInfo.prgId = :prgId")
    Double findOverallAverageByPrgId(@Param("prgId") Integer prgId);
    
    /**
     * 특정 프로그램에 대한 만족도 조사 응답 존재 여부 확인
     */
    @Query("SELECT COUNT(e) > 0 FROM Dgstfn_Eval e WHERE e.ncsPrgInfo.prgId = :prgId")
    boolean existsSurveyDataByPrgId(@Param("prgId") Integer prgId);
    
    /**
     * ✅ 학생 ID와 프로그램 ID로 기존 만족도 조사 ID 조회 (새로 추가)
     */
    @Query("SELECT DISTINCT e.surEvalId FROM Dgstfn_Eval e " +
           "WHERE e.stdInfo.stdId = :stdId AND e.ncsPrgInfo.prgId = :prgId")
    String findSurEvalIdByStdIdAndPrgId(@Param("stdId") Integer stdId, @Param("prgId") Integer prgId);
}