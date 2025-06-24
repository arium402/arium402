package com.team.arium.counselor.counsel;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.team.arium.domain.Std_Info;

public interface counselor_repo extends JpaRepository<Std_Info, Long>{
	
	
	List<Std_Info> findAllByOrderByStdId(); 

}
