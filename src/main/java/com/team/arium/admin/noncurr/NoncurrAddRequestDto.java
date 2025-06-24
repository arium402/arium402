package com.team.arium.admin.noncurr;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.util.List;

//비교과 등록 요청 DTO

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class NoncurrAddRequestDto {
    
    @NotBlank(message = "프로그램명은 필수입니다")
    @Size(max = 100, message = "프로그램명은 100자 이하여야 합니다")
    private String programName;
    
    @NotBlank(message = "프로그램 설명은 필수입니다")
    @Size(max = 500, message = "프로그램 설명은 500자 이하여야 합니다")
    private String description;
    
    @NotNull(message = "모집 시작일은 필수입니다")
    private LocalDate recruitStart;
    
    @NotNull(message = "모집 마감일은 필수입니다")
    private LocalDate recruitEnd;
    
    @NotNull(message = "운영 시작일은 필수입니다")
    private LocalDate operationStart;
    
    @NotNull(message = "운영 종료일은 필수입니다")
    private LocalDate operationEnd;
    
    @NotNull(message = "모집인원은 필수입니다")
    @Min(value = 1, message = "모집인원은 1명 이상이어야 합니다")
    private Integer capacity;
    
    @NotBlank(message = "운영부서는 필수입니다")
    private String department;
    
    @NotBlank(message = "문의 전화번호는 필수입니다")
    @Pattern(regexp = "^[0-9-]+$", message = "올바른 전화번호 형식이 아닙니다")
    private String contact;
    
    @NotNull(message = "만족도조사 마감일은 필수입니다")
    private LocalDate surveyDeadline;
    
    @NotNull(message = "마일리지 점수는 필수입니다")
    @Min(value = 0, message = "마일리지 점수는 0 이상이어야 합니다")
    private Integer mileagePoints;
    
    @NotEmpty(message = "핵심역량을 하나 이상 선택해주세요")
    private List<String> competencies;
    
    private MultipartFile programImage;
    private MultipartFile attachmentFile;
}
