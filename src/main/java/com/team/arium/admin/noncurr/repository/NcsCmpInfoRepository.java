package com.team.arium.admin.noncurr.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.team.arium.domain.Ncs_CmpInfo;

@Repository
public interface NcsCmpInfoRepository extends JpaRepository<Ncs_CmpInfo, Integer> {
    
    /**
     * 신청 ID로 이수 정보 조회
     */
    Optional<Ncs_CmpInfo> findByNcsPrgAply_AplyId(Integer aplyId);
    
    /**
     * 프로그램 ID로 이수 정보 조회
     */
    @Query("SELECT c FROM Ncs_CmpInfo c WHERE c.ncsPrgInfo.prgId = :prgId")
    List<Ncs_CmpInfo> findByPrgId(@Param("prgId") Integer prgId);
    
    /**
     * 학생 ID로 이수 정보 조회
     */
    @Query("SELECT c FROM Ncs_CmpInfo c WHERE c.stdInfo.stdId = :stdId")
    List<Ncs_CmpInfo> findByStdId(@Param("stdId") Integer stdId);
    
    /**
     * 프로그램별 이수 현황 통계
     */
    @Query(value = """
        SELECT 
            COUNT(*) as total_count,
            SUM(CASE WHEN cmp_yn = 'Y' THEN 1 ELSE 0 END) as completed_count,
            SUM(CASE WHEN survey_yn = 'Y' THEN 1 ELSE 0 END) as survey_count
        FROM ncs_cmp_info 
        WHERE prg_id = :prgId
        """, nativeQuery = true)
    Object[] getCompletionStatsByPrgId(@Param("prgId") Integer prgId);
}
