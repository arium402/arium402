package com.team.arium.student.login;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class logins extends SimpleUrlAuthenticationSuccessHandler {
	
	
	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {
		/*
		System.out.println(authentication.getName());
		System.out.println(authentication.getPrincipal());
		System.out.println(authentication.getDetails());
		System.out.println(authentication.getAuthorities());
		*/
		Collection<? extends GrantedAuthority> iter = authentication.getAuthorities();
		Iterator<? extends GrantedAuthority> all = iter.iterator();
		GrantedAuthority gauth = all.next();
		
		String user_level = gauth.getAuthority();
		//System.out.println(user_level);
		
		
		//String usenm = gauth.getUnames();
		super.onAuthenticationSuccess(request, response, authentication);
	}
}
