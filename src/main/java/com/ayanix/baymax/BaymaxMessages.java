package com.ayanix.baymax;

import discord4j.core.object.entity.User;

/**
 * Baymax - Developed by Lewes D. B. (Boomclaw).
 * All rights reserved 2020.
 */
public class BaymaxMessages
{

	/**
	 * Fetch the introduction message.
	 * This is often used when the user types a command without any argments.
	 *
	 * @param user The user requesting the introduction.
	 * @return The formatted introduction message.
	 */
	public static String getIntroduction(User user)
	{
		return "Hello " + user.getMention() + ". I am Baymax. Your personal Southampton 2020 Discord companion.";
	}

}
