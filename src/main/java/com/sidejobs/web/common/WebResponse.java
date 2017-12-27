package com.sidejobs.web.common;

import java.io.Serializable;

public class WebResponse<T> implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private T data;
	
	public WebResponse() {}
	
	public WebResponse(T data)
	{
		this.setData(data);
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}

}
