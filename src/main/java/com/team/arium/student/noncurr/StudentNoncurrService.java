package com.team.arium.student.noncurr;

import com.team.arium.admin.noncurr.repository.NcsPrgInfoRepository;
import com.team.arium.admin.admin_module;
import com.team.arium.admin.noncurr.repository.CommonCodeRepository;
import com.team.arium.admin.noncurr.repository.NcsPrgAplyRepository;
import com.team.arium.domain.Ncs_PrgInfo;
import com.team.arium.domain.Ncs_PrgAply;
import com.team.arium.domain.Std_Info;
import com.team.arium.domain.Yn;
import com.team.arium.domain.Common_Code;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.team.arium.admin.noncurr.repository.NcsCclRelRepository;
import com.team.arium.admin.noncurr.repository.NcsCmpInfoRepository;
import com.team.arium.admin.noncurr.repository.StdCclScoreRepository;
import com.team.arium.admin.noncurr.repository.CoreCptInfoRepository;
import com.team.arium.admin.noncurr.repository.DgstfnEvalRepository;
import com.team.arium.admin.noncurr.repository.DgstfnQstRepository;
import com.team.arium.domain.Ncs_CclRel;
import com.team.arium.domain.Ncs_CmpInfo;
import com.team.arium.domain.Std_CclScore;
import com.team.arium.domain.Core_CptInfo;
import com.team.arium.domain.Dgstfn_Eval;
import com.team.arium.domain.Dgstfn_Qst;



@Service
public class StudentNoncurrService {
    
    @Autowired
    private NcsPrgInfoRepository ncsPrgInfoRepository;
    
    @Autowired
    private NcsPrgAplyRepository ncsPrgAplyRepository;
    
    @Autowired
    private CommonCodeRepository commonCodeRepository;
    
    @Autowired
    private StdInfoRepository stdInfoRepository;
    
    @Autowired
    private NcsCclRelRepository ncsCclRelRepository;

    @Autowired
    private StdCclScoreRepository stdCclScoreRepository;

    @Autowired
    private CoreCptInfoRepository coreCptInfoRepository;
    
    @Autowired
    private DgstfnQstRepository dgstfnQstRepository;
    
    @Autowired
    private DgstfnEvalRepository dgstfnEvalRepository;
    
    @Autowired
    private NcsCmpInfoRepository ncsCmpInfoRepository;
    
    @Autowired
    @Qualifier("admin_module")
    private admin_module adminModule;
    
    
    
    /**
     * 학생용 프로그램 목록 조회
     */
    public List<ProgramListDTO> getStudentProgramList(String keyword, Integer stdId) {
        // 1. 활성 프로그램들 조회 (상태 코드 1 = 활성)
        List<Ncs_PrgInfo> programs = ncsPrgInfoRepository.findActiveRecruitmentPrograms(1);
        
        // 2. 키워드 필터링 (필요시)
        if (keyword != null && !keyword.trim().isEmpty()) {
            programs = programs.stream()
                .filter(p -> p.getPrgNm().toLowerCase().contains(keyword.toLowerCase()) ||
                           p.getPrgDesc().toLowerCase().contains(keyword.toLowerCase()))
                .collect(Collectors.toList());
        }
        
        // 3. DTO 변환
        return programs.stream()
            .map(program -> convertToDTO(program, stdId))
            .collect(Collectors.toList());
    }
    
    /**
     * 프로그램 상세 조회
     */
    public ProgramListDTO getProgramDetail(Integer prgId, Integer stdId) {
        Ncs_PrgInfo program = ncsPrgInfoRepository.findById(prgId)
            .orElseThrow(() -> new RuntimeException("프로그램을 찾을 수 없습니다."));
        
        return convertToDTO(program, stdId);
    }
    
    /**
     * ✅ 프로그램 신청 (개선된 버전)
     */
    @Transactional
    public boolean applyProgram(Integer prgId, Integer stdId) {
        try {
            System.out.println("신청 처리 시작 - 프로그램 ID: " + prgId + ", 학생 ID: " + stdId);
            
            // 1. 프로그램 존재 여부 확인
            Ncs_PrgInfo program = ncsPrgInfoRepository.findById(prgId)
                .orElseThrow(() -> new RuntimeException("프로그램을 찾을 수 없습니다."));
            
            // 2. 학생 정보 조회
            Std_Info std = stdInfoRepository.findById(stdId)
            		.orElseThrow(() -> new RuntimeException("학생 정보를 찾을 수 없습니다."));
            //휴학생 체크
            if(std.getStdStatCd() != null) {
            	Integer statusCode = std.getStdStatCd().getCodeId();
            	
            	if(statusCode != null && statusCode == 12) {
            		System.out.println("휴학생 신청 차단 - 학생 ID:"+stdId + ", 상태 코드: " + statusCode);
            		throw new RuntimeException("휴학 중인 학생은 비교과 프로그램 신청이 불가능합니다. 복학 후 신청해주세요.");
            		
            	}
            	System.out.println("학생 상태 확인 완료 - 상태 코드: " + statusCode);
            } else {
                System.out.println("학생 상태 코드가 없습니다 - 학생 ID: " + stdId);
                // 상태 코드가 없는 경우 신청 가능 (또는 정책에 따라 차단 가능)
            }
            
            // 3. 중복 신청 체크 (캐시 무시)
            if (isAlreadyApplied(prgId, stdId)) {
                throw new RuntimeException("이미 신청한 프로그램입니다.");
            }
            
            
            
            // 4. 신청 가능 여부 체크
            int currentApplicants = ncsPrgAplyRepository.countByPrgId(prgId);
            String applicationPeriodStatus = getApplicationPeriodStatus(program);
            
            // 정원 체크
            if (currentApplicants >= program.getMaxCnt()) {
                throw new RuntimeException("모집 정원이 마감되었습니다.");
            }
            
            // 신청 기간 체크
            if ("BEFORE_PERIOD".equals(applicationPeriodStatus)) {
                throw new RuntimeException("아직 신청 기간이 시작되지 않았습니다. 신청 시작일: " + program.getRecruitStDt());
            } else if ("AFTER_PERIOD".equals(applicationPeriodStatus)) {
                throw new RuntimeException("신청 기간이 종료되었습니다. 신청 마감일: " + program.getRecruitEndDt());
            } else if (!"DURING_PERIOD".equals(applicationPeriodStatus)) {
                throw new RuntimeException("현재 신청할 수 없는 프로그램입니다.");
            }
            
            // 5. 신청 정보 저장
            Common_Code aplyStatCode = commonCodeRepository.findById(61)
                .orElseThrow(() -> new RuntimeException("신청 상태 코드를 찾을 수 없습니다."));
            
            Std_Info student = stdInfoRepository.findById(stdId)
                .orElseThrow(() -> new RuntimeException("학생 정보를 찾을 수 없습니다."));
            
            String todayStr = adminModule.todays_module();
            
            Ncs_PrgAply application = Ncs_PrgAply.builder()
                .ncsPrgInfo(program)
                .stdInfo(student)
                .aplyDt(todayStr)
                .aplyStatCd(aplyStatCode)
                .build();
            
            Ncs_PrgAply savedApplication = ncsPrgAplyRepository.save(application);
            
            // ✅ 강제로 플러시하여 즉시 DB 반영
            ncsPrgAplyRepository.flush();
            
            System.out.println("신청 저장 완료 - 신청 ID: " + savedApplication.getAplyId());
            return true;
            
        } catch (Exception e) {
            System.err.println("신청 처리 오류: " + e.getMessage());
            throw new RuntimeException("" + e.getMessage());
        }
    }
    
    /**
     * 학생의 신청 내역 조회 (자동 이수 처리 포함)
     */
    public List<ProgramListDTO> getMyApplications(Integer stdId) {
        // ✅ 자동 이수 처리 먼저 실행
        processCompletedPrograms(stdId);
        
        // ✅ 기존 완료된 만족도 조사 일괄 업데이트
        updateExistingSurveyCompletions(stdId);
        
        // 기존 로직
        List<Ncs_PrgAply> applications = ncsPrgAplyRepository.findByStdId(stdId);
        
        return applications.stream()
            .map(app -> convertToDTO(app.getNcsPrgInfo(), stdId))
            .collect(Collectors.toList());
    }
    
    // ==================== Private 메서드들 ====================
    
    /**
     * 엔티티를 DTO로 변환 (수정된 버전)
     */
    private ProgramListDTO convertToDTO(Ncs_PrgInfo program, Integer stdId) {
        // 현재 신청인원 수 조회
        int currentApplicants = ncsPrgAplyRepository.countByPrgId(program.getPrgId());
        
        // 해당 학생의 신청 여부 확인
        String applicationStatus = isAlreadyApplied(program.getPrgId(), stdId) ? "APPLIED" : "NOT_APPLIED";
        
        // D-day 계산
        int dDay = calculateDDay(program.getRecruitEndDt());
        String dDayText = getDDayText(dDay, currentApplicants, program.getMaxCnt());
        
        // 프로그램 상태 결정
        String programStatus = getProgramStatus(dDay, currentApplicants, program.getMaxCnt());
        
        // ✅ 신청 기간 상태 확인
        String applicationPeriodStatus = getApplicationPeriodStatus(program);
        
        // ✅ 신청 가능 여부 계산
        Boolean canApply = calculateCanApply(program, currentApplicants, applicationStatus);
        
        // ✅ 취소 가능 여부 및 이유 계산
        CancelInfo cancelInfo = calculateCancelInfo(program, applicationStatus);

        // ✅ 만족도 조사 상태 계산
        String satisfactionStatus = calculateSatisfactionStatus(program, stdId);
        boolean surveyCompleted = "completed".equals(satisfactionStatus);

        
        return ProgramListDTO.builder()
            .prgId(program.getPrgId())
            .prgCd(program.getPrgCd())
            .prgNm(program.getPrgNm())
            .prgDesc(program.getPrgDesc())
            .recruitStDt(program.getRecruitStDt())
            .recruitEndDt(program.getRecruitEndDt())
            .prgStDt(program.getPrgStDt())
            .prgEndDt(program.getPrgEndDt())
            .maxCnt(program.getMaxCnt())
            .mlgDefScore(program.getMlgDefScore())
            .prgDept(program.getPrgDept())
            .prgTel(program.getPrgTel())
            .currentApplicants(currentApplicants)
            .applicationStatus(applicationStatus)
            .programStatus(programStatus)
            .dDay(dDay)
            .dDayText(dDayText)
            .imageUrl(getImageUrl(program))
            .canApply(canApply)
            .canCancel(cancelInfo.canCancel)
            .cancelReasonMessage(cancelInfo.reasonMessage)
            .applicationPeriodStatus(applicationPeriodStatus)  // ✅ 신청 기간 상태 추가
            .surveyCompleted(surveyCompleted)           // ✅ 추가
            .satisfactionStatus(satisfactionStatus)     // ✅ 추가
            .build();
    }
    
    
    
    
    
    /**
     * ✅ 취소 가능 여부 및 이유 계산
     */
    private CancelInfo calculateCancelInfo(Ncs_PrgInfo program, String applicationStatus) {
        try {
            // 신청하지 않은 경우
            if (!"APPLIED".equals(applicationStatus)) {
                return new CancelInfo(false, "");
            }
            
            String applicationPeriodStatus = getApplicationPeriodStatus(program);
            
            // 신청기간 중: 취소 가능
            if ("DURING_PERIOD".equals(applicationPeriodStatus)) {
                return new CancelInfo(true, "");
            }
            
            // 신청기간 시작 전: 이론적으로는 취소 가능하지만 실제로는 발생하지 않아야 함
            if ("BEFORE_PERIOD".equals(applicationPeriodStatus)) {
                return new CancelInfo(false, "(신청기간 시작 전)");
            }
            
            // 신청기간 종료 후: 운영기간 시작 여부에 따라 구분
            if ("AFTER_PERIOD".equals(applicationPeriodStatus)) {
                LocalDate today = getCurrentDate();
                LocalDate programStartDate = LocalDate.parse(program.getPrgStDt());
                
                if (today.isBefore(programStartDate)) {
                    // 신청기간은 끝났지만 운영기간은 시작되지 않음
                    return new CancelInfo(false, "(신청기간 종료로 취소 불가)");
                } else {
                    // 운영기간 시작됨
                    return new CancelInfo(false, "(운영기간 시작으로 취소 불가)");
                }
            }
            
            // 기타 경우 (예상하지 못한 상황)
            return new CancelInfo(false, "(취소 불가)");
            
        } catch (Exception e) {
            System.err.println("취소 가능 여부 계산 오류: " + e.getMessage());
            return new CancelInfo(false, "(오류로 인한 취소 불가)");
        }
    }
    
    /**
     * ✅ 취소 정보를 담는 내부 클래스
     */
    private static class CancelInfo {
        final boolean canCancel;
        final String reasonMessage;
        
        CancelInfo(boolean canCancel, String reasonMessage) {
            this.canCancel = canCancel;
            this.reasonMessage = reasonMessage;
        }
    }
    
    /**
     * ✅ 신청 가능 여부 계산
     */
    private Boolean calculateCanApply(Ncs_PrgInfo program, int currentApplicants, String applicationStatus) {
        try {
            // 1. 이미 신청한 경우 신청 불가
            if ("APPLIED".equals(applicationStatus)) {
                return false;
            }
            
            // 2. 정원 마감시 신청 불가
            if (currentApplicants >= program.getMaxCnt()) {
                return false;
            }
            
            // 3. 신청기간이 아니면 신청 불가
            LocalDate today = getCurrentDate();
            LocalDate recruitStartDate = LocalDate.parse(program.getRecruitStDt());
            LocalDate recruitEndDate = LocalDate.parse(program.getRecruitEndDt());
            
            boolean withinRecruitPeriod = !today.isBefore(recruitStartDate) && !today.isAfter(recruitEndDate);
            
            return withinRecruitPeriod;
            
        } catch (Exception e) {
            System.err.println("신청 가능 여부 계산 오류: " + e.getMessage());
            return false;
        }
    }

    /**
     * ✅ 신청 상태 구분 (신청 전/중/후 구분)
     */
    private String getApplicationPeriodStatus(Ncs_PrgInfo program) {
        try {
            LocalDate today = getCurrentDate();
            LocalDate recruitStartDate = LocalDate.parse(program.getRecruitStDt());
            LocalDate recruitEndDate = LocalDate.parse(program.getRecruitEndDt());
            
            if (today.isBefore(recruitStartDate)) {
                return "BEFORE_PERIOD";  // 신청기간 시작 전
            } else if (!today.isAfter(recruitEndDate)) {
                return "DURING_PERIOD";  // 신청기간 중
            } else {
                return "AFTER_PERIOD";   // 신청기간 종료
            }
            
        } catch (Exception e) {
            System.err.println("신청 기간 상태 계산 오류: " + e.getMessage());
            return "UNKNOWN";
        }
    }
    
    
    /**
     * 신청취소 가능 여부 계산 (신청기간 내에서만 취소 가능)
     */
    private Boolean calculateCanCancel(Ncs_PrgInfo program, String applicationStatus) {
        // 신청하지 않은 경우 취소 불가
        if (!"APPLIED".equals(applicationStatus)) {
            return false;
        }
        
        try {
            LocalDate today = getCurrentDate();
            
            // ✅ 신청기간 확인 (운영기간이 아닌 신청기간)
            LocalDate recruitStartDate = LocalDate.parse(program.getRecruitStDt());  // 신청 시작일
            LocalDate recruitEndDate = LocalDate.parse(program.getRecruitEndDt());    // 신청 마감일
            
            // 현재 날짜가 신청기간 내에 있어야 취소 가능
            boolean withinRecruitPeriod = !today.isBefore(recruitStartDate) && !today.isAfter(recruitEndDate);
            
            System.out.println("취소 가능 여부 계산:");
            System.out.println("  - 오늘 날짜: " + today);
            System.out.println("  - 신청 시작일: " + recruitStartDate);
            System.out.println("  - 신청 마감일: " + recruitEndDate);
            System.out.println("  - 신청기간 내: " + withinRecruitPeriod);
            System.out.println("  - 취소 가능: " + withinRecruitPeriod);
            
            return withinRecruitPeriod;
            
        } catch (Exception e) {
            System.err.println("취소 가능 여부 계산 오류: " + e.getMessage());
            return false; // 오류 시 취소 불가
        }
    }
    
    /**
     * ✅ 중복 신청 체크 (캐시 무시 버전)
     */
    private boolean isAlreadyApplied(Integer prgId, Integer stdId) {
        try {
            // ✅ 캐시를 무시하고 직접 DB 조회
            List<Ncs_PrgAply> applications = ncsPrgAplyRepository.findByPrgIdAndStdIdWithRefresh(prgId, stdId);
            boolean isApplied = !applications.isEmpty();
            
            System.out.println("중복 신청 체크 - 프로그램 ID: " + prgId + ", 학생 ID: " + stdId + ", 결과: " + isApplied);
            return isApplied;
            
        } catch (Exception e) {
            System.err.println("중복 신청 체크 오류: " + e.getMessage());
            // 안전을 위해 true 반환 (신청 차단)
            return true;
        }
    }
    
    /**
     * 모집 기간 체크
     */
    private boolean isRecruitmentPeriod(Ncs_PrgInfo program) {
        LocalDate today = getCurrentDate();
        LocalDate startDate = LocalDate.parse(program.getRecruitStDt());
        LocalDate endDate = LocalDate.parse(program.getRecruitEndDt());
        
        return !today.isBefore(startDate) && !today.isAfter(endDate);
    }
    
    /**
     * D-day 계산 (안전한 버전)
     */
    private int calculateDDay(String recruitEndDt) {
        try {
            if (recruitEndDt == null || recruitEndDt.trim().isEmpty()) {
                return -999; // 기본값
            }
            
            LocalDate today = getCurrentDate();
            LocalDate endDate = LocalDate.parse(recruitEndDt.trim());
            int dDay = (int) ChronoUnit.DAYS.between(today, endDate);
            
            System.out.println("Today: " + today + ", EndDate: " + endDate + ", D-Day: " + dDay);
            return dDay;
        } catch (Exception e) {
            System.err.println("D-day 계산 오류: " + e.getMessage());
            return -999; // 오류 시 기본값
        }
    }
    
    /**
     * D-day 텍스트 생성 (안전한 버전)
     */
    private String getDDayText(int dDay, int currentApplicants, int maxCnt) {
        try {
            if (currentApplicants >= maxCnt) {
                return "마감";
            }
            if (dDay < 0) {
                return "마감";
            }
            if (dDay == 0) {
                return "D-DAY";
            }
            return "D-" + dDay;
        } catch (Exception e) {
            System.err.println("D-day 텍스트 생성 오류: " + e.getMessage());
            return "D-?"; // 오류 시 기본값
        }
    }
    
    /**
     * 프로그램 상태 결정
     */
    private String getProgramStatus(int dDay, int currentApplicants, int maxCnt) {
        if (currentApplicants >= maxCnt || dDay < 0) {
            return "closed";
        }
        if (dDay <= 7) {
            return "closing";
        }
        return "available";
    }
    
