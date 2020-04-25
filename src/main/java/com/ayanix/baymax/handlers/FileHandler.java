package com.ayanix.baymax.handlers;

import lombok.Getter;
import lombok.SneakyThrows;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.stream.Stream;

public class FileHandler
{

	private final File        file;
	@Getter
	private final JsonHandler jsonHandler;

	/**
	 * Wraps a file handler around a file.
	 *
	 * @param file The file to wrap around.
	 */
	public FileHandler(File file)
	{
		this.file = file;
		this.jsonHandler = new JsonHandler();
	}

	/**
	 * Fetch the contents of the file in string format.
	 *
	 * @return The contents of the file, or an empty string if the file does not exist.
	 */
	public String readFromFile()
	{
		if (!file.exists())
		{
			return "";
		}

		StringBuilder contentBuilder = new StringBuilder();

		try (Stream<String> stream = Files.lines(file.toPath(), StandardCharsets.UTF_8))
		{
			stream.forEach(s -> contentBuilder.append(s).append("\n"));
		} catch (IOException e)
		{
			e.printStackTrace();

			return "";
		}

		return contentBuilder.toString();
	}

	/**
	 * Saves the string to the file.
	 * This will override the existing content of the file.
	 *
	 * @param string The string to save.
	 */
	@SneakyThrows
	public synchronized void saveToFile(String string)
	{
		if (!file.exists())
		{
			if (!file.getParentFile().exists())
			{
				file.getParentFile().mkdirs();
			}

			file.createNewFile();
		}

		try (FileWriter fileWriter = new FileWriter(file))
		{
			fileWriter.write(string);
		}
	}

}