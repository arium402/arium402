// 5. 비교과-핵심역량 매핑 Repository
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
    List<Ncs_CclRel> findByPrgId(Integer prgId);
    
    // 핵심역량별 프로그램 조회
    List<Ncs_CclRel> findByCclId(Integer cclId);
    
    // 프로그램-핵심역량 조합으로 조회
    @Query("SELECT r FROM Ncs_CclRel r WHERE r.prgId = :prgId AND r.cclId = :cclId")
    List<Ncs_CclRel> findByPrgIdAndCclId(@Param("prgId") Integer prgId, @Param("cclId") Integer cclId);
    
    // 프로그램별 매핑 삭제 (수정시 사용)
    @Modifying
    @Query("DELETE FROM Ncs_CclRel r WHERE r.prgId = :prgId")
    void deleteByPrgId(@Param("prgId") Integer prgId);
    
    // 핵심역량별 매핑 삭제
    @Modifying
    @Query("DELETE FROM Ncs_CclRel r WHERE r.cclId = :cclId")
    void deleteByCclId(@Param("cclId") Integer cclId);
    
    // 프로그램과 핵심역량 정보 함께 조회
    @Query("SELECT r FROM Ncs_CclRel r " +
           "JOIN FETCH r.ncsPrgInfo " +
           "JOIN FETCH r.coreCptInfo " +
           "WHERE r.prgId = :prgId")
    List<Ncs_CclRel> findByPrgIdWithInfo(@Param("prgId") Integer prgId);
}