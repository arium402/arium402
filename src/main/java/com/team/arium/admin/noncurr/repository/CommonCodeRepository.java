// 4. CommonCodeRepository
package com.team.arium.admin.noncurr.repository;

import com.team.arium.domain.Common_Code;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface CommonCodeRepository extends JpaRepository<Common_Code, Integer> {
    
    @Query("SELECT c FROM Common_Code c WHERE c.codeType = :codeType AND c.code = :code")
    Optional<Common_Code> findByCodeTypeAndCode(@Param("codeType") String codeType, @Param("code") String code);
}