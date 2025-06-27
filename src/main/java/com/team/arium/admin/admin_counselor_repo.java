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
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.team.arium.DTO.admin_counselor_DTO;
import com.team.arium.domain.Empl_Info;

import jakarta.transaction.Transactional;
@Transactional
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
	
	@Query(value = "SELECT * from empl_info where empl_id = :emplid", nativeQuery = true) 
	//List<admin_counselor_DTO> dtoList(@Param("emplid") int emplId);
	List<Empl_Info> dtoList(@Param("emplid") int emplId);
	
	//상담사 등록
	@Query(value = "insert into empl_info (empl_id, empl_no, empl_name, cnsl_cd, empl_stat_cd,empl_tellno,empl_eml_addr,file_id,reg_dt,upd_dt) " +
            "values ('0', :#{#dkey.emplNo}, :#{#dkey.emplName}, :#{#dkey.cnslCd}, :#{#dkey.emplStatCd},:#{#dkey.emplTellno},:#{#dkey.emplEmlAddr},null,now(),null)", nativeQuery = true)
	@Transactional	
	@Modifying(clearAutomatically = true)
	int mysql_insert(@Param("dkey") admin_counselor_DTO dto);
	
	
	//상담사 로그인 가능하도록 처리
	@Query(value = "insert into user_info values ('0', null, :id, :pw, :rols, now(), null)", nativeQuery = true)
	@Modifying(clearAutomatically = true)
	int user_insert(@Param("id")String id, @Param("pw")String pw, @Param("rols")String rols);
	

	@Query(value = "select * from empl_info where empl_stat_cd=:statcode", nativeQuery = true)
	List<Empl_Info> findAllByemplStatCd(@Param("statcode") int statcode);
	
	
	
	
	
}