package com.team.arium.student.mileage;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.team.arium.domain.Std_MileageUse;

@Repository
public interface StdMileageUseRepository extends JpaRepository<Std_MileageUse, Integer>{

	/*
	 * 학생별 총 사용 마일리지 조회
	 * 조건- mlg_use_cd = 83 (지급완료 상태)
	 */
	@Query("""
		    SELECT COALESCE(SUM(mu.aplyMlgScore), 0)
		    FROM Std_MileageUse mu
		    WHERE mu.stdInfo.stdId = :stdId
		      AND mu.mlgUseCd.codeId IN (81, 83)
		    """)
	    Integer getTotalUsedMileageByStdId(@Param("stdId") Integer stdId);
	    
	/*
	 * 학생별 전환받은 총 장학금 조회
	 * 조건-pay_money IS NOT NULL(실제 지급된 것만)
	 * NULL이 아닌 경우만 합산
	 */
	@Query("""
	        SELECT COALESCE(SUM(mu.payMoney), 0)
	        FROM Std_MileageUse mu
	        WHERE mu.stdInfo.stdId = :stdId
	          AND mu.payMoney IS NOT NULL
	        """)
	    Integer getTotalConvertedMoneyByStdId(@Param("stdId") Integer stdId);
	
	/**
	 * 특정 학생의 특정 날짜 신청 내역 존재 여부 확인
	 */
	@Query("""
	    SELECT COUNT(mu) > 0
	    FROM Std_MileageUse mu
	    WHERE mu.stdInfo.stdId = :stdId
	      AND mu.aplyDt = :aplyDt
	    """)
	boolean existsByStdIdAndAplyDt(@Param("stdId") Integer stdId, @Param("aplyDt") String aplyDt);
	//======================
	
	//관리자 전환 관리용
	
	//전체 전환 신청 목록 조회(최신순, 페이징)
    @Query("""
            SELECT mu
            FROM Std_MileageUse mu
            JOIN FETCH mu.stdInfo s
            JOIN FETCH s.deptInfo d
            JOIN FETCH mu.mlgUseCd c
            ORDER BY mu.aplyDt DESC, mu.mlgUseId DESC
            """)
        Page<Std_MileageUse> getAll(Pageable pageable);
	
	//상태별 전환 신청 목록 조회 (최신순, 페이징)
    @Query("""
            SELECT mu
            FROM Std_MileageUse mu
            JOIN FETCH mu.stdInfo s
            JOIN FETCH s.deptInfo d
            JOIN FETCH mu.mlgUseCd c
            WHERE c.codeId = :statusCode
            ORDER BY mu.aplyDt DESC, mu.mlgUseId DESC
            """)
        Page<Std_MileageUse> getByStatus(
            @Param("statusCode") Integer statusCode, 
            Pageable pageable
        );
        
        //단건 상세 조회 (모달용)
        @Query("""
                SELECT mu
                FROM Std_MileageUse mu
                JOIN FETCH mu.stdInfo s
                JOIN FETCH s.deptInfo d
                JOIN FETCH mu.mlgUseCd c
                WHERE mu.mlgUseId = :mlgUseId
                """)
            Optional<Std_MileageUse> findByIdWithDetails(@Param("mlgUseId") Integer mlgUseId);
            
          
        //전체 건수 조회 (페이징 정보용)
        @Query("""
                SELECT COUNT(mu)
                FROM Std_MileageUse mu
                """)
            long countAll();
        
        //상태별 건수 조회(페이징 정보용)
        /*
         * @param statusCode 상태 코드
         * @return 해당 상태의 전환 신청 건수
         */
        @Query("""
                SELECT COUNT(mu)
                FROM Std_MileageUse mu
                WHERE mu.mlgUseCd.codeId = :statusCode
                """)
            long countByStatus(@Param("statusCode") Integer statusCode);
        
        
}
