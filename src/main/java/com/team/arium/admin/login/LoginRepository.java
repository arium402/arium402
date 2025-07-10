package com.team.arium.admin.login;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.team.arium.domain.User_Info;

public interface LoginRepository extends JpaRepository<User_Info, Integer> {
	Optional<User_Info> findByEmplNo(String emplNo); //로그인 ID로(관리자 id, 사번) 조회
	Optional<User_Info> findByStdNo(String stdNo); //학번 조회
	
	//중복 로그인 방지용, 존재 여부 확인
	boolean existsByEmplNo(String emplNo);
	boolean existsByStdNo(String stdNo); 
}
