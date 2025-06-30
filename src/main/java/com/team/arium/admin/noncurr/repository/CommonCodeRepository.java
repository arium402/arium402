// 3. 공통코드 Repository
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
    
    // 코드값으로 조회
    Optional<Common_Code> findByCode(String code);
    
    // 코드 설명으로 조회
    List<Common_Code> findByCodeDesc(String codeDesc);
    
    // 코드 타입별 조회
    List<Common_Code> findByCodeType(String codeType);
    
    // 비교과 프로그램 상태 코드 조회 (코드 타입으로 조회)
    @Query("SELECT c FROM Common_Code c WHERE c.codeType = 'PRG_STAT'")
    List<Common_Code> findProgramStatusCodes();
    
    // 기본 상태 코드 조회 (등록시 사용) - 실제 코드값에 맞게 수정
    @Query("SELECT c FROM Common_Code c WHERE c.codeType = 'PRG_STAT' AND c.code = 'ACTIVE'")
    Optional<Common_Code> findDefaultProgramStatus();
}