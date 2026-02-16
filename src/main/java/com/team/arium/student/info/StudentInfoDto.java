package com.team.arium.student.info;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StudentInfoDto {
	private String studentName;
	private String studentPhone;
	private String studentEmail;
}
