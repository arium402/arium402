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
@Table(name = "CNSL_PRE_QST_OPT",
       uniqueConstraints = @UniqueConstraint(columnNames = {"PRE_QST_ID", "OPT_ORD"}))
public class Cnsl_PreQstOpt {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PRE_OPT_ID")
    private Long preOptId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PRE_QST_ID", nullable = false)
    private Cnsl_PreQst cnslPreQst;
    
    @Column(name = "OPT_CONTENT", length = 200, nullable = false)
    private String optContent;
    
    @Column(name = "OPT_ORD", nullable = false)
    private Integer optOrd;
    
    @CreationTimestamp
    @Column(name = "REG_DT", nullable = false, updatable = false)
    private LocalDateTime regDt;
    
    @UpdateTimestamp
    @Column(name = "UPD_DT", insertable = false)
    private LocalDateTime updDt;
}