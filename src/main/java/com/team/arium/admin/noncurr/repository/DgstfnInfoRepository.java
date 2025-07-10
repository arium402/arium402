package com.team.arium.admin.noncurr.repository;

import com.team.arium.domain.Dgstfn_Info;
import com.team.arium.domain.Dgstfn_Qst;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

// 만족도 조사 정보 Repository
@Repository
public interface DgstfnInfoRepository extends JpaRepository<Dgstfn_Info, Integer> {
    
    @Query("SELECT d FROM Dgstfn_Info d ORDER BY d.regDt DESC")
    List<Dgstfn_Info> findAllOrderByRegDtDesc();
}