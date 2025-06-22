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
@Table(name = "USER_INFO")
public class User_Info {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "USER_ID")
    private Long userId;
    
    @Column(name = "STD_NO", length = 20, unique = true)
    private String stdNo;
    
    @Column(name = "EMPL_NO", length = 20, unique = true)
    private String emplNo;
    
    @Column(name = "LOGIN_PW", length = 100, nullable = false)
    private String loginPw;
    
    @Column(name = "USER_ROLE", length = 10, nullable = false)
    private String userRole;
    
    @CreationTimestamp
    @Column(name = "REG_DT", nullable = false, updatable = false)
    private LocalDateTime regDt;
    
    @UpdateTimestamp
    @Column(name = "UPD_DT", insertable = false)
    private LocalDateTime updDt;
}