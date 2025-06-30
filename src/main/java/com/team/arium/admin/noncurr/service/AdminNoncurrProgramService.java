package com.team.arium.admin.noncurr.service;

import com.team.arium.admin.noncurr.dto.NoncurrProgramDTO;
import com.team.arium.admin.noncurr.repository.*;
import com.team.arium.domain.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminNoncurrProgramService {

    private final NcsPrgInfoRepository ncsPrgInfoRepository;
    private final CoreCptInfoRepository coreCptInfoRepository;
    private final CommonCodeRepository commonCodeRepository;
    private final CommonFileRepository commonFileRepository;
    private final NcsCclRelRepository ncsCclRelRepository;

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
                .prgNm(dto.getPrgNm())
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
                saveCompetencyMappings(savedProgram.getPrgId(), dto.getCompetencyIds());
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
     * 프로그램 목록 조회 (페이징)
     */
    public Page<NoncurrProgramDTO> getProgramList(String searchKeyword, Pageable pageable) {
        Page<Ncs_PrgInfo> programs;
        
        if (searchKeyword != null && !searchKeyword.trim().isEmpty()) {
            programs = ncsPrgInfoRepository.findByKeywordContaining(searchKeyword.trim(), pageable);
        } else {
            programs = ncsPrgInfoRepository.findAll(pageable);
        }
        
        return programs.map(this::convertToDto);
    }

    /**
     * 프로그램 상세 조회
     */
    public NoncurrProgramDTO getProgramDetail(Integer prgId) {
        Ncs_PrgInfo program = ncsPrgInfoRepository.findById(prgId)
            .orElseThrow(() -> new RuntimeException("프로그램을 찾을 수 없습니다. ID: " + prgId));
        
        NoncurrProgramDTO dto = convertToDto(program);
        
        // 핵심역량 정보 추가 (복합키 방식)
        List<Ncs_CclRel> competencyMappings = ncsCclRelRepository.findByPrgId(prgId);
        List<Integer> competencyIds = competencyMappings.stream()
            .map(Ncs_CclRel::getCclId)
            .collect(Collectors.toList());
        dto.setCompetencyIds(competencyIds);
        
        return dto;
    }

    /**
     * 프로그램 수정
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
            
            // 2. 프로그램 정보 업데이트 (updDt는 @UpdateTimestamp로 자동 설정)
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
            // ✅ updDt 제거 - @UpdateTimestamp가 자동 처리, regDt는 updatable=false라 수정 안됨
            
            ncsPrgInfoRepository.save(program);
            
            // 3. 기존 핵심역량 매핑 삭제 후 새로 저장
            ncsCclRelRepository.deleteByPrgId(prgId);
            if (dto.getCompetencyIds() != null && !dto.getCompetencyIds().isEmpty()) {
                saveCompetencyMappings(prgId, dto.getCompetencyIds());
            }
            
            log.info("비교과 프로그램 수정 완료: ID={}", prgId);
            
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
        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        
        // 1000~9999 사이 랜덤 숫자 생성하여 고유한 파일명 만들기
        String savedFileName;
        int rnd;
        do {
            rnd = ThreadLocalRandom.current().nextInt(1000, 10000);
            savedFileName = today + "_" + rnd + extension; // e.g. "20250627_4832.jpg"
        } while (checkFileExistsOnFTP(savedFileName)); // FTP에서 중복 파일명 체크
        
        // FTP 서버에 파일 업로드
        uploadToFTPServer(file, savedFileName);
        
        // 파일 정보 DB에 저장 (실제 DB 스키마에 맞게 수정)
        Common_File fileEntity = Common_File.builder()
            .orgFileName(originalFilename)           // ORG_FILE_NAME
            .saveFileName(savedFileName)             // SAVE_FILE_NAME  
            .fileName(originalFilename)              // FILE_NAME
            .filePath(ftpRemoteDir)                  // FILE_PATH (FTP 경로)
            // fileSize, fileType은 DB에 없으므로 제거
            .build();
        
        return commonFileRepository.save(fileEntity);
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
     * 핵심역량 매핑 저장 (복합키 방식)
     */
    private void saveCompetencyMappings(Integer prgId, List<Integer> competencyIds) {
        // 프로그램 엔티티 조회
        Ncs_PrgInfo program = ncsPrgInfoRepository.findById(prgId)
            .orElseThrow(() -> new RuntimeException("프로그램을 찾을 수 없습니다."));
        
        // 핵심역량 엔티티들 조회
        List<Core_CptInfo> competencies = coreCptInfoRepository.findByCclIdIn(competencyIds);
        
        // 매핑 엔티티 생성 및 저장
        for (Core_CptInfo competency : competencies) {
            Ncs_CclRel mapping = Ncs_CclRel.builder()
                .prgId(prgId)                    // 복합키 필드
                .cclId(competency.getCclId())    // 복합키 필드
                .ncsPrgInfo(program)             // 연관관계
                .coreCptInfo(competency)         // 연관관계
                .cclScore(100)                   // 기본 점수
                .build();
            
            ncsCclRelRepository.save(mapping);
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
     * 엔티티 -> DTO 변환
     */
    private NoncurrProgramDTO convertToDto(Ncs_PrgInfo entity) {
        return NoncurrProgramDTO.builder()
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
            // Common_Code의 실제 필드명 사용 (code_desc 또는 code)
            .prgStatNm(entity.getPrgStatCd() != null ? entity.getPrgStatCd().getCodeDesc() : "")
            .fileId(entity.getComFile() != null ? entity.getComFile().getFileId() : null)
            .imageUrl(entity.getComFile() != null ? 
                "/uploads/noncurr/images/" + entity.getComFile().getSaveFileName() : null)
            // Common_File의 실제 필드명 사용
            .orgFileName(entity.getComFile() != null ? entity.getComFile().getOrgFileName() : null)
            .saveFileName(entity.getComFile() != null ? entity.getComFile().getSaveFileName() : null)
            .regDt(entity.getRegDt())
            .updDt(entity.getUpdDt())
            .build();
    }
}