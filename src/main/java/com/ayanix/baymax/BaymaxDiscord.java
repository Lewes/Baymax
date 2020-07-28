package com.ayanix.baymax;

import com.ayanix.baymax.commands.IntroductionCommand;
import com.ayanix.baymax.commands.RoleCommand;
import discord4j.core.DiscordClient;
import discord4j.core.DiscordClientBuilder;
import discord4j.core.event.domain.message.MessageCreateEvent;
import lombok.Getter;

/**
 * Baymax - Developed by Lewes D. B. (Boomclaw).
 * All rights reserved 2020.
 */
public class BaymaxDiscord
{

	private final Baymax        bot;
	@Getter
	private       DiscordClient client;

	public BaymaxDiscord(Baymax bot)
	{
		this.bot = bot;
	}

	/**
	 * Connects the bot to Discord with provided token in settings.
	 *
	 * @return Whether or not connection to Discord was successful.
	 */
	public boolean connect()
	{
		DiscordClientBuilder clientBuilder = new DiscordClientBuilder(bot.getSettings().getConfiguration().getString("discord.token"));

		try
		{
			client = clientBuilder.build();
			client.login().subscribe();
		} catch (Exception e)
		{
			BaymaxBoostrap.getLogger().error("Unable to connect to Discord", e);
			return false;
		}

		return true;
	}

	/**
	 * Registers listeners for the bot.
	 */
	public void registerListeners()
	{
		if (!bot.getSettings().isCommandsEnabled())
		{
			return;
		}

		client.getEventDispatcher().on(MessageCreateEvent.class).subscribe(new RoleCommand(bot));
		client.getEventDispatcher().on(MessageCreateEvent.class).subscribe(new IntroductionCommand(bot));
	}

}
