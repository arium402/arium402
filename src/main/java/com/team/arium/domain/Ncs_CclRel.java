package com.team.arium.domain;

import jakarta.persistence.*;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "NCS_CCL_REL")
@IdClass(Ncs_CclRelId.class)
public class Ncs_CclRel {
    
    @Id
    @Column(name = "PRG_ID")
    private Long prgId;
    
    @Id
    @Column(name = "CCL_ID")
    private Long cclId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PRG_ID", insertable = false, updatable = false)
    private Ncs_PrgInfo ncsPrgInfo;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CCL_ID", insertable = false, updatable = false)
    private Core_CptInfo coreCptInfo;
    
    @Column(name = "CCL_SCORE", nullable = false)
    private Integer cclScore;
}