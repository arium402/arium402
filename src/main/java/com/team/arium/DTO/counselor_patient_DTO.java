package com.team.arium.DTO;

import lombok.Data;

@Data
public class counselor_patient_DTO {
	private Integer stdId; //학생아이디
	private String stdNo;  //학번
    private String stdNm; 	//학생명
    private String deptInfo;	//학과아이디
    private String cns_regDt;  //상담신청일
    private String deptName; //학과명
}
