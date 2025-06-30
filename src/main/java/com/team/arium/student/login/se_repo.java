package com.team.arium.student.login;

import org.springframework.data.jpa.repository.JpaRepository;

import com.team.arium.domain.User_Info;

public interface se_repo extends JpaRepository<User_Info, Integer> {
	//로그인에서 사용하는 JPA Query
	User_Info findByEmplNo(String userid);
}