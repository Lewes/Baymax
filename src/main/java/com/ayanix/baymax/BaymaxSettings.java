package com.ayanix.baymax;

import com.ayanix.baymax.utils.MathsUtils;
import com.ayanix.panther.storage.configuration.Configuration;
import com.ayanix.panther.storage.configuration.ConfigurationProvider;
import com.ayanix.panther.storage.configuration.YamlConfiguration;
import lombok.Getter;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Baymax - Developed by Lewes D. B. (Boomclaw).
 * All rights reserved 2020.
 */
public class BaymaxSettings
{

	private final Baymax        bot;
	private       File          file;
	@Getter
	private       Configuration configuration;

	BaymaxSettings(Baymax bot)
	{
		this.bot = bot;
	}

	/**
	 * Loads settings from file, or inserts default settings.
	 *
	 * @return Whether or not the settings were successfully loaded.
	 */
	public boolean load()
	{
		String fullName = "settings.yml";

		this.file = new File(new File("."), fullName);

		if (!file.exists())
		{
			try
			{
				if (!file.createNewFile())
				{
					throw new IOException();
				}
			} catch (IOException e)
			{
				BaymaxBoostrap.getLogger().error("Unable to create settings.yml", e);
				return false;
			}
		}

		try
		{
			this.configuration = ConfigurationProvider.getProvider(YamlConfiguration.class).load(file);
		} catch (IOException e)
		{
			BaymaxBoostrap.getLogger().error("Unable to load settings.yml", e);
			return false;
		}

		return loadDefault();
	}

	/**
	 * Attempts to load default values if the config has not been made yet.
	 *
	 * @return True if successfully saved default values or no need to, or false if something went wrong.
	 */
	private boolean loadDefault()
	{
		if (configuration.getInt("version", 0) == 1)
		{
			// config is already loaded with default values
			return true;
		}

		configuration.set("version", 1);

		configuration.set("discord.token", "");

		configuration.set("admins", Collections.singletonList("ADMIN_ROLE_ID"));
		configuration.set("roles.science", Collections.singletonList("SCIENCE_ROLE_ID"));

		configuration.set("channels.introduction", 0);

		return save();
	}

	/**
	 * Saves configuration to file.
	 *
	 * @return Whether or not the file was successfully saved.
	 */
	private boolean save()
	{
		try
		{
			ConfigurationProvider.getProvider(YamlConfiguration.class).save(configuration, file);
		} catch (IOException e)
		{
			BaymaxBoostrap.getLogger().error("Unable to save settings.yml", e);
			return false;
		}

		return true;
	}

	/**
	 * Gets the role ID associated with a name.
	 *
	 * @return An optional containing the role ID, or empty if none configured.
	 */
	public Optional<Long> getRoleID(String name)
	{
		Configuration parent = configuration.getSection("roles");

		if (parent == null)
		{
			return Optional.empty();
		}

		for (String rawRoleID : parent.getKeys())
		{
			if (!MathsUtils.get().isLong(rawRoleID))
			{
				BaymaxBoostrap.getLogger().error(rawRoleID + " is a configured role ID but is not an acceptable value.");
				continue;
			}

			long role = Long.parseLong(rawRoleID);

			List<String> names = parent.getStringList(rawRoleID);

			// check by exact name first
			if (names.stream().anyMatch(possibleName -> possibleName.equalsIgnoreCase(name)))
			{
				return Optional.of(role);
			}

			// check by containing
			// minimum length of 5 letters to prevent random letters matching
			if (names.stream().anyMatch(possibleName -> (name.length() >= 5 && possibleName.contains(name) ||
					name.contains(possibleName))))
			{
				return Optional.of(role);
			}
		}

		return Optional.empty();
	}

	/**
	 * Clears the names from a role ID so it cannot be assigned through the bot.
	 *
	 * @param role The role ID.
	 */
	public void clearRole(long role)
	{
		configuration.set("roles." + role, null);

		save();
	}

	/**
	 * Adds the role name to the settings.
	 *
	 * @param role The role ID.
	 * @param name A name representing the role.
	 */
	public void addRoleAlias(long role, String name)
	{
		List<String> preExisting = getRoleNames(role);

		preExisting.add(name);

		save();
	}

	/**
	 * @param role The role ID.
	 * @return List of role names associated with role ID
	 */
	public List<String> getRoleNames(long role)
	{
		return configuration.getStringList("roles." + role);
	}

	/**
	 * @param role The role ID.
	 * @return Whether or not the role ID is an admin as set in the settings file.
	 */
	public boolean isAdmin(long role)
	{
		return getAdminRoles().contains(role);
	}

	/**
	 * @return List of roles identified as an admin in the settings file.
	 */
	public List<Long> getAdminRoles()
	{
		return configuration.getLongList("admins");
	}

	/**
	 * @return The introduction channel ID
	 */
	public long getIntroductionChannel()
	{
		return configuration.getLong("channels.introduction", 0);
	}

}