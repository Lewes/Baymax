package com.ayanix.baymax;

import lombok.Getter;

/**
 * Baymax - Developed by Lewes D. B. (Boomclaw).
 * All rights reserved 2020.
 */
public class Baymax
{

	@Getter
	private BaymaxSettings          settings;
	@Getter
	private BaymaxDiscord           discord;
	@Getter
	private BaymaxPersistentStorage storage;

	/**
	 * Starts Baymax connectivity to Discord, and loads settings and storage.
	 *
	 * @return Whether or not the bot was successfully started.
	 */
	public boolean start()
	{
		if (!loadSettings())
		{
			BaymaxBoostrap.getLogger().error("Settings were not loaded. Disabling Baymax.");
			return false;
		}

		if (!connectToDiscord())
		{
			BaymaxBoostrap.getLogger().error("Connection to Discord failed. Disabling Baymax.");
			return false;
		}

		loadStorage();

		return true;
	}

	/**
	 * Loads settings from file, or creates settings file if not already existing.
	 *
	 * @return Whether or not settings were successfully loaded.
	 */
	private boolean loadSettings()
	{
		this.settings = new BaymaxSettings(this);

		return settings.load();
	}

	/**
	 * Connects to Discord and registers listeners.
	 *
	 * @return Whether or not the connection was successful.
	 */
	private boolean connectToDiscord()
	{
		this.discord = new BaymaxDiscord(this);

		if (!discord.connect())
		{
			return false;
		}

		discord.registerListeners();

		return true;
	}

	/**
	 * Loads user data from file.
	 */
	public void loadStorage()
	{
		this.storage = new BaymaxPersistentStorage(this);
		storage.loadAllData();
	}

}