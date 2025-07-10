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
public class MileagePaymentResultDTO {
    
    private Boolean success;                    // 성공 여부
    private String message;                     // 결과 메시지
    private Integer totalCount;                 // 총 대상자 수
    private Integer successCount;               // 성공 건수
    private Integer failCount;                  // 실패 건수
    private List<String> failedStudents;       // 실패한 학생 목록
    private String paymentDate;                 // 지급일

    // ✅ 명시적 Getter/Setter 추가
    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Integer totalCount) {
        this.totalCount = totalCount;
    }

    public Integer getSuccessCount() {
        return successCount;
    }

    public void setSuccessCount(Integer successCount) {
        this.successCount = successCount;
    }

    public Integer getFailCount() {
        return failCount;
    }

    public void setFailCount(Integer failCount) {
        this.failCount = failCount;
    }

    public List<String> getFailedStudents() {
        return failedStudents;
    }

    public void setFailedStudents(List<String> failedStudents) {
        this.failedStudents = failedStudents;
    }

    public String getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(String paymentDate) {
        this.paymentDate = paymentDate;
    }
}