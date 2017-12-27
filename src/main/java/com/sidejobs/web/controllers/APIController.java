package com.sidejobs.web.controllers;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.google.gson.reflect.TypeToken;
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
import com.sidejobs.api.common.LoginResponse;
import com.sidejobs.api.common.RegistrationResponse;
import com.sidejobs.api.common.ResponseStatus;
import com.sidejobs.api.common.ResponseWrapper;
import com.sidejobs.api.entities.User;
import com.sidejobs.web.common.WebLoginResponse;
import com.sidejobs.web.common.WebResponse;
import com.sidejobs.web.common.WebResponseCode;
import com.sidejobs.web.utils.Deserializer;
import com.sidejobs.web.utils.ModelAndViewBuilder;
import com.sidejobs.web.utils.ParameterStringBuilder;
import com.sidejobs.web.utils.RestClient;
import com.sidejobs.web.utils.TimeFrame;

@Controller
public class APIController {

	@Autowired
	private Environment env;
	
	@RequestMapping(value = "/jobs/list/categories", method=RequestMethod.GET)
	@ResponseBody
	public String getCategories() {
		
		String data = "";
		String endpoint = env.getProperty("api.jobs.list.categories"); 
		try{
			String url = endpoint;
			RestClient client = new RestClient();
			data = client.get(url);
		}catch(IOException e) {
			System.out.println("There was an error");
			e.printStackTrace();
		}
				return data;
	}
	
	@RequestMapping(value= "/user/login", method=RequestMethod.POST)
	@ResponseBody
	public WebResponse<WebLoginResponse> userLogin(
			HttpServletResponse response,
			@RequestParam(value="email", required=true, defaultValue="None")String email,
			@RequestParam(value="password", required=true, defaultValue="None")String password
			) throws IOException, JOSEException{
		
		String endpoint = env.getProperty("api.users.login");
		
		Map<String,String> params = new HashMap<String,String>();
		params.put("email", email);
		params.put("password", password);
		
		System.out.println("user email: "+email+" user password: "+password);
		
		//get user from email and password
		String url = endpoint+"?"+ParameterStringBuilder.getParamsString(params);
		String data = new RestClient().post(url);
		
		System.out.println(data);
		
		Deserializer<ResponseWrapper<LoginResponse>> des = 
				new Deserializer<ResponseWrapper<LoginResponse>>
					(new TypeToken<ResponseWrapper<LoginResponse>>() {}.getType());
		
		ResponseWrapper<LoginResponse> res = des.deserialize(data);
		
		WebResponse<WebLoginResponse> webresponse = new WebResponse<WebLoginResponse>();
		
		//wrong email and/or password
		if(res.getStatus() == ResponseStatus.Failure)
		{
			WebLoginResponse wlr = new WebLoginResponse();
			wlr.setMessage("The email or password you have entered is incorrect, please try again");
			wlr.setSummary("Unable To Verify Login Info");
			wlr.setCode(WebResponseCode.Failure);
			webresponse.setData(wlr);
			return webresponse;
		}

				
		User user = res.getData().getUser();
		
		
		switch(user.getStatus())
		{
			//email account not verified
			case "Unverified":
				WebLoginResponse unverified = new WebLoginResponse();
				unverified.setCode(WebResponseCode.Failure);
				unverified.setSummary(env.getProperty("sidejobs.web.messages.account.unverified"));
				unverified.setMessage("Account Unverified ");
				webresponse.setData(unverified);
				return webresponse;	
			case "Locked":
				WebLoginResponse locked = new WebLoginResponse();
				locked.setCode(WebResponseCode.Failure);
				locked.setSummary(env.getProperty("sidejobs.web.messages.account.locked"));
				locked.setMessage("Login Failure");
				webresponse.setData(locked);
				return webresponse;
			case "Suspended":
				WebLoginResponse suspended = new WebLoginResponse();
				suspended.setCode(WebResponseCode.Failure);
				suspended.setSummary(env.getProperty("sidejobs.web.messages.account.suspended"));
				suspended.setMessage("Login Failure");
				webresponse.setData(suspended);
				return webresponse;
		}
		
		if(res.getStatus() == ResponseStatus.Failure)
		{
			WebLoginResponse noAccount = new WebLoginResponse();
			noAccount.setCode(WebResponseCode.Failure);
			noAccount.setSummary(env.getProperty("sidejobs.web.messages.login.failure"));
			noAccount.setMessage("Login Failure");
			webresponse.setData(noAccount);
			return webresponse;
		}
		
		WebLoginResponse noAccount = new WebLoginResponse();
		noAccount.setCode(WebResponseCode.Success);
		noAccount.setSummary(env.getProperty("sidejobs.web.messages.login.failure"));
		noAccount.setMessage("Login Success");
		webresponse.setData(noAccount);
		
		// decode the base64 encoded string
		byte[] decodedKey = env.getProperty("app.jwt.key").getBytes();
		// rebuild key using SecretKeySpec
		SecretKey originalKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES"); 
					
		JWSSigner signer = new MACSigner(originalKey);
		JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
				.subject(user.getId())
				.issueTime(new Date())
				.issuer(env.getProperty("app.root"))
				.claim("id",user.getId())
				.claim("role", user.getRole())
				.claim("fname", user.getFirst_name())
				.claim("lname",user.getLast_name())
				.claim("allowed", "true")
				.claim("status", "active")
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
		String jweString = jweObject.serialize();
			
		//add jwt token
		Cookie ctoken = new Cookie("token",jweString);
		ctoken.setMaxAge(TimeFrame.getSecondsPerDay(1));
		ctoken.setPath(env.getProperty("app.jwt.cookie.path"));
		ctoken.setHttpOnly(true);
		
		response.addCookie(ctoken);
		
		return webresponse;
	}
	
	@RequestMapping(value = "/user/verification", method=RequestMethod.GET)
	public ModelAndView  verifyRegistrationToken(
			@RequestParam(value="token", required=true, defaultValue="None")String token,
			@RequestParam(value="user", required=true, defaultValue="None")String user
			) throws IOException, KeyLengthException, JOSEException {
		
		String endpoint = env.getProperty("api.token.verification");
				
		String url = endpoint+"/"+token;
		String data = new RestClient().get(url);
		
		System.out.println(data);
		
		Deserializer<ResponseWrapper<RegistrationResponse>> des = 
				new Deserializer<ResponseWrapper<RegistrationResponse>>
					(new TypeToken<ResponseWrapper<RegistrationResponse>>() {}.getType());
		
		ResponseWrapper<RegistrationResponse> res = des.deserialize(data);
		
		if(res.getStatus() == ResponseStatus.Failure)
		{
			ModelAndViewBuilder builder = new ModelAndViewBuilder("error/error-verification");
			ModelAndView model = builder.buildModelAndView();
			return model;
		}
		
		
		RegistrationResponse reg = res.getData();
		
		// decode the base64 encoded string
		byte[] decodedKey = env.getProperty("app.jwt.key").getBytes();
		// rebuild key using SecretKeySpec
		SecretKey originalKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES"); 
					
		JWSSigner signer = new MACSigner(originalKey);
		JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
				.subject(user)
				.issueTime(new Date())
				.issuer(env.getProperty("app.root"))
				.claim("id",reg.getUserId())
				.claim("role", "worker")
				.claim("allowed", "true")
				.claim("status", "active")
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
		String jweString = jweObject.serialize();
			
		//add jwt token
		Cookie ctoken = new Cookie("token",jweString);
		ctoken.setMaxAge(TimeFrame.getSecondsPerDay(1));
		ctoken.setPath(env.getProperty("app.jwt.cookie.path"));
		ctoken.setHttpOnly(true);
		
		System.out.println(ctoken);
		
		Map<String,String> cookies = new HashMap<String,String>();
		cookies.put("token", jweString);
		ModelAndViewBuilder builder = new ModelAndViewBuilder("registration/success",env,cookies);
		ModelAndView model = builder.buildModelAndView();
		
		return model;
	}
	
	@RequestMapping(value="/user/registration/worker", method=RequestMethod.POST)
	@ResponseBody
	public String registerWorker(HttpServletResponse response,
			@RequestParam("password")String password,
			@RequestParam("email")String email,
			@RequestParam("firstname")String firstName,
			@RequestParam("lastname")String lastName
			) throws IOException, KeyLengthException, JOSEException{
		
		System.out.println("Registering Student...");
		
		/*check if .edu email, including subdomains
		if(email.matches(env.getProperty("regex.email.ends.edu")))
		{
			//error
		}*/
		String category = "None";
		String data = "";
		String endpoint = env.getProperty("api.registration.register.worker"); 

		System.out.println("Contacting server: "+endpoint);
		
		String url = endpoint;
			RestClient client = new RestClient();
			client.setUrlParameters("password", password);
			client.setUrlParameters("email", email);
			client.setUrlParameters("firstname", firstName);
			client.setUrlParameters("lastname", lastName);
			client.setUrlParameters("category", category);

			data = client.post(url);
			System.out.println("Received data from api: "+data);
			
			Deserializer<ResponseWrapper<RegistrationResponse>> des = new Deserializer<ResponseWrapper<RegistrationResponse>>(new TypeToken<ResponseWrapper<RegistrationResponse>>() {}.getType());
			
			ResponseWrapper<RegistrationResponse> res = des.deserialize(data);
			
			if(res.getStatus() == ResponseStatus.Failure)
			{
				return data;
			}

		return data;
	
	}
}
