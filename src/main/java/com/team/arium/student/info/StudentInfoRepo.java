package com.team.arium.student.info;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.team.arium.domain.Std_Info;

public interface StudentInfoRepo extends JpaRepository<Std_Info, Integer> {

	Optional<Std_Info> findByStdNmAndStdTellnoAndStdEmlAddr(String stdNm, String stdTellno, String stdEmlAddr);
}
