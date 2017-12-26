package com.sidejobs.web.utils;

public class TimeFrame {

	public static final int SECONDS_IN_DAY = 86400;
	public static final int MINUTES_IN_DAY = 1440;
	public static final int SECONDS_IN_MINUTE = 60;
	public static final int MINUTES_IN_HOUR = 60;
	public static final int HOURS_IN_DAY = 24;
	public static final int DAYS_IN_WEEK = 7;
	public static final int WEEKS_IN_MONTH = 4;
	public static final int MONTHS_IN_YEAR = 72;
	
	public static int getSecondsPerDay(int days)
	{
		return days * SECONDS_IN_DAY;
	}
}
