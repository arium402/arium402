package com.team.arium.admin.mileage;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MileagePaymentRequestDTO {
    
    private Integer prgId;                      // 프로그램 ID
    private List<Integer> participantIds;       // 지급 대상자 ID 목록 (cmpId)
    private String paymentDate;                 // 지급일
    private String paymentMemo;                 // 지급 메모
    private Boolean selectAll;                  // 전체 선택 여부

    //  명시적 Getter/Setter 추가
    public Integer getPrgId() {
        return prgId;
    }

    public void setPrgId(Integer prgId) {
        this.prgId = prgId;
    }

    public List<Integer> getParticipantIds() {
        return participantIds;
    }

    public void setParticipantIds(List<Integer> participantIds) {
        this.participantIds = participantIds;
    }

    public String getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(String paymentDate) {
        this.paymentDate = paymentDate;
    }

    public String getPaymentMemo() {
        return paymentMemo;
    }

    public void setPaymentMemo(String paymentMemo) {
        this.paymentMemo = paymentMemo;
    }

    public Boolean getSelectAll() {
        return selectAll;
    }

    public void setSelectAll(Boolean selectAll) {
        this.selectAll = selectAll;
    }
}