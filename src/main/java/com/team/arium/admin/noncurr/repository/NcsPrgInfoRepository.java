// 1. 비교과 프로그램 Repository
package com.team.arium.admin.noncurr.repository;

import com.team.arium.domain.Ncs_PrgInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NcsPrgInfoRepository extends JpaRepository<Ncs_PrgInfo, Integer> {
    
    // 프로그램 코드로 중복 체크
    boolean existsByPrgCd(String prgCd);
    
    // 프로그램 코드로 조회
    Optional<Ncs_PrgInfo> findByPrgCd(String prgCd);
    
    // 상태별 조회
    @Query("SELECT p FROM Ncs_PrgInfo p WHERE p.prgStatCd.codeId = :statusId")
    List<Ncs_PrgInfo> findByPrgStatCd(@Param("statusId") Integer statusId);
    
    // 부서별 조회
    List<Ncs_PrgInfo> findByPrgDept(String prgDept);
    
    List<Ncs_PrgInfo> findByPrgNmContainingIgnoreCase(String prgNm);
    List<Ncs_PrgInfo> findByPrgDeptContainingIgnoreCase(String prgDept);
    
 // NcsPrgInfoRepository.java에 추가할 메서드

    /**
     * 프로그램명으로 존재 여부 확인
     */
    boolean existsByPrgNm(String prgNm);

    /**
     * 프로그램명으로 조회 (정확히 일치)
     */
    Optional<Ncs_PrgInfo> findByPrgNm(String prgNm);

    /**
     * 프로그램명 중복 체크 (대소문자 무시, 공백 제거)
     */
    @Query("SELECT COUNT(p) > 0 FROM Ncs_PrgInfo p WHERE UPPER(TRIM(p.prgNm)) = UPPER(TRIM(:prgNm))")
    boolean existsByPrgNmIgnoreCaseAndTrim(@Param("prgNm") String prgNm);
    
    // 키워드 검색 (프로그램명, 설명, 부서)
    @Query("SELECT p FROM Ncs_PrgInfo p WHERE " +
           "p.prgNm LIKE %:keyword% OR " +
           "p.prgDesc LIKE %:keyword% OR " +
           "p.prgDept LIKE %:keyword%")
    Page<Ncs_PrgInfo> findByKeywordContaining(@Param("keyword") String keyword, Pageable pageable);
    
    @Query(
    	    value = """
    	      SELECT * 
    	        FROM ncs_prg_info 
    	       WHERE prg_stat_cd = :statusId
    	         AND STR_TO_DATE(recruit_st_dt, '%Y-%m-%d') <= CURRENT_DATE()
    	         AND STR_TO_DATE(recruit_end_dt, '%Y-%m-%d') >= CURRENT_DATE()
    	    """,
    	    nativeQuery = true
    	  )
    	  List<Ncs_PrgInfo> findActiveRecruitmentPrograms(@Param("statusId") Integer statusId);
    
    // 최근 등록된 프로그램 조회
    @Query("SELECT p FROM Ncs_PrgInfo p ORDER BY p.regDt DESC")
    List<Ncs_PrgInfo> findRecentPrograms(Pageable pageable);
    
/*
    @Query(value = "SELECT COUNT(*) FROM ncs_prg_aply WHERE prg_id = ?1", nativeQuery = true)
    int countApplicantsByPrgId(Integer prgId);
 * 
 */

    // 또는 승인된 신청만 카운트하려면

    
    
}