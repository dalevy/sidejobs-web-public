package com.sidejobs.web.utils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class RestClient {
	
	private Map<String, String> parameters;
	private HttpURLConnection connection;
	private boolean hasUrlParams;
	private final int CONNECTION_TIMEOUT = 30000;
	private final int READ_TIMEOUT = 30000;
	
	
	public RestClient() 
	{
		
	}
	
	public void setUrlParameters(String key, String value)
	{
		if(parameters == null)
		{
			parameters = new HashMap<String,String>();
			hasUrlParams = true;
		}
		
		parameters.put(key, value);
	}
	
	public String get(String endpoint) throws IOException
	{
		return GET(endpoint.replace(" ", "%20"));
	}
	
	public String post(String endpoint) throws IOException
	{
		System.out.println("Sending post...");
		return POST(endpoint.replace(" ", "%20"));
	}
	
	
	private String POST(String endpoint) throws IOException
	{
		URL url = new URL(endpoint);
		connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod("POST");
		connection.setDoInput(true);
		connection.setRequestProperty( "Content-Type", "application/x-www-form-urlencoded"); 
		connection.setConnectTimeout(CONNECTION_TIMEOUT);
		connection.setReadTimeout(READ_TIMEOUT);
		
		if(hasUrlParams)
		{
			connection.setDoOutput(true);
			DataOutputStream out = new DataOutputStream(connection.getOutputStream());
			out.writeBytes(ParameterStringBuilder.getParamsString(parameters));
			out.flush();
			out.close();
		}
		
		int status = connection.getResponseCode();
		
		BufferedReader in = new BufferedReader(
				  new InputStreamReader(connection.getInputStream()));
				String inputLine;
				StringBuffer content = new StringBuffer();
				while ((inputLine = in.readLine()) != null) {
				    content.append(inputLine);
				}
				in.close();
				
		return content.toString();
	
	}
	
	private String GET(String endpoint) throws IOException
	{
		URL url = new URL(endpoint);
		connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod("GET");
		connection.setDoInput(true);
		connection.setConnectTimeout(CONNECTION_TIMEOUT);
		connection.setReadTimeout(READ_TIMEOUT);
		
		if(hasUrlParams)
		{
			connection.setDoOutput(true);
			DataOutputStream out = new DataOutputStream(connection.getOutputStream());
			out.writeBytes(ParameterStringBuilder.getParamsString(parameters));
			out.flush();
			out.close();
		}
		
		int status = connection.getResponseCode();
		
		BufferedReader in = new BufferedReader(
				  new InputStreamReader(connection.getInputStream()));
				String inputLine;
				StringBuffer content = new StringBuffer();
				while ((inputLine = in.readLine()) != null) {
				    content.append(inputLine);
				}
				in.close();
				
		return content.toString();
	
	}
}
