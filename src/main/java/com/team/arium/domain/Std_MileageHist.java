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
@Table(name = "STD_MILEAGE_HIST",
       uniqueConstraints = @UniqueConstraint(columnNames = {"STD_ID", "CMP_ID", "SUR_EVAL_ID"}))
public class Std_MileageHist {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MLG_ID")
    private Long mlgId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "STD_ID", nullable = false)
    private Std_Info stdInfo;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CMP_ID", nullable = false)
    private Ncs_CmpInfo ncsCmpInfo;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SUR_EVAL_ID", nullable = false)
    private Dgstfn_Eval dgstfnEval;
    
    @Column(name = "MLG_SCORE", nullable = false)
    private Integer mlgScore;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MLG_ADD_CD", nullable = false)
    private Common_Code mlgAddCd;
    
    @Column(name = "MLG_DT", nullable = false)
    private LocalDate mlgDt;
    
    @CreationTimestamp
    @Column(name = "REG_DT", nullable = false, updatable = false)
    private LocalDateTime regDt;
    
    @UpdateTimestamp
    @Column(name = "UPD_DT", insertable = false)
    private LocalDateTime updDt;
}