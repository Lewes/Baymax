package com.ayanix.baymax.commands;

import com.ayanix.baymax.Baymax;
import com.ayanix.baymax.BaymaxBoostrap;
import com.ayanix.baymax.BaymaxMessages;
import com.ayanix.baymax.BaymaxPersistentData;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.TextChannel;
import discord4j.core.object.entity.User;
import discord4j.core.object.util.Snowflake;

import java.util.Arrays;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * Baymax - Developed by Lewes D. B. (Boomclaw).
 * All rights reserved 2020.
 */
public class IntroductionCommand implements Consumer<MessageCreateEvent>
{

	private final Baymax bot;

	public IntroductionCommand(Baymax bot)
	{
		this.bot = bot;
	}

	@Override
	public void accept(MessageCreateEvent event)
	{
		// check if its the right command
		Optional<String> messageOpt = event.getMessage().getContent();

		if (!messageOpt.isPresent())
		{
			BaymaxBoostrap.getLogger().warn("An empty message was received");
			return;
		}

		String message = messageOpt.get().toLowerCase();

		if (!message.startsWith("!introduction"))
		{
			return;
		}

		// fetch author
		if (!event.getMessage().getAuthor().isPresent())
		{
			BaymaxBoostrap.getLogger().warn("A message was received with no author: " + message);
			return;
		}

		User author = event.getMessage().getAuthor().get();

		// parse message
		if (!message.contains(" "))
		{
			event.getMessage().getChannel().subscribe(messageChannel -> {
				messageChannel.createMessage(BaymaxMessages.getIntroduction(author) + "\n" +
						"I see you're trying to introduce yourself. Type **!introduction <message>**.").subscribe();
			});

			return;
		}

		// fetch introduction
		String[] parts = message.split(" ");

		String introductionMessage = String.join(" ", Arrays.copyOfRange(parts, 1, parts.length));

		// fetch data
		BaymaxPersistentData data = bot.getStorage().getData(author.getId().asLong());

		// find the introduction channel
		event.getGuild().subscribe(guild -> {
			guild.getChannelById(Snowflake.of(bot.getSettings().getIntroductionChannel()))
					.doOnError(throwable -> {
						// channel not found
						event.getMessage().getChannel().subscribe(messageChannel -> {
							messageChannel.createMessage("Hello " + author.getMention() + ". Something went wrong. Please contact an admin.").subscribe();
						});
					})
					.subscribe(guildChannel -> {
						TextChannel textChannel = (TextChannel) guildChannel;

						// fetch user's previous introduction
						textChannel.getMessageById(Snowflake.of(data.getIntroduction()))
								.doOnError(throwable -> {
									// no previous introduction
									textChannel.createMessage(getMessage(author, introductionMessage)).subscribe(messageObj -> {
										data.setIntroduction(messageObj.getId().asLong());

										bot.getStorage().save(data);

										event.getMessage().getChannel().subscribe(messageChannel -> {
											messageChannel.createMessage("Hello " + author.getMention() + ". " +
													"Thanks for introducing yourself." + "\n" +
													"If you want to edit your introduction, type **!introduction <message>**.").subscribe();
										});
									});
								})
								.subscribe(message1 -> message1.edit(messageEditSpec -> {
									// introduction found, edit it.
									messageEditSpec.setContent(getMessage(author, introductionMessage));

									event.getMessage().getChannel().subscribe(messageChannel -> {
										messageChannel.createMessage("Hello " + author.getMention() + ". I've updated your introduction.").subscribe();
									});
								}).subscribe());
					});
		});
	}

	/**
	 * Get the introduction message.
	 *
	 * @param user         The user introducing themselves.
	 * @param introduction Their introduction.
	 * @return A formatted introduction.
	 */
	private String getMessage(User user, String introduction)
	{
		return "**" + user.getMention() + " says hello:**\n\n" + introduction;
	}

}