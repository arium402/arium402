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
@Table(name = "empl_info")
public class Empl_Info {
//상담사 정보 
	
	//상담사 아이디 
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "empl_id")
    private Long emplId;
    
    //사번
    @Column(name = "empl_no", length = 20, nullable = false, unique = true)
    private String emplNo;
    
    //상담사명
    @Column(name = "empl_name", length = 100, nullable = false)
    private String emplName;
    
    //상담코드 
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cnsl_cd", nullable = false)
    private Common_Code cnslCd;
    
    //상담사 상태코드 
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "empl_stat_cd", nullable = false)
    private Common_Code emplStatCd;
    
    //상담사 연락처 
    @Column(name = "empl_tellno", length = 20, nullable = false, unique = true)
    private String emplTellno;
    
    //상담사 이메일 
    @Column(name = "empl_eml_addr", length = 100, nullable = false, unique = true)
    private String emplEmlAddr;
    
    //파일번호 
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "file_id")
    private Common_File comFile;
    
    //등록일 
    @CreationTimestamp
    @Column(name = "reg_dt", nullable = false, updatable = false)
    private LocalDateTime regDt;
    
    //수정일 
    @UpdateTimestamp
    @Column(name = "upd_dt", insertable = false)
    private LocalDateTime updDt;
}