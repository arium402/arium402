//package com.team.arium.admin;
//
//import org.springframework.data.jdbc.repository.query.Query;
//import org.springframework.data.jpa.repository.JpaRepository;
//
//import com.team.arium.domain.Empl_Info;
//
//
//public interface admin_counselor_repo extends JpaRepository<Empl_Info, Long>{
//	
//	//사번 조회
//	@Query(value = "SELECT EMPL_NO FROM EMPL_INFO WHERE EMPL_NO LIKE CONCAT(:today, '%') ORDER BY EMPL_NO DESC LIMIT 1")
//	String findLastEmpNoByDate(String today);
//
//}

package com.team.arium.admin;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;  
import org.springframework.data.repository.query.Param;  
import com.team.arium.domain.Empl_Info;

public interface admin_counselor_repo extends JpaRepository<Empl_Info, Long>{
	
	//사번 조회
	@Query(value = "SELECT EMPL_NO FROM EMPL_INFO WHERE EMPL_NO LIKE CONCAT(:today, '%') ORDER BY EMPL_NO DESC LIMIT 1", 
	       nativeQuery = true)  
	String findLastEmpNoByToday(@Param("today") String today); 
	
	//오늘날짜 반환 
	@Query("select now() as today")
	String mysql_today();
	
	//=> select * from empl_info order by emplNo;
	List<Empl_Info> findAllByOrderByEmplId(); 
}