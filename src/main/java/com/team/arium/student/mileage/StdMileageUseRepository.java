package com.team.arium.student.mileage;

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
	
	
}
