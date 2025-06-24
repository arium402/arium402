package com.team.arium.admin.noncurr;

import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class NoncurrListResponseDto {
    private Integer prgId;
    private String prgNm;
    private String department;
    private String recruitmentPeriod;
    private String operationPeriod;
    private Integer currentCapacity;
    private Integer maxCapacity;
    private String status;
    private String prgStatCd;
}
