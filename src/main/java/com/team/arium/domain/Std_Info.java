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
@Table(name = "std_info")
public class Std_Info {
//학생 정보 
	
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "std_id")
    private Integer stdId;
    
    @Column(name = "std_no", length = 20, nullable = false, unique = true)
    private String stdNo;
    
    @Column(name = "std_nm", length = 100, nullable = false)
    private String stdNm;
    
    @Column(name = "birth_dt", nullable = false)
    private String birthDt;
    
    @Column(name = "std_gender", length = 10, nullable = false)
    private String stdGender;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dept_id", nullable = false)
    private Dept_Info deptInfo;
    
    @Column(name = "sch_yr", nullable = false)
    private Integer schYr;
    
    @Column(name = "entr_dt", nullable = false)
    private String entrDt;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "std_stat_cd", nullable = false)
    private Common_Code stdStatCd;
    
    @Column(name = "bank_acnt", length = 30, nullable = false)
    private String bankAcnt;
    
    @Column(name = "bank_nm", length = 50, nullable = false)
    private String bankNm;
    
    @Column(name = "depositor", length = 50, nullable = false)
    private String depositor;
    
    @Column(name = "zip", length = 6, nullable = false)
    private String zip;
    
    @Column(name = "addr", length = 200, nullable = false)
    private String addr;
    
    @Column(name = "daddr", length = 200, nullable = false)
    private String daddr;
    
    @Column(name = "std_tellno", length = 20, nullable = false, unique = true)
    private String stdTellno;
    
    @Column(name = "std_eml_addr", length = 100, nullable = false, unique = true)
    private String stdEmlAddr;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "file_id")
    private Common_File comFile;
    
    @CreationTimestamp
    @Column(name = "reg_dt", nullable = false, updatable = false)
    private String regDt;
    
    @UpdateTimestamp
    @Column(name = "upd_dt", insertable = false)
    private String updDt;
}
