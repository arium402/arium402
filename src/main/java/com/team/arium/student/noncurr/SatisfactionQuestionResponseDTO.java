package com.team.arium.student.noncurr;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SatisfactionQuestionResponseDTO {
    private Integer prgId;
    private String prgNm;
    private List<QuestionDTO> section1Questions; // 종합 만족도
    private List<QuestionDTO> section2Questions; // 프로그램 내용
    private List<QuestionDTO> section3Questions; // 강사
}