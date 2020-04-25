package com.ayanix.baymax.handlers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Getter;

public class JsonHandler
{

	@Getter
	private final Gson gson;

	/**
	 * Initiate a JSONHandler.
	 */
	public JsonHandler()
	{
		this.gson = new GsonBuilder().create();
	}

}