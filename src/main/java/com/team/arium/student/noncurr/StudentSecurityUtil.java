package com.team.arium.student.noncurr;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.team.arium.admin.login.StuUserDetails;
import com.team.arium.domain.User_Info;

/**
 * 학생 보안 관련 유틸리티 클래스
 */
@Component
public class StudentSecurityUtil {
    
    /**
     * 현재 로그인된 학생의 ID를 반환
     * 
     * @return 학생 ID (std_id)
     * @throws RuntimeException 로그인되지 않았거나 학생이 아닌 경우
     */
    public Integer getCurrentStudentId() {
        try {
            // 1. 현재 인증 정보 가져오기
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            
            if (authentication == null || !authentication.isAuthenticated()) {
                throw new RuntimeException("로그인이 필요합니다.");
            }
            
            // 2. 익명 사용자 체크
            if ("anonymousUser".equals(authentication.getPrincipal())) {
                throw new RuntimeException("로그인이 필요합니다.");
            }
            
            // 3. StuUserDetails로 캐스팅
            Object principal = authentication.getPrincipal();
            if (!(principal instanceof StuUserDetails)) {
                throw new RuntimeException("학생 계정으로 로그인해주세요.");
            }
            
            // 4. 학생 정보 추출
            StuUserDetails studentDetails = (StuUserDetails) principal;
            User_Info userInfo = studentDetails.getUser();
            
            if (userInfo == null || userInfo.getStdInfo() == null) {
                throw new RuntimeException("학생 정보를 찾을 수 없습니다.");
            }
            
            Integer stdId = userInfo.getStdInfo().getStdId();
            if (stdId == null) {
                throw new RuntimeException("유효하지 않은 학생 ID입니다.");
            }
            
            return stdId;
            
        } catch (Exception e) {
            throw new RuntimeException("현재 학생 정보를 가져오는 중 오류가 발생했습니다: " + e.getMessage());
        }
    }
    
    /**
     * 현재 로그인된 학생의 정보를 반환
     * 
     * @return User_Info 객체
     */
    public User_Info getCurrentUserInfo() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            
            if (authentication == null || !authentication.isAuthenticated() || 
                "anonymousUser".equals(authentication.getPrincipal())) {
                throw new RuntimeException("로그인이 필요합니다.");
            }
            
            Object principal = authentication.getPrincipal();
            if (!(principal instanceof StuUserDetails)) {
                throw new RuntimeException("학생 계정으로 로그인해주세요.");
            }
            
            StuUserDetails studentDetails = (StuUserDetails) principal;
            return studentDetails.getUser();
            
        } catch (Exception e) {
            throw new RuntimeException("사용자 정보를 가져오는 중 오류가 발생했습니다: " + e.getMessage());
        }
    }
    
    /**
     * 현재 로그인된 학생의 학번을 반환
     * 
     * @return 학번 (std_no)
     */
    public String getCurrentStudentNumber() {
        User_Info userInfo = getCurrentUserInfo();
        return userInfo.getStdNo();
    }
    
    /**
     * 현재 로그인된 학생의 이름을 반환
     * 
     * @return 학생 이름
     */
    public String getCurrentStudentName() {
        User_Info userInfo = getCurrentUserInfo();
        if (userInfo.getStdInfo() != null) {
            return userInfo.getStdInfo().getStdNm();
        }
        return "이름 없음";
    }
}