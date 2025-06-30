package com.team.arium.admin.noncurr.service;

import com.team.arium.admin.admin_module;
import com.team.arium.admin.noncurr.dto.ApplicantDTO;
import com.team.arium.admin.noncurr.dto.NoncurrProgramDTO;
import com.team.arium.admin.noncurr.repository.*;
import com.team.arium.domain.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminNoncurrProgramService {

    private final NcsPrgInfoRepository ncsPrgInfoRepository;
    private final NcsCmpInfoRepository ncsCmpInfoRepository;
    private final CoreCptInfoRepository coreCptInfoRepository;
    private final CommonCodeRepository commonCodeRepository;
    private final CommonFileRepository commonFileRepository;
    private final NcsCclRelRepository ncsCclRelRepository;
    private final admin_module adminModule;
    private final NcsPrgAplyRepository ncsPrgAplyRepository;

    
    
    // FTP 서버 설정
    @Value("${app.ftp.host}")
    private String ftpHost;
    
    @Value("${app.ftp.port}")
    private int ftpPort;
    
    @Value("${app.ftp.username}")
    private String ftpUsername;
    
    @Value("${app.ftp.password}")
    private String ftpPassword;
    
    @Value("${app.ftp.remote-dir:/uploads/noncurr/images}")
    private String ftpRemoteDir;

    @Value("${app.upload.max-size:5242880}") // 5MB
    private long maxFileSize;
    
    
    /**
     * 비교과 프로그램 등록
     */
    @Transactional
    public Integer createProgram(NoncurrProgramDTO dto) {
        log.info("비교과 프로그램 등록 시작: {}", dto.getPrgNm());

        // ✅ 0. 프로그램명 중복 체크 (가장 먼저 실행)
        if (ncsPrgInfoRepository.existsByPrgNmIgnoreCaseAndTrim(dto.getPrgNm())) {
            throw new RuntimeException("이미 등록된 프로그램명입니다: " + dto.getPrgNm());
        }
        try {
        	
            // 1. 파일 업로드 처리 (FTP)
            Common_File uploadedFile = null;
            if (dto.getImageFile() != null && !dto.getImageFile().isEmpty()) {
                uploadedFile = handleFileUpload(dto.getImageFile());
            }
            
            // 2. 기본 상태 코드 조회 (간단한 방법으로 수정)
            Common_Code statusCode;
            Optional<Common_Code> defaultStatus = commonCodeRepository.findDefaultProgramStatus();
            
            if (defaultStatus.isPresent()) {
                statusCode = defaultStatus.get();
            } else {
                // 기본값이 없으면 PRG_STAT 타입의 첫 번째 코드 사용
                List<Common_Code> programStatuses = commonCodeRepository.findByCodeType("PRG_STAT");
                if (programStatuses.isEmpty()) {
                    throw new RuntimeException("프로그램 상태 코드가 설정되지 않았습니다. 관리자에게 문의하세요.");
                }
                statusCode = programStatuses.get(0);
            }
            
            // 3. 엔티티 생성 및 저장 (regDt, updDt는 @CreationTimestamp, @UpdateTimestamp로 자동 설정)
            Ncs_PrgInfo entity = Ncs_PrgInfo.builder()
                .prgCd(generateProgramCode())
                .prgNm(dto.getPrgNm().trim())
                .prgDesc(dto.getPrgDesc())
                .recruitStDt(dto.getRecruitStDt())
                .recruitEndDt(dto.getRecruitEndDt())
                .prgStDt(dto.getPrgStDt())
                .prgEndDt(dto.getPrgEndDt())
                .maxCnt(dto.getMaxCnt())
                .prgDept(dto.getPrgDept())
                .prgTel(dto.getPrgTel())
                .mlgDefScore(dto.getMlgDefScore())
                .surveyDt(dto.getSurveyDt())
                .comFile(uploadedFile)
                .prgStatCd(statusCode)
                // ✅ regDt, updDt 제거 - @CreationTimestamp, @UpdateTimestamp가 자동 처리
                .build();
            
            Ncs_PrgInfo savedProgram = ncsPrgInfoRepository.save(entity);
            
            // 4. 핵심역량 매핑 저장
            if (dto.getCompetencyIds() != null && !dto.getCompetencyIds().isEmpty()) {
            	saveCompetencyMappings(savedProgram.getPrgId(), dto.getCompetencyIds(), dto.getCompetencyScores());
            }
            
            log.info("비교과 프로그램 등록 완료: ID={}, 코드={}", savedProgram.getPrgId(), savedProgram.getPrgCd());
            return savedProgram.getPrgId();
            
        } catch (Exception e) {
            log.error("비교과 프로그램 등록 실패: {}", e.getMessage(), e);
            throw new RuntimeException("프로그램 등록 중 오류가 발생했습니다.", e);
        }
    }

    /**
     * 핵심역량 목록 조회
     */
    public List<Core_CptInfo> getAllCompetencies() {
        return coreCptInfoRepository.findByUpCclIdIsNull();
    }
    
    /**
     * 현재 진행중인 프로그램 조회 (서버 시간 기준)
     */
    public List<NoncurrProgramDTO> getOngoingPrograms() {
        // ✅ 서버 시간을 기준으로 비교 (날짜 문자열 비교)
        String currentTime = LocalDateTime.now(ZoneId.of("Asia/Seoul"))
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        
        // Repository에서 파라미터 방식으로 비교 (JPQL 타입 에러 방지)
        List<Ncs_PrgInfo> programs = ncsPrgInfoRepository.findAll().stream()
            .filter(p -> p.getPrgStDt() != null && p.getPrgEndDt() != null)
            .filter(p -> p.getPrgStDt().compareTo(currentTime) <= 0 && p.getPrgEndDt().compareTo(currentTime) >= 0)
            .collect(Collectors.toList());
        
        return programs.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    /**
     * 모집중인 프로그램 조회 (서버 시간 기준)
     */
    public List<NoncurrProgramDTO> getRecruitingPrograms() {
        // ✅ 서버 시간을 기준으로 모집 기간 체크
        String currentTime = LocalDateTime.now(ZoneId.of("Asia/Seoul"))
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        
        List<Ncs_PrgInfo> programs = ncsPrgInfoRepository.findAll().stream()
            .filter(p -> p.getRecruitStDt() != null && p.getRecruitEndDt() != null)
            .filter(p -> p.getRecruitStDt().compareTo(currentTime) <= 0 && p.getRecruitEndDt().compareTo(currentTime) >= 0)
            .collect(Collectors.toList());
        
        return programs.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    /**
     * 오늘 날짜 기준 프로그램 조회 (날짜만 비교)
     */
    public List<NoncurrProgramDTO> getTodayPrograms() {
        // ✅ 오늘 날짜만 비교 (시간 제외)
        String today = LocalDate.now(ZoneId.of("Asia/Seoul"))
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        
        List<Ncs_PrgInfo> programs = ncsPrgInfoRepository.findAll().stream()
            .filter(p -> p.getPrgStDt() != null)
            .filter(p -> p.getPrgStDt().startsWith(today))
            .collect(Collectors.toList());
        
        return programs.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    /**
     * 프로그램 상태 업데이트 (서버 시간 기준)
     */
    @Transactional
    public void updateProgramStatus() {
        // ✅ 서버 시간 기준으로 프로그램 상태 자동 업데이트
        String currentTime = LocalDateTime.now(ZoneId.of("Asia/Seoul"))
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        List<Ncs_PrgInfo> allPrograms = ncsPrgInfoRepository.findAll();
        
        for (Ncs_PrgInfo program : allPrograms) {
            String newStatus = calculateProgramStatus(program, currentTime);
            if (newStatus != null) {
                // 상태 코드 조회 및 업데이트
                commonCodeRepository.findByCode(newStatus).ifPresent(statusCode -> {
                    program.setPrgStatCd(statusCode);
                    // ✅ updDt는 @UpdateTimestamp로 자동 설정됨
                    ncsPrgInfoRepository.save(program);
                });
            }
        }
    }
    
    /**
     * 프로그램 상태 계산 (서버 시간 기준)
     */
    private String calculateProgramStatus(Ncs_PrgInfo program, String currentTime) {
        if (program.getRecruitStDt() != null && program.getRecruitEndDt() != null) {
            if (currentTime.compareTo(program.getRecruitStDt()) < 0) {
                return "BEFORE_RECRUIT"; // 모집 전
            } else if (currentTime.compareTo(program.getRecruitEndDt()) <= 0) {
                return "RECRUITING"; // 모집중
            } else if (program.getPrgStDt() != null && currentTime.compareTo(program.getPrgStDt()) < 0) {
                return "BEFORE_START"; // 시작 전
            } else if (program.getPrgEndDt() != null && currentTime.compareTo(program.getPrgEndDt()) <= 0) {
                return "ONGOING"; // 진행중
            } else {
                return "COMPLETED"; // 완료
            }
        }
        return null; // 상태 변경 없음
    }

    /**
     * 프로그램 목록 조회 (페이징 + 필터링) - 개선된 버전
     */
    public Page<NoncurrProgramDTO> getProgramList(String searchKeyword, String period, 
                                                String status, String searchType, Pageable pageable) {
        log.info("프로그램 목록 조회 - 검색어: {}, 기간: {}, 상태: {}, 검색타입: {}", searchKeyword, period, status, searchType);
        
        // 1. 전체 데이터 조회 (페이징 없이)
        List<Ncs_PrgInfo> allPrograms;
        
        if (searchKeyword != null && !searchKeyword.trim().isEmpty()) {
            // 검색어가 있는 경우 - 검색 타입에 따라 분기
            if ("prgDept".equals(searchType)) {
                allPrograms = ncsPrgInfoRepository.findByPrgDeptContainingIgnoreCase(searchKeyword.trim());
            } else {
                // 기본값은 프로그램명 검색
                allPrograms = ncsPrgInfoRepository.findByPrgNmContainingIgnoreCase(searchKeyword.trim());
            }
        } else {
            allPrograms = ncsPrgInfoRepository.findAll();
        }
        
        // 2. DTO 변환
        List<NoncurrProgramDTO> allDtos = allPrograms.stream()
            .map(this::convertToDto)
            .collect(Collectors.toList());
        
        // 3. 필터링 적용
        List<NoncurrProgramDTO> filteredDtos = allDtos.stream()
            .filter(program -> applyPeriodFilter(program, period))
            .filter(program -> applyStatusFilter(program, status))
            .sorted((p1, p2) -> {
                // regDt 기준 내림차순 정렬
                if (p1.getRegDt() == null && p2.getRegDt() == null) return 0;
                if (p1.getRegDt() == null) return 1;
                if (p2.getRegDt() == null) return -1;
                return p2.getRegDt().compareTo(p1.getRegDt());
            })
            .collect(Collectors.toList());
        
        // 4. 수동 페이징 처리
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), filteredDtos.size());
        
        List<NoncurrProgramDTO> pagedContent = start <= filteredDtos.size() ? 
            filteredDtos.subList(start, end) : 
            Collections.emptyList();
        
        // 5. Page 객체 생성
        return new PageImpl<>(pagedContent, pageable, filteredDtos.size());
    }
    
    /**
     * 기간 필터 적용
     */
    private boolean applyPeriodFilter(NoncurrProgramDTO program, String period) {
        if (period == null || period.trim().isEmpty()) {
            return true; // 필터 없으면 모든 데이터 통과
        }
        
        // 프로그램 시작일에서 년도 추출
        if (program.getPrgStDt() != null) {
            String programYear = program.getPrgStDt().substring(0, 4);
            return programYear.equals(period);
        }
        
        return false;
    }

    /**
     * 상태 필터 적용
     */
    private boolean applyStatusFilter(NoncurrProgramDTO program, String status) {
        if (status == null || status.trim().isEmpty()) {
            return true; // 필터 없으면 모든 데이터 통과
        }
        
        // 프로그램 상태와 필터 상태 비교
        return status.equals(program.getPrgStatNm());
    }
    
    
    /**
     * ✅ 핵심역량 매핑 정보를 점수와 함께 조회하는 새로운 메서드
     */
    public List<Ncs_CclRel> getCompetencyMappingsWithScores(Integer prgId) {
        log.info("핵심역량 점수 정보 조회: 프로그램ID={}", prgId);
        
        try {
            // 핵심역량 매핑 정보 조회 (점수 포함)
            List<Ncs_CclRel> competencyMappings = ncsCclRelRepository.findByPrgId(prgId);
            
            log.info("핵심역량 점수 정보 조회 완료: 프로그램ID={}, 매핑 개수={}", prgId, competencyMappings.size());
            
            return competencyMappings;
            
        } catch (Exception e) {
            log.error("핵심역량 점수 정보 조회 실패: 프로그램ID={}, 오류={}", prgId, e.getMessage(), e);
            return Collections.emptyList();
        }
    }
    
    /**
     * ✅ 프로그램 상세 조회 메서드 - 수정 페이지용 (점수 정보 포함)
     */
    public NoncurrProgramDTO getProgramDetail(Integer prgId) {
        Ncs_PrgInfo program = ncsPrgInfoRepository.findById(prgId)
            .orElseThrow(() -> new RuntimeException("프로그램을 찾을 수 없습니다. ID: " + prgId));
        
        NoncurrProgramDTO dto = convertToDto(program);
        
        // ✅ 핵심역량 정보 추가 (기존 - ID만)
        List<Ncs_CclRel> competencyMappings = ncsCclRelRepository.findByPrgId(prgId);
        List<Integer> competencyIds = competencyMappings.stream()
            .map(Ncs_CclRel::getCclId)
            .collect(Collectors.toList());
        dto.setCompetencyIds(competencyIds);
        
        // ✅ 핵심역량 점수 정보 추가 (수정 페이지에서 필요)
        Map<Integer, Integer> competencyScores = competencyMappings.stream()
            .collect(Collectors.toMap(
                Ncs_CclRel::getCclId,
                mapping -> mapping.getCclScore() != null ? mapping.getCclScore() : 100,
                (existing, replacement) -> existing  // 중복 키 처리
            ));
        dto.setCompetencyScores(competencyScores);
        
        // ✅ 점수 정보를 문자열로도 설정 (수정 페이지 JavaScript에서 사용)
        if (!competencyScores.isEmpty()) {
            String scoresStr = competencyScores.entrySet().stream()
                .map(entry -> entry.getKey() + ":" + entry.getValue())
                .collect(Collectors.joining(","));
            dto.setCompetencyScoresStr(scoresStr);
        }
        
        log.info("프로그램 상세 조회 완료: ID={}, 핵심역량 개수={}, 점수 정보={}", 
                prgId, competencyIds.size(), competencyScores.size());
        
        return dto;
    }
    
    
    /**
     * 프로그램 수정 - admin_module.todays_module() + 한국 시간 조합
     */
    @Transactional
    public void updateProgram(Integer prgId, NoncurrProgramDTO dto) {
        log.info("비교과 프로그램 수정 시작: ID={}", prgId);
        
        Ncs_PrgInfo program = ncsPrgInfoRepository.findById(prgId)
            .orElseThrow(() -> new RuntimeException("프로그램을 찾을 수 없습니다. ID: " + prgId));
        
        try {
            // 1. 새 이미지 파일이 있으면 FTP 업로드 처리
            if (dto.getImageFile() != null && !dto.getImageFile().isEmpty()) {
                Common_File newFile = handleFileUpload(dto.getImageFile());
                program.setComFile(newFile);
            }
            
            // ✅ 2. admin_module의 todays_module() + 한국 시간 조합
            String koreanDate = adminModule.todays_module(); // "2025-06-30" 형태
            String koreanTime = LocalDateTime.now(ZoneId.of("Asia/Seoul"))
                .format(DateTimeFormatter.ofPattern("HH:mm:ss")); // "14:30:25" 형태
            String currentKoreanDateTime = koreanDate + " " + koreanTime; // "2025-06-30 14:30:25" 형태
            
            // 3. 프로그램 정보 업데이트
            program.setPrgNm(dto.getPrgNm());
            program.setPrgDesc(dto.getPrgDesc());
            program.setRecruitStDt(dto.getRecruitStDt());
            program.setRecruitEndDt(dto.getRecruitEndDt());
            program.setPrgStDt(dto.getPrgStDt());
            program.setPrgEndDt(dto.getPrgEndDt());
            program.setMaxCnt(dto.getMaxCnt());
            program.setPrgDept(dto.getPrgDept());
            program.setPrgTel(dto.getPrgTel());
            program.setMlgDefScore(dto.getMlgDefScore());
            program.setSurveyDt(dto.getSurveyDt());
            
            // ✅ admin_module 날짜 + 한국 시간으로 설정 (updDt)
            program.setUpdDt(currentKoreanDateTime);
            // regDt는 수정하지 않음 (최초 등록일 유지)
            
            log.info("프로그램 수정 시간 설정: ID={}, 수정일시={}", prgId, currentKoreanDateTime);
            
            ncsPrgInfoRepository.save(program);
            
            // 4. 기존 핵심역량 매핑 삭제 후 새로 저장
            ncsCclRelRepository.deleteByPrgId(prgId);
            if (dto.getCompetencyIds() != null && !dto.getCompetencyIds().isEmpty()) {
                saveCompetencyMappings(prgId, dto.getCompetencyIds(), dto.getCompetencyScores());
            }
            
            log.info("비교과 프로그램 수정 완료: ID={}, 수정시간={}", prgId, currentKoreanDateTime);
            
        } catch (Exception e) {
            log.error("비교과 프로그램 수정 실패: ID={}, 오류={}", prgId, e.getMessage(), e);
            throw new RuntimeException("프로그램 수정 중 오류가 발생했습니다.", e);
        }
    }

    /**
     * 프로그램 삭제
     */
    @Transactional
    public void deleteProgram(Integer prgId) {
        log.info("비교과 프로그램 삭제 시작: ID={}", prgId);
        
        if (!ncsPrgInfoRepository.existsById(prgId)) {
            throw new RuntimeException("프로그램을 찾을 수 없습니다. ID: " + prgId);
        }
        
        try {
            // 핵심역량 매핑 먼저 삭제 (복합키 방식)
            ncsCclRelRepository.deleteByPrgId(prgId);
            
            // 프로그램 삭제
            ncsPrgInfoRepository.deleteById(prgId);
            
            log.info("비교과 프로그램 삭제 완료: ID={}", prgId);
            
        } catch (Exception e) {
            log.error("비교과 프로그램 삭제 실패: ID={}, 오류={}", prgId, e.getMessage(), e);
            throw new RuntimeException("프로그램 삭제 중 오류가 발생했습니다.", e);
        }
    }

    /**
     * 파일 업로드 처리
     */
    private Common_File handleFileUpload(MultipartFile file) throws IOException {
        // 파일 검증
        validateFile(file);
        
        // 고유한 파일명 생성 (yyyyMMdd + 랜덤숫자 방식)
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        
        // 오늘 날짜 문자열 ("yyyyMMdd")
        String today = adminModule.todays_module().replaceAll("-", "");
        
        // 1000~9999 사이 랜덤 숫자 생성하여 고유한 파일명 만들기
        String savedFileName;
        int rnd;
        do {
            rnd = ThreadLocalRandom.current().nextInt(1000, 10000);
            savedFileName = today + "_" + rnd + extension; // e.g. "20250627_4832.jpg"
        } while (checkFileExistsOnFTP(savedFileName)); // FTP에서 중복 파일명 체크
        
        // ✅ 1. FTP 서버에 업로드 (기존 로직 유지)
        try {
            uploadToFTPServer(file, savedFileName);
            log.info("FTP 업로드 성공: {}", savedFileName);
        } catch (IOException e) {
            log.error("FTP 업로드 실패, 로컬만 저장: {}", e.getMessage());
        }
        
        // ✅ 2. 로컬에도 동시 저장 (새로 추가)
        try {
            uploadToLocalServer(file, savedFileName);
            log.info("로컬 저장 성공: {}", savedFileName);
        } catch (IOException e) {
            log.error("로컬 저장 실패: {}", e.getMessage());
            // 로컬 저장 실패해도 계속 진행 (FTP는 성공했으므로)
        }
        
        // 파일 정보 DB에 저장 (실제 DB 스키마에 맞게 수정)
        Common_File fileEntity = Common_File.builder()
            .orgFileName(originalFilename)           // ORG_FILE_NAME
            .saveFileName(savedFileName)             // SAVE_FILE_NAME  
            .fileName(originalFilename)              // FILE_NAME
            .filePath(ftpRemoteDir)                  // FILE_PATH (FTP 경로)
            // fileSize, fileType은 DB에 없으므로 제거
            .build();
        
        // ✅ 저장 전 디버깅
        log.info("=== Common_File 저장 전 ===");
        log.info("fileEntity 빌드 완료: orgFileName={}, saveFileName={}", originalFilename, savedFileName);
        
        Common_File savedFile = commonFileRepository.save(fileEntity);
        
        // ✅ 저장 후 디버깅 (가장 중요!)
        log.info("=== Common_File 저장 후 ===");
        log.info("savedFile.getFileId(): {}", savedFile.getFileId());
        log.info("savedFile.getOrgFileName(): {}", savedFile.getOrgFileName());
        log.info("savedFile.getSaveFileName(): {}", savedFile.getSaveFileName());
        
        return savedFile;
    }
    
    /**
     * ✅ 로컬 서버에 파일 저장 (새로 추가)
     */
    private void uploadToLocalServer(MultipartFile file, String savedFileName) throws IOException {
        // 로컬 업로드 디렉토리 설정
        String localUploadDir = System.getProperty("user.dir") + "/src/main/resources/static/uploads/noncurr/images/";
        
        // 디렉토리 생성 (없으면)
        File directory = new File(localUploadDir);
        if (!directory.exists()) {
            boolean created = directory.mkdirs();
            if (created) {
                log.info("로컬 업로드 디렉토리 생성: {}", localUploadDir);
            }
        }
        
        // 파일 저장
        File destFile = new File(localUploadDir + savedFileName);
        file.transferTo(destFile);
        
        log.info("로컬 파일 저장 완료: {}", destFile.getAbsolutePath());
    }
    
    /**
     * ✅ 로컬에서 파일 존재 여부 확인 (새로 추가)
     */
    private boolean checkFileExistsLocal(String fileName) {
        String localUploadDir = System.getProperty("user.dir") + "/src/main/resources/static/uploads/noncurr/images/";
        File file = new File(localUploadDir + fileName);
        return file.exists();
    }

    /**
     * FTP 서버에 파일 업로드
     */
    private void uploadToFTPServer(MultipartFile file, String savedFileName) throws IOException {
        FTPClient ftpClient = new FTPClient();
        
        try {
            // FTP 서버 연결
            log.info("FTP 서버 연결 시도: {}:{}", ftpHost, ftpPort);
            ftpClient.connect(ftpHost, ftpPort);
            
            // FTP 로그인
            boolean loginSuccess = ftpClient.login(ftpUsername, ftpPassword);
            if (!loginSuccess) {
                throw new IOException("FTP 로그인 실패: " + ftpClient.getReplyString());
            }
            
            log.info("FTP 로그인 성공");
            
            // Passive 모드 설정 (방화벽 환경에서 안전)
            ftpClient.enterLocalPassiveMode();
            
            // 바이너리 모드 설정 (이미지 파일 깨짐 방지)
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
            
            // 원격 디렉토리로 이동 (없으면 생성)
            createRemoteDirectoryIfNotExists(ftpClient, ftpRemoteDir);
            ftpClient.changeWorkingDirectory(ftpRemoteDir);
            
            // 파일 업로드
            try (InputStream inputStream = file.getInputStream()) {
                boolean uploadSuccess = ftpClient.storeFile(savedFileName, inputStream);
                if (!uploadSuccess) {
                    throw new IOException("FTP 파일 업로드 실패: " + ftpClient.getReplyString());
                }
            }
            
            log.info("FTP 파일 업로드 성공: {}", savedFileName);
            
        } finally {
            // FTP 연결 종료
            if (ftpClient.isConnected()) {
                try {
                    ftpClient.logout();
                    ftpClient.disconnect();
                } catch (IOException e) {
                    log.warn("FTP 연결 종료 중 오류: {}", e.getMessage());
                }
            }
        }
    }

    /**
     * FTP 서버에서 파일 존재 여부 확인
     */
    private boolean checkFileExistsOnFTP(String fileName) {
        FTPClient ftpClient = new FTPClient();
        
        try {
            ftpClient.connect(ftpHost, ftpPort);
            ftpClient.login(ftpUsername, ftpPassword);
            ftpClient.enterLocalPassiveMode();
            
            ftpClient.changeWorkingDirectory(ftpRemoteDir);
            
            // 파일 목록에서 해당 파일명 찾기
            String[] fileNames = ftpClient.listNames();
            if (fileNames != null) {
                for (String name : fileNames) {
                    if (fileName.equals(name)) {
                        return true;
                    }
                }
            }
            
            return false;
            
        } catch (IOException e) {
            log.warn("FTP 파일 존재 확인 중 오류 (계속 진행): {}", e.getMessage());
            return false; // 오류 시 false 반환하여 계속 진행
        } finally {
            if (ftpClient.isConnected()) {
                try {
                    ftpClient.logout();
                    ftpClient.disconnect();
                } catch (IOException e) {
                    // 무시
                }
            }
        }
    }

    /**
     * FTP 서버에 원격 디렉토리 생성
     */
    private void createRemoteDirectoryIfNotExists(FTPClient ftpClient, String remotePath) throws IOException {
        String[] pathElements = remotePath.split("/");
        String currentPath = "";
        
        for (String folder : pathElements) {
            if (folder.isEmpty()) continue;
            
            currentPath += "/" + folder;
            
            // 디렉토리가 존재하지 않으면 생성
            if (!ftpClient.changeWorkingDirectory(currentPath)) {
                ftpClient.makeDirectory(currentPath);
                log.info("FTP 디렉토리 생성: {}", currentPath);
            }
        }
        
        // 루트로 돌아가기
        ftpClient.changeWorkingDirectory("/");
    }

    /**
     * 파일 검증
     */
    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new RuntimeException("파일이 비어있습니다.");
        }
        
        if (file.getSize() > maxFileSize) {
            throw new RuntimeException("파일 크기가 5MB를 초과합니다.");
        }
        
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new RuntimeException("이미지 파일만 업로드 가능합니다.");
        }
        
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.trim().isEmpty()) {
            throw new RuntimeException("파일명이 올바르지 않습니다.");
        }
        
        // 파일 확장자 검증
        String[] allowedExtensions = {".jpg", ".jpeg", ".png", ".gif", ".bmp"};
        String lowerFileName = originalFilename.toLowerCase();
        boolean validExtension = false;
        for (String ext : allowedExtensions) {
            if (lowerFileName.endsWith(ext)) {
                validExtension = true;
                break;
            }
        }
        
        if (!validExtension) {
            throw new RuntimeException("지원하지 않는 파일 형식입니다. (jpg, jpeg, png, gif, bmp만 허용)");
        }
    }

    /**
     * 핵심역량 매핑 저장 (복합키 방식) - 점수 정보 포함
     */
    private void saveCompetencyMappings(Integer prgId, List<Integer> competencyIds, Map<Integer, Integer> competencyScores) {
        // 프로그램 엔티티 조회
        Ncs_PrgInfo program = ncsPrgInfoRepository.findById(prgId)
            .orElseThrow(() -> new RuntimeException("프로그램을 찾을 수 없습니다."));
        
        // 핵심역량 엔티티들 조회
        List<Core_CptInfo> competencies = coreCptInfoRepository.findByCclIdIn(competencyIds);
        
        // 매핑 엔티티 생성 및 저장
        for (Core_CptInfo competency : competencies) {
            Integer competencyId = competency.getCclId();
            
            // ✅ 수정: 설정된 점수 사용, 없으면 기본값 100
            Integer score = 100; // 기본값
            if (competencyScores != null && competencyScores.containsKey(competencyId)) {
                Integer userScore = competencyScores.get(competencyId);
                if (userScore != null && userScore >= 15 && userScore <= 100) {
                    score = userScore;
                }
            }
            
            Ncs_CclRel mapping = Ncs_CclRel.builder()
                .prgId(prgId)                    // 복합키 필드
                .cclId(competency.getCclId())    // 복합키 필드
                .ncsPrgInfo(program)             // 연관관계
                .coreCptInfo(competency)         // 연관관계
                .cclScore(score)                 // ✅ 수정: 설정된 점수 사용
                .build();
            
            ncsCclRelRepository.save(mapping);
            
            log.info("핵심역량 매핑 저장: 프로그램ID={}, 역량ID={}, 점수={}", prgId, competencyId, score);
        }
        
        log.info("핵심역량 매핑 저장 완료: 프로그램ID={}, 역량 개수={}", prgId, competencies.size());
    }

    /**
     * 프로그램 코드 생성
     */
    private String generateProgramCode() {
        String prefix = "PGM";
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        int sequence = 1;
        
        String code;
        do {
            code = String.format("%s%s%03d", prefix, timestamp, sequence);
            sequence++;
        } while (ncsPrgInfoRepository.existsByPrgCd(code));
        
        return code;
    }

    /**
     * 엔티티 -> DTO 변환 (동적 상태 계산 포함)
     */
    private NoncurrProgramDTO convertToDto(Ncs_PrgInfo entity) {
        NoncurrProgramDTO dto = NoncurrProgramDTO.builder()
            .prgId(entity.getPrgId())
            .prgCd(entity.getPrgCd())
            .prgNm(entity.getPrgNm())
            .prgDesc(entity.getPrgDesc())
            .recruitStDt(entity.getRecruitStDt())
            .recruitEndDt(entity.getRecruitEndDt())
            .prgStDt(entity.getPrgStDt())
            .prgEndDt(entity.getPrgEndDt())
            .maxCnt(entity.getMaxCnt())
            .prgDept(entity.getPrgDept())
            .prgTel(entity.getPrgTel())
            .mlgDefScore(entity.getMlgDefScore())
            .surveyDt(entity.getSurveyDt())
            .comFile(entity.getComFile())
            .prgStatCd(entity.getPrgStatCd())
            .currentCnt(getCurrentApplicantCount(entity.getPrgId()))
            .fileId(entity.getComFile() != null ? entity.getComFile().getFileId() : null)
            .imageUrl(entity.getComFile() != null ? 
                "/uploads/noncurr/images/" + entity.getComFile().getSaveFileName() : null)
            .orgFileName(entity.getComFile() != null ? entity.getComFile().getOrgFileName() : null)
            .saveFileName(entity.getComFile() != null ? entity.getComFile().getSaveFileName() : null)
            .regDt(entity.getRegDt())
            .updDt(entity.getUpdDt())
            .build();
        
        // ✅ 동적 상태 계산 (admin_module 활용)
        String dynamicStatus = calculateDynamicStatus(dto);
        dto.setPrgStatNm(dynamicStatus);
        
        return dto;
    }
    
    /**
     * 현재 신청인원 조회 (임시로 0 반환 - 서버 실행 우선)
     */
 // ✅ getCurrentApplicantCount 메서드 수정
    private Integer getCurrentApplicantCount(Integer prgId) {
        try {
            return ncsPrgAplyRepository.countByPrgId(prgId);
        } catch (Exception e) {
            log.warn("신청인원 조회 실패: 프로그램 ID={}, 오류={}", prgId, e.getMessage());
            return 0;
        }
    }
    
    /**
     * 프로그램 상태 동적 계산 (문자열 비교 방식)
     */
    private String calculateDynamicStatus(NoncurrProgramDTO dto) {
        // ✅ admin_module로 한국 시간 기준 오늘 날짜 ("2025-06-28" 형태)
        String today = adminModule.todays_module();
        
        log.info("=== 상태 계산 (한국시간 문자열 비교) ===");
        log.info("오늘 날짜: {}", today);
        log.info("프로그램: {}", dto.getPrgNm());
        
        try {
            // 날짜 문자열에서 YYYY-MM-DD 부분만 추출
            String recruitStart = extractDatePart(dto.getRecruitStDt());
            String recruitEnd = extractDatePart(dto.getRecruitEndDt());
            String programStart = extractDatePart(dto.getPrgStDt());
            String programEnd = extractDatePart(dto.getPrgEndDt());
            
            Integer currentCnt = dto.getCurrentCnt() != null ? dto.getCurrentCnt() : 0;
            Integer maxCnt = dto.getMaxCnt() != null ? dto.getMaxCnt() : 0;
            
            log.info("모집기간: {} ~ {}", recruitStart, recruitEnd);
            log.info("운영기간: {} ~ {}", programStart, programEnd);
            log.info("신청인원: {}/{}", currentCnt, maxCnt);
            
            // 1순위: 인원마감 (모집기간 내 + 인원 충족)
            if (recruitStart != null && recruitEnd != null) {
                boolean isRecruitPeriod = today.compareTo(recruitStart) >= 0 && today.compareTo(recruitEnd) <= 0;
                log.info("모집기간 중인가? {} (today:{} >= start:{} && today:{} <= end:{})", 
                        isRecruitPeriod, today, recruitStart, today, recruitEnd);
                
                if (isRecruitPeriod && currentCnt >= maxCnt && maxCnt > 0) {
                    log.info("✅ 계산된 상태: 인원 마감 (모집기간 내 + 인원충족: {}/{})", currentCnt, maxCnt);
                    return "인원 마감";
                }
            }
            
            // 2순위: 진행 (운영기간 내)
            if (programStart != null && programEnd != null) {
                boolean isProgramRunning = today.compareTo(programStart) >= 0 && today.compareTo(programEnd) <= 0;
                log.info("운영기간 중인가? {} (today:{} >= start:{} && today:{} <= end:{})", 
                        isProgramRunning, today, programStart, today, programEnd);
                
                if (isProgramRunning) {
                    log.info("✅ 계산된 상태: 진행 (운영기간 내)");
                    return "진행";
                }
            }
            
            // 3순위: 종료 (운영종료일 지남)
            if (programEnd != null && today.compareTo(programEnd) > 0) {
                log.info("운영 종료되었는가? true (today:{} > end:{})", today, programEnd);
                log.info("✅ 계산된 상태: 종료");
                return "종료";
            }
            
            // 4순위: 기본값 오픈
            log.info("✅ 계산된 상태: 오픈 (기본값)");
            return "오픈";
            
        } catch (Exception e) {
            log.error("상태 계산 오류: 프로그램 ID={}, 오류={}", dto.getPrgId(), e.getMessage(), e);
            return "오픈";
        }
    }
    
    /**
     * 날짜 문자열에서 YYYY-MM-DD 부분만 추출
     */
    private String extractDatePart(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            return null;
        }
        
        try {
            // "2025-06-28 10:30:00" -> "2025-06-28"
            // "2025-06-28" -> "2025-06-28" (그대로)
            String datePart = dateStr.length() >= 10 ? dateStr.substring(0, 10) : dateStr;
            log.debug("날짜 추출: '{}' -> '{}'", dateStr, datePart);
            return datePart;
        } catch (Exception e) {
            log.warn("날짜 추출 실패: {}", dateStr);
            return null;
        }
    }
    
    /**
     * 신청자 목록 조회 (실제 DB 연동)
     */
    public Page<ApplicantDTO> getApplicantList(Integer prgId, Pageable pageable) {
        log.info("신청자 목록 조회: 프로그램ID={}, 페이지={}", prgId, pageable.getPageNumber());
        
        try {
            // ✅ 실제 DB에서 신청자 정보 조회
            Page<Object[]> rawData = ncsPrgAplyRepository.findApplicantDetailsByPrgIdWithPaging(prgId, pageable);
            
            // ✅ Object[] 배열을 ApplicantDTO로 변환
            List<ApplicantDTO> applicants = rawData.getContent().stream()
                .map(this::convertToApplicantDTO)
                .collect(Collectors.toList());
            
            // ✅ Page 객체 생성
            return new PageImpl<>(applicants, pageable, rawData.getTotalElements());
            
        } catch (Exception e) {
            log.error("신청자 목록 조회 실패: 프로그램ID={}, 오류={}", prgId, e.getMessage(), e);
            return new PageImpl<>(Collections.emptyList(), pageable, 0);
        }
    }
        
        /**
         * Object[] 배열을 ApplicantDTO로 변환
         * 네이티브 쿼리 결과를 DTO로 매핑
         */
        private ApplicantDTO convertToApplicantDTO(Object[] row) {
            try {
                return ApplicantDTO.builder()
                    .id(row[0] != null ? ((Number) row[0]).longValue() : null)          // aply_id
                    .prgId(row[1] != null ? ((Number) row[1]).intValue() : null)        // prg_id
                    .stdId(row[2] != null ? ((Number) row[2]).intValue() : null)        // std_id
                    .applyDate(row[3] != null ? row[3].toString() : null)               // aply_dt
                    .regDt(row[4] != null ? row[4].toString() : null)                   // reg_dt
                    .updDt(row[5] != null ? row[5].toString() : null)                   // upd_dt
                    .studentId(row[6] != null ? row[6].toString() : "")                 // std_no
                    .name(row[7] != null ? row[7].toString() : "")                      // std_nm
                    .schYr(row[8] != null ? ((Number) row[8]).intValue() : null)        // sch_yr
                    .stdGender(row[9] != null ? row[9].toString() : "")                 // std_gender
                    .stdTellno(row[10] != null ? row[10].toString() : "")               // std_tellno
                    .stdEmlAddr(row[11] != null ? row[11].toString() : "")              // std_eml_addr
                    .department(row[12] != null ? row[12].toString() : "")              // dept_nm
                    .college(row[13] != null ? row[13].toString() : "")                 // college
                    .status(row[14] != null ? row[14].toString() : "")                  // aply_stat_desc
                    .statusCode(row[15] != null ? row[15].toString() : "")              // aply_stat_code
                    .cmpId(row[16] != null ? ((Number) row[16]).intValue() : null)      // cmp_id
                    .completed("Y".equals(row[17]))                                     // cmp_yn
                    .surveyCompleted("Y".equals(row[18]))                               // survey_yn
                    .appliedDateFormatted(formatApplyDate(row[3]))                      // 포맷된 날짜
                    .statusBadgeClass(getStatusBadgeClass(row[15]))                     // CSS 클래스
                    .canEdit(true)                                                      // 수정 가능 여부
                    .build();
            } catch (Exception e) {
                log.error("ApplicantDTO 변환 실패: {}", e.getMessage(), e);
                return ApplicantDTO.builder()
                    .name("변환 오류")
                    .studentId("ERROR")
                    .department("변환 실패")
                    .completed(false)
                    .surveyCompleted(false)
                    .build();
            }
        }
        
        /**
         * 신청자 상태 업데이트 (관리자가 이수여부/만족도조사 직접 수정)
         */
        @Transactional
        public boolean updateApplicantCompletionStatus(Integer aplyId, String type, boolean status) {
            log.info("신청자 상태 업데이트: 신청ID={}, 타입={}, 상태={}", aplyId, type, status);
            
            try {
                // 신청 정보 조회
                Ncs_PrgAply application = ncsPrgAplyRepository.findById(aplyId)
                    .orElseThrow(() -> new RuntimeException("신청 정보를 찾을 수 없습니다."));
                
                // 이수 정보 조회 또는 생성
                Optional<Ncs_CmpInfo> existingCompletion = ncsCmpInfoRepository.findByNcsPrgAply_AplyId(aplyId);
                
                Ncs_CmpInfo completion;
                if (existingCompletion.isPresent()) {
                    completion = existingCompletion.get();
                } else {
                    // 새로운 이수 정보 생성
                    completion = Ncs_CmpInfo.builder()
                        .ncsPrgAply(application)
                        .ncsPrgInfo(application.getNcsPrgInfo())
                        .stdInfo(application.getStdInfo())
                        .cmpYn(yn.N)
                        .surveyYn(yn.N)
                        .build();
                }
                
                // 타입에 따라 상태 업데이트
                if ("completion".equals(type)) {
                    completion.setCmpYn(status ? yn.Y : yn.N);
                } else if ("survey".equals(type)) {
                    completion.setSurveyYn(status ? yn.Y : yn.N);
                } else {
                    throw new RuntimeException("알 수 없는 상태 타입: " + type);
                }
                
                ncsCmpInfoRepository.save(completion);
                
                log.info("신청자 상태 업데이트 완료: 신청ID={}", aplyId);
                return true;
                
            } catch (Exception e) {
                log.error("신청자 상태 업데이트 실패: 신청ID={}, 오류={}", aplyId, e.getMessage(), e);
                return false;
            }
        }
        
        /**
         * 신청일 포맷팅
         */
        private String formatApplyDate(Object dateObj) {
            if (dateObj == null) return "";
            
            try {
                String dateStr = dateObj.toString();
                // "2024-06-15 10:30:00" -> "2024-06-15"
                return dateStr.length() >= 10 ? dateStr.substring(0, 10) : dateStr;
            } catch (Exception e) {
                log.warn("날짜 포맷팅 실패: {}", dateObj);
                return dateObj.toString();
            }
        }   
    
        /**
         * 상태에 따른 CSS 클래스 반환
         */
        private String getStatusBadgeClass(Object statusCodeObj) {
            if (statusCodeObj == null) return "secondary";
            
            String statusCode = statusCodeObj.toString();
            switch (statusCode) {
                case "승인":
                case "APPROVED":
                    return "success";
                case "대기":
                case "PENDING":
                    return "warning";
                case "거부":
                case "REJECTED":
                    return "danger";
                default:
                    return "secondary";
            }
        }  
        
        /**
         * 프로그램별 신청자 통계 조회
         */
        public Map<String, Object> getApplicantStatistics(Integer prgId) {
            log.info("신청자 통계 조회: 프로그램ID={}", prgId);
            
            Map<String, Object> stats = new HashMap<>();
            
            try {
                // 전체 신청자 수
                int totalCount = ncsPrgAplyRepository.countByPrgId(prgId);
                stats.put("totalCount", totalCount);
                
                // 상태별 통계
                List<Object[]> statusStats = ncsPrgAplyRepository.countApplicantsByStatus(prgId);
                Map<String, Integer> statusMap = new HashMap<>();
                for (Object[] row : statusStats) {
                    String statusName = row[0] != null ? row[0].toString() : "알 수 없음";
                    Integer count = row[1] != null ? ((Number) row[1]).intValue() : 0;
                    statusMap.put(statusName, count);
                }
                stats.put("statusStats", statusMap);
                
                // 이수 현황 통계 (NcsCmpInfoRepository 필요)
                // Object[] completionStats = ncsCmpInfoRepository.getCompletionStatsByPrgId(prgId);
                // if (completionStats != null) {
                //     stats.put("totalCompletions", completionStats[0]);
                //     stats.put("completedCount", completionStats[1]);
                //     stats.put("surveyCount", completionStats[2]);
                // }
                
                return stats;
                
            } catch (Exception e) {
                log.error("신청자 통계 조회 실패: 프로그램ID={}, 오류={}", prgId, e.getMessage(), e);
                stats.put("totalCount", 0);
                stats.put("statusStats", new HashMap<>());
                return stats;
            }
        } 
    
    /**
     * 샘플 신청자 데이터 생성 (실제로는 DB에서 조회)
     */
    private List<ApplicantDTO> createSampleApplicants() {
        List<ApplicantDTO> applicants = new ArrayList<>();
        
        applicants.add(ApplicantDTO.builder()
            .id(1L)
            .studentId("202012345")
            .name("김학생")
            .department("컴퓨터과학과")
            .completed(true)
            .surveyCompleted(true)
            .applyDate("2024-06-15")
            .status("승인")
            .build());
            
        applicants.add(ApplicantDTO.builder()
            .id(2L)
            .studentId("202012346")
            .name("이학생")
            .department("정보통신학과")
            .completed(true)
            .surveyCompleted(false)
            .applyDate("2024-06-16")
            .status("승인")
            .build());
            
        applicants.add(ApplicantDTO.builder()
            .id(3L)
            .studentId("202012347")
            .name("박학생")
            .department("경영학과")
            .completed(false)
            .surveyCompleted(false)
            .applyDate("2024-06-17")
            .status("대기")
            .build());
            
        // 더 많은 샘플 데이터 추가 (테스트용)
        for (int i = 4; i <= 25; i++) {
            applicants.add(ApplicantDTO.builder()
                .id((long) i)
                .studentId("20201234" + i)
                .name("학생" + i)
                .department(i % 3 == 0 ? "컴퓨터과학과" : i % 3 == 1 ? "경영학과" : "정보통신학과")
                .completed(i % 4 != 0)
                .surveyCompleted(i % 5 != 0)
                .applyDate("2024-06-" + String.format("%02d", 10 + (i % 20)))
                .status(i % 6 == 0 ? "대기" : "승인")
                .build());
        }
        
        return applicants;
    }

}