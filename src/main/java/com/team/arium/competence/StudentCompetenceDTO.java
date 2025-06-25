package com.team.arium.competence;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentCompetenceDTO {

   private String categoryName;   // 상위 카테고리(핵심역량)명
   private String cclNm;   // 역량명
   private String cclDesc;   // 역량 설명

	private Integer qstOrd;	// 질문 순서
	private String qstContent;	// 질문 내용
	private Integer qstId;	// 질문 id
	private Integer cclId;	// 역량 id
	
	private Integer stdId;	// 학생 id
	private Integer ansScore;	// 응답 점수 (1~5)
	private String evalId;	// 진단 실시 ID
}