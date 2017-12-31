package com.sidejobs.web.utils;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import com.nimbusds.jose.EncryptionMethod;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWEAlgorithm;
import com.nimbusds.jose.JWEHeader;
import com.nimbusds.jose.JWEObject;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.KeyLengthException;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.crypto.DirectEncrypter;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.sidejobs.api.entities.User;

import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

public class JwtTokenBuilder {

	private byte[] decodedKey;
	private User user;
	private String appRoot;

	public JwtTokenBuilder(String key, String appRoot, User user) {
		this.decodedKey = key.getBytes();
		this.user = user;
		this.appRoot = appRoot;
	}
	
	public String getJwt() throws JOSEException {
	
		String token = null;
		
		// rebuild key using SecretKeySpec
				SecretKey originalKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES"); 
							
				JWSSigner signer = new MACSigner(originalKey);
				JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
						.subject(user.getId())
						.issueTime(new Date())
						.issuer(appRoot)
						.claim("id",user.getId())
						.claim("role", user.getRole())
						.claim("firstname", user.getFirst_name())
						.claim("lastname",user.getLast_name())
						.claim("status", user.getStatus())
						.build();
				
				SignedJWT signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claimsSet);
				signedJWT.sign(signer);
				
			
				// Create JWE object with signed JWT as payload
				JWEObject jweObject = new JWEObject(
				    new JWEHeader.Builder(JWEAlgorithm.DIR, EncryptionMethod.A256CBC_HS512)
				        .contentType("JWT") // required to signal nested JWT
				        .build(),
				    new Payload(signedJWT));
				
				jweObject.encrypt(new DirectEncrypter(originalKey.getEncoded()));
				
				
				// Serialise to JWE compact form
				token = jweObject.serialize();
				
				return token;
		
	}
}
