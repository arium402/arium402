package com.team.arium.admin.noncurr.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponseDTO<T> {
    
    private boolean success;
    private String message;
    private T data;
    private Map<String, Object> metadata;
    
    // 성공 응답 생성
    public static <T> ApiResponseDTO<T> success(T data) {
        return ApiResponseDTO.<T>builder()
                .success(true)
                .data(data)
                .build();
    }
    
    public static <T> ApiResponseDTO<T> success(T data, String message) {
        return ApiResponseDTO.<T>builder()
                .success(true)
                .message(message)
                .data(data)
                .build();
    }
    
    // 실패 응답 생성
    public static <T> ApiResponseDTO<T> failure(String message) {
        return ApiResponseDTO.<T>builder()
                .success(false)
                .message(message)
                .build();
    }
    
    public static <T> ApiResponseDTO<T> failure(String message, T data) {
        return ApiResponseDTO.<T>builder()
                .success(false)
                .message(message)
                .data(data)
                .build();
    }
    
    // 메타데이터 추가
    public ApiResponseDTO<T> addMetadata(String key, Object value) {
        if (this.metadata == null) {
            this.metadata = new HashMap<>();
        }
        this.metadata.put(key, value);
        return this;
    }
    
    // 페이징 메타데이터 추가
    public ApiResponseDTO<T> addPagingMetadata(int currentPage, int totalPages, long totalElements, int size) {
        return this.addMetadata("currentPage", currentPage)
                   .addMetadata("totalPages", totalPages)
                   .addMetadata("totalElements", totalElements)
                   .addMetadata("size", size)
                   .addMetadata("first", currentPage == 0)
                   .addMetadata("last", currentPage == totalPages - 1);
    }
}