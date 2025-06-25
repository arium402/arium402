package com.team.arium.admin.noncurr.repository;

import com.team.arium.domain.Ncs_PrgInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface NcsPrgInfoRepository extends JpaRepository<Ncs_PrgInfo, Integer> {
    
    // 프로그램 ID로 조회
    @Query("SELECT p FROM Ncs_PrgInfo p WHERE p.prgId = :prgId")
    Optional<Ncs_PrgInfo> findByPrgId(Integer prgId);
    
    // 프로그램 코드로 중복 체크
    boolean existsByPrgCd(String prgCd);
    
    // 검색 조건에 따른 프로그램 조회 (목록 페이지용)
    @Query("SELECT p FROM Ncs_PrgInfo p WHERE " +
           "(:searchKeyword IS NULL OR :searchKeyword = '' OR p.prgNm LIKE %:searchKeyword%) AND " +
           "(:department IS NULL OR :department = '' OR p.prgNm LIKE %:department%) AND " +
           "(:statusCode IS NULL OR p.prgStatCd.codeId = :statusCode) " +
           "ORDER BY p.regDt DESC")
    Page<Ncs_PrgInfo> findBySearchConditions(@Param("searchKeyword") String searchKeyword, 
                                            @Param("department") String department, 
                                            @Param("statusCode") Integer statusCode, 
                                            Pageable pageable);
}