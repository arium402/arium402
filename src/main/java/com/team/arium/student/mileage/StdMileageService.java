package com.team.arium.student.mileage;

import java.time.LocalDate;

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
	
	
}
