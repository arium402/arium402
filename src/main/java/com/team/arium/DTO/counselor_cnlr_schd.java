package com.team.arium.DTO;

import org.springframework.stereotype.Repository;

import lombok.Data;

@Data
@Repository("cnlr_schd_DTO")
public class counselor_cnlr_schd {

	int schd_id, empl_id, work_day;
	String work_year, work_month, start_time, end_time, reg_dt, upd_dt;
	
}
