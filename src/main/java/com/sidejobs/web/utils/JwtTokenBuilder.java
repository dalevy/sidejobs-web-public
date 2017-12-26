package com.sidejobs.web.utils;

import java.util.HashMap;
import java.util.Map;

import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

public class JwtTokenBuilder {

	private String key;
	private Map<String,String> claims;
	private final long TOKEN_TIMEOUT_MILLIS = 100000;
		
	public JwtTokenBuilder(String key) {
		this.key = key;
	}
	
	public void addClaim(String key, String value)
	{
		if(claims == null)
			claims = new HashMap<String,String>();
		
		claims.put(key, value);
	}
	
	public String getJwt() {
		
		JwtBuilder jwt = Jwts.builder();
		
		for(Map.Entry<String, String> cursor: claims.entrySet())
			jwt.claim(cursor.getKey(),cursor.getValue());
		
		jwt.claim("expireTime",System.currentTimeMillis()+TOKEN_TIMEOUT_MILLIS);
		
		
		jwt.signWith(SignatureAlgorithm.HS512,key);
		
		return jwt.compact();
		
	}
}
