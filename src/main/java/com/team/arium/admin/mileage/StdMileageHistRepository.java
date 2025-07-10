package com.team.arium.admin.mileage;


import com.team.arium.domain.Std_MileageHist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StdMileageHistRepository extends JpaRepository<Std_MileageHist, Integer> {

    /**
     * 프로그램별 마일리지 지급 여부 확인
     */
    @Query("""
        SELECT COUNT(mh) > 0 
        FROM Std_MileageHist mh 
        WHERE mh.ncsCmpInfo.ncsPrgInfo.prgId = :prgId
        """)
    Boolean existsMileageByPrgId(@Param("prgId") Integer prgId);

    /**
     * 특정 이수 ID로 마일리지 지급 여부 확인
     */
    @Query("""
        SELECT COUNT(mh) > 0 
        FROM Std_MileageHist mh 
        WHERE mh.ncsCmpInfo.cmpId = :cmpId
        """)
    Boolean existsMileageByCmpId(@Param("cmpId") Integer cmpId);

    /**
     * 프로그램별 마일리지 지급 내역 조회
     */
    @Query("""
        SELECT mh FROM Std_MileageHist mh 
        WHERE mh.ncsCmpInfo.ncsPrgInfo.prgId = :prgId
        ORDER BY mh.regDt DESC
        """)
    List<Std_MileageHist> findMileageHistoryByPrgId(@Param("prgId") Integer prgId);

    /**
     * 여러 프로그램의 마일리지 지급 여부를 한번에 조회
     */
    @Query("""
        SELECT mh.ncsCmpInfo.ncsPrgInfo.prgId, COUNT(mh) > 0
        FROM Std_MileageHist mh 
        WHERE mh.ncsCmpInfo.ncsPrgInfo.prgId IN :prgIds
        GROUP BY mh.ncsCmpInfo.ncsPrgInfo.prgId
        """)
    List<Object[]> checkMileageStatusByPrograms(@Param("prgIds") List<Integer> prgIds);

    /**
     * 학생별 마일리지 내역 조회
     */
    @Query("SELECT mh FROM Std_MileageHist mh WHERE mh.stdInfo.stdId = :stdId ORDER BY mh.mlgDt DESC")
    List<Std_MileageHist> findByStdIdOrderByMlgDt(@Param("stdId") Integer stdId);

    /**
     * 특정 기간 마일리지 지급 내역 조회
     */
    @Query("""
        SELECT mh FROM Std_MileageHist mh 
        WHERE mh.mlgDt BETWEEN :startDate AND :endDate
        ORDER BY mh.mlgDt DESC
        """)
    List<Std_MileageHist> findByMlgDtBetween(@Param("startDate") String startDate, 
                                            @Param("endDate") String endDate);
}