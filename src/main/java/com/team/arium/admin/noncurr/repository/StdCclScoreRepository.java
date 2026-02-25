package com.team.arium.admin.noncurr.repository;

import com.team.arium.domain.Std_CclScore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface StdCclScoreRepository extends JpaRepository<Std_CclScore, Integer> {
    
	   /**
     * 학생별 핵심역량 점수 조회
     */
    @Query("SELECT s FROM Std_CclScore s WHERE s.stdInfo.stdId = :stdId")
    List<Std_CclScore> findByStdId(@Param("stdId") Integer stdId);
    
    /**
     *  중복 체크 (학생 + 역량 + 이수ID)
     */
    @Query("SELECT COUNT(s) > 0 FROM Std_CclScore s " +
           "WHERE s.stdInfo.stdId = :stdId " +
           "AND s.coreCptInfo.cclId = :cclId " +
           "AND s.ncsCmpInfo.cmpId = :cmpId")
    boolean existsByStdIdAndCclIdAndCmpId(
        @Param("stdId") Integer stdId,
        @Param("cclId") Integer cclId,
        @Param("cmpId") Integer cmpId
    );
    
    /**
     *  이수 ID로 역량 점수 삭제
     */
    @Modifying
    @Transactional
    @Query("DELETE FROM Std_CclScore s WHERE s.ncsCmpInfo.cmpId = :cmpId")
    void deleteByCmpId(@Param("cmpId") Integer cmpId);

}