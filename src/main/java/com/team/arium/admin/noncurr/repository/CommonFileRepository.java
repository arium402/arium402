package com.team.arium.admin.noncurr.repository;

import com.team.arium.domain.Common_File;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommonFileRepository extends JpaRepository<Common_File, Integer> {
    
    // 저장 파일명으로 조회
    Optional<Common_File> findBySaveFileName(String saveFileName);
    
    // 원본 파일명으로 조회
    List<Common_File> findByOrgFileName(String orgFileName);
    
    // 파일명으로 검색
    List<Common_File> findByFileNameContainingIgnoreCase(String fileName);
    
    // 파일 경로로 조회
    List<Common_File> findByFilePathContaining(String filePath);
    
    // 등록일자별 조회
    @Query("SELECT f FROM Common_File f WHERE DATE(STR_TO_DATE(f.regDt, '%Y-%m-%d %H:%i:%s')) = :regDate")
    List<Common_File> findByRegDate(@Param("regDate") String regDate);
    
    // 최근 등록된 파일들 조회
    @Query("SELECT f FROM Common_File f ORDER BY STR_TO_DATE(f.regDt, '%Y-%m-%d %H:%i:%s') DESC")
    List<Common_File> findRecentFiles();
}