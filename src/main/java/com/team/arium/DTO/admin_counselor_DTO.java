package com.team.arium.DTO;

import org.springframework.stereotype.Repository;

import lombok.Data;

@Data
@Repository("admin_counselor_DTO")
public class admin_counselor_DTO {
	public Integer emplId;
	public String emplNo;
	public String emplName;
	public String cnslCd;
	public String emplTellno;
	public String emplEmlAddr;
	public String emplStatCd;
	public String cnslCdDesc;
	public String emplStatCdDesc; 
	public String regDt;
	public String updDt;
}
