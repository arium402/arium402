package com.team.arium.admin;

import java.util.List;

import com.team.arium.DTO.admin_counselor_DTO;
import com.team.arium.domain.Empl_Info;

public interface admin_service {

	List<admin_counselor_DTO> getCounselorDtoList();
	//재직,퇴사를 검토하는 메소드
	List<admin_counselor_DTO> getCounselorDtoList(int statcode);
	
	
	//Empl_Info insert_counselor(admin_counselor_DTO admindto);
}
