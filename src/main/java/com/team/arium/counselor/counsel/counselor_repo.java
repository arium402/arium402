package com.team.arium.counselor.counsel;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.team.arium.DTO.admin_counselor_DTO;
import com.team.arium.DTO.counselor_patient_DTO;
import com.team.arium.domain.Cnsl_Aply;
import com.team.arium.domain.Std_Info;

import jakarta.transaction.Transactional;
@Transactional
public interface counselor_repo extends JpaRepository<Cnsl_Aply, Long>{
	
	List<Cnsl_Aply> findAllByOrderByCnslAplyId(); 
	
	@Query(value = "insert into cnlr_schd (schd_id, empl_id, work_year, work_month, work_day,start_time,end_time,reg_dt,upd_dt) " +
            "values ('0', :#{#dkey.emplNo}, :#{#dkey.emplName}, :#{#dkey.cnslCd}, :#{#dkey.emplStatCd},:#{#dkey.emplTellno},null,now(),null)", nativeQuery = true)
	@Modifying(clearAutomatically = true)
	int schedule_insert(@Param("dkey") counselor_patient_DTO dto);
	
}
