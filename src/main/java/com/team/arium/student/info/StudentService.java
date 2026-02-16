package com.team.arium.student.info;

import org.springframework.stereotype.Service;

@Service
public interface StudentService {

	String findStudentId(String stdNm, String stdTellno, String stdEmlAddr);
}