    /**
     * 이미지 URL 생성 (디버깅 추가)
     */
    private String getImageUrl(Ncs_PrgInfo program) {
        if (program.getComFile() != null) {
            String imageUrl = "/uploads/noncurr/images/" + program.getComFile().getSaveFileName();
            System.out.println("이미지 URL 생성: " + imageUrl);
            System.out.println("   - 원본 파일명: " + program.getComFile().getOrgFileName());
            System.out.println("   - 저장 파일명: " + program.getComFile().getSaveFileName());
            return imageUrl;
        }
        return null;
    }
    
    
    /**
     * 페이징된 프로그램 목록 조회 (안전한 null 처리)
     */
    public PagedProgramResponseDTO searchProgramsWithPaging(String keyword, String dept, 
                                                           String mileageFilter, String statusFilter, 
                                                           String sortBy, Integer stdId, int page, int size) {
    	System.out.println("sortBy 값 확인: [" + sortBy + "]");
        // 1. 페이징 객체 생성
        Sort sort = createSort(sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        
        // 2. 페이징된 프로그램 조회
        Page<Ncs_PrgInfo> programPage = ncsPrgInfoRepository.findByKeywordAndDept(
                keyword, dept, pageable
                );
        
        
        // 3. DTO 변환
        List<ProgramListDTO> dtoList = programPage.getContent().stream()
            .map(program -> convertToDTO(program, stdId))
            .collect(Collectors.toList());
        
        if (mileageFilter != null && !mileageFilter.trim().isEmpty()) {
            dtoList = filterByMileage(dtoList, mileageFilter);
        }
        
        if (statusFilter != null && !statusFilter.trim().isEmpty()) {
            dtoList = filterByStatus(dtoList, statusFilter);
        }
        
        
        //메모리 필터 이후 명시적 재정렬 (DB 정렬이 필터 후 깨질 수 있으므로)
        if (sortBy != null && sortBy.trim().equalsIgnoreCase("deadline")) {
            // 마감임박순: dDay 오름차순, 단 마감(closed)은 맨 뒤로
            dtoList = dtoList.stream()
                .sorted((a, b) -> {
                    boolean aClosed = "closed".equals(a.getProgramStatus());
                    boolean bClosed = "closed".equals(b.getProgramStatus());
                    if (aClosed && !bClosed) return 1;   // a가 마감이면 뒤로
                    if (!aClosed && bClosed) return -1;  // b가 마감이면 앞으로
                    return Integer.compare(a.getDDay(), b.getDDay()); // 둘 다 아니면 dDay 오름차순
                })
                .collect(Collectors.toList());
        } else if ("mileage".equals(sortBy)) {
            // 마일리지순: 높은 것부터
            dtoList = dtoList.stream()
                .sorted((a, b) -> Integer.compare(b.getMlgDefScore(), a.getMlgDefScore()))
                .collect(Collectors.toList());
        }
        
        
        // 5. 페이징 응답 DTO 생성
        return PagedProgramResponseDTO.builder()
            .programs(dtoList)
            .currentPage(page)
            .totalPages(programPage.getTotalPages())
            .totalElements(programPage.getTotalElements())
            .size(size)
            .hasNext(programPage.hasNext())
            .hasPrevious(programPage.hasPrevious())
            .isFirst(programPage.isFirst())
            .isLast(programPage.isLast())
            .build();
    }

    private Sort createSort(String sortBy) {
        // null 체크 추가
        if (sortBy == null || sortBy.trim().isEmpty()) {
            return Sort.by(Sort.Direction.DESC, "regDt"); // 기본값: 최신순
        }
        
        switch (sortBy.trim()) {  // trim() 추가로 공백 제거
            case "mileage":
                return Sort.by(Sort.Direction.DESC, "mlgDefScore");
            case "deadline":
                return Sort.by(Sort.Direction.DESC, "regDt");
            default: // 최신순
                return Sort.by(Sort.Direction.DESC, "regDt");
        }
    }
    
    /**
     * 프로그램 검색 (페이징 없는 버전)
     */
    public List<ProgramListDTO> searchPrograms(String keyword, String dept, 
                                             String mileageFilter, String statusFilter, 
                                             String sortBy, Integer stdId) {
        // 1. 기본 목록 조회
        List<Ncs_PrgInfo> programs = ncsPrgInfoRepository.findActiveRecruitmentPrograms(1);
        
        // 2. 키워드 필터링
        if (keyword != null && !keyword.trim().isEmpty()) {
            programs = programs.stream()
                .filter(p -> p.getPrgNm().toLowerCase().contains(keyword.toLowerCase()) ||
                           p.getPrgDesc().toLowerCase().contains(keyword.toLowerCase()))
                .collect(Collectors.toList());
        }
        
        // 3. 운영부서 필터링
        if (dept != null && !dept.isEmpty()) {
            programs = programs.stream()
                .filter(p -> p.getPrgDept().equals(dept))
                .collect(Collectors.toList());
        }
        
        // 4. DTO 변환
        List<ProgramListDTO> dtoList = programs.stream()
            .map(program -> convertToDTO(program, stdId))
            .collect(Collectors.toList());
        
        // 5. 마일리지 필터링
        if (mileageFilter != null && !mileageFilter.isEmpty()) {
            dtoList = filterByMileage(dtoList, mileageFilter);
        }
        
        // 6. 상태 필터링
        if (statusFilter != null && !statusFilter.isEmpty()) {
            dtoList = filterByStatus(dtoList, statusFilter);
        }
        
        // 7. 정렬
        if (sortBy != null && !sortBy.isEmpty()) {
            dtoList = sortPrograms(dtoList, sortBy);
        }
        
        return dtoList;
    }

    /**
     * 마일리지 필터링
     */
    private List<ProgramListDTO> filterByMileage(List<ProgramListDTO> programs, String filter) {
        return programs.stream()
            .filter(p -> {
                switch (filter) {
                    case "low": return p.getMlgDefScore() <= 30;
                    case "mid": return p.getMlgDefScore() > 30 && p.getMlgDefScore() <= 50;
                    case "high": return p.getMlgDefScore() > 50;
                    default: return true;
                }
            })
            .collect(Collectors.toList());
    }

    /**
     * 상태 필터링
     */
    private List<ProgramListDTO> filterByStatus(List<ProgramListDTO> programs, String filter) {
        return programs.stream()
            .filter(p -> {
                switch (filter) {
                    case "available":
                        // "신청가능" 필터 = closed가 아닌 것 전부 (available + closing 둘 다 통과)
                        return "available".equals(p.getProgramStatus()) || "closing".equals(p.getProgramStatus());
                    case "closing":
                        return "closing".equals(p.getProgramStatus());
                    case "closed":
                        return "closed".equals(p.getProgramStatus());
                    default:
                        return true;
                }
            })
            .collect(Collectors.toList());
    }

    /**
     * 프로그램 정렬
     */
    private List<ProgramListDTO> sortPrograms(List<ProgramListDTO> programs, String sortBy) {
        switch (sortBy) {
            case "mileage":
                return programs.stream()
                    .sorted((a, b) -> b.getMlgDefScore().compareTo(a.getMlgDefScore()))
                    .collect(Collectors.toList());
            case "deadline":
                return programs.stream()
                    .sorted((a, b) -> a.getDDay().compareTo(b.getDDay()))
                    .collect(Collectors.toList());
            default: // 최신순
                return programs;
        }
    }

    /**
     * ✅ 프로그램 신청 취소 (개선된 버전)
     */
    @Transactional
    public boolean cancelApplication(Integer prgId, Integer stdId) {
        try {
            List<Ncs_PrgAply> applications = ncsPrgAplyRepository.findByPrgIdAndStdId(prgId, stdId);
            
            if (applications.isEmpty()) {
                throw new RuntimeException("신청 내역을 찾을 수 없습니다.");
            }
            
            // 신청 삭제
            Ncs_PrgAply application = applications.get(0);
            ncsPrgAplyRepository.delete(application);
            
            // ✅ 강제로 플러시하여 즉시 DB 반영
            ncsPrgAplyRepository.flush();
            
            System.out.println("신청 취소 완료 - 프로그램 ID: " + prgId + ", 학생 ID: " + stdId);
            return true;
            
        } catch (Exception e) {
            System.err.println("신청 취소 오류: " + e.getMessage());
            throw new RuntimeException("신청 취소 중 오류가 발생했습니다: " + e.getMessage());
        }
    }
    
    public CompetencyChartDTO getCompetencyChartData(Integer prgId, Integer stdId) {
        try {
            // 1. 상위 핵심역량만 조회 (up_ccl_id가 null인 것들)
            List<Core_CptInfo> mainCompetencies = coreCptInfoRepository.findByUpCclIdIsNull();
            
            // 2. 프로그램 핵심역량 조회
            List<Ncs_CclRel> programCompetencies = ncsCclRelRepository.findByPrgId(prgId);
            
            // 3. 학생 핵심역량 조회
            List<Std_CclScore> studentScores = stdCclScoreRepository.findByStdId(stdId);
            
            // 4. 프로그램 데이터 구성
            List<CompetencyData> programData = mainCompetencies.stream()
                .map(comp -> {
                    Integer score = programCompetencies.stream()
                        .filter(rel -> rel.getCclId().equals(comp.getCclId()))
                        .map(Ncs_CclRel::getCclScore)
                        .findFirst()
                        .orElse(0);
                    
                    return CompetencyData.builder()
                        .competencyName(comp.getCclNm())
                        .score(score)
                        .build();
                })
                .collect(Collectors.toList());
            
            // 5. 학생 데이터 구성
            List<StudentCompetencyData> studentData = mainCompetencies.stream()
                .map(comp -> {
                    Integer currentScore = studentScores.stream()
                        .filter(score -> score.getCoreCptInfo().getCclId().equals(comp.getCclId()))
                        .mapToInt(Std_CclScore::getScore)
                        .sum();
                    
                    Integer programScore = programCompetencies.stream()
                        .filter(rel -> rel.getCclId().equals(comp.getCclId()))
                        .map(Ncs_CclRel::getCclScore)
                        .findFirst()
                        .orElse(0);
                    
                    return StudentCompetencyData.builder()
                        .competencyName(comp.getCclNm())
                        .currentScore(currentScore)
                        .programScore(programScore)
                        .build();
                })
                .collect(Collectors.toList());
            
            return CompetencyChartDTO.builder()
                .programCompetencies(programData)
                .studentCompetencies(studentData)
                .build();
                
        } catch (Exception e) {
            e.printStackTrace();
            // 기본값 반환
            return CompetencyChartDTO.builder()
                .programCompetencies(new ArrayList<>())
                .studentCompetencies(new ArrayList<>())
                .build();
        }
    }
    
    /**
     * 운영기간이 끝난 프로그램의 자동 이수 처리
     */
    @Transactional
    public void processCompletedPrograms(Integer stdId) {
        try {
            // 1. 해당 학생의 모든 신청 내역 조회
            List<Ncs_PrgAply> applications = ncsPrgAplyRepository.findByStdId(stdId);
            
            LocalDate today = getCurrentDate();
            
            for (Ncs_PrgAply application : applications) {
                Ncs_PrgInfo program = application.getNcsPrgInfo();
                LocalDate programEndDate = LocalDate.parse(program.getPrgEndDt());
                
                // 2. 운영기간이 끝났는지 확인
                if (today.isAfter(programEndDate)) {
                    // 3. 이미 이수 정보가 있는지 확인
                    Optional<Ncs_CmpInfo> existingCompletion = ncsCmpInfoRepository.findByNcsPrgAply_AplyId(application.getAplyId());
                    
                    if (existingCompletion.isEmpty()) {
                        // 4. 이수 정보 자동 생성
                        Ncs_CmpInfo completion = Ncs_CmpInfo.builder()
                            .ncsPrgAply(application)
                            .ncsPrgInfo(program)
                            .stdInfo(application.getStdInfo())
                            .cmpYn(Yn.Y)  // ✅ ENUM 사용
                            .surveyYn(Yn.N)  // ✅ ENUM 사용
                            .build();
                        
                        ncsCmpInfoRepository.save(completion);
                        
                        System.out.println("자동 이수 처리 완료 - 프로그램: " + program.getPrgNm() + ", 학생 ID: " + stdId);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("자동 이수 처리 오류: " + e.getMessage());
        }
    }
    
    /**
     * 만족도 조사 완료 시 이수 정보 업데이트
     */
    @Transactional
    public void updateSurveyCompletion(Integer prgId, Integer stdId) {
        try {
            System.out.println("=== 만족도 조사 완료 플래그 업데이트 ===");
            System.out.println("프로그램 ID: " + prgId + ", 학생 ID: " + stdId);
        	
            // 해당 프로그램과 학생의 이수 정보 찾기
            List<Ncs_PrgAply> applications = ncsPrgAplyRepository.findByPrgIdAndStdId(prgId, stdId);
            System.out.println("신청 내역 개수: " + applications.size());
            
            if (!applications.isEmpty()) {
                Ncs_PrgAply application = applications.get(0);
                System.out.println("신청 ID: " + application.getAplyId());
                
                Optional<Ncs_CmpInfo> completion = ncsCmpInfoRepository.findByNcsPrgAply_AplyId(application.getAplyId());
                System.out.println("이수 정보 존재 여부: " + completion.isPresent());
                
                if (completion.isPresent()) {
                    Ncs_CmpInfo cmpInfo = completion.get();
                    System.out.println("기존 survey_yn: " + cmpInfo.getSurveyYn());
                    
                    cmpInfo.setSurveyYn(Yn.Y);  // 만족도 조사 완료로 업데이트
                    Ncs_CmpInfo saved = ncsCmpInfoRepository.save(cmpInfo);
                    
                    System.out.println("업데이트 후 survey_yn: " + saved.getSurveyYn());
                    System.out.println("만족도 조사 완료 업데이트 성공 - 프로그램: " + prgId + ", 학생: " + stdId);
                } else {
                    System.out.println("이수 정보를 찾을 수 없습니다!");
                }
            } else {
                System.out.println("신청 내역을 찾을 수 없습니다!");
            }
            System.out.println("==========================================");
        } catch (Exception e) {
            System.err.println("만족도 조사 완료 업데이트 오류: " + e.getMessage());
            e.printStackTrace();
        }
    }

    
    /**
     * 기존 완료된 만족도 조사에 대해 survey_yn 일괄 업데이트
     */
    @Transactional
    public void updateExistingSurveyCompletions(Integer stdId) {
        try {
            System.out.println("=== 기존 만족도 조사 완료 상태 일괄 업데이트 ===");
            
            // 해당 학생의 모든 이수 정보 조회
            List<Ncs_CmpInfo> completions = ncsCmpInfoRepository.findByStdId(stdId);
            
            for (Ncs_CmpInfo completion : completions) {
                Integer prgId = completion.getNcsPrgInfo().getPrgId();
                
                // dgstfn_eval에서 만족도 조사 완료 여부 확인
                boolean surveyExists = dgstfnEvalRepository.existsByPrgIdAndStdId(prgId, stdId);
                
                if (surveyExists && completion.getSurveyYn() == Yn.N) {
                    // 만족도 조사는 완료했는데 survey_yn이 N인 경우 업데이트
                    completion.setSurveyYn(Yn.Y);
                    ncsCmpInfoRepository.save(completion);
                    
                    System.out.println("프로그램 " + prgId + " survey_yn을 Y로 업데이트");
                }
            }
            
            System.out.println("=== 일괄 업데이트 완료 ===");
        } catch (Exception e) {
            System.err.println("기존 만족도 조사 일괄 업데이트 오류: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private LocalDate getCurrentDate() {
        try {
            String todayStr = adminModule.todays_module();
            return LocalDate.parse(todayStr);
        } catch (Exception e) {
            System.err.println("현재 날짜 조회 오류: " + e.getMessage());
            return getCurrentDate(); // fallback
        }
    }
    
    /**
     * 만족도 조사 문항 조회 (섹션별로 구분)
     */
    public SatisfactionQuestionResponseDTO getSatisfactionQuestions(Integer prgId) {
        try {
            // 1. 프로그램 정보 조회
            Ncs_PrgInfo program = ncsPrgInfoRepository.findById(prgId)
                .orElseThrow(() -> new RuntimeException("프로그램을 찾을 수 없습니다."));
            
            // 2. 기본 만족도 조사 문항들 조회 (survey_id = 1)
            List<Dgstfn_Qst> allQuestions = dgstfnQstRepository.findBySurveyIdOrderBySurOrd(1);
            
            // 3. 섹션별로 분류
            List<QuestionDTO> section1 = allQuestions.stream()
                .filter(q -> q.getSurOrd() >= 1 && q.getSurOrd() <= 5)
                .map(this::convertToQuestionDTO)
                .collect(Collectors.toList());
                
            List<QuestionDTO> section2 = allQuestions.stream()
                .filter(q -> q.getSurOrd() >= 6 && q.getSurOrd() <= 8)
                .map(this::convertToQuestionDTO)
                .collect(Collectors.toList());
                
            List<QuestionDTO> section3 = allQuestions.stream()
                .filter(q -> q.getSurOrd() >= 9 && q.getSurOrd() <= 11)
                .map(this::convertToQuestionDTO)
                .collect(Collectors.toList());
            
            return SatisfactionQuestionResponseDTO.builder()
                .prgId(prgId)
                .prgNm(program.getPrgNm())
                .section1Questions(section1)
                .section2Questions(section2)
                .section3Questions(section3)
                .build();
                
        } catch (Exception e) {
            System.err.println("만족도 조사 문항 조회 오류: " + e.getMessage());
            throw new RuntimeException("만족도 조사 문항을 가져오는 중 오류가 발생했습니다.");
        }
    }

    private QuestionDTO convertToQuestionDTO(Dgstfn_Qst question) {
        return QuestionDTO.builder()
            .surId(question.getSurId())
            .surContent(question.getSurContent())
            .surOrd(question.getSurOrd())
            .build();
    }
    
    /**
     * 학생 기본 정보 조회 (만족도 조사용)
     */
    public StudentBasicInfoDTO getStudentBasicInfo(Integer stdId) {
        try {
            Std_Info student = stdInfoRepository.findById(stdId)
                .orElseThrow(() -> new RuntimeException("학생 정보를 찾을 수 없습니다."));
            
            // 학년 표시 (1->1학년, 2->2학년...)
            String gradeDisplay = student.getSchYr() + "학년";
            
            // 학과명 가져오기
            String deptName = student.getDeptInfo() != null ? student.getDeptInfo().getDeptNm() : "학과 미정";
            
            return StudentBasicInfoDTO.builder()
                .stdId(student.getStdId())
                .stdNm(student.getStdNm())
                .stdGender(student.getStdGender())
                .schYr(gradeDisplay)
                .deptNm(deptName)
                .build();
                
        } catch (Exception e) {
            System.err.println("학생 정보 조회 오류: " + e.getMessage());
            throw new RuntimeException("학생 정보를 조회하는 중 오류가 발생했습니다.");
        }
    }
    
    /**
     * 만족도 조사 결과 저장
     */
    @Transactional
    public boolean submitSatisfactionSurvey(Integer prgId, Integer stdId, Map<String, Integer> surveyData) {
        try {
            System.out.println("만족도 조사 저장 시작 - 프로그램 ID: " + prgId + ", 학생 ID: " + stdId);
            
            // 1. 프로그램과 학생 정보 조회
            Ncs_PrgInfo program = ncsPrgInfoRepository.findById(prgId)
                .orElseThrow(() -> new RuntimeException("프로그램을 찾을 수 없습니다."));
            
            Std_Info student = stdInfoRepository.findById(stdId)
                .orElseThrow(() -> new RuntimeException("학생 정보를 찾을 수 없습니다."));
            
            // 2. 만족도 조사 실시 ID 생성 (예: S + 현재시간 + 랜덤숫자)
            String surEvalId = generateSurveyEvalId();
            
            // 3. 각 문항별 응답 저장
            List<Dgstfn_Eval> evaluations = new ArrayList<>();
            
            for (Map.Entry<String, Integer> entry : surveyData.entrySet()) {
                String questionKey = entry.getKey(); // 예: "section1_1", "section2_6" 등
                Integer score = entry.getValue();
                
                // 문항 ID 추출 (section1_1 -> 1, section2_6 -> 6)
                Integer surId = extractSurIdFromKey(questionKey);
                
                if (surId != null) {
                    // 문항 정보 조회
                    Dgstfn_Qst question = dgstfnQstRepository.findById(surId)
                        .orElseThrow(() -> new RuntimeException("문항을 찾을 수 없습니다: " + surId));
                    
                    // 만족도 조사 응답 생성
                    Dgstfn_Eval evaluation = Dgstfn_Eval.builder()
                        .surEvalId(surEvalId)
                        .ncsPrgInfo(program)
                        .stdInfo(student)
                        .dgstfnQst(question)
                        .ansScore(score)
                        .build();
                    
                    evaluations.add(evaluation);
                }
            }
            
            // 4. 일괄 저장
            dgstfnEvalRepository.saveAll(evaluations);
            
            // ✅ 만족도 조사 완료 후 이수 정보 업데이트
            updateSurveyCompletion(prgId, stdId);
            
            // ✅ 만족도 조사 완료 후 이수 정보 업데이트
            System.out.println("만족도 조사 완료 플래그 업데이트 시작");
            updateSurveyCompletion(prgId, stdId);
            System.out.println("만족도 조사 완료 플래그 업데이트 완료");
            
            System.out.println("만족도 조사 저장 완료 - 총 " + evaluations.size() + "개 응답 저장");
            return true;
            
        } catch (Exception e) {
            System.err.println("만족도 조사 저장 오류: " + e.getMessage());
            throw new RuntimeException("만족도 조사 저장 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    /**
     * 만족도 조사 실시 ID 생성
     */
    private String generateSurveyEvalId() {
        // 현재 시간 기반으로 고유 ID 생성 (예: S25070801)
        String todayStr = adminModule.todays_module().replace("-", "").substring(2); // 250708
        int randomNum = (int)(Math.random() * 100); // 00-99
        return String.format("S%s%02d", todayStr, randomNum);
    }

    /**
     * 문항 키에서 실제 문항 ID 추출
     */
    private Integer extractSurIdFromKey(String questionKey) {
        try {
            // section1_1 -> 1, section2_6 -> 6, section3_9 -> 9
            String[] parts = questionKey.split("_");
            if (parts.length == 2) {
                return Integer.parseInt(parts[1]);
            }
            return null;
        } catch (Exception e) {
            System.err.println("문항 키 파싱 오류: " + questionKey);
            return null;
        }
    }
    
    /**
     * 만족도 조사 완료 여부 확인 (디버깅 버전)
     */
    private boolean isSurveyCompleted(Integer prgId, Integer stdId) {
        try {
            // 응답 개수 확인
            Long count = dgstfnEvalRepository.countByPrgIdAndStdId(prgId, stdId);
            boolean exists = dgstfnEvalRepository.existsByPrgIdAndStdId(prgId, stdId);
           
            
            return exists;
        } catch (Exception e) {
            System.err.println("만족도 조사 완료 여부 확인 오류: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * ✅ 만족도 조사 상태 계산 (디버깅 버전)
     */
    private String calculateSatisfactionStatus(Ncs_PrgInfo program, Integer stdId) {
        try {
            
            // 1. 운영기간이 끝났는지 확인
            LocalDate today = getCurrentDate();
            LocalDate programEndDate = LocalDate.parse(program.getPrgEndDt());
            
            
            if (today.isBefore(programEndDate) || today.isEqual(programEndDate)) {
                System.out.println("결과: none (운영기간 중)");
                return "none";
            }
            
            // 2. 운영기간이 끝났으면 만족도 조사 완료 여부 확인
            boolean surveyCompleted = isSurveyCompleted(program.getPrgId(), stdId);
            
            if (surveyCompleted) {
                return "completed";
            } else {
                return "pending";
            }
            
        } catch (Exception e) {
            System.err.println("만족도 조사 상태 계산 오류: " + e.getMessage());
            e.printStackTrace();
            return "none";
        }
    }
    
}