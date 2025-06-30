package com.team.arium.admin;

import java.util.List;

import com.team.arium.DTO.admin_counselor_DTO;
import com.team.arium.domain.Empl_Info;

public interface admin_service {

	List<admin_counselor_DTO> getCounselorDtoList();
	//재직,퇴사를 검토하는 메소드
	List<admin_counselor_DTO> getCounselorDtoList(int statcode);
	//상담사 개인정보 확인 메소드
	List<admin_counselor_DTO> my_counselor_info(int statcode);
	
	//상담사 로그인 확인 메소드 부분
	List<admin_counselor_DTO> my_counselor_info2(String sno);
	
	List<admin_counselor_DTO> conunselorlist_data();
	//Empl_Info insert_counselor(admin_counselor_DTO admindto);
}
