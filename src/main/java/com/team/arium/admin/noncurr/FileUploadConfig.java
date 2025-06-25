package com.team.arium.admin.noncurr;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;

import java.io.File;

@Configuration
@Getter
public class FileUploadConfig {
    
    @Value("${app.upload.base-path:}")
    private String configuredBasePath;
    
    @Value("${app.upload.url-prefix:http://localhost:8082/uploads}")
    private String urlPrefix;
    
    @Value("${app.upload.max-image-size:5242880}") // 5MB
    private long maxImageSize;
    
    @Value("${app.upload.max-attachment-size:10485760}") // 10MB  
    private long maxAttachmentSize;
    
    // 새로운 설정들 추가
    @Value("${app.upload.max-files-per-request:2}")
    private int maxFilesPerRequest;
    
    @Value("${app.upload.connection-timeout:60000}")
    private long connectionTimeout;
    
    // 실제 사용될 경로들
    private String uploadBasePath;
    private String programImagePath;
    private String attachmentPath;
    
    // 허용된 파일 확장자
    private String[] allowedImageExtensions = {".jpg", ".jpeg", ".png", ".gif", ".bmp", ".webp"};
    private String[] allowedAttachmentExtensions = {".pdf", ".doc", ".docx", ".hwp", ".xls", ".xlsx", ".ppt", ".pptx", ".txt", ".jpg", ".jpeg", ".png"};
    
    @PostConstruct
    public void init() {
        System.out.println("=== 개선된 파일 업로드 설정 ===");
        
        // 온프레미스 서버용 경로 설정
        if (configuredBasePath == null || configuredBasePath.trim().isEmpty()) {
            // 온프레미스 서버에서 안전한 기본 경로들 시도
            this.uploadBasePath = findSafeUploadPath();
        } else {
            this.uploadBasePath = configuredBasePath;
            System.out.println("Properties에서 설정된 경로 사용: " + uploadBasePath);
        }
        
        // 하위 디렉토리 경로 설정
        this.programImagePath = uploadBasePath + File.separator + "program-images";
        this.attachmentPath = uploadBasePath + File.separator + "attachments";
        
        System.out.println("최종 업로드 경로: " + uploadBasePath);
        System.out.println("이미지 저장 경로: " + programImagePath);
        System.out.println("첨부파일 저장 경로: " + attachmentPath);
        System.out.println("웹 URL 접두사: " + urlPrefix);
        System.out.println("최대 파일 개수: " + maxFilesPerRequest);
        System.out.println("연결 타임아웃: " + connectionTimeout + "ms");
        
        // 업로드 디렉토리 생성 및 권한 확인
        boolean success = createAndSetupDirectories();
        
        if (success) {
            System.out.println("✅ 개선된 파일 업로드 설정 완료");
        } else {
            System.err.println("❌ 파일 업로드 설정 실패");
            printTroubleshootingGuide();
        }
        
        System.out.println("===================================");
    }
    
    /**
     * 온프레미스 서버에서 안전한 업로드 경로 찾기 (개선된 버전)
     */
    private String findSafeUploadPath() {
        String[] candidatePaths = {
            "/var/lib/arium/uploads",           // 1순위: 시스템 권장 경로
            "/opt/arium/uploads",               // 2순위: 애플리케이션 전용 경로  
            "/tmp/arium/uploads",               // 3순위: 임시 경로 (재시작시 삭제됨)
            System.getProperty("user.dir") + "/uploads",  // 4순위: 프로젝트 내 경로
            System.getProperty("user.home") + "/arium/uploads"  // 5순위: 사용자 홈
        };
        
        System.out.println("안전한 업로드 경로 탐색 중...");
        
        for (String path : candidatePaths) {
            File testDir = new File(path);
            
            System.out.println("테스트 경로: " + path);
            
            // 경로 생성 시도
            if (!testDir.exists()) {
                boolean created = testDir.mkdirs();
                if (!created) {
                    System.out.println("  ❌ 디렉토리 생성 실패");
                    continue;
                }
            }
            
            // 권한 확인
            if (testDir.canRead() && testDir.canWrite()) {
                System.out.println("  ✅ 읽기/쓰기 권한 확인");
                
                // 실제 파일 생성 테스트
                if (testWritePermission(testDir)) {
                    System.out.println("  ✅ 파일 생성 테스트 통과");
                    System.out.println("선택된 경로: " + path);
                    return path;
                } else {
                    System.out.println("  ❌ 파일 생성 테스트 실패");
                }
            } else {
                System.out.println("  ❌ 읽기/쓰기 권한 없음");
            }
        }
        
        // 모든 경로 실패시 임시 디렉토리 사용
        String fallbackPath = System.getProperty("java.io.tmpdir") + File.separator + "arium-uploads";
        System.err.println("⚠️  모든 경로 실패, 임시 경로 사용: " + fallbackPath);
        return fallbackPath;
    }
    
