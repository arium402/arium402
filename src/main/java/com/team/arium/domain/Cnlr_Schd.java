package com.team.arium.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "CNLR_SCHD",
       uniqueConstraints = @UniqueConstraint(columnNames = {"EMPL_ID", "WORK_YEAR", "WORK_MONTH", "WORK_DAY"}))
public class Cnlr_Schd {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SCHD_ID")
    private Long schdId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "EMPL_ID", nullable = false)
    private Empl_Info emplInfo;
    
    @Column(name = "WORK_YEAR", length = 4, nullable = false)
    private String workYear;
    
    @Column(name = "WORK_MONTH", length = 2, nullable = false)
    private String workMonth;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "WORK_DAY", nullable = false)
    private Common_Code workDay;
    
    @Column(name = "START_TIME", nullable = false)
    private LocalTime startTime;
    
    @Column(name = "END_TIME", nullable = false)
    private LocalTime endTime;
    
    @CreationTimestamp
    @Column(name = "REG_DT", nullable = false, updatable = false)
    private LocalDateTime regDt;
    
    @UpdateTimestamp
    @Column(name = "UPD_DT", insertable = false)
    private LocalDateTime updDt;
    
    @Builder.Default
    @OneToMany(mappedBy = "cnlrSchd", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Cnlr_SchdSlot> slots = new ArrayList<>();
}