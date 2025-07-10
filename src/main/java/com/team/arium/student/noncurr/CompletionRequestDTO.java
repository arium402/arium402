package com.team.arium.student.noncurr;

import lombok.Builder;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * 이수 처리 요청 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompletionRequestDTO {
    private Integer prgId;           // 프로그램 ID
    private Boolean forceProcess;    // 강제 처리 여부 (기본: false)
    private String processingNote;   // 처리 메모
}