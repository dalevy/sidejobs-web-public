package com.sidejobs.web.utils;

import java.lang.reflect.Type;

import com.google.gson.Gson;

public class Deserializer<T>{

	Type type;
	
	public Deserializer(Type type) {
		this.type = type;
	}
	
	
	/***
	 * Deserializes generic classes by using the TypeToken to provide the Gson deserializer with enough
	 * information to deserialize the generic type class.
	 * @param json
	 * @return
	 */
	public T deserialize(String json)
	{
		Gson gson = new Gson();
		T result = gson.fromJson(json, type);
		return result;
	}
	
}
