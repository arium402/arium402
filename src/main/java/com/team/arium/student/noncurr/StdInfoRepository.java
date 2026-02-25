// 기존 StdInfoRepository에 마일리지용 메서드 추가

package com.team.arium.student.noncurr;

import com.team.arium.domain.Std_Info;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StdInfoRepository extends JpaRepository<Std_Info, Integer> {
    
    //  기존 메서드들 (그대로 유지)
    
    /**
     * 학번으로 학생 조회
     */
    Optional<Std_Info> findByStdNo(String stdNo);

    Optional<Std_Info> findById(Integer stdId);
    
    /**
     * 학번 존재 여부 확인
     */
    boolean existsByStdNo(String stdNo);

    /**
     * 학과별 학생 조회
     */
    @Query("SELECT s FROM Std_Info s WHERE s.deptInfo.deptId = :deptId")
    List<Std_Info> findByDeptId(@Param("deptId") Integer deptId);

    /**
     * 학생명으로 검색
     */
    @Query("SELECT s FROM Std_Info s WHERE s.stdNm LIKE %:name%")
    List<Std_Info> findByStdNmContaining(@Param("name") String name);

    /**
     * 학번으로 검색
     */
    @Query("SELECT s FROM Std_Info s WHERE s.stdNo LIKE %:stdNo%")
    List<Std_Info> findByStdNoContaining(@Param("stdNo") String stdNo);

    /**
     * 이메일로 학생 조회
     */
    Optional<Std_Info> findByStdEmlAddr(String stdEmlAddr);

    /**
     * 연락처로 학생 조회
     */
    Optional<Std_Info> findByStdTellno(String stdTellno);

    /**
     * 계좌정보 있는 학생만 조회 (마일리지 지급용)
     */
    @Query("""
        SELECT s FROM Std_Info s 
        WHERE s.bankAcnt IS NOT NULL 
        AND s.bankNm IS NOT NULL 
        AND s.depositor IS NOT NULL
        AND TRIM(s.bankAcnt) != ''
        AND TRIM(s.bankNm) != ''
        AND TRIM(s.depositor) != ''
        """)
    List<Std_Info> findStudentsWithBankInfo();

    //  마일리지용 추가 메서드들
    
    /**
     * 특정 학생들의 계좌 정보 조회 (마일리지 지급용)
     */
    @Query("""
        SELECT s.stdId, s.stdNm, s.stdNo, s.bankNm, s.bankAcnt, s.depositor 
        FROM Std_Info s 
        WHERE s.stdId IN :stdIds
        AND s.bankAcnt IS NOT NULL 
        AND s.bankNm IS NOT NULL
        """)
    List<Object[]> findBankInfoByStdIds(@Param("stdIds") List<Integer> stdIds);

    /**
     * 학생 기본 정보 조회 (마일리지 지급 대상자용)
     */
    @Query("""
        SELECT s.stdId, s.stdNo, s.stdNm, s.schYr, 
               d.deptNm, d.college, s.stdTellno, s.stdEmlAddr
        FROM Std_Info s 
        LEFT JOIN s.deptInfo d
        WHERE s.stdId = :stdId
        """)
    Object[] findBasicInfoByStdId(@Param("stdId") Integer stdId);

    /**
     * 여러 학생의 기본 정보 일괄 조회 (마일리지용)
     */
    @Query("""
        SELECT s.stdId, s.stdNo, s.stdNm, s.schYr, 
               d.deptNm, d.college, s.stdTellno, s.stdEmlAddr,
               s.bankNm, s.bankAcnt, s.depositor
        FROM Std_Info s 
        LEFT JOIN s.deptInfo d
        WHERE s.stdId IN :stdIds
        ORDER BY s.stdNm
        """)
    List<Object[]> findBasicInfoByStdIds(@Param("stdIds") List<Integer> stdIds);
}