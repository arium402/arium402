package com.team.arium.admin.noncurr.exception;

/**
 * 비교과 프로그램 관련 기본 예외 클래스
 */
public class NoncurrProgramException extends RuntimeException {
    
    public NoncurrProgramException(String message) {
        super(message);
    }
    
    public NoncurrProgramException(String message, Throwable cause) {
        super(message, cause);
    }
}

/**
 * 프로그램을 찾을 수 없는 경우 예외
 */
class ProgramNotFoundException extends NoncurrProgramException {
    
    public ProgramNotFoundException(Integer prgId) {
        super("프로그램을 찾을 수 없습니다. ID: " + prgId);
    }
    
    public ProgramNotFoundException(String prgCd) {
        super("프로그램을 찾을 수 없습니다. 코드: " + prgCd);
    }
}

/**
 * 프로그램 코드 중복 예외
 */
class DuplicateProgramCodeException extends NoncurrProgramException {
    
    public DuplicateProgramCodeException(String prgCd) {
        super("이미 존재하는 프로그램 코드입니다: " + prgCd);
    }
}

/**
 * 파일 업로드 관련 예외
 */
class FileUploadException extends NoncurrProgramException {
    
    public FileUploadException(String message) {
        super("파일 업로드 실패: " + message);
    }
    
    public FileUploadException(String message, Throwable cause) {
        super("파일 업로드 실패: " + message, cause);
    }
}

/**
 * 유효하지 않은 데이터 예외
 */
class InvalidDataException extends NoncurrProgramException {
    
    public InvalidDataException(String message) {
        super("유효하지 않은 데이터: " + message);
    }
}

/**
 * 프로그램 삭제 불가 예외
 */
class ProgramDeletionNotAllowedException extends NoncurrProgramException {
    
    public ProgramDeletionNotAllowedException(String reason) {
        super("프로그램을 삭제할 수 없습니다: " + reason);
    }
}

/**
 * 권한 없음 예외
 */
class InsufficientPermissionException extends NoncurrProgramException {
    
    public InsufficientPermissionException(String operation) {
        super("작업을 수행할 권한이 없습니다: " + operation);
    }
}