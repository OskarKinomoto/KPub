package me.kinomoto.kpub;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Stores language information – ID, English name, Polish name *
 */
public class Lang {

	public static final int LANG_COLUMNS_COUNT = 3;
	private int langID;
	private String nameEN;
	private String namePL;

	/**
	 * @param langID
	 * @param nameEN
	 * @param namePL
	 */
	public Lang(int langID, String nameEN, String namePL) {
		super();
		this.langID = langID;
		this.nameEN = nameEN;
		this.namePL = namePL;
	}

	/**
	 * Make new Lang in database
	 * 
	 * @throws SQLException
	 */
	public Lang() throws SQLException {
		Connection.makeConnection();
		Statement st = Connection.getStatement();
		st.executeUpdate("INSERT INTO Lang (LangID, NamePL, NameEN) VALUES (NULL, 'język', 'language')", Statement.RETURN_GENERATED_KEYS);
		ResultSet rs = st.getGeneratedKeys();
		rs.next();
		langID = rs.getInt(1);
		rs.close();
		st.close();
		Connection.closeConnection();
		this.nameEN = "language";
		this.namePL = "język";
	}

	/**
	 * @return langID
	 */
	public int getLangID() {
		return langID;
	}

	/**
	 * @return English language name
	 */
	public String getNameEN() {
		return nameEN;
	}

	/**
	 * @return Polish language name
	 */
	public String getNamePL() {
		return namePL;
	}

	/**
	 * fires {@link ArticleMainView#updateLangs()} if {@link ArticleMainView#getRef()} is not <code>null</code>
	 */
	static void updateLangArticleModel() {
		ArticleMainView v = ArticleMainView.getRef();
		if (v != null)
			v.updateLangs();
	}

	/**
	 * Sets language name in English
	 * 
	 * @throws SQLException
	 */
	public void setNameEN(String name) throws SQLException {
		Connection.makeConnection();
		PreparedStatement st = Connection.prepareStatement("UPDATE `Lang` SET NameEN = ? WHERE LangID = " + this.langID);
		st.setString(1, name);
		st.execute();

		this.nameEN = name;
		updateLangArticleModel();

		st.close();
		Connection.closeConnection();
	}

	/**
	 * Sets language name in Polish
	 * 
	 * @throws SQLException
	 */
	public void setNamePL(String name) throws SQLException {
		Connection.makeConnection();
		PreparedStatement st = Connection.prepareStatement("UPDATE `Lang` SET NamePL = ? WHERE LangID = " + this.langID);
		st.setString(1, name);
		st.execute();

		this.namePL = name;
		updateLangArticleModel();

		st.close();
		Connection.closeConnection();
	}

}
