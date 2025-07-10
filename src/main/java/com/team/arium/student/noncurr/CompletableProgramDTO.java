package com.team.arium.student.noncurr;

import lombok.Builder;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * 이수 처리 가능한 프로그램 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompletableProgramDTO {
    private Integer prgId;              // 프로그램 ID
    private String prgNm;               // 프로그램명
    private String prgEndDt;            // 프로그램 종료일
    private Integer totalApplicants;    // 전체 신청자 수
    private Integer completedApplicants; // 이수 처리된 신청자 수
    private Boolean needsProcessing;    // 처리 필요 여부
    private String status;              // 상태 (처리완료/처리필요)
    
    // 편의 메서드
    public String getStatus() {
        if (totalApplicants == 0) {
            return "신청자 없음";
        } else if (completedApplicants.equals(totalApplicants)) {
            return "처리 완료";
        } else {
            return "처리 필요 (" + completedApplicants + "/" + totalApplicants + ")";
        }
    }
}
