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
@Table(name = "user_info")
public class User_Info {
//사용자 정보   
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Integer userId;
    
    @Column(name = "std_no", length = 20, unique = true)
    private String stdNo;
    
    @Column(name = "empl_no", length = 20, unique = true)
    private String emplNo;
    
    @Column(name = "login_pw", length = 100, nullable = false)
    private String loginPw;
    
    @Column(name = "user_role", length = 30, nullable = false)
    private String userRole;
    
    @CreationTimestamp
    @Column(name = "reg_dt", nullable = false, updatable = false)
    private String regDt;
    
    @UpdateTimestamp
    @Column(name = "upd_dt", insertable = false)
    private String updDt;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "empl_no", referencedColumnName = "empl_no", insertable = false, updatable = false)
    private Empl_Info emplInfo;
}