package com.team.arium.student.noncurr;

import com.team.arium.admin.noncurr.repository.NcsPrgInfoRepository;
import com.team.arium.admin.noncurr.repository.NcsPrgAplyRepository;
import com.team.arium.domain.Ncs_PrgInfo;
import com.team.arium.domain.Ncs_PrgAply;
import com.team.arium.domain.Std_Info;
import com.team.arium.domain.Common_Code;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@Service
public class StudentNoncurrService {
    
    @Autowired
    private NcsPrgInfoRepository ncsPrgInfoRepository;
    
    @Autowired
    private NcsPrgAplyRepository ncsPrgAplyRepository;
    
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
     * 프로그램 신청
     */
    @Transactional
    public boolean applyProgram(Integer prgId, Integer stdId) {
        try {
            // 1. 중복 신청 체크
            if (isAlreadyApplied(prgId, stdId)) {
                throw new RuntimeException("이미 신청한 프로그램입니다.");
            }
            
            // 2. 프로그램 존재 여부 및 신청 가능 여부 체크
            Ncs_PrgInfo program = ncsPrgInfoRepository.findById(prgId)
                .orElseThrow(() -> new RuntimeException("프로그램을 찾을 수 없습니다."));
            
            // 3. 모집 기간 체크
            if (!isRecruitmentPeriod(program)) {
                throw new RuntimeException("신청 기간이 아닙니다.");
            }
            
            // 4. 정원 체크
            int currentApplicants = ncsPrgAplyRepository.countByPrgId(prgId);
            if (currentApplicants >= program.getMaxCnt()) {
                throw new RuntimeException("모집 정원이 마감되었습니다.");
            }
            
            // 5. 신청 정보 저장
            Ncs_PrgAply application = Ncs_PrgAply.builder()
                .ncsPrgInfo(program)
                .stdInfo(Std_Info.builder().stdId(stdId).build())
                .aplyDt(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                .aplyStatCd(Common_Code.builder().codeId(1).build()) // 신청완료 상태
                .build();
            
            ncsPrgAplyRepository.save(application);
            return true;
            
        } catch (Exception e) {
            throw new RuntimeException("신청 처리 중 오류가 발생했습니다: " + e.getMessage());
        }
    }
    
    /**
     * 학생의 신청 내역 조회
     */
    public List<ProgramListDTO> getMyApplications(Integer stdId) {
        // 해당 학생의 신청 정보들 조회
        List<Ncs_PrgAply> applications = ncsPrgAplyRepository.findByPrgIdAndStdId(null, stdId);
        
        return applications.stream()
            .map(app -> convertToDTO(app.getNcsPrgInfo(), stdId))
            .collect(Collectors.toList());
    }
    
    // ==================== Private 메서드들 ====================
    
    /**
     * 엔티티를 DTO로 변환
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
        
        System.out.println("Program: " + program.getPrgNm());
        System.out.println("D-Day: " + dDay);
        System.out.println("D-Day Text: " + dDayText);
        System.out.println("Program Status: " + programStatus);
        
        
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
            .build();
    }
    
    /**
     * 중복 신청 체크
     */
    private boolean isAlreadyApplied(Integer prgId, Integer stdId) {
        return !ncsPrgAplyRepository.findByPrgIdAndStdId(prgId, stdId).isEmpty();
    }
    
    /**
     * 모집 기간 체크
     */
    private boolean isRecruitmentPeriod(Ncs_PrgInfo program) {
        LocalDate today = LocalDate.now();
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
            
            LocalDate today = LocalDate.now();
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
        System.out.println("🖼️ 파일 정보 없음: " + program.getPrgNm());
        return null;
    }
    
    
    /**
     * 페이징된 프로그램 목록 조회 (안전한 null 처리)
     */
    public PagedProgramResponseDTO searchProgramsWithPaging(String keyword, String dept, 
                                                           String mileageFilter, String statusFilter, 
                                                           String sortBy, Integer stdId, int page, int size) {
        
        // 1. 페이징 객체 생성
        Sort sort = createSort(sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        
        // 2. 페이징된 프로그램 조회
        Page<Ncs_PrgInfo> programPage;
        
        if (keyword != null && !keyword.trim().isEmpty()) {
            // 키워드 검색
            programPage = ncsPrgInfoRepository.findByKeywordContaining(keyword, pageable);
        } else {
            // 전체 조회
            programPage = ncsPrgInfoRepository.findAll(pageable);
        }
        
        // 3. DTO 변환
        List<ProgramListDTO> dtoList = programPage.getContent().stream()
            .map(program -> convertToDTO(program, stdId))
            .collect(Collectors.toList());
        
        // 4. 안전한 필터링 (null 체크 추가)
        if (dept != null && !dept.trim().isEmpty()) {
            dtoList = dtoList.stream()
                .filter(p -> p.getPrgDept().equals(dept))
                .collect(Collectors.toList());
        }
        
        if (mileageFilter != null && !mileageFilter.trim().isEmpty()) {
            dtoList = filterByMileage(dtoList, mileageFilter);
        }
        
        if (statusFilter != null && !statusFilter.trim().isEmpty()) {
            dtoList = filterByStatus(dtoList, statusFilter);
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
                return Sort.by(Sort.Direction.ASC, "recruitEndDt");
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
            .filter(p -> filter.equals(p.getProgramStatus()))
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
     * 프로그램 신청 취소
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
            
            return true;
            
        } catch (Exception e) {
            throw new RuntimeException("신청 취소 중 오류가 발생했습니다: " + e.getMessage());
        }
    }
    
    
    
    
}