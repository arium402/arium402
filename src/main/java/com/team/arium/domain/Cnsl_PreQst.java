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
@Table(name = "CNSL_PRE_QST",
       uniqueConstraints = @UniqueConstraint(columnNames = {"PRE_SURVEY_ID", "PRE_QST_ORD"}))
public class Cnsl_PreQst {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PRE_QST_ID")
    private Long preQstId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PRE_SURVEY_ID", nullable = false)
    private Cnsl_PreInfo cnslPreInfo;
    
    @Column(name = "PRE_QST_CONTENT", length = 500, nullable = false)
    private String preQstContent;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PRE_QST_TYPE", nullable = false)
    private Common_Code preQstType;
    
    @Column(name = "PRE_QST_ORD", nullable = false)
    private Integer preQstOrd;
    
    @CreationTimestamp
    @Column(name = "REG_DT", nullable = false, updatable = false)
    private LocalDateTime regDt;
    
    @UpdateTimestamp
    @Column(name = "UPD_DT", insertable = false)
    private LocalDateTime updDt;
    
    @Builder.Default
    @OneToMany(mappedBy = "cnslPreQst", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Cnsl_PreQstOpt> options = new ArrayList<>();
}
