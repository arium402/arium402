// 2. CoreCptInfoRepository  
package com.team.arium.admin.noncurr.repository;

import com.team.arium.domain.Core_CptInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CoreCptInfoRepository extends JpaRepository<Core_CptInfo, Integer> {
    
    // 활성화된 핵심역량 목록 조회 (등록 페이지용)
    @Query("SELECT c FROM Core_CptInfo c WHERE c.upCclId IS NULL ORDER BY c.cclCd")
    List<Core_CptInfo> findActiveCompetencies();
    
    // 역량 ID로 조회
    Core_CptInfo findByCclId(Integer cclId);
}
