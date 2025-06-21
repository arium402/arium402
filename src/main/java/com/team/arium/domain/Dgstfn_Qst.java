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
@Table(name = "DGSTFN_QST",
       uniqueConstraints = @UniqueConstraint(columnNames = {"SURVEY_ID", "SUR_ORD"}))
public class Dgstfn_Qst {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SUR_ID")
    private Long surId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SURVEY_ID", nullable = false)
    private Dgstfn_Info dgstfnInfo;
    
    @Column(name = "SUR_CONTENT", length = 500, nullable = false)
    private String surContent;
    
    @Column(name = "SUR_ORD", nullable = false)
    private Integer surOrd;
    
    @CreationTimestamp
    @Column(name = "REG_DT", nullable = false, updatable = false)
    private LocalDateTime regDt;
    
    @UpdateTimestamp
    @Column(name = "UPD_DT", insertable = false)
    private LocalDateTime updDt;
}