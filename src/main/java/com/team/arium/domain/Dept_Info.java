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
@Table(name = "dept_info")
public class Dept_Info {
//학과 정보    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "dept_id")
    private Integer deptId;
    
    @Column(name = "dept_cd", length = 20, nullable = false, unique = true)
    private String deptCd;
    
    @Column(name = "dept_nm", length = 100, nullable = false)
    private String deptNm;
    
    @Column(name = "college", length = 50, nullable = false)
    private String college;
    
    @CreationTimestamp
    @Column(name = "reg_dt", nullable = false, updatable = false)
    private String regDt;
}