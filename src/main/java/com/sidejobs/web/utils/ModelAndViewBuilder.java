package com.sidejobs.web.utils;

import java.text.ParseException;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.web.servlet.ModelAndView;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

public class ModelAndViewBuilder {
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	ModelAndView mav;
	Map<String,String> cookies;
	Environment env;
	
	public ModelAndViewBuilder(String view)
	{
		mav = new ModelAndView(view);
	}
	
	public ModelAndViewBuilder(String view, Environment env, Map<String,String> cookies)
	{
		this(view);
		this.cookies = cookies;
		this.env = env;
	}
	
	public ModelAndView buildModelAndView()
	{
		//standard template objects
		 logger.debug("User not authenticated, generating default ModelAndView object");
		 mav.addObject("username", "Login");
		 mav.addObject("authenticated",false); //so thymeleaf knows if this user is logged in
		 mav.addObject("messages", "0");
		 
		if(cookies == null || cookies.isEmpty())
			return mav;
		
		for (Entry<String, String> entry : cookies.entrySet())
	          setModelAttributes(entry.getKey(),entry.getValue());

		return mav;
	}
	
	private void setModelAttributes(String key, String val)
	{
		switch(key)
		{
			case "token":
				if(!val.isEmpty() || !val.equalsIgnoreCase("none"))
				{
					String secretKey = env.getProperty("app.jwt.key");
					
					UserProfileBuilder ubuilder = new UserProfileBuilder(secretKey);
					
					try {
						
						SignedJWT jwt = ubuilder.getSignedJWT(val);
						
						if(jwt == null)
							return;
						
						logger.debug("Found JWT token with value: "+val);
						JWTClaimsSet claims = jwt.getJWTClaimsSet();

						mav.addObject("id",claims.getClaim("id"));
						mav.addObject("authenticated",true);
						mav.addObject("role",claims.getClaim("role"));
						mav.addObject("username",claims.getClaim("firstname")+" "+((String)claims.getClaim("lastname")).substring(0, 1)+".");
						
						logger.debug("Adding mav objects - id: "+claims.getClaim("id")+" authenticated: "+true);
						
						//.claim("id",reg.getUserId())
						//.claim("user", userName)
						//.claim("role", "student")
						//.claim("allowed", "true")
						//.claim("status", "active")
						//System.out.println("sub : "+jwt.getJWTClaimsSet().getSubject());
						//System.out.println("id : "+jwt.getJWTClaimsSet().getClaim("id"));
						//System.out.println("role : "+jwt.getJWTClaimsSet().getClaim("role"));
						//System.out.println("time : "+jwt.getJWTClaimsSet().getExpirationTime());
															
					} catch (JOSEException | ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
						
			break;
			default:
				System.out.println("WARNING: Unknown Cookie key value detected when attempting to build Model");
			break;
		}
	}
	
	
}
