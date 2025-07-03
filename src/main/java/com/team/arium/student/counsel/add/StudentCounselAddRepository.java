package com.team.arium.student.counsel.add;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.team.arium.domain.Cnlr_Schd;
import com.team.arium.domain.Cnsl_Aply;

@Repository
public interface StudentCounselAddRepository extends JpaRepository<Cnsl_Aply, Integer> {

	// 특정 날짜의 예약된 상담 조회
	@Query("SELECT ca FROM Cnsl_Aply ca " + "WHERE ca.cnclDt = :date " + "AND ca.cnslStatCd.codeId = 121")
	List<Cnsl_Aply> findBookingsByDate(@Param("date") String date);
	
	// 특정 상담사의 특정 날짜 예약 조회
	@Query("SELECT ca FROM Cnsl_Aply ca " + "WHERE ca.emplInfo.emplId = :emplId " + "AND ca.cnclDt = :date " + "AND ca.cnslStatCd.codeId = 121")
	List<Cnsl_Aply> findBookingsByEmplAndDate(@Param("emplId") Integer emplId, @Param("date") String date);
	
	// 특정 년월에 근무하는 모든 상담사의 스케줄 조회
	@Query("SELECT cs FROM Cnlr_Schd cs " + 
			"JOIN FETCH cs.emplInfo " + "JOIN FETCH cs.workDay " +
			"WHERE cs.workYear = :year AND cs.workMonth = :month " +
			"ORDER BY cs.workDay.codeId, cs.emplInfo.emplName")
	List<Cnlr_Schd> findByYearAndMonth(@Param("year") String year, @Param("month") String month);
}
