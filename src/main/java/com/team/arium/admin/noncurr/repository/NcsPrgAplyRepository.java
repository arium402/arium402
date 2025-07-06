package com.team.arium.admin.noncurr.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.team.arium.domain.Ncs_PrgAply;

import jakarta.persistence.QueryHint;

@Repository
public interface NcsPrgAplyRepository extends JpaRepository<Ncs_PrgAply, Integer> {
    

    // ✅ 네이티브 쿼리 사용 (DB 컬럼명 직접 사용)
    @Query(value = "SELECT COUNT(*) FROM ncs_prg_aply WHERE prg_id = ?1", nativeQuery = true)
    int countByPrgId(Integer prgId);

    /**
     * ✅ 캐시를 무시하고 프로그램과 학생으로 신청 내역 조회 (취소 후 재신청용)
     */
    @Query("SELECT a FROM Ncs_PrgAply a WHERE a.ncsPrgInfo.prgId = :prgId AND a.stdInfo.stdId = :stdId")
    @QueryHints({
        @QueryHint(name = "javax.persistence.cache.retrieveMode", value = "BYPASS"),
        @QueryHint(name = "javax.persistence.cache.storeMode", value = "REFRESH")
    })
    List<Ncs_PrgAply> findByPrgIdAndStdIdWithRefresh(@Param("prgId") Integer prgId, @Param("stdId") Integer stdId);
    
    
    // ✅ 네이티브 쿼리 사용 (상태별)
    @Query(value = "SELECT COUNT(*) FROM ncs_prg_aply WHERE prg_id = ?1 AND aply_stat_cd = ?2", nativeQuery = true)
    int countByPrgIdAndAplyStatCd(Integer prgId, Integer aplyStatCd);

    // ✅ 연관관계를 통한 접근 (JPQL)
    @Query("SELECT a FROM Ncs_PrgAply a WHERE a.ncsPrgInfo.prgId = :prgId")
    List<Ncs_PrgAply> findByPrgId(@Param("prgId") Integer prgId);
    
    /**
     * 프로그램별 신청자 상세 정보 조회 (페이징)
     * 학생정보, 학과정보, 신청상태, 이수정보를 모두 JOIN해서 가져옴
     */
    @Query(value = """
        SELECT 
            a.aply_id,
            a.prg_id,
            a.std_id,
            a.aply_dt,
            a.reg_dt,
            a.upd_dt,
            s.std_no,
            s.std_nm,
            s.sch_yr,
            s.std_gender,
            s.std_tellno,
            s.std_eml_addr,
            d.dept_nm,
            d.college,
            stat.code_desc as aply_stat_desc,
            stat.code as aply_stat_code,
            c.cmp_id,
            COALESCE(c.cmp_yn, 'N') as cmp_yn,
            COALESCE(c.survey_yn, 'N') as survey_yn
        FROM ncs_prg_aply a
        INNER JOIN std_info s ON a.std_id = s.std_id
        INNER JOIN dept_info d ON s.dept_id = d.dept_id
        INNER JOIN common_code stat ON a.aply_stat_cd = stat.code_id
        LEFT JOIN ncs_cmp_info c ON a.aply_id = c.aply_id
        WHERE a.prg_id = :prgId
        ORDER BY a.aply_dt DESC, a.aply_id DESC
        """, 
        countQuery = """
        SELECT COUNT(*)
        FROM ncs_prg_aply a
        WHERE a.prg_id = :prgId
        """,
        nativeQuery = true)
    Page<Object[]> findApplicantDetailsByPrgIdWithPaging(@Param("prgId") Integer prgId, Pageable pageable);
    
    /**
     * 프로그램별 신청자 상세 정보 조회 (전체)
     */
    @Query(value = """
        SELECT 
            a.aply_id,
            a.prg_id,
            a.std_id,
            a.aply_dt,
            a.reg_dt,
            a.upd_dt,
            s.std_no,
            s.std_nm,
            s.sch_yr,
            s.std_gender,
            s.std_tellno,
            s.std_eml_addr,
            d.dept_nm,
            d.college,
            stat.code_desc as aply_stat_desc,
            stat.code as aply_stat_code,
            c.cmp_id,
            COALESCE(c.cmp_yn, 'N') as cmp_yn,
            COALESCE(c.survey_yn, 'N') as survey_yn
        FROM ncs_prg_aply a
        INNER JOIN std_info s ON a.std_id = s.std_id
        INNER JOIN dept_info d ON s.dept_id = d.dept_id
        INNER JOIN common_code stat ON a.aply_stat_cd = stat.code_id
        LEFT JOIN ncs_cmp_info c ON a.aply_id = c.aply_id
        WHERE a.prg_id = :prgId
        ORDER BY a.aply_dt DESC, a.aply_id DESC
        """, 
        nativeQuery = true)
    List<Object[]> findApplicantDetailsByPrgId(@Param("prgId") Integer prgId);
    
    /**
     * 특정 학생의 특정 프로그램 신청 정보 조회
     */
    @Query("SELECT a FROM Ncs_PrgAply a WHERE a.ncsPrgInfo.prgId = :prgId AND a.stdInfo.stdId = :stdId")
    List<Ncs_PrgAply> findByPrgIdAndStdId(@Param("prgId") Integer prgId, @Param("stdId") Integer stdId);
    
    /**
     * 신청 상태별 카운트
     */
    @Query(value = """
        SELECT 
            stat.code_desc as status_name,
            COUNT(*) as count
        FROM ncs_prg_aply a
        INNER JOIN common_code stat ON a.aply_stat_cd = stat.code_id
        WHERE a.prg_id = :prgId
        GROUP BY stat.code_desc, stat.code
        ORDER BY stat.code
        """, nativeQuery = true)
    List<Object[]> countApplicantsByStatus(@Param("prgId") Integer prgId);

    /**
     * 특정 학생의 모든 신청 내역 조회
     */
    @Query("SELECT a FROM Ncs_PrgAply a WHERE a.stdInfo.stdId = :stdId ORDER BY a.aplyDt DESC")
    List<Ncs_PrgAply> findByStdIdOrderByAplyDt(@Param("stdId") Integer stdId);

    /**
     * 학생의 특정 프로그램 신청 여부 확인
     */
    @Query("SELECT COUNT(a) > 0 FROM Ncs_PrgAply a WHERE a.ncsPrgInfo.prgId = :prgId AND a.stdInfo.stdId = :stdId")
    boolean existsByPrgIdAndStdId(@Param("prgId") Integer prgId, @Param("stdId") Integer stdId);
    
}