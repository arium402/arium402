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
    
    /**
     * 프로그램 코드로 조회
     */
    Optional<Ncs_PrgInfo> findByPrgCd(String prgCd);
    
    /**
     * 연도별 프로그램 조회 (String 타입 날짜 필드용)
     */
    @Query("SELECT p FROM Ncs_PrgInfo p WHERE SUBSTRING(p.prgStDt, 1, 4) = :year")
    Page<Ncs_PrgInfo> findByYear(@Param("year") String year, Pageable pageable);
    
    /**
     * 프로그램 코드 생성을 위한 최신 코드 조회
     */
    @Query("SELECT p.prgCd FROM Ncs_PrgInfo p WHERE p.prgCd LIKE :prefix% ORDER BY p.prgCd DESC")
    List<String> findLatestPrgCdWithPrefix(@Param("prefix") String prefix);
    
    /**
     * 상태별 프로그램 수 조회
     */
    @Query("SELECT COUNT(p) FROM Ncs_PrgInfo p WHERE p.prgStatCd.codeId = :statusCodeId")
    Long countByPrgStatCd(@Param("statusCodeId") Integer statusCodeId);
    
    /**
     * 전체 검색 조건으로 프로그램 조회 (신청자 수 제외)
     */
    @Query("""
        SELECT p FROM Ncs_PrgInfo p 
        WHERE (:keyword IS NULL OR 
               LOWER(p.prgNm) LIKE LOWER(CONCAT('%', :keyword, '%')) OR 
               LOWER(p.prgDesc) LIKE LOWER(CONCAT('%', :keyword, '%')) OR 
               LOWER(p.prgCd) LIKE LOWER(CONCAT('%', :keyword, '%'))) 
        AND (:statusCodeId IS NULL OR p.prgStatCd.codeId = :statusCodeId) 
        AND (:year IS NULL OR SUBSTRING(p.prgStDt, 1, 4) = :year)
        ORDER BY p.regDt DESC
    """)
    Page<Ncs_PrgInfo> findByAllSearchConditions(
        @Param("keyword") String keyword, 
        @Param("statusCodeId") Integer statusCodeId, 
        @Param("year") String year, 
        Pageable pageable
    );
    
    /**
     * 프로그램명으로 검색 (자동완성용)
     */
    @Query("SELECT p FROM Ncs_PrgInfo p WHERE LOWER(p.prgNm) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Ncs_PrgInfo> findByPrgNmContainingIgnoreCase(@Param("keyword") String keyword);
    
    /**
     * 활성 프로그램 조회 (특정 상태 제외)
     */
    @Query("SELECT p FROM Ncs_PrgInfo p WHERE p.prgStatCd.codeId NOT IN :excludeStatusIds")
    Page<Ncs_PrgInfo> findActivePrograms(@Param("excludeStatusIds") List<Integer> excludeStatusIds, Pageable pageable);
    
    /**
     * 기간별 프로그램 조회
     */
    @Query("""
        SELECT p FROM Ncs_PrgInfo p 
        WHERE (:startDate IS NULL OR p.prgStDt >= :startDate) 
        AND (:endDate IS NULL OR p.prgEndDt <= :endDate)
    """)
    Page<Ncs_PrgInfo> findByDateRange(
        @Param("startDate") String startDate, 
        @Param("endDate") String endDate, 
        Pageable pageable
    );
}