package com.team.arium.student.mileage;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

//마일리지 차트 데이터 DTO
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StdMileChartDTO {
	
	Integer myMile; //내 마일리지
	Integer deptAvg; //학과 평균
	Integer gradeAvg; //학년 평균
	Integer totalAvg; //전체 평균

}
