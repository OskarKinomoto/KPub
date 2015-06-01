package me.kinomoto.kpub;

import java.util.ArrayList;
import java.util.List;

/**
 * Stores author name. And provides static methods to manage authors.
 * 
 * @see #getAuthors(String)
 * @see #contains(String)
 */
public class Author {

	/**
	 * Author name
	 */
	private String name;

	/**
	 * Creates Author
	 * 
	 * @param name
	 */
	public Author(String name) {
		this.name = name;
	}

	/**
	 * Create {@link ArrayList} with {@link Author Authors} from {@link String} uses comma and 'and' as separators
	 */
	public static List<Author> getAuthors(String authors) {
		List<Author> tmp = new ArrayList<Author>();
		if (authors.length() == 0)
			return tmp;
		authors = authors.replaceAll(", and ", ", ");
		authors = authors.replaceAll(" and ", ", ");
		String[] splited = authors.split(", ");
		for (String string : splited) {
			tmp.add(new Author(string));
		}
		return tmp;
	}

	/**
	 * @return {@link #name}
	 */
	public String getName() {
		return name;
	}

	/**
	 * Check if {@link Author#name Author's name} contains string, case insensitive.
	 * 
	 * @param string
	 */
	private boolean contains(String string) {
		return name.toLowerCase().contains(string.toLowerCase());
	}

	/**
	 * Check if all authors from <b>filterAuthors</b> are founded in <b>articleAuthors</b>
	 */
	public static boolean contains(List<Author> articleAuthors, String filterAuthors) {
		List<Author> filter = Author.getAuthors(filterAuthors.toLowerCase());
		for (Author f : filter) {
			boolean tmp = false;
			for (Author ar : articleAuthors) {
				if (ar.contains(f.getName())) {
					tmp = true;
					break;
				}
			}
			if (!tmp)
				return false;
		}
		return true;
	}

}
