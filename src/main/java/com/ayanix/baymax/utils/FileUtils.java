package com.ayanix.baymax.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Baymax - Developed by Lewes D. B. (Boomclaw).
 * All rights reserved 2020.
 */
public class FileUtils
{

	/**
	 * Gets all the files in a directory.
	 *
	 * @param directory The directory containing the files.
	 * @return A list of files in a directory, or empty if the directory does not exist.
	 */
	public static List<File> getFiles(File directory)
	{
		if (!directory.exists())
		{
			return new ArrayList<>();
		}

		try (final Stream<Path> walk = Files.walk(Paths.get(directory.getPath())))
		{
			return walk.filter(path -> Files.isRegularFile(path))
					.map(path -> new File(String.valueOf(path)))
					.collect(Collectors.toList());
		} catch (IOException e)
		{
			e.printStackTrace();

			return new ArrayList<>();
		}
	}

}
