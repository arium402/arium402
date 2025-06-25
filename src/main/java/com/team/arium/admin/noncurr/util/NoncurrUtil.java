package com.team.arium.admin.noncurr.util;

import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Slf4j
public class NoncurrUtil {
    
    // 날짜 포맷터
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter CODE_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");
    
    /**
     * 현재 날짜를 문자열로 반환
     * @return yyyy-MM-dd 형식의 날짜 문자열
     */
    public static String getCurrentDateString() {
        return LocalDate.now().format(DATE_FORMATTER);
    }
    
    /**
     * 현재 날짜시간을 문자열로 반환
     * @return yyyy-MM-dd HH:mm:ss 형식의 날짜시간 문자열
     */
    public static String getCurrentDateTimeString() {
        return LocalDateTime.now().format(DATETIME_FORMATTER);
    }
    
    /**
     * 날짜 문자열을 LocalDate로 변환
     * @param dateString yyyy-MM-dd 형식의 날짜 문자열
     * @return LocalDate 객체
     */
    public static LocalDate parseDate(String dateString) {
        try {
            return LocalDate.parse(dateString, DATE_FORMATTER);
        } catch (Exception e) {
            log.warn("날짜 파싱 실패: {}", dateString);
            return null;
        }
    }
    
    /**
     * LocalDate를 문자열로 변환
     * @param date LocalDate 객체
     * @return yyyy-MM-dd 형식의 날짜 문자열
     */
    public static String formatDate(LocalDate date) {
        if (date == null) return null;
        return date.format(DATE_FORMATTER);
    }
    
    /**
     * 날짜 유효성 검사
     * @param startDate 시작일
     * @param endDate 종료일
     * @return 유효한 경우 true
     */
    public static boolean isValidDateRange(String startDate, String endDate) {
        try {
            LocalDate start = parseDate(startDate);
            LocalDate end = parseDate(endDate);
            
            if (start == null || end == null) return false;
            
            return !start.isAfter(end);
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * 프로그램 코드 생성
     * @param prefix 접두사 (기본: PRG)
     * @return 생성된 프로그램 코드
     */
    public static String generateProgramCode(String prefix) {
        if (prefix == null || prefix.isEmpty()) {
            prefix = "PRG";
        }
        
        String datepart = LocalDate.now().format(CODE_DATE_FORMATTER);
        String randomPart = String.format("%03d", (int) (Math.random() * 1000));
        
        return prefix + datepart + randomPart;
    }
    
    /**
     * 프로그램 코드 생성 (기본 접두사 사용)
     * @return 생성된 프로그램 코드
     */
    public static String generateProgramCode() {
        return generateProgramCode("PRG");
    }
    
    /**
     * 파일명에서 확장자 추출
     * @param fileName 파일명
     * @return 확장자 (.포함)
     */
    public static String getFileExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf("."));
    }
    
    /**
     * 고유한 파일명 생성
     * @param originalFileName 원본 파일명
     * @return UUID 기반 고유 파일명
     */
    public static String generateUniqueFileName(String originalFileName) {
        String extension = getFileExtension(originalFileName);
        return UUID.randomUUID().toString() + extension;
    }
    
    /**
     * 문자열이 비어있거나 null인지 확인
     * @param str 확인할 문자열
     * @return 비어있으면 true
     */
    public static boolean isEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }
    
    /**
     * 문자열이 비어있지 않은지 확인
     * @param str 확인할 문자열
     * @return 비어있지 않으면 true
     */
    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }
    
    /**
     * 안전한 문자열 변환 (null 방지)
     * @param obj 변환할 객체
     * @param defaultValue 기본값
     * @return 문자열 또는 기본값
     */
    public static String safeToString(Object obj, String defaultValue) {
        if (obj == null) return defaultValue;
        String str = obj.toString();
        return isEmpty(str) ? defaultValue : str;
    }
    
    /**
     * 안전한 정수 변환
     * @param str 변환할 문자열
     * @param defaultValue 기본값
     * @return 정수 또는 기본값
     */
    public static Integer safeToInteger(String str, Integer defaultValue) {
        try {
            return isEmpty(str) ? defaultValue : Integer.parseInt(str.trim());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
    
    /**
     * 파일 크기를 읽기 쉬운 형태로 변환
     * @param bytes 바이트 크기
     * @return 읽기 쉬운 크기 문자열
     */
    public static String formatFileSize(long bytes) {
        if (bytes < 1024) return bytes + " B";
        if (bytes < 1024 * 1024) return String.format("%.1f KB", bytes / 1024.0);
        if (bytes < 1024 * 1024 * 1024) return String.format("%.1f MB", bytes / (1024.0 * 1024.0));
        return String.format("%.1f GB", bytes / (1024.0 * 1024.0 * 1024.0));
    }
    
    /**
     * 프로그램 상태 한글명을 영문 코드로 변환
     * @param statusName 상태 한글명
     * @return 영문 상태 코드
     */
    public static String convertStatusNameToCode(String statusName) {
        if (isEmpty(statusName)) return "unknown";
        
        switch (statusName.trim()) {
            case "오픈": return "open";
            case "진행": 
            case "진행중": return "progress";
            case "완료": return "completed";
            case "인원 마감":
            case "마감": return "full";
            default: return "unknown";
        }
    }
    
    /**
     * 영문 상태 코드를 한글명으로 변환
     * @param statusCode 영문 상태 코드
     * @return 상태 한글명
     */
    public static String convertStatusCodeToName(String statusCode) {
        if (isEmpty(statusCode)) return "미설정";
        
        switch (statusCode.toLowerCase().trim()) {
            case "open": return "오픈";
            case "progress": return "진행중";
            case "completed": return "완료";
            case "full": return "마감";
            default: return "미설정";
        }
    }
}