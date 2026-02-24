package com.team.arium.student.mileage;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StdMileageHistoryDTO {
	
	// 날짜
    private String mlgDt;         // 날짜 (yyyy-MM-dd)
    private String mlgDtFmt;      // 날짜 포맷 (yyyy.MM.dd)
    // 점수/금액
    private Integer mlgScore;     // 점수 또는 금액 (숫자)
    private String mlgScoreFmt;   // 점수 포맷 (+2000P, -200P, 2,000원)
    // 유형
    private String mlgType;       // "지급" or "차감"
    // 비고
    private String notes;         // 비고 내용
    // 상태
    private String statusNm;      // 상태명 ("지급완료", "대기", "취소")
    private String statusClass;   // CSS 클래스
    // 금액 정보 (전환 완료 시)
    private Integer payMoney;     // 지급 금액 (원)
}
