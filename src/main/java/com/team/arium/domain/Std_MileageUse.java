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
@Table(name = "STD_MILEAGE_USE",
       uniqueConstraints = @UniqueConstraint(columnNames = {"STD_ID", "APLY_DT"}))
public class Std_MileageUse {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MLG_USE_ID")
    private Long mlgUseId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "STD_ID", nullable = false)
    private Std_Info stdInfo;
    
    @Column(name = "APLY_DT", nullable = false)
    private LocalDateTime aplyDt;
    
    @Column(name = "APLY_MLG_SCORE", nullable = false)
    private Integer aplyMlgScore;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MLG_USE_CD", nullable = false)
    private Common_Code mlgUseCd;
    
    @Column(name = "PAY_DT")
    private LocalDate payDt;
    
    @Column(name = "PAY_MONEY")
    private Integer payMoney;
    
    @CreationTimestamp
    @Column(name = "REG_DT", nullable = false, updatable = false)
    private LocalDateTime regDt;
    
    @UpdateTimestamp
    @Column(name = "UPD_DT", insertable = false)
    private LocalDateTime updDt;
}