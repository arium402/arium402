package com.team.arium.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "EMPL_INFO")
public class Empl_Info {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "EMPL_ID")
    private Long emplId;
    
    @Column(name = "EMPL_NO", length = 20, nullable = false, unique = true)
    private String emplNo;
    
    @Column(name = "EMPL_NAME", length = 100, nullable = false)
    private String emplName;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CNSL_CD", nullable = false)
    private Common_Code cnslCd;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "EMPL_STAT_CD", nullable = false)
    private Common_Code emplStatCd;
    
    @Column(name = "EMPL_TELLNO", length = 20, nullable = false, unique = true)
    private String emplTellno;
    
    @Column(name = "EMPL_EML_ADDR", length = 100, nullable = false, unique = true)
    private String emplEmlAddr;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FILE_ID")
    private Common_File comFile;
    
    @CreationTimestamp
    @Column(name = "REG_DT", nullable = false, updatable = false)
    private LocalDateTime regDt;
    
    @UpdateTimestamp
    @Column(name = "UPD_DT", insertable = false)
    private LocalDateTime updDt;
}