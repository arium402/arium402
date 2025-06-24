package com.team.arium.admin.noncurr;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.team.arium.domain.Ncs_PrgInfo;

@Repository
public interface NcsPrgInfoRepository extends JpaRepository<Ncs_PrgInfo, Integer> {
  /*
     
    // 프로그램 코드로 중복 체크
    boolean existsByPrgCd(String prgCd);
    
    // 상태별 프로그램 조회
    @Query("SELECT p FROM Ncs_PrgInfo p WHERE p.prgStatCd.codeId = :statusCode")
    List<Ncs_PrgInfo> findByStatus(Integer statusCode);
    
    // 검색 조건에 따른 프로그램 조회
    @Query("SELECT p FROM Ncs_PrgInfo p WHERE " +
           "(:searchKeyword IS NULL OR p.prgNm LIKE %:searchKeyword%) AND " +
           "(:department IS NULL OR p.department = :department) AND " +
           "(:statusCode IS NULL OR p.prgStatCd.codeId = :statusCode)")
    Page<Ncs_PrgInfo> findBySearchConditions(String searchKeyword, String department, 
                                            Integer statusCode, Pageable pageable);
    
    // 신청자 수 포함 조회
    @Query("SELECT p, COUNT(a) as applyCnt FROM Ncs_PrgInfo p " +
           "LEFT JOIN p.applications a " +
           "WHERE a.aplyStatCd.code = 'APPROVED' OR a.aplyStatCd.code IS NULL " +
           "GROUP BY p.prgId")
    List<Object[]> findProgramsWithApplyCount();
   */
}
