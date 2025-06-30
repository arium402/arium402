// 4. 파일 Repository
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
    
    // 원본 파일명으로 조회
    List<Common_File> findByOrgFileName(String orgFileNm);
    
    // 저장 파일명으로 조회
    Optional<Common_File> findBySaveFileName(String saveFileNm);
    
    // 파일 타입별 조회
    //List<Common_File> findByFileType(String fileType);
    
    /** 파일명 자체로 조회(필요하면 추가) → fileName 프로퍼티 */
    List<Common_File> findByFileName(String fileName);
    
    // 파일 경로로 조회
    List<Common_File> findByFilePath(String filePath);
    
    
    
    // 이미지 파일만 조회
    //@Query("SELECT f FROM Common_File f WHERE f.fileType LIKE 'image%'")
    //List<Common_File> findImageFiles();
    
    // 특정 크기 이하 파일 조회
   // @Query("SELECT f FROM Common_File f WHERE f.fileSize <= :maxSize")
    //List<Common_File> findByFileSizeLessThanEqual(@Param("maxSize") Long maxSize);
}