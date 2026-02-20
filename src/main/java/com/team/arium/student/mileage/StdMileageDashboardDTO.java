package com.team.arium.student.mileage;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

//학생 마일리지 카드 섹션용
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StdMileageDashboardDTO {

    /* 전체 보유 마일리지
     * 계산: 총 적립 마일리지 - 총 사용 마일리지*/
    private Integer totalMile;
    /* 적립예정 마일리지 (현재 년도 기준)
     * 조건: 이수완료 + 만족도완료 했지만 관리자가 아직 지급 안한 것*/
    private Integer pendingMile;
    /* 전체 사용 마일리지
     * 출처: std_mileage_use 테이블에서 승인완료된 것들의 합계*/
    private Integer usedMile;
    
    /* 전환받은 장학금 (원 단위)
     * 출처: std_mileage_use 테이블의 pay_money 합계*/
    private Integer convertedMoney;
    
    //java코드 계산시 필요
    private String currentYear;
	
    private String bankName;        // 은행명 (bank_nm)
    private String bankAccount;   // 계좌번호 (bank_acnt)
    private String depositor;   // 예금주 (depositor)
    /**
     * 학생 이름 (나중에 필요 시)
     */
    // private String stdNm;
    
    /**
     * 학생 학번 (나중에 필요 시)
     */
    // private String stdNo;
    
}
