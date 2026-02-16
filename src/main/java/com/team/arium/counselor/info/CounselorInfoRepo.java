package com.team.arium.counselor.info;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.team.arium.domain.Empl_Info;

public interface CounselorInfoRepo extends JpaRepository<Empl_Info, Integer>{

	Optional<Empl_Info> findByEmplNameAndEmplTellnoAndEmplEmlAddr(String emplName, String emplTellno, String emplEmlAddr);
	
}
