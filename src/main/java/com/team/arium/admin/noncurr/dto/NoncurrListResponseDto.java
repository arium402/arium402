// 2. 목록 응답 DTO  
package com.team.arium.admin.noncurr.dto;

import lombok.*;

@Data
@AllArgsConstructor
@Builder
public class NoncurrListResponseDto {
    private Integer prgId;
    private String prgCd;
    private String prgNm;
    private String department;
    private String recruitmentPeriod;
    private String operationPeriod;
    private Integer currentCapacity;
    private Integer maxCapacity;
    private String status;
    private String prgStatCd;
    private Integer mlgDefScore;
    private String regDt;
}