package com.ayanix.baymax.commands;

import com.ayanix.baymax.Baymax;
import com.ayanix.baymax.BaymaxBoostrap;
import com.ayanix.baymax.BaymaxMessages;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.User;
import discord4j.core.object.util.Snowflake;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.Arrays;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * Baymax - Developed by Lewes D. B. (Boomclaw).
 * All rights reserved 2020.
 */
public class RoleCommand implements Consumer<MessageCreateEvent>
{

	private final Baymax bot;

	public RoleCommand(Baymax bot)
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

		if (!message.startsWith("!role") && !message.startsWith("!society"))
		{
			return;
		}

		boolean society = message.startsWith("!society");
		String  cmdName = society ? "society" : "role";

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
				if (!society)
				{
					messageChannel.createMessage(BaymaxMessages.getIntroduction(author) + "\n" +
							"I see you're trying to assign yourself a role. Type **!role <category>**.").subscribe();
				} else
				{
					messageChannel.createMessage(BaymaxMessages.getIntroduction(author) + "\n" +
							"I see you're trying to join a society. Type **!society <society>**.").subscribe();
				}
			});

			return;
		}

		// fetch requested role
		String[] parts = message.split(" ");

		String requestedName = String.join(" ", Arrays.copyOfRange(parts, 1, parts.length));

		Optional<Long> roleId = bot.getSettings().getRoleID(requestedName);

		if (!roleId.isPresent())
		{
			event.getMessage().getChannel().subscribe(messageChannel -> {
				if (!society)
				{
					messageChannel.createMessage(author.getMention() + ", I couldn't find the role " + requestedName + ".\n" +
							"Type **!" + cmdName + " <category>** to try again.").subscribe();
				} else
				{
					messageChannel.createMessage(author.getMention() + ", I couldn't find the society " + requestedName + ".\n" +
							"Type **!society <society>** to try again.").subscribe();
				}
			});

			return;
		}

		event.getGuild().subscribe(guild -> {
			guild.getRoleById(Snowflake.of(roleId.get())).onErrorContinue((throwable, o) -> event.getMessage().getChannel().subscribe(messageChannel -> {
				messageChannel.createMessage(author.getMention() + ", something went wrong. Please contact an admin.").subscribe();
				throwable.printStackTrace();
			})).subscribe(role -> {
				author.asMember(guild.getId()).onErrorContinue((throwable, o) -> event.getMessage().getChannel().subscribe(messageChannel -> {
					messageChannel.createMessage(author.getMention() + ", something went wrong. Please contact an admin.").subscribe();
					throwable.printStackTrace();
				})).subscribe(member -> {
					if (!member.getRoleIds().contains(role.getId()))
					{
						member.addRole(role.getId())
								.subscribe(new Subscriber<Void>()
								{
									@Override
									public void onSubscribe(Subscription subscription)
									{

									}

									@Override
									public void onNext(Void aVoid)
									{

									}

									@Override
									public void onError(Throwable throwable)
									{
										event.getMessage().getChannel().subscribe(messageChannel -> {
											messageChannel.createMessage(author.getMention() + ", something went wrong. Please contact an admin.").subscribe();
											throwable.printStackTrace();
										});
									}

									@Override
									public void onComplete()
									{
										event.getMessage().getChannel().subscribe(messageChannel -> {
											messageChannel.createMessage(author.getMention() + ", I've added you to the " + cmdName + " **" + role.getName() + "**.").subscribe();
										});
									}
								});
					} else
					{
						member.removeRole(role.getId())
								.subscribe(new Subscriber<Void>()
								{
									@Override
									public void onSubscribe(Subscription subscription)
									{

									}

									@Override
									public void onNext(Void aVoid)
									{

									}

									@Override
									public void onError(Throwable throwable)
									{
										event.getMessage().getChannel().subscribe(messageChannel -> {
											messageChannel.createMessage(author.getMention() + ", something went wrong. Please contact an admin.").subscribe();
											throwable.printStackTrace();
										});
									}

									@Override
									public void onComplete()
									{
										event.getMessage().getChannel().subscribe(messageChannel -> {
											messageChannel.createMessage(author.getMention() + ", I've removed you from the " + cmdName + " **" + role.getName() + "**.").subscribe();
										});
									}
								});
					}
				});
			});
		});
	}

}