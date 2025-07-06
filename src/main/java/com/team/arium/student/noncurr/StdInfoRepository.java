package com.team.arium.student.noncurr;

import com.team.arium.domain.Std_Info;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StdInfoRepository extends JpaRepository<Std_Info, Integer> {
    // 기본 CRUD만 사용 (findById는 JpaRepository에서 제공)
}