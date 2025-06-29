package com.team.arium.student.counsel.before;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentCounselBeforeDTO {

	// 사전검사 기본 정보
	private Integer preSurveyId;
	private String preTypeCd;      // 사전검사 타입 코드
	private String preTypeName;    // 사전검사 타입 이름
	private String preSurveyTitle; // 사전검사 제목
	private String preSurveyDesc;  // 사전검사 설명
	
	// 문항 정보
	private Integer preQstId;
	private String preQstContent;
	private Integer preQstOrd;
	private Integer preQstType;    // 문항 타입 코드 ID
	private String preQstTypeCd;   // 문항 타입 코드
	private String qstTypeName;    // 문항 타입 이름
	
	// 옵션 정보
	private Integer preOptId;
	private String optContent;
	private Integer optOrd;
}