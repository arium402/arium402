package com.team.arium.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "DEPT_INFO")
public class Dept_Info {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "DEPT_ID")
    private Long deptId;
    
    @Column(name = "DEPT_CD", length = 20, nullable = false, unique = true)
    private String deptCd;
    
    @Column(name = "DEPT_NM", length = 100, nullable = false)
    private String deptNm;
    
    @Column(name = "COLLEGE", length = 50, nullable = false)
    private String college;
    
    @CreationTimestamp
    @Column(name = "REG_DT", nullable = false, updatable = false)
    private LocalDateTime regDt;
}