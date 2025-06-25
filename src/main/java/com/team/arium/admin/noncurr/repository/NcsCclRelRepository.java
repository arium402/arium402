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
    
    // 프로그램ID로 연결된 핵심역량 조회
    List<Ncs_CclRel> findByPrgId(Integer prgId);
    
    // 핵심역량ID로 연결된 프로그램 조회
    List<Ncs_CclRel> findByCclId(Integer cclId);
    
    // 프로그램ID로 연결 삭제
    @Modifying
    @Query("DELETE FROM Ncs_CclRel n WHERE n.prgId = :prgId")
    void deleteByPrgId(@Param("prgId") Integer prgId);
    
    // 특정 프로그램의 핵심역량 정보와 함께 조회
    @Query("SELECT n, c FROM Ncs_CclRel n " +
           "JOIN n.coreCptInfo c " +
           "WHERE n.prgId = :prgId")
    List<Object[]> findCompetenciesWithDetailsByPrgId(@Param("prgId") Integer prgId);
    
    // 핵심역량별 연결된 프로그램 수
    @Query("SELECT n.cclId, COUNT(n) FROM Ncs_CclRel n GROUP BY n.cclId")
    List<Object[]> countProgramsByCompetency();
}