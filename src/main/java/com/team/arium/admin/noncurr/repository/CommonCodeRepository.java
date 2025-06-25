package com.team.arium.admin.noncurr.repository;

import com.team.arium.domain.Common_Code;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommonCodeRepository extends JpaRepository<Common_Code, Integer> {
    
    // 코드 타입별 조회
    List<Common_Code> findByCodeType(String codeType);
    
    // 코드 타입과 코드로 조회
    Optional<Common_Code> findByCodeTypeAndCode(String codeType, String code);
    
    // 코드 타입별 정렬된 조회
    @Query("SELECT c FROM Common_Code c WHERE c.codeType = :codeType ORDER BY c.code")
    List<Common_Code> findByCodeTypeOrderByCode(@Param("codeType") String codeType);
    
    // 프로그램 상태 코드 조회
    @Query("SELECT c FROM Common_Code c WHERE c.codeType = 'PRG_STAT' ORDER BY c.code")
    List<Common_Code> findProgramStatusCodes();
    
    // 부서 코드 조회
    @Query("SELECT c FROM Common_Code c WHERE c.codeType = 'DEPT' ORDER BY c.code")
    List<Common_Code> findDepartmentCodes();
    
    // 마일리지 관련 코드 조회
    @Query("SELECT c FROM Common_Code c WHERE c.codeType = 'MLG_ADD' ORDER BY c.code")
    List<Common_Code> findMileageAddCodes();
}