package me.kinomoto.kpub;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Stores and manages all {@link Lang Langs}. Updates lang combobox in {@link ArticleMainView}.
 */
public class LangModel {
	/**
	 * Store all {@link Lang Langs} here
	 */
	private static List<Lang> langs = new ArrayList<>();

	private LangModel() {
	}

	/**
	 * Loads {@link Lang Langs} from database.
	 * 
	 * @param statement
	 *            from {@link Connection#getStatement()}
	 * @throws SQLException
	 */
	public static void loadLang(Statement statement) throws SQLException {
		ResultSet rs = statement.executeQuery("SELECT * FROM Lang ORDER BY NameEN");

		while (rs.next())
			langs.add(new Lang(rs.getInt("LangID"), rs.getString("NameEN"), rs.getString("NamePL")));

		rs.close();
		LangEditor.ref.updateTable();
	}

	/**
	 * returns first {@link Lang} in {@link #langs} with {@link Lang#getLangID()} = ID or <code>null</code> if not founded
	 */
	public static Lang getLangById(int id) {
		for (Lang lang : langs)
			if (lang.getLangID() == id)
				return lang;
		return null;
	}

	/**
	 * returns first {@link Lang} in {@link #langs} with {@link Lang#getNameEN()} = nameEN or <code>null</code> if not founded
	 */
	public static Lang getLangByNameEN(String nameEN) {
		for (Lang lang : langs)
			if (lang.getNameEN() == nameEN)
				return lang;
		return null;
	}

	/**
	 * uses {@link Lang#getNameEN()} to generate {@link String}
	 */
	public static List<String> getLangsList() {
		List<String> tmp = new ArrayList<String>();
		tmp.add("");
		for (Lang lang : langs)
			tmp.add(lang.getNameEN());
		return tmp;
	}

	/**
	 * @return table to be used by {@link LangEditor}
	 */
	public static String[][] getModelStringArray() {
		String[][] out = new String[langs.size()][Lang.LANG_COLUMNS_COUNT];

		for (int i = 0; i < langs.size(); i++) {
			out[i] = new String[] { String.valueOf(langs.get(i).getLangID()), langs.get(i).getNameEN(), langs.get(i).getNamePL() };
		}

		return out;
	}

	/**
	 * create new {@link Lang}, fires {@link Lang#updateLangArticleModel()}
	 * 
	 * @throws SQLException
	 */
	public static void newLang() throws SQLException {
		langs.add(new Lang());
		Lang.updateLangArticleModel();
	}

	/**
	 * removes {@link Lang} from {@link #langs} and database, and fires {@link Lang#updateLangArticleModel()}
	 * 
	 * @param lang
	 * @throws SQLException
	 */
	public static void removeLang(Lang lang) throws SQLException {
		Connection.makeConnection();
		PreparedStatement st = Connection.prepareStatement("DELETE FROM Lang WHERE LangID = " + lang.getLangID());
		st.execute();
		st.close();
		st = Connection.prepareStatement("UPDATE Article SET LangID = 0 WHERE LangID = " + lang.getLangID());
		st.execute();
		st.close();
		Connection.closeConnection();
		langs.remove(lang);
		Lang.updateLangArticleModel();
	}

}
