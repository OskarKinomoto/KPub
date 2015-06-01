package me.kinomoto.kpub;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import me.kinomoto.kpub.Issn.ChecksumException;
import me.kinomoto.kpub.Issn.EmptyNameException;
import me.kinomoto.kpub.Issn.IssnLenghtException;
import me.kinomoto.kpub.Issn.IssnZeroException;
import me.kinomoto.kpub.Issn.WrongTypeException;

/**
 * Stores and manages all {@link Issn}. Updates issn comboboxes by {@link ArticleView#updateIssn()}
 */
public class IssnModel {

	/**
	 * Store all {@link Issn Issns} here
	 */
	static List<Issn> issns = new ArrayList<Issn>();

	private IssnModel() {
	}

	/**
	 * Loads {@link Issn Issns} from database
	 * 
	 * @throws SQLException
	 */
	public static void loadIssns(Statement st) throws SQLException {
		ResultSet rs = st.executeQuery("SELECT * FROM ISSN ORDER BY ISSN");
		while (rs.next())
			issns.add(new Issn(rs.getInt("ISSN"), rs.getString("ISSNName"), rs.getString("List")));
		rs.close();
		IssnEditor.getRef().updateTable();
	}

	/**
	 * returns first {@link Issn} in {@link #issns} with {@link Issn#getIssn7()} = issn7 or null if not founded
	 * 
	 * @param issn7
	 */
	public static Issn getIssn(int issn7) {
		for (Issn is : issns)
			if (is.getIssn7() == issn7)
				return is;
		return null;
	}

	/**
	 * Uses {@link Issn#getIssnString()} to generate Strings in this list
	 */
	public static List<String> getIssnsString() {
		List<String> tmp = new ArrayList<String>();
		for (Issn issn : issns)
			tmp.add(issn.getIssnString());
		return tmp;
	}

	/**
	 * Uses {@link Issn#getName()} to generate Strings in this list
	 */
	public static List<String> getIssnNames() {
		List<String> tmp = new ArrayList<String>();
		for (Issn issn : issns) {
			tmp.add(issn.getName());
		}
		return tmp;
	}

	/**
	 * Returns first {@link Issn} in {@link #issns} with {@link Issn#getIssnString()} = issn or null if not founded
	 */
	public static Issn getIssnByIssn(String issn) {
		Issn tmp = null;
		for (Issn issn2 : issns)
			if (issn2.getIssnString().equalsIgnoreCase(issn))
				return issn2;
		return tmp;
	}

	/**
	 * Returns first {@link Issn} in {@link #issns} with {@link Issn#getName()} = name case insensitive or null if not founded
	 */
	public static Issn getIssnByName(String name) {
		Issn tmp = null;
		for (Issn issn2 : issns)
			if (issn2.getName().equalsIgnoreCase(name))
				return issn2;
		return tmp;
	}

	/**
	 * Returns table to be used by {@link IssnEditor}
	 */
	public static String[][] getModelStringArray() {
		String[][] out = new String[issns.size()][Issn.ISSN_COLUMNS_COUNT];

		for (int i = 0; i < issns.size(); i++) {
			out[i] = new String[] { issns.get(i).getIssnString(), issns.get(i).getName(), issns.get(i).getTypeString() };
		}

		return out;
	}

	/**
	 * Remove {@link Issn} from {@link #issns} and database, fires {@link Issn#updateIssnArticleModel()}
	 * 
	 * @param issn
	 * @throws SQLException
	 */
	public static void removeIssn(Issn issn) throws SQLException {
		issn.removeIssn();
		issns.remove(issn);
		IssnEditor.getRef().updateTable();
		Issn.updateIssnArticleModel();
	}

	/**
	 * Adds {@link Issn} to {@link IssnModel#issns}
	 * 
	 * @param issn
	 */
	private static void addIssn(Issn issn) {
		issns.add(issn);
		Collections.sort(issns, new Comparator<Issn>() {
			@Override
			public int compare(Issn i1, Issn i2) {
				return i1.getIssn7() - i2.getIssn7();
			}
		});

		IssnEditor.getRef().updateTable();
		Issn.updateIssnArticleModel();
	}

	/**
	 * Create new {@link Issn} and adds it to {@link IssnModel#issns} and database, uses {@link #addIssn(Issn)}
	 * 
	 * @param issn
	 * @param name
	 * @param type
	 * @throws ChecksumException
	 * @throws EmptyNameException
	 * @throws WrongTypeException
	 * @throws SQLException
	 * @throws IssnZeroException
	 * @throws IssnLenghtException
	 */
	public static Issn newIssn(String issn, String name, String type) throws ChecksumException, EmptyNameException, WrongTypeException, SQLException, IssnZeroException, IssnLenghtException {
		Issn issnNew = new Issn(issn, name, type);
		addIssn(issnNew);
		return issnNew;
	}
}
