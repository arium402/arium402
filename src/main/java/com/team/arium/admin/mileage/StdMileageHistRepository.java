package com.team.arium.admin.mileage;


import com.team.arium.domain.Std_MileageHist;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    
    /*
     * 학생별 총 적립 마일리지 조회
     * 전체보유 마일리지 계산 시 사용
     * 1학생이 지금까지 받은 마일리지 총합
     */
    @Query("""
            SELECT COALESCE(SUM(mh.mlgScore), 0)
            FROM Std_MileageHist mh
            WHERE mh.stdInfo.stdId = :stdId
            """)
        Integer getTotalMileageByStdId(@Param("stdId") Integer stdId);
        
    /*
     * 학생별 적립예정 마일리지 조회
     * 조건-이수완료/만족도완료/마일리지 지급안됨
     */
    @Query("""
            SELECT COALESCE(SUM(p.mlgDefScore), 0)
            FROM Ncs_CmpInfo c
            INNER JOIN c.ncsPrgInfo p
            LEFT JOIN Std_MileageHist mh ON c.cmpId = mh.ncsCmpInfo.cmpId
            WHERE c.stdInfo.stdId = :stdId
              AND c.cmpYn = 'Y'
              AND c.surveyYn = 'Y'
              AND mh.mlgId IS NULL
            """)
        Integer getPendingMileageByStdId(@Param("stdId") Integer stdId);
    
    /*
     * 학생 마일리지 내역 조회 (필터 적용)
     * - 비교과 프로그램 지급 내역
     * - 장학금 전환 신청 내역
     * - 장학금 전환 완료 내역
     * - 필터: type(유형), status(상태), startDate, endDate
     */
    @Query(value = """
        SELECT * FROM (
            SELECT 
                h.mlg_dt as event_dt,
                h.mlg_score,
                NULL as pay_money,
                '지급' as mlg_type,
                CONCAT(COALESCE(p.prg_nm, '프로그램'), ' 비교과 프로그램 참여') as notes,
                '지급완료' as status_nm,
                'status-completed' as status_class
            FROM std_mileage_hist h
            LEFT JOIN ncs_cmp_info c ON h.cmp_id = c.cmp_id
            LEFT JOIN ncs_prg_info p ON c.prg_id = p.prg_id
            WHERE h.std_id = :stdId
            
            UNION ALL
            
            SELECT 
                u.aply_dt as event_dt,
                -u.aply_mlg_score as mlg_score,
                NULL as pay_money,
                '차감' as mlg_type,
                '마일리지 장학금 전환 신청' as notes,
                CASE 
                    WHEN u.mlg_use_cd = 82 THEN '취소'
                    ELSE '대기'
                END as status_nm,
                CASE 
                    WHEN u.mlg_use_cd = 82 THEN 'status-cancelled'
                    ELSE 'status-applied'
                END as status_class
            FROM std_mileage_use u
            WHERE u.std_id = :stdId
            
            UNION ALL
            
            SELECT 
                u.pay_dt as event_dt,
                u.pay_money as mlg_score,
                u.pay_money,
                '지급' as mlg_type,
                '마일리지 장학금 전환 완료' as notes,
                '지급완료' as status_nm,
                'status-completed' as status_class
            FROM std_mileage_use u
            WHERE u.std_id = :stdId
            AND u.pay_dt IS NOT NULL
            AND u.pay_dt != ''
            AND u.mlg_use_cd = 83
        ) AS combined
        WHERE 1=1
            AND (:type IS NULL OR mlg_type = :type)
            AND (:status IS NULL OR status_nm = :status)
            AND (:startDate IS NULL OR event_dt >= :startDate)
            AND (:endDate IS NULL OR event_dt <= :endDate)
        ORDER BY event_dt DESC
        """,
        countQuery = """
        SELECT COUNT(*) FROM (
            SELECT h.mlg_dt as event_dt, '지급' as mlg_type, '지급완료' as status_nm
            FROM std_mileage_hist h
            WHERE h.std_id = :stdId
            
            UNION ALL
            
            SELECT u.aply_dt, '차감',
                CASE WHEN u.mlg_use_cd = 82 THEN '취소' ELSE '대기' END
            FROM std_mileage_use u
            WHERE u.std_id = :stdId
            
            UNION ALL
            
            SELECT u.pay_dt, '지급', '지급완료'
            FROM std_mileage_use u
            WHERE u.std_id = :stdId 
            AND u.pay_dt IS NOT NULL 
            AND u.pay_dt != '' 
            AND u.mlg_use_cd = 83
        ) AS combined
        WHERE 1=1
            AND (:type IS NULL OR mlg_type = :type)
            AND (:status IS NULL OR status_nm = :status)
            AND (:startDate IS NULL OR event_dt >= :startDate)
            AND (:endDate IS NULL OR event_dt <= :endDate)
        """,
        nativeQuery = true)
    Page<Object[]> findMileHistoryByStdIdWithFilters(
        @Param("stdId") Integer stdId,
        @Param("type") String type,
        @Param("status") String status,
        @Param("startDate") String startDate,
        @Param("endDate") String endDate,
        Pageable pageable
    );
    
    /*
     * 학과별 평균 마일리지 조회
     * - 재학생(11) + 휴학생(12)만 포함
     * - 마일리지 0인 학생도 포함
     */
    @Query(value = """
    	    SELECT COALESCE(AVG(mile), 0)
    	    FROM (
    	        SELECT 
    	            s.std_id,
    	            (
    	                COALESCE(
    	                    (SELECT SUM(h.mlg_score) 
    	                     FROM std_mileage_hist h 
    	                     WHERE h.std_id = s.std_id), 0
    	                )
    	                -
    	                COALESCE(
    	                    (SELECT SUM(u.aply_mlg_score) 
    	                     FROM std_mileage_use u 
    	                     WHERE u.std_id = s.std_id), 0
    	                )
    	            ) AS mile
    	        FROM std_info s
    	        WHERE s.dept_id = :deptId
    	        AND s.std_stat_cd IN (11, 12)
    	    ) AS dept_miles
    	    """, nativeQuery = true)
    Integer getDeptAvgMile(@Param("deptId") Integer deptId);
    
    
    /*
     * 학년별 평균 마일리지 조회
     * - 재학생(11) + 휴학생(12)만 포함
     * - 마일리지 0인 학생도 포함
     */
    @Query(value = """
    	    SELECT COALESCE(AVG(mile), 0)
    	    FROM (
    	        SELECT 
    	            s.std_id,
    	            (
    	                COALESCE(
    	                    (SELECT SUM(h.mlg_score) 
    	                     FROM std_mileage_hist h 
    	                     WHERE h.std_id = s.std_id), 0
    	                )
    	                -
    	                COALESCE(
    	                    (SELECT SUM(u.aply_mlg_score) 
    	                     FROM std_mileage_use u 
    	                     WHERE u.std_id = s.std_id), 0
    	                )
    	            ) AS mile
    	        FROM std_info s
    	        WHERE s.sch_yr = :schYr
    	        AND s.std_stat_cd IN (11, 12)
    	    ) AS grade_miles
    	    """, nativeQuery = true)
    Integer getGradeAvgMile(@Param("schYr") Integer schYr);
    
    /*
     * 전체 평균 마일리지 조회
     * - 재학생(11) + 휴학생(12)만 포함
     * - 마일리지 0인 학생도 포함
     */
    @Query(value = """
    	    SELECT COALESCE(AVG(mile), 0)
    	    FROM (
    	        SELECT 
    	            s.std_id,
    	            (
    	                COALESCE(
    	                    (SELECT SUM(h.mlg_score) 
    	                     FROM std_mileage_hist h 
    	                     WHERE h.std_id = s.std_id), 0
    	                )
    	                -
    	                COALESCE(
    	                    (SELECT SUM(u.aply_mlg_score) 
    	                     FROM std_mileage_use u 
    	                     WHERE u.std_id = s.std_id), 0
    	                )
    	            ) AS mile
    	        FROM std_info s
    	        WHERE s.std_stat_cd IN (11, 12)
    	    ) AS total_miles
    	    """, nativeQuery = true)
    Integer getTotalAvgMile();
    
    
    
    
}