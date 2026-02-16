// 2. 핵심역량 Repository
package com.team.arium.admin.noncurr.repository;

import com.team.arium.domain.Core_CptInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CoreCptInfoRepository extends JpaRepository<Core_CptInfo, Integer> {
    
    // 핵심역량 코드로 조회
    Optional<Core_CptInfo> findByCclCd(String cclCd);
    
    // 핵심역량 코드 중복 체크
    boolean existsByCclCd(String cclCd);
    
    // 상위 역량별 조회
    List<Core_CptInfo> findByUpCclId(Integer upCclId);
    
    // 최상위 역량 조회 (상위 역량이 없는 것들)
    List<Core_CptInfo> findByUpCclIdIsNull();
    
    // 핵심역량명으로 검색
    @Query("SELECT c FROM Core_CptInfo c WHERE c.cclNm LIKE %:name%")
    List<Core_CptInfo> findByCclNmContaining(String name);
    
    // ID 목록으로 조회 (체크박스에서 선택된 것들)
    List<Core_CptInfo> findByCclIdIn(List<Integer> cclIds);
}
