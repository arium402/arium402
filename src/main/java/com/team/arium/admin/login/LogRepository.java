package com.team.arium.admin.login;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.team.arium.domain.User_Info;

public interface LogRepository extends JpaRepository<User_Info, Integer> {
	Optional<User_Info> findByEmplNo(String emplNo); //로그인 ID로 조회
	
}
