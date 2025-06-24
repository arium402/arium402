package com.team.arium.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "cnsl_pre_qst",
       uniqueConstraints = @UniqueConstraint(columnNames = {"pre_survey_id", "pre_qst_ord"}))
public class Cnsl_PreQst {
//상담 사전 검사 문항 
	
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pre_qst_id")
    private Integer preQstId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pre_survey_id", nullable = false)
    private Cnsl_PreInfo cnslPreInfo;
    
    @Column(name = "pre_qst_content", length = 500, nullable = false)
    private String preQstContent;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pre_qst_type", nullable = false)
    private Common_Code preQstType;
    
    @Column(name = "pre_qst_ord", nullable = false)
    private Integer preQstOrd;
    
    @CreationTimestamp
    @Column(name = "reg_dt", nullable = false, updatable = false)
    private LocalDateTime regDt;
    
    @UpdateTimestamp
    @Column(name = "upd_Dt", insertable = false)
    private LocalDateTime updDt;
    
    @Builder.Default
    @OneToMany(mappedBy = "cnslPreQst", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Cnsl_PreQstOpt> options = new ArrayList<>();
}
