package com.team.arium.admin.noncurr.dto;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;
import jakarta.validation.constraints.*;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NoncurrAddRequestDto {
    
    @NotBlank(message = "프로그램명은 필수입니다")
    private String prgNm; // prg_nm
    
    @NotBlank(message = "프로그램 설명은 필수입니다")
    private String prgDesc; // prg_desc
    
    @NotBlank(message = "모집 시작일은 필수입니다")
    private String recruitStart; // 별도 테이블이므로 그대로
    
    @NotBlank(message = "모집 마감일은 필수입니다")
    private String recruitEnd; // 별도 테이블이므로 그대로
    
    @NotBlank(message = "운영 시작일은 필수입니다")
    private String prgStDt; // prg_st_dt
    
    @NotBlank(message = "운영 종료일은 필수입니다")
    private String prgEndDt; // prg_end_dt
    
    @NotNull(message = "모집인원은 필수입니다")
    @Min(value = 1, message = "모집인원은 1명 이상이어야 합니다")
    private Integer maxCnt; // max_cnt
    
    @NotBlank(message = "운영부서는 필수입니다")
    private String department; // 별도 관리
    
    @NotBlank(message = "문의 전화번호는 필수입니다")
    private String contact; // 별도 관리
    
    @NotBlank(message = "만족도조사 마감일은 필수입니다")
    private String surveyDt; // survey_dt
    
    @NotNull(message = "마일리지 점수는 필수입니다")
    @Min(value = 0, message = "마일리지 점수는 0 이상이어야 합니다")
    private Integer mlgDefScore; // mlg_def_score
    
    // 핵심역량 ID와 점수를 간단하게 처리
    @NotEmpty(message = "핵심역량을 하나 이상 선택해주세요")
    private List<Integer> selectedCompetencyIds;
    
    private List<Integer> competencyScores; // 각 역량별 점수
    
    private MultipartFile programImage;
    private MultipartFile attachmentFile;
}