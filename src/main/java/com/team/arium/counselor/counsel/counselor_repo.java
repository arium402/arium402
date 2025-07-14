package com.team.arium.counselor.counsel;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.team.arium.DTO.admin_counselor_DTO;
import com.team.arium.DTO.counselor_cnlr_schd;
import com.team.arium.DTO.counselor_patient_DTO;
import com.team.arium.domain.Cnsl_Aply;
import com.team.arium.domain.Empl_Info;
import com.team.arium.domain.Std_Info;

import jakarta.transaction.Transactional;
@Transactional
public interface counselor_repo extends JpaRepository<Cnsl_Aply, Long>{
	
	List<Cnsl_Aply> findAllByOrderByCnslAplyId(); 
	
	@Query(value = "insert into cnlr_schd (schd_id, empl_id, work_year, work_month, work_day,start_time,end_time,reg_dt,upd_dt) " +
            "values ('0', :#{#dkey.empl_id}, :#{#dkey.work_year}, :#{#dkey.work_month}, :#{#dkey.work_day},:#{#dkey.start_time},:#{#dkey.end_time},now(),null)", nativeQuery = true)
	@Modifying(clearAutomatically = true)
	int schedule_insert(@Param("dkey") counselor_cnlr_schd dto);
	
	
	//스케줄 시간조회	
	@Query(value = "INSERT INTO cnlr_schd_slot (slot_id, schd_id, slot_type_id, reg_dt)"
			+ " SELECT '0', schd_id, :typeid, NOW()"
			+ " FROM cnlr_schd"
			+ " WHERE work_year = :year AND work_month = :month AND empl_id = :empl_id order by schd_id desc limit 0,1", nativeQuery = true)
	@Modifying(clearAutomatically = true)
	int schedule_insert(@Param("typeid") String typeid, @Param("year") String year, @Param("month") String month, @Param("empl_id") String empl_id);
}
