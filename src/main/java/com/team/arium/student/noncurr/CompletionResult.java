package com.team.arium.student.noncurr;

import lombok.Builder;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * 이수 처리 결과 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompletionResult {
    private Boolean success;                // 처리 성공 여부
    private Integer processedCount;         // 새로 처리된 인원 수
    private Integer alreadyProcessedCount;  // 이미 처리된 인원 수
    private String message;                 // 결과 메시지
    private String errorDetail;             // 오류 상세 (실패시)
}