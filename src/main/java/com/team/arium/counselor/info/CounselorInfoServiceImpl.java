package com.team.arium.counselor.info;

import org.springframework.stereotype.Service;

import com.team.arium.domain.Empl_Info;

@Service
public class CounselorInfoServiceImpl implements CounselorService {

	private final CounselorInfoRepo counselorInfoRepo;
	
	public CounselorInfoServiceImpl(CounselorInfoRepo counselorInfoRepo) {
		this.counselorInfoRepo = counselorInfoRepo;
	}
	
	@Override
	public String findCounselorId(String emplName, String emplTellno, String emplEmlAddr) {
		return counselorInfoRepo.findByEmplNameAndEmplTellnoAndEmplEmlAddr(emplName, emplTellno, emplEmlAddr)
                .map(Empl_Info::getEmplNo)
                .orElse(null);
	}
}
