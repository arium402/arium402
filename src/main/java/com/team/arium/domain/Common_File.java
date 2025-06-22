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
@Table(name = "COM_FILE")
public class Common_File {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "FILE_ID")
    private Long fileId;
    
    @Column(name = "ORG_FILE_NAME", length = 100, nullable = false)
    private String orgFileName;
    
    @Column(name = "SAVE_FILE_NAME", length = 100, nullable = false, unique = true)
    private String saveFileName;
    
    @Column(name = "FILE_NAME", length = 100, nullable = false)
    private String fileName;
    
    @Column(name = "FILE_PATH", length = 500, nullable = false)
    private String filePath;
    
    @CreationTimestamp
    @Column(name = "REG_DT", nullable = false, updatable = false)
    private LocalDateTime regDt;
}