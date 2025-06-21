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
@Table(name = "CORE_CPT_QST",
       uniqueConstraints = @UniqueConstraint(columnNames = {"CCL_ID", "QST_ORD"}))
public class Core_CptQst {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "QST_ID")
    private Long qstId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CCL_ID", nullable = false)
    private Core_CptInfo coreCptInfo;
    
    @Column(name = "QST_CONTENT", length = 500, nullable = false)
    private String qstContent;
    
    @Column(name = "QST_ORD", nullable = false)
    private Integer qstOrd;
    
    @CreationTimestamp
    @Column(name = "REG_DT", nullable = false, updatable = false)
    private LocalDateTime regDt;
    
    @UpdateTimestamp
    @Column(name = "UPD_DT", insertable = false)
    private LocalDateTime updDt;
}