package com.ayanix.baymax;

import discord4j.core.object.presence.Activity;
import discord4j.core.object.presence.Presence;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Baymax - Developed by Lewes D. B. (Boomclaw).
 * All rights reserved 2020.
 * <p>
 * This class was taken from Panther.
 */
public class BaymaxCountdown
{

	private final Baymax bot;

	/**
	 * Initiate a BaymaxCountdown instance.
	 *
	 * @param bot The bot instance.
	 */
	public BaymaxCountdown(Baymax bot)
	{
		this.bot = bot;
	}

	/**
	 * Pushes a timer update to the Discord platform.
	 */
	public void update()
	{
		bot.getDiscord().getClient().updatePresence(Presence.online(Activity.playing(getTimeTillResultsDay() + " till results day"))).subscribe();
	}

	/**
	 * @return The time till results day (8am August 13th 2020).
	 */
	public String getTimeTillResultsDay()
	{
		return formatDateDiff(1597305600);
	}

	/**
	 * Converts a unix timestamp to string format of time distance (1 years, 2 months etc)
	 *
	 * @param unixTime The unix timestamp in future or past.
	 * @return A date difference between unixTime and current time.
	 */
	public String formatDateDiff(long unixTime)
	{
		Calendar c = new GregorianCalendar();
		c.setTimeInMillis(unixTime * 1000L);
		Calendar now = new GregorianCalendar();

		return this.formatDateDiff(now, c);
	}

	/**
	 * Converts a unix timestamp to string format of time distance (1 years, 2 months etc).
	 *
	 * @param fromDate The date to start from.
	 * @param toDate   The date to end at.
	 * @return A date difference between the two dates.
	 */
	private String formatDateDiff(Calendar fromDate, Calendar toDate)
	{
		boolean future = false;

		if (toDate.equals(fromDate))
		{
			return "now";
		} else
		{
			if (toDate.after(fromDate))
			{
				future = true;
			}

			StringBuilder sb    = new StringBuilder();
			int[]         types = new int[]{1, 2, 5, 11, 12, 13};
			String[] names = new String[]{"year", "years", "month", "months", "day", "days", "hour", "hours", "minute", "minutes", "second",
			                              "seconds"};
			int accuracy = 0;

			for (int i = 0; i < types.length && accuracy <= 3; ++i)
			{
				int diff = this.dateDiff(types[i], fromDate, toDate, future);
				if (diff > 0)
				{
					++accuracy;
					sb.append(' ').append(diff).append(' ').append(names[i * 2 + (diff > 1 ? 1 : 0)]);
				}
			}

			return sb.length() == 0 ? "now" : sb.toString().trim();
		}
	}

	/**
	 * Calculates dates distance in numerical format with different units.
	 *
	 * @param type     The unit.
	 * @param fromDate The date to start from.
	 * @param toDate   The date to end at.
	 * @param future   If true, the date is in the future. If false, the date is in the past.
	 * @return The date difference.
	 */
	private int dateDiff(int type, Calendar fromDate, Calendar toDate, boolean future)
	{
		int year     = 1;
		int fromYear = fromDate.get(year);
		int toYear   = toDate.get(year);

		if (Math.abs(fromYear - toYear) > 100000)
		{
			toDate.set(year, fromYear + (future ? 100000 : -100000));
		}

		int diff = 0;

		long savedDate;

		for (savedDate = fromDate.getTimeInMillis(); future && !fromDate.after(toDate) || !future && !fromDate.before(toDate); ++diff)
		{
			savedDate = fromDate.getTimeInMillis();
			fromDate.add(type, future ? 1 : -1);
		}

		--diff;
		fromDate.setTimeInMillis(savedDate);

		return diff;
	}

}
