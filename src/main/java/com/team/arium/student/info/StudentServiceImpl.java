package com.team.arium.student.info;

import org.springframework.stereotype.Service;

import com.team.arium.domain.Std_Info;

//StudentService를 받음, 실제 구현
@Service
public class StudentServiceImpl implements StudentService {

	private final StudentInfoRepo studentInfoRepo;
	
	public StudentServiceImpl(StudentInfoRepo studentInfoRepo) { //생성자를 통해 repo 주입받음
		this.studentInfoRepo = studentInfoRepo;
	}
	
	/*name, phone, email을 기준으로 DB에서 학생 정보 검색 
	 *검색 결과가 StudentInfoRepo의 Optional<Std_info>로 반환됨
	 *결과가 있으며 .map()으로 stdNo(학번)을, 없으면 orElse(null)로 리턴*/
	@Override
	public String findStudentId(String stdNm, String stdTellno, String stdEmlAddr) {
		return studentInfoRepo.findByStdNmAndStdTellnoAndStdEmlAddr(stdNm, stdTellno, stdEmlAddr)
                .map(Std_Info::getStdNo)
                .orElse(null);
	}
}
