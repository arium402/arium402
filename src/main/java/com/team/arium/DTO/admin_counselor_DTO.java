package com.team.arium.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class admin_counselor_DTO {
	private String emplNo;
	private String emplName;
    private String cnslCd;
    private String emplTellno;
    private String emplEmlAddr;
    private String emplStatCd;
    private String cnslCdDesc;
    private String emplStatCdDesc; 
    private String regDt;
    private String updDt;
}
