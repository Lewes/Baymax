package com.ayanix.baymax.utils;

import java.util.List;

/**
 * Baymax - Developed by Lewes D. B. (Boomclaw).
 * All rights reserved 2020.
 */
public class StringUtils
{

	/**
	 * @param name  The string to check.
	 * @param names The possible strings to compare against.
	 * @return Whether or not there is an exact match (ignoring case).
	 */
	public static boolean isExactMatch(String name, List<String> names)
	{
		return names.stream().anyMatch(possibleName -> possibleName.equalsIgnoreCase(name));
	}

	/**
	 * This is case sensitive. It will only check if there is a partial match for names greater than 4 characters.
	 *
	 * @param name  The string to check.
	 * @param names The possible strings to compare against.
	 * @return Whether or not there is a partial match.
	 */
	public static boolean isContainMatch(String name, List<String> names)
	{
		return names.stream().anyMatch(possibleName -> (name.length() >= 5 && possibleName.contains(name) || name.contains(possibleName)));
	}

}
