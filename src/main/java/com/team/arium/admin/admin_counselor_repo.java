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

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;  // ✅ 올바른 import
import org.springframework.data.repository.query.Param;  // ✅ 추가 필요
import com.team.arium.domain.Empl_Info;

public interface admin_counselor_repo extends JpaRepository<Empl_Info, Long>{
	
	//사번 조회
	@Query(value = "SELECT EMPL_NO FROM EMPL_INFO WHERE EMPL_NO LIKE CONCAT(:today, '%') ORDER BY EMPL_NO DESC LIMIT 1", 
	       nativeQuery = true)  // ✅ nativeQuery 속성 추가
	String findLastEmpNoByDate(@Param("today") String today);  // ✅ @Param 추가
}