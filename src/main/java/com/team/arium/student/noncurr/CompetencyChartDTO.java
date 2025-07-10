package com.team.arium.student.noncurr;

import lombok.Builder;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompetencyChartDTO {
    private List<CompetencyData> programCompetencies;
    private List<StudentCompetencyData> studentCompetencies;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class CompetencyData {
    private String competencyName;
    private Integer score;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class StudentCompetencyData {
    private String competencyName;
    private Integer currentScore;
    private Integer programScore;
}