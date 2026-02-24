package com.team.arium.student.mileage;

import java.time.LocalDate;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.team.arium.admin.admin_module;
import com.team.arium.admin.mileage.StdMileageHistRepository;
import com.team.arium.admin.noncurr.repository.CommonCodeRepository;
import com.team.arium.domain.Common_Code;
import com.team.arium.domain.Std_Info;
import com.team.arium.domain.Std_MileageUse;
import com.team.arium.student.noncurr.StdInfoRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StdMileageService {
	//적립내역
	private final StdMileageHistRepository mileHistRepo;
	//사용내역
	private final StdMileageUseRepository mileUseRepo;
	//학생 정보
	private final StdInfoRepository stdInfoRepo;
	//공통코드 정보
	private final CommonCodeRepository CommonCodeRopo;
	//한국 시간대
	private final admin_module adminModule;
	
	public StdMileageDashboardDTO getDashboard(Integer stdId) {
		log.info("학생 마일리지 대시보드 조회 시작 :", stdId);
		try {
			//총 적립 마일리지 조회
			Integer totalEarned = mileHistRepo.getTotalMileageByStdId(stdId);
			log.info("총 적립 마일리지: {}", totalEarned);
			
			//총 사용 마일리지 조회
			Integer totalUsed = mileUseRepo.getTotalUsedMileageByStdId(stdId);
			log.info("총 사용 마일리지: {}", totalUsed);
			
			//보유 마일리지 계산: 보유 = 적립-사용
			Integer totalMile = totalEarned - totalUsed;
			log.info("보유 마일리지:{}", totalMile, totalEarned, totalUsed);
			
			//적립예정 마일리지 조회 : 이수완료+만족도완료+관리자 미지급
			Integer pendingMile = mileHistRepo.getPendingMileageByStdId(stdId);
			log.info("적립예정 마일리지:{}",pendingMile);
			
			//전환받은 장학금 조회
			Integer convertedMoney = mileUseRepo.getTotalConvertedMoneyByStdId(stdId);
			log.info("전환받은 장학금:{}", convertedMoney);
			
			//현재년도 계산
			String currentYear = String.valueOf(LocalDate.now().getYear());
			
			//학생 정보 조회 (계좌 정보 포함)
			Std_Info std = stdInfoRepo.findById(stdId)
					.orElseThrow(() -> new RuntimeException("학생 정보를 찾을 수 없습니다."));
			
			//계좌 정보 추출
			String bankName = std.getBankNm();
			String bankAccount = std.getBankAcnt();
			String depositor = std.getDepositor();
			
			StdMileageDashboardDTO dashboard = StdMileageDashboardDTO.builder()
					.totalMile(totalMile)
					.pendingMile(pendingMile)
					.usedMile(totalUsed)
					.convertedMoney(convertedMoney)
					.currentYear(currentYear)
					.bankName(bankName)
					.bankAccount(bankAccount)
					.depositor(depositor)
					.build();
			
			log.info("학생 마일리지 대시보드 조회 완료:{} ",stdId, totalMile, pendingMile);
			
			return dashboard;
		} catch (Exception e) {
			log.error("학생 마일리지 대시보드 조회 실패: {}", stdId, e.getMessage(), e);
			throw new RuntimeException("마일리지 데이터를 불러오는 중 오류가 발생했습니다.", e);
		}
	}
	
	@Transactional
	public boolean convertMileToMoney(
		Integer stdId,
		Integer convertAmount,
		String bankName,
		String bankAccount,
		String depositor
	) {
		try {
			log.info("마일리지 전환 신청 시작: stdId={}, 금액={}P", stdId, convertAmount);
			
			//오늘 날짜
			String today = adminModule.todays_module();
			
			//당일 중복 신청 체크
			boolean alreadyApplied = mileUseRepo.existsByStdIdAndAplyDt(stdId, today);
			if(alreadyApplied) {
				log.info("중복 신청 차단: stdId={}, 날짜={}", stdId, today);
				throw new RuntimeException("오늘 이미 전환 신청을 하셨습니다. 하루에 한 번만 신청 가능합니다.");
			}
			
			//보유 마일리지 확인
			Integer totalEarned = mileHistRepo.getTotalMileageByStdId(stdId);
			Integer totalUsed = mileUseRepo.getTotalUsedMileageByStdId(stdId);
			Integer availableMile = totalEarned - totalUsed;
			
			if (availableMile < convertAmount) {
				throw new RuntimeException("보유 마일리지가 부족합니다. (보유:{} " + availableMile + "P)");
			}
			log.info("보유 마일리지 확인 완료{}", availableMile);
			
			//전환 금액 계산(1000P = 100,000원, 1P = 100원)
			Integer convertedMoney  = (convertAmount / 10) * 100;
			log.info("전환 금액 계산:{}P → {}원", convertAmount, convertedMoney);
			
			
			//학생 정보 조회
			Std_Info std = stdInfoRepo.findById(stdId)
					.orElseThrow(()-> new RuntimeException("학생 정보를 찾을 수 없습니다."));
			
			//대기 상태 코드 조회(code_id  = 81)
			Common_Code code = CommonCodeRopo.findById(81)
					.orElseThrow(() -> new RuntimeException("마일리지 사용 상태 코드를 찾을 수 없습니다."));
		
			//Std_MileageUse 객체 생성
			Std_MileageUse mileUse = Std_MileageUse.builder()
				.stdInfo(std)
				.aplyDt(today)
				.aplyMlgScore(convertAmount)
				.mlgUseCd(code)
				.payMoney(null) //null로 설정(관리자 승인 전)
				.payDt(null)
				.build();
					
			//db저장
			Std_MileageUse savedUse = mileUseRepo.save(mileUse);
			log.info("마일리지 전환 신청 완료: mlgUseId={}, 금액={}",
					savedUse.getMlgUseId(), convertedMoney);
			
			return true;
			
		} catch (Exception e) {
			
	        throw new RuntimeException("마일리지 전환 신청 중 오류가 발생했습니다: " + e.getMessage());
		}
		
	}
	
	
	//마일리지 내역 조회(페이징)
	public Map<String, Object> getMileHistory(
			Integer stdId, int page, int size,
			String type, String status, String startDate, String endDate){
		try {
			Pageable pg = PageRequest.of(page, size);
			Page<Object[]> histPage = mileHistRepo.findMileHistoryByStdIdWithFilters(stdId, type, status, startDate, endDate, pg);
			
			//Object[] => DTO변환
			List<StdMileageHistoryDTO> hists  = histPage.getContent().stream()
					.map(this::convertToDTO)
					.collect(Collectors.toList());
			
			//페이지네이션 정보
			Map<String, Object> paging = new HashMap<>();
			paging.put("currentPage", page);
			paging.put("totalPages", histPage.getTotalPages());
			paging.put("totalElements", histPage.getTotalElements());
			paging.put("size", size);
			paging.put("hasNext", histPage.hasNext());
			paging.put("hasPrevious", histPage.hasPrevious());
			
			Map<String, Object> res = new HashMap<>();
			res.put("histories", hists);
			res.put("pagination", paging);
			
			return res;
		} catch (Exception e) {
			log.error("마일리지 내역 조회 실패: stdId={}, 오류={}", stdId, e.getMessage(), e);
			throw new RuntimeException("마일리지 내역 조회 중 오류가 발생했습니다: " + e.getMessage());
		}
	}
	
	//Object[] -> DTO변환
	private StdMileageHistoryDTO convertToDTO(Object[] row) {
		String mlgDt = (String) row[0];
		
		Integer mlgScore = row[1] != null ? ((Number) row[1]).intValue() : null;
		Integer payMoney = row[2] != null ? ((Number) row[2]).intValue() : null;
		String mlgType = (String) row[3];
		String notes = (String) row[4];
		String statusNm = (String) row[5];
		String statusClass = (String) row[6];
		
		//날짜 포맷 (- => .)
		String mlgDtFmt = mlgDt.replace("-", ".");
		
		//점수/금액 포맷
		String mlgScoreFmt;
		if(payMoney != null && "지급".equals(mlgType)) {
			//전환완료:금액표시
			mlgScoreFmt = String.format("%d원", mlgScore);
		}else {
			//일반 : 포인트 표시
			//// mlgScore > 0 이면 +, mlgScore < 0 이면 이미 -가 붙어있음
			if(mlgScore > 0) {
				mlgScoreFmt = "+" + mlgScore + "P";
			}else {
				//음수는 이미 -붙어있음
				mlgScoreFmt = mlgScore + "P";
			}
		}
		
		return StdMileageHistoryDTO.builder()
				.mlgDt(mlgDt)
				.mlgDtFmt(mlgDtFmt)
				.mlgScore(mlgScore)
				.mlgScoreFmt(mlgScoreFmt)
				.payMoney(payMoney)
				.mlgType(mlgType)
				.notes(notes)
				.statusNm(statusNm)
				.statusClass(statusClass)
				.build();
	}
	
	public StdMileChartDTO getChartData(Integer stdId) {
		try {
			//학생 정보 조회(학과/학년)
			Std_Info std = stdInfoRepo.findById(stdId)
					.orElseThrow(() -> new RuntimeException("학생 정보를 찾을 수 없습니다."));
			Integer deptId = std.getDeptInfo().getDeptId();	//학과 ID
			Integer schYr = std.getSchYr();	//학년
			
			//내 마일리지 계산 (적립-사용)
			Integer totalEarned = mileHistRepo.getTotalMileageByStdId(stdId);
			Integer totalUsed = mileUseRepo.getTotalUsedMileageByStdId(stdId);
			Integer myMile = totalEarned - totalUsed;
			
			//학과 평균 조회
			Integer deptAvg = mileHistRepo.getDeptAvgMile(deptId);
			//학년 평균 조회
			Integer gradeAvg = mileHistRepo.getGradeAvgMile(schYr);
			//전체 평균 조회
			Integer totalAvg = mileHistRepo.getTotalAvgMile();
			//DTO 생성 및 반환
			StdMileChartDTO chartData = StdMileChartDTO.builder()
					.myMile(myMile)
					.deptAvg(deptAvg)
					.gradeAvg(gradeAvg)
					.totalAvg(totalAvg)
					.build();
			return chartData;			
		} catch (Exception e) {
			log.error("마일리지 차트 데이터 조회 실패: stdId={}, 오류={}", stdId, e.getMessage(), e);
			throw new RuntimeException("차트 데이터 조회 중 오류가 발생했습니다: " + e.getMessage());
		}
	}
	
	/*
	 * 최근 마일리지 알림 조회 (최대 2개)
	 * -지급 + 전환 완료
	 */
	public List<StdMileNotiDTO> getNotis(Integer stdId) {
		try {
			// 1. Repository 호출
			List<Object[]> rows = mileHistRepo.findRecentNotis(stdId);
			
			// 2. DTO 변환
			List<StdMileNotiDTO> notis = rows.stream()
				.map(this::toNotiDTO)
				.collect(Collectors.toList());
			return notis;
			
		} catch (Exception e) {
			log.error("마일리지 알림 조회 실패: stdId={}, 오류={}", stdId, e.getMessage(), e);
			return Collections.emptyList();
		}
	}
	
	/*
	 * Object[] → StdMileNotiDTO 변환
	 */
	private StdMileNotiDTO toNotiDTO(Object[] row) {
		try {
			String title = row[0] != null ? row[0].toString() : "";
			Integer score = row[1] != null ? ((Number) row[1]).intValue() : 0;
			String dt = row[2] != null ? row[2].toString() : "";
			String type = row[3] != null ? row[3].toString() : "earned";
			String icon = row[4] != null ? row[4].toString() : "fas fa-bell";
			
			//날짜에 시간 추가 (날짜만 있는 경우 대비)
			if (dt.length() == 10) {
				dt += " 00:00:00";
			}
			
			//scoreText 생성
			String scoreText;
			if ("earned".equals(type)) {
				scoreText = String.format("+%dP", score);
			} else if ("pending".equals(type)) {
				scoreText = String.format("%dP", score);  // 신청은 마이너스
			} else {  // converted
				scoreText = String.format("%,d원", score);
			}
			
			return StdMileNotiDTO.builder()
				.title(title)
				.score(score)
				.dt(dt)
				.type(type)
				.icon(icon)
				.scoreText(scoreText)
				.build();
				
		} catch (Exception e) {
			log.error("알림 DTO 변환 실패: {}", e.getMessage(), e);
			// 기본값 반환
			return StdMileNotiDTO.builder()
				.title("변환 오류")
				.score(0)
				.dt(adminModule.datetime_module())
				.type("earned")
				.icon("fas fa-bell")
				.scoreText("+0P")
				.build();
		}
	}
	
}
