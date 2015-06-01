package me.kinomoto.kpub;

import me.kinomoto.kpub.Article.WORK_TYPE;

/**
 * Holds filters status and checks if {@link Article} passes all filters.
 */
public class Filters {
	private static Article.WORK_TYPE workType = null;
	private static int yearStart = 0;
	private static int yearStop = 0;
	private static String title = null;
	private static String authors = null;

	private Filters() {
	}

	/**
	 * Sets WorkType filter.
	 */
	public static void setWorkType(String s) {
		try {
			workType = WORK_TYPE.valueOf(s.toUpperCase());
		} catch (IllegalArgumentException e) {
			workType = null;
		}
	}

	/**
	 * Sets start year filter.
	 */
	public static void setYearStart(String i) {
		try {
			yearStart = Integer.valueOf(i);
		} catch (java.lang.NumberFormatException e) {
			yearStart = 0;
		}
	}

	/**
	 * Sets end year filter.
	 */
	public static void setYearStop(String i) {
		try {
			yearStop = Integer.valueOf(i);
		} catch (java.lang.NumberFormatException e) {
			yearStop = 0;
		}
	}

	/**
	 * Checks if {@link Article} passes all filters.
	 */
	public static boolean passesFilters(Article article) {
		int year = article.getYear();
		if (yearStart != 0 && year < yearStart)
			return false;
		if (yearStop != 0 && year > yearStop)
			return false;
		if (workType != null && article.getType() != workType)
			return false;
		if (title != null && !article.getName().toLowerCase().contains(title))
			return false;
		if (authors != null && !Author.contains(article.getAuthorsFromUnit(), authors))
			return false;
		return true;
	}

	/**
	 * Sets title filter.
	 */
	public static void setTitle(String title) {
		if (title == "") {
			Filters.title = null;
		} else {
			Filters.title = title.toLowerCase();
		}
	}

	/**
	 * Sets author from unit filter.
	 */
	public static void setAuthorsFromUnit(String authors) {
		if (authors == "") {
			Filters.authors = null;
		} else {
			Filters.authors = authors.toLowerCase();
		}
	}
}
