package com.sidejobs.web.common;

import java.io.Serializable;

public class WebLoginResponse implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String message;
	private String summary;
	private WebResponseCode code;
	
	public WebLoginResponse() {}
	
	public WebLoginResponse(String message, WebResponseCode code)
	{
		this.message = message;
		this.code = code;
	}
	
	public WebLoginResponse(String message, WebResponseCode code, String summary)
	{
		this(message,code);
		this.summary = summary;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public WebResponseCode getCode() {
		return code;
	}

	public void setCode(WebResponseCode code) {
		this.code = code;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}
}
