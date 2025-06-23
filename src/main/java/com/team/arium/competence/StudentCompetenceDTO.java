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

	private String categoryName;	// 상위 카테고리(핵심역량)명
	private String cclNm;	// 역량명
	private String cclDesc;	// 역량 설명

}