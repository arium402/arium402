// 3. NcsCclRelRepository
package com.team.arium.admin.noncurr.repository;

import com.team.arium.domain.Ncs_CclRel;
import com.team.arium.domain.Ncs_CclRelId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface NcsCclRelRepository extends JpaRepository<Ncs_CclRel, Ncs_CclRelId> {
    
    // 프로그램별 핵심역량 조회
    @Query("SELECT r FROM Ncs_CclRel r WHERE r.prgId = :prgId")
    List<Ncs_CclRel> findByPrgId(@Param("prgId") Integer prgId);
    
    // 프로그램별 핵심역량 연결 삭제 (등록시 기존 것 삭제용)
    @Modifying
    @Query("DELETE FROM Ncs_CclRel r WHERE r.prgId = :prgId")
    void deleteByPrgId(@Param("prgId") Integer prgId);
}