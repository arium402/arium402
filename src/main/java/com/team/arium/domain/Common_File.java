package com.team.arium.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "com_file")
public class Common_File {
//공통 파일 
	
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "file_id")
    private Integer fileId;
    
    @Column(name = "org_file_name", length = 100, nullable = false)
    private String orgFileName;
    
    @Column(name = "save_file_name", length = 100, nullable = false, unique = true)
    private String saveFileName;
    
    @Column(name = "file_name", length = 100, nullable = false)
    private String fileName;
    
    @Column(name = "file_path", length = 500, nullable = false)
    private String filePath;
    
    @CreationTimestamp
    @Column(name = "reg_dt", nullable = false, updatable = false)
    private String regDt;
}