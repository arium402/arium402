// 5. CommonFileRepository (파일 업로드용)
package com.team.arium.admin.noncurr.repository;

import com.team.arium.domain.Common_File;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommonFileRepository extends JpaRepository<Common_File, Integer> {
}