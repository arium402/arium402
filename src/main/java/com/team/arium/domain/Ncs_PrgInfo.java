package com.team.arium.domain;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "ncs_prg_info")
public class Ncs_PrgInfo {
//비교과 정보 
	
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "prg_id")
    private Integer prgId;
    
    @Column(name = "prg_cd", length = 20, nullable = false, unique = true)
    private String prgCd;
    
    @Column(name = "prg_nm", length = 100, nullable = false)
    private String prgNm;
    
    @Column(name = "prg_desc", length = 500, nullable = false)
    private String prgDesc;
    
    // 모집기간 (신규 추가)
    @Column(name = "recruit_st_dt", nullable = false)
    private String recruitStDt;
    
    @Column(name = "recruit_end_dt", nullable = false)
    private String recruitEndDt;
    
    @Column(name = "prg_st_dt", nullable = false)
    private String prgStDt;
    
    @Column(name = "prg_end_dt", nullable = false)
    private String prgEndDt;
    
    @Column(name = "max_cnt", nullable = false)
    private Integer maxCnt;
    
    //운영부서
    @Column(name = "prg_dept", length = 100, nullable = false)
    private String prgDept;
    
    //운영문의 휴대번호
    @Column(name = "prg_tel", length = 20, nullable = false)
    private String prgTel;
    
    
    @Column(name = "mlg_def_score", nullable = false)
    private Integer mlgDefScore;
    
    @Column(name = "survey_dt", nullable = false)
    private String surveyDt;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "file_id")
    private Common_File comFile;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prg_stat_cd", nullable = false)
    private Common_Code prgStatCd;
    
    @CreationTimestamp
    @Column(name = "reg_dt", nullable = false, updatable = false)
    private String regDt;
    
    @UpdateTimestamp
    @Column(name = "upd_dt", insertable = false)
    private String updDt;
    
    // 저장 직전: regDt/updDt 모두 서버 시간으로 덮어쓰기
    @PrePersist
    public void onPrePersist() {
      String now = LocalDateTime
        .now(ZoneId.of("Asia/Seoul"))
        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
      this.regDt = now;
      this.updDt = now;
    }

    // 수정 직전: updDt만 서버 시간으로 덮어쓰기
    @PreUpdate
    public void onPreUpdate() {
      String now = LocalDateTime
        .now(ZoneId.of("Asia/Seoul"))
        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
      this.updDt = now;
    }
    
    
    @Builder.Default
    @OneToMany(mappedBy = "ncsPrgInfo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Ncs_PrgAply> applications = new ArrayList<>();
}