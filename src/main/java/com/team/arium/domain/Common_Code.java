package com.team.arium.domain;

import jakarta.persistence.*;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "common_code")
public class Common_Code {
//공통 코드 
	
    @Id
    @Column(name = "code_id")
    private Integer codeId;
    
    @Column(name = "code_type", length = 30, nullable = false)
    private String codeType;
    
    @Column(name = "code", length = 60, nullable = false)
    private String code;
    
    @Column(name = "code_desc", length = 100)
    private String codeDesc;
}