package com.ayanix.baymax;

import com.ayanix.baymax.handlers.FileHandler;
import com.ayanix.baymax.utils.FileUtils;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Baymax - Developed by Lewes D. B. (Boomclaw).
 * All rights reserved 2020.
 */
public class BaymaxPersistentStorage
{

	private final Baymax                          bot;
	private final Map<Long, BaymaxPersistentData> data;

	BaymaxPersistentStorage(Baymax bot)
	{
		this.bot = bot;
		this.data = new ConcurrentHashMap<>();
	}

	/**
	 * Loads all data from files and inserts into data cache.
	 */
	public void loadAllData()
	{
		List<File> files = FileUtils.getFiles(getUsersDirectory());

		files.forEach(this::load);
	}

	/**
	 * Loads data from given file and inserts into data cache.
	 * This will override any data already in data cache related to the user.
	 *
	 * @param file The file to load data from.
	 */
	private void load(File file)
	{
		FileHandler fileHandler = new FileHandler(file);
		String      json        = fileHandler.readFromFile();

		if (json.isEmpty())
		{
			BaymaxBoostrap.getLogger().error("Unable to load data from file " + file.getName(), new RuntimeException());
			return;
		}

		BaymaxPersistentData data = fileHandler.getJsonHandler().getGson().fromJson(json, BaymaxPersistentData.class);

		if (data.getUser() == 0)
		{
			BaymaxBoostrap.getLogger().error("Missing user ID from " + file.getName(), new RuntimeException());
			return;
		}

		this.data.put(data.getUser(), data);

		BaymaxBoostrap.getLogger().info("Loaded user " + data.getUser());
	}

	/**
	 * @return The user directory containing all the user data files.
	 */
	private static File getUsersDirectory()
	{
		return new File("users");
	}

	/**
	 * Saves data to file.
	 * This will run on a separate thread to avoid impacting command performance.
	 *
	 * @param data The data to save.
	 */
	public void save(BaymaxPersistentData data)
	{
		FileHandler fileHandler = new FileHandler(new File(getUsersDirectory(), data.getUser() + ".json"));

		BaymaxBoostrap.getExecutors().execute(() -> {
			fileHandler.saveToFile(fileHandler.getJsonHandler().getGson().toJson(data));

			BaymaxBoostrap.getLogger().info("Saved user " + data.getUser());
		});
	}

	/**
	 * Fetches the data related to the given user.
	 * If the data does not exist, it will be created and stored in the data cache.
	 *
	 * @param user The user related to the data.
	 * @return The data related to the user.
	 */
	public BaymaxPersistentData getData(long user)
	{
		if (data.containsKey(user))
		{
			return data.get(user);
		}

		BaymaxPersistentData data = new BaymaxPersistentData();
		data.setUser(user);

		this.data.put(user, data);

		return data;
	}

}