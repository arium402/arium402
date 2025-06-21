package com.team.arium.domain;

import lombok.*;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Ncs_CclRelId implements Serializable {
    private Long prgId;
    private Long cclId;
}