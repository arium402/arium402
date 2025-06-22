package com.team.arium.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "STD_INFO")
public class Std_Info {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "STD_ID")
    private Long stdId;
    
    @Column(name = "STD_NO", length = 20, nullable = false, unique = true)
    private String stdNo;
    
    @Column(name = "STD_NM", length = 100, nullable = false)
    private String stdNm;
    
    @Column(name = "BIRTH_DT", nullable = false)
    private LocalDate birthDt;
    
    @Column(name = "STD_GENDER", length = 10, nullable = false)
    private String stdGender;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DEPT_ID", nullable = false)
    private Dept_Info deptInfo;
    
    @Column(name = "SCH_YR", nullable = false)
    private Integer schYr;
    
    @Column(name = "ENTR_DT", nullable = false)
    private LocalDateTime entrDt;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "STD_STAT_CD", nullable = false)
    private Common_Code stdStatCd;
    
    @Column(name = "BANK_ACNT", length = 30, nullable = false)
    private String bankAcnt;
    
    @Column(name = "BANK_NM", length = 50, nullable = false)
    private String bankNm;
    
    @Column(name = "DEPOSITOR", length = 50, nullable = false)
    private String depositor;
    
    @Column(name = "ZIP", length = 6, nullable = false)
    private String zip;
    
    @Column(name = "ADDR", length = 200, nullable = false)
    private String addr;
    
    @Column(name = "DADDR", length = 200, nullable = false)
    private String daddr;
    
    @Column(name = "STD_TELLNO", length = 20, nullable = false, unique = true)
    private String stdTellno;
    
    @Column(name = "STD_EML_ADDR", length = 100, nullable = false, unique = true)
    private String stdEmlAddr;
    
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
