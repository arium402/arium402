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
@Table(name = "core_cpt_info")
public class Core_CptInfo {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ccl_id")
    private Long cclId;
    
    @Column(name = "ccl_cd", length = 10, nullable = false, unique = true)
    private String cclCd;
    
    @Column(name = "up_ccl_id")
    private Integer upCclId;
    
    @Column(name = "ccl_nm", length = 100, nullable = false)
    private String cclNm;
    
    @Column(name = "ccl_desc", length = 500)
    private String cclDesc;
    
    @CreationTimestamp
    @Column(name = "reg_dt", nullable = false, updatable = false)
    private LocalDateTime regDt;
    
    @UpdateTimestamp
    @Column(name = "upd_dt", insertable = false)
    private LocalDateTime updDt;
    
    @Builder.Default
    @OneToMany(mappedBy = "coreCptInfo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Core_CptQst> questions = new ArrayList<>();
}