package com.team.arium.admin;

import org.springframework.data.jpa.repository.JpaRepository;

import com.team.arium.domain.Empl_Info;


public interface admin_repo extends JpaRepository<Empl_Info, Integer>{

}
