package com.sidejobs.web.utils;

import java.text.ParseException;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWEObject;
import com.nimbusds.jose.KeyLengthException;
import com.nimbusds.jose.crypto.DirectDecrypter;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.SignedJWT;

public class UserProfileBuilder {

	private String key;
	
	public UserProfileBuilder(String key)
	{
		this.key = key;
	}
	
	public SignedJWT getSignedJWT(String token) throws KeyLengthException, JOSEException, ParseException
	{
		System.out.println("Got token ==>"+token);
		SignedJWT signedJWT= null;
		
		if(token == null || token.equalsIgnoreCase("none") || token.equalsIgnoreCase("") || token.isEmpty()) {
			System.out.println("UserProfileBuilder: No token detected");
			return signedJWT;
			
		}
		
		System.out.println("Found token: "+token);
		
		JWEObject jweObject = JWEObject.parse(token);
		
		// decode the base64 encoded string
		byte[] decodedKey = key.getBytes();
		
		// rebuild key using SecretKeySpec
		SecretKey originalKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES"); 
		
		jweObject.decrypt(new DirectDecrypter(originalKey.getEncoded()));
		
		System.out.println("Exctracting payload..");
		// Extract payload
		signedJWT = jweObject.getPayload().toSignedJWT();
		
		if(!signedJWT.verify(new MACVerifier(originalKey.getEncoded()))){
			System.out.println("Unable to verify jwt!!");
		}else {
			System.out.println("JWT verified");
		}
		
		//System.out.println("sub : "+signedJWT.getJWTClaimsSet().getSubject());
		//System.out.println("id : "+signedJWT.getJWTClaimsSet().getClaim("id"));
		//System.out.println("role : "+signedJWT.getJWTClaimsSet().getClaim("role"));
		//System.out.println("time : "+signedJWT.getJWTClaimsSet().getExpirationTime());
		
		return signedJWT;
	}
}