    /**
     * 실제 파일 쓰기 권한 테스트
     */
    private boolean testWritePermission(File directory) {
        try {
            File testFile = new File(directory, "test-write-permission.tmp");
            boolean created = testFile.createNewFile();
            if (created) {
                boolean deleted = testFile.delete();
                return true;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * 디렉토리 생성 및 설정
     */
    private boolean createAndSetupDirectories() {
        boolean success = true;
        
        // 기본 디렉토리들 생성
        success &= createDirectoryIfNotExists(uploadBasePath);
        success &= createDirectoryIfNotExists(programImagePath);
        success &= createDirectoryIfNotExists(attachmentPath);
        
        if (success) {
            // 권한 상세 확인
            checkDetailedPermissions();
        }
        
        return success;
    }
    
    /**
     * 상세 권한 확인
     */
    private void checkDetailedPermissions() {
        File baseDir = new File(uploadBasePath);
        
        System.out.println("--- 상세 권한 정보 ---");
        System.out.println("절대 경로: " + baseDir.getAbsolutePath());
        System.out.println("소유자: " + System.getProperty("user.name"));
        System.out.println("OS: " + System.getProperty("os.name"));
        System.out.println("Java 버전: " + System.getProperty("java.version"));
        
        System.out.println("디렉토리 상태:");
        System.out.println("  존재: " + baseDir.exists());
        System.out.println("  디렉토리: " + baseDir.isDirectory());
        System.out.println("  읽기: " + baseDir.canRead());
        System.out.println("  쓰기: " + baseDir.canWrite());
        System.out.println("  실행: " + baseDir.canExecute());
        
        // 디스크 공간 확인 추가
        long freeSpace = baseDir.getFreeSpace();
        long totalSpace = baseDir.getTotalSpace();
        System.out.println("디스크 공간:");
        System.out.println("  사용 가능: " + formatBytes(freeSpace));
        System.out.println("  전체 용량: " + formatBytes(totalSpace));
    }
    
    /**
     * 바이트를 읽기 쉬운 형태로 변환
     */
    private String formatBytes(long bytes) {
        if (bytes < 1024) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(1024));
        String pre = "KMGTPE".charAt(exp - 1) + "";
        return String.format("%.1f %sB", bytes / Math.pow(1024, exp), pre);
    }
    
    /**
     * 문제 해결 가이드 출력
     */
    private void printTroubleshootingGuide() {
        System.err.println("");
        System.err.println("🔧 파일 업로드 설정 문제 해결 가이드:");
        System.err.println("");
        System.err.println("1. 권장 해결책 (서버 관리자에게 요청):");
        System.err.println("   sudo mkdir -p /var/lib/arium/uploads");
        System.err.println("   sudo chown $(whoami):$(whoami) /var/lib/arium/uploads");
        System.err.println("   sudo chmod 755 /var/lib/arium/uploads");
        System.err.println("");
        System.err.println("2. application.properties에 경로 직접 설정:");
        System.err.println("   app.upload.base-path=/your/safe/path/uploads");
        System.err.println("");
        System.err.println("3. 임시 해결책 (재시작시 파일 삭제됨):");
        System.err.println("   app.upload.base-path=/tmp/arium/uploads");
        System.err.println("");
        System.err.println("4. 메모리 부족시:");
        System.err.println("   export JAVA_OPTS=\"-Xmx2g -Xms1g\"");
        System.err.println("");
    }
    
    /**
     * 디렉토리 생성
     */
    private boolean createDirectoryIfNotExists(String directoryPath) {
        File directory = new File(directoryPath);
        
        if (!directory.exists()) {
            boolean created = directory.mkdirs();
            if (created) {
                System.out.println("✅ 디렉토리 생성: " + directoryPath);
                
                // Linux/Mac에서 권한 설정 시도
                try {
                    if (!System.getProperty("os.name").toLowerCase().contains("windows")) {
                        directory.setReadable(true, false);
                        directory.setWritable(true, false);
                        directory.setExecutable(true, false);
                        System.out.println("  → 권한 설정 완료");
                    }
                } catch (Exception e) {
                    System.out.println("  → 권한 설정 실패: " + e.getMessage());
                }
                
                return true;
            } else {
                System.err.println("❌ 디렉토리 생성 실패: " + directoryPath);
                return false;
            }
        } else {
            System.out.println("디렉토리 존재: " + directoryPath);
            return true;
        }
    }
    
    /**
     * 웹 접근 가능한 URL 생성
     */
    public String buildWebUrl(String relativePath) {
        if (relativePath == null || relativePath.isEmpty()) {
            return null;
        }
        
        String cleanPath = relativePath.replace("\\", "/");
        if (cleanPath.startsWith("/")) {
            cleanPath = cleanPath.substring(1);
        }
        
        return urlPrefix + "/" + cleanPath;
    }
    
    /**
     * 파일 크기를 MB 단위로 변환
     */
    public String formatFileSize(long bytes) {
        return String.format("%.1f MB", bytes / 1024.0 / 1024.0);
    }
    
    /**
     * 이미지 파일 여부 확인
     */
    public boolean isImageExtension(String extension) {
        if (extension == null) return false;
        String lowerExt = extension.toLowerCase();
        for (String allowed : allowedImageExtensions) {
            if (allowed.equals(lowerExt)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * 첨부파일 확장자 여부 확인
     */
    public boolean isAttachmentExtension(String extension) {
        if (extension == null) return false;
        String lowerExt = extension.toLowerCase();
        for (String allowed : allowedAttachmentExtensions) {
            if (allowed.equals(lowerExt)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * 현재 설정된 업로드 경로 반환 (절대 경로)
     */
    public String getAbsoluteUploadPath() {
        return new File(uploadBasePath).getAbsolutePath();
    }
    
    /**
     * 요청당 최대 파일 개수 검증
     */
    public boolean validateFileCount(int fileCount) {
        if (fileCount > maxFilesPerRequest) {
            System.err.println("❌ 파일 개수 초과: " + fileCount + " > " + maxFilesPerRequest);
            return false;
        }
        return true;
    }
}