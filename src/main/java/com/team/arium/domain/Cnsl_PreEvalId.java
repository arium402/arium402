package com.team.arium.domain;

import lombok.*;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Cnsl_PreEvalId implements Serializable {
    private String preEvalId;
    private Long preQstId;
}