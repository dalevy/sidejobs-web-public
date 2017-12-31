package com.sidejobs.web.controllers;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.sidejobs.web.utils.ModelAndViewBuilder;

@Controller
public class SiteController {
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private Environment env;
	
	@RequestMapping(value="/", method=RequestMethod.GET)
	public ModelAndView land(
			HttpServletResponse response,
			@CookieValue(value="token", defaultValue="none",required=false) String token
			) {
		
		Map<String,String> cookies = new HashMap<String,String>();
		cookies.put("token", token);
		ModelAndViewBuilder builder = new ModelAndViewBuilder("index",env,cookies);
		ModelAndView model = builder.buildModelAndView();
	
		 return model;
	}
	
	/**
	 * URL for the job posting page. Only workers may post jobs.
	 * @param response
	 * @param token
	 * @return
	 */
	@RequestMapping(value ="/jobs/post", method=RequestMethod.GET)
	public ModelAndView postNewJob(
			HttpServletResponse response,
			@CookieValue(value="token", required=false)String token) {
		

		Map<String,String> cookies = new HashMap<String,String>();
		cookies.put("token", token);
		ModelAndViewBuilder builder = new ModelAndViewBuilder("jobs/post/new-job-post-form-1",env,cookies);
		ModelAndView model = builder.buildModelAndView();
		
		return model;
		
	}
	
	/**
	 * URL for the job posting page form 2. Only workers may post jobs.
	 * @param response
	 * @param token
	 * @return
	 */
	@RequestMapping(value ="/jobs/post/2", method=RequestMethod.GET)
	public ModelAndView postNewJobForm2(
			HttpServletResponse response,
			@CookieValue(value="token", required=false)String token,
			@RequestParam(value="category", required=true)String category,
			@RequestParam(value="area", required = true) String area,
			@RequestParam(value="specialties",required=true)String specialites
			) {
		

		Map<String,String> cookies = new HashMap<String,String>();
		cookies.put("token", token);
		ModelAndViewBuilder builder = new ModelAndViewBuilder("jobs/post/new-job-post-form-2",env,cookies);
		ModelAndView model = builder.buildModelAndView();
		
		return model;
		
	}
			
	
	@RequestMapping(value ="/login", method=RequestMethod.GET)
	public ModelAndView verificationError(HttpServletResponse response,
			@CookieValue(value="token", defaultValue="none",required=false) String token) {
		
		ModelAndViewBuilder builder = new ModelAndViewBuilder("login");
		ModelAndView model = builder.buildModelAndView();
	
		return model;
	}
	
	
	@RequestMapping(value ="/errors/verification")
	public ModelAndView verificationError() {
		
		ModelAndViewBuilder builder = new ModelAndViewBuilder("error/error-verification");
		ModelAndView model = builder.buildModelAndView();
	
		return model;
	}
	
	@RequestMapping(value ="/notification/registration/verification")
	public ModelAndView checkEmailVerification() {
		
		ModelAndViewBuilder builder = new ModelAndViewBuilder("registration/check-email");
		ModelAndView model = builder.buildModelAndView();
	
		return model;
	}
	
	@RequestMapping(value="/workers/registration", method=RequestMethod.GET)
	public ModelAndView workerRegistration(
			HttpServletResponse response,
			@CookieValue(value="token", defaultValue="none",required=false) String token
			) {
		
		Map<String,String> cookies = new HashMap<String,String>();
		cookies.put("token", token);
		ModelAndViewBuilder builder = new ModelAndViewBuilder("registration/worker-registration",env,cookies);
		ModelAndView model = builder.buildModelAndView();
	
		return model;
	}
}
