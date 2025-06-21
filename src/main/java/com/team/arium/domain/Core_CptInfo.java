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
@Table(name = "CORE_CPT_INFO")
public class Core_CptInfo {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CCL_ID")
    private Long cclId;
    
    @Column(name = "CCL_CD", length = 10, nullable = false, unique = true)
    private String cclCd;
    
    @Column(name = "UP_CCL_ID")
    private Integer upCclId;
    
    @Column(name = "CCL_NM", length = 100, nullable = false)
    private String cclNm;
    
    @Column(name = "CCL_DESC", length = 500)
    private String cclDesc;
    
    @CreationTimestamp
    @Column(name = "REG_DT", nullable = false, updatable = false)
    private LocalDateTime regDt;
    
    @UpdateTimestamp
    @Column(name = "UPD_DT", insertable = false)
    private LocalDateTime updDt;
    
    @Builder.Default
    @OneToMany(mappedBy = "coreCptInfo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Core_CptQst> questions = new ArrayList<>();
}