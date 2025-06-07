package com.becoder.service;

import org.springframework.security.core.userdetails.UserDetails;

import com.becoder.entity.User;

public interface JwtService {

	public String GenerateToken(User user);

	public String extractUsername(String token);
	
	public Boolean validateToken(String token,UserDetails userDetails);
}
