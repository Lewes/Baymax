package com.ayanix.baymax;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Baymax - Developed by Lewes D. B. (Boomclaw).
 * All rights reserved 2020.
 */
public class BaymaxBoostrap
{

	@Getter
	private static final Logger          logger    = LoggerFactory.getLogger(BaymaxBoostrap.class);
	@Getter
	private static final ExecutorService executors = Executors.newCachedThreadPool(new ThreadFactoryBuilder().setNameFormat("Baymax %d").build());
	@Getter
	private static       Baymax          bot;

	public static void main(String[] args)
	{
		bot = new Baymax();

		if (!bot.start())
		{
			// baymax failed to start
			return;
		}

		// baymax started, lets listen for commands
		listen();
	}

	/**
	 * This will listen to commands on the main thread by looping for input.
	 * The Discord bot operates on another thread therefore this is not a concern for blocking.
	 */
	private static void listen()
	{
		while (true)
		{
			// todo: not yet coded but keeps the bot alive
		}
	}

}