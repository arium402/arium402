package com.team.arium.counselor.info;

import org.springframework.stereotype.Service;

@Service
public interface CounselorService {

	String findCounselorId(String emplName, String emplTellno, String emplEmlAddr);
}
