package com.team.arium.admin.noncurr.repository;

import com.team.arium.domain.Std_CclScore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StdCclScoreRepository extends JpaRepository<Std_CclScore, Integer> {
    
    /**
     * 학생별 핵심역량 점수 조회
     */
    @Query("SELECT s FROM Std_CclScore s WHERE s.stdInfo.stdId = :stdId")
    List<Std_CclScore> findByStdId(@Param("stdId") Integer stdId);
}