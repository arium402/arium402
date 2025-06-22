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
//상담사 정보 
	
	//상담사 아이디 
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "EMPL_ID")
    private Long emplId;
    
    //사번
    @Column(name = "EMPL_NO", length = 20, nullable = false, unique = true)
    private String emplNo;
    
    //상담사명
    @Column(name = "EMPL_NAME", length = 100, nullable = false)
    private String emplName;
    
    //상담코드 
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CNSL_CD", nullable = false)
    private Common_Code cnslCd;
    
    //상담사 상태코드 
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "EMPL_STAT_CD", nullable = false)
    private Common_Code emplStatCd;
    
    //상담사 연락처 
    @Column(name = "EMPL_TELLNO", length = 20, nullable = false, unique = true)
    private String emplTellno;
    
    //상담사 이메일 
    @Column(name = "EMPL_EML_ADDR", length = 100, nullable = false, unique = true)
    private String emplEmlAddr;
    
    //파일번호 
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FILE_ID")
    private Common_File comFile;
    
    //등록일 
    @CreationTimestamp
    @Column(name = "REG_DT", nullable = false, updatable = false)
    private LocalDateTime regDt;
    
    //수정일 
    @UpdateTimestamp
    @Column(name = "UPD_DT", insertable = false)
    private LocalDateTime updDt;
}