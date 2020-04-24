package com.ayanix.baymax.utils;

/**
 * Baymax - Developed by Lewes D. B. (Boomclaw).
 * All rights reserved 2020.
 */
public class MathsUtils extends com.ayanix.panther.impl.common.utils.MathsUtils
{

	private static MathsUtils instance;

	public static MathsUtils get()
	{
		if (instance == null)
		{
			instance = new MathsUtils();
		}

		return instance;
	}

	/**
	 * @param str Possible long.
	 * @return Whether or not the provided string is a long.
	 */
	public boolean isLong(String str)
	{
		try
		{
			Long.parseLong(str);
		} catch (Exception e)
		{
			return false;
		}

		return true;
	}

}