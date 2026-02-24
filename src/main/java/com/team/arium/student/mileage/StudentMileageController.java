package com.team.arium.student.mileage;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.team.arium.student.noncurr.StudentSecurityUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/student/mileage")
public class StudentMileageController {
	
	//의존성 주입
	//학생 마일리지 service
	private final StdMileageService stdMileService;
	//학생 보안 유틸리티 : spring security로 현재 로그인된 학생 정보 제공
	private final StudentSecurityUtil stdSecUtil;
	
	@GetMapping("/my")
	public String myPage(Model m) {
		
		try {
			//spring security로 현재 학생 ID 가져오기
			//로그인 여부/학생계정 여부확인/학생ID 추출/null체크
			Integer stdId = stdSecUtil.getCurrentStudentId();
			log.info("학생 마일리지 대시보드 조회:{}", stdId);
			
			//service 호출(대시보드 데이터 조회)
			StdMileageDashboardDTO dashboard = stdMileService.getDashboard(stdId);
			m.addAttribute("dashboard", dashboard);
			
			return "/student/mileage/student_mileage_my";	// 나의 마일리지
		} catch (RuntimeException  e) { 
			//로그인이 안 됐을 경우/학생 계정이 아닌경우/정보를 찾을 수 없는 경우
			log.error("학생 마일리지 로드 실패:{}", e.getMessage(), e);
			
			//로그인 관련 에러일 경우 로그인 페이지로 리다이렉트
			if(e.getMessage().contains("로그인")) {
				log.warn("로그인되지 않은 사용자의 마일리지 페이지 접근 시도");
				return "redirect:/login";
			}
			m.addAttribute("errorMessage", "마일리지 정보를 불러오는 중 오류 발생!");
            m.addAttribute("errorDetail", e.getMessage());
            return "error";

		} catch (Exception e) {
			log.error("예상치 못한 오류 발생:{}", e.getMessage(), e);
			m.addAttribute("errorMessage", "마일리지 정보를 불러오는 중 오류 발생!");
			m.addAttribute("errorDetail", e.getMessage());
			return "error";
		}
		
	}
	
	//마일리지 카드섹션 API
	@PostMapping("/api/convert") 
	public ResponseEntity<Map<String, Object>> convertMile(
		@RequestParam("convertAmount") Integer convertAmount,
		@RequestParam("bankName") String bankName,
		@RequestParam("bankAccount") String bankAccount,
		@RequestParam("depositor") String depositor
	){
		log.info("마일리지 전환 신청 API요청: 금액{}",convertAmount);
		try {
			//현재 로그인된 학생 ID가져오기
			Integer stdId = stdSecUtil.getCurrentStudentId();
			//입력값 검증
			if(convertAmount == null || convertAmount <= 0) {
				throw new RuntimeException("전환할 마일리지를 입력해주세요.");
			}
			if(convertAmount < 100) {
				throw new RuntimeException("최소 전환 단위는 100P입니다.");
			}
			if (convertAmount % 100 != 0) {
				throw new RuntimeException("100P 단위로 입력해주세요.");
			}
			
			//service 호출
			boolean success = stdMileService.convertMileToMoney
					(stdId, convertAmount, bankName, bankAccount, depositor);
			//응답 생성
			Map<String, Object> res = new HashMap<>();
			if(success) {
				res.put("success", true);
				res.put("message", "마일리지 전환 신청이 완료되었습니다.");
				
				log.info("마일리지 전환 신청 성공: stdId={}, 금액={}", stdId, convertAmount);
				
				return ResponseEntity.ok(res);
			}else {
				res.put("success", false);
				res.put("message", "전환 신청에 실패했습니다.");
				return ResponseEntity.badRequest().body(res);
			}
			
		} catch (RuntimeException e) {
			//에러처리
			log.warn("마일리지 전환 신청 거부: {}", e.getMessage());
			Map<String, Object> res = new HashMap<>();
	        res.put("success", false);
	        res.put("message", e.getMessage());
	        
	        return ResponseEntity.badRequest().body(res);
		}
	}
		
	//마일리지 내역 조회 API
	@GetMapping("/api/history")
	public ResponseEntity<Map<String, Object>> getMileHistory(
			@RequestParam(name = "page", defaultValue = "0") int page,
			@RequestParam(name = "size", defaultValue = "10") int size,
			@RequestParam(name = "type", required = false) String type,
			@RequestParam(name = "status", required = false) String status,
			@RequestParam(name = "startDate", required = false) String startDate,
			@RequestParam(name = "endDate", required = false) String endDate){
			
		Map<String, Object> res = new HashMap<>();
		try {
			//현재 로그인된 학생
			Integer stdId = stdSecUtil.getCurrentStudentId();
				
			//Service 호출
			Map<String, Object> data = stdMileService.getMileHistory(
					stdId, page, size, type, status, startDate, endDate
				);
				
			res.put("success", true);
			res.put("histories", data.get("histories"));
			res.put("pagination", data.get("pagination"));
				
			log.info("마일리지 내역 조회 성공: stdId={}, 건수={}", 
					stdId, ((java.util.List<?>) data.get("histories")).size());
			return ResponseEntity.ok(res);
				
		} catch (RuntimeException  e) {
			log.warn("마일리지 내역조회 실패: {}", e.getMessage());
			res.put("success", false);
				
			if(e.getMessage().contains("로그인")) {
				res.put("message", "로그인이 필요합니다.");
				res.put("redirectUrl", "/login");
			} else {
				res.put("message", e.getMessage());
			}
				
			return ResponseEntity.badRequest().body(res);
		}
	}
	
	// 마일리지 차트 데이터 조회 API
	@GetMapping("/api/chart")
	public ResponseEntity<Map<String, Object>> getChartData() {
		Map<String, Object> res = new HashMap<>();
		
		try {
			// 현재 로그인된 학생 ID
			Integer stdId = stdSecUtil.getCurrentStudentId();
			// Service 호출
			StdMileChartDTO chartData = stdMileService.getChartData(stdId);
			
			res.put("success", true);
			res.put("myMile", chartData.getMyMile());
			res.put("deptAvg", chartData.getDeptAvg());
			res.put("gradeAvg", chartData.getGradeAvg());
			res.put("totalAvg", chartData.getTotalAvg());
			
			return ResponseEntity.ok(res);
		} catch (RuntimeException e) {
			log.warn("마일리지 차트 데이터 조회 실패: {}", e.getMessage());
			res.put("success", false);
			
			if (e.getMessage().contains("로그인")) {
				res.put("message", "로그인이 필요합니다.");
				res.put("redirectUrl", "/login");
			} else {
				res.put("message", e.getMessage());
			}
			
			return ResponseEntity.badRequest().body(res);
		}
	}
}