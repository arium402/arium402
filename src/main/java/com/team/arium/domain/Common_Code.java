package com.team.arium.domain;

import jakarta.persistence.*;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "COMMON_CODE")
public class Common_Code {
    
    @Id
    @Column(name = "CODE_ID")
    private Integer codeId;
    
    @Column(name = "CODE_TYPE", length = 30, nullable = false)
    private String codeType;
    
    @Column(name = "CODE", length = 60, nullable = false)
    private String code;
    
    @Column(name = "CODE_DESC", length = 100)
    private String codeDesc;
}