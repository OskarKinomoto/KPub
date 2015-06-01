package me.kinomoto.kpub;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Stores and manages output format for data.
 */
public class OutputFormat {

	private static String articleFormat = "";
	private static String posterFormat = "";
	private static String speechFormat = "";
	private static String fileFormat = "";

	private static boolean loaded = false;

	private OutputFormat() {
	}

	/**
	 * loads variables from database, works only once – uses {@link #loaded} variable
	 * 
	 * @throws SQLException
	 */
	public static void load() throws SQLException {
		if (loaded)
			return;

		Connection.makeConnection();
		Statement st = Connection.getStatement();
		ResultSet rs = st.executeQuery("SELECT Format FROM Format WHERE Name = 'Article'");
		rs.next();
		articleFormat = rs.getString(1);

		rs.close();
		rs = st.executeQuery("SELECT Format FROM Format WHERE Name = 'Poster'");
		rs.next();
		posterFormat = rs.getString(1);

		rs.close();
		rs = st.executeQuery("SELECT Format FROM Format WHERE Name = 'Speech'");
		rs.next();
		speechFormat = rs.getString(1);

		rs.close();
		rs = st.executeQuery("SELECT Format FROM Format WHERE Name = 'Out'");
		rs.next();
		fileFormat = rs.getString(1);

		rs.close();
		st.close();
		Connection.closeConnection();
		loaded = true;

		OutputFormatEditor.update();

	}

	/**
	 * @return {@link #articleFormat}
	 */
	public static String getArticleFormat() {
		return articleFormat;
	}

	/**
	 * @return {@link #posterFormat}
	 */
	public static String getPosterFormat() {
		return posterFormat;
	}

	/**
	 * @return {@link #speechFormat}
	 */
	public static String getSpeechFormat() {
		return speechFormat;
	}

	/**
	 * @return {@link #fileFormat}
	 */
	public static String getFileFormat() {
		return fileFormat;
	}

	/**
	 * Updates {@link #articleFormat} in {@link OutputFormat} and database
	 * 
	 * @param articleFormat
	 * @throws SQLException
	 */
	public static void setArticleFormat(String articleFormat) throws SQLException {
		Connection.makeConnection();
		PreparedStatement ps = Connection.prepareStatement("UPDATE Format SET Format = ? WHERE Name = 'Article'");
		ps.setString(1, articleFormat);
		ps.execute();
		ps.close();
		Connection.closeConnection();

		OutputFormat.articleFormat = articleFormat;
	}

	/**
	 * Updates {@link #posterFormat} in {@link OutputFormat} and database
	 * 
	 * @param posterFormat
	 * @throws SQLException
	 */
	public static void setPosterFormat(String posterFormat) throws SQLException {
		Connection.makeConnection();
		PreparedStatement ps = Connection.prepareStatement("UPDATE Format SET Format = ? WHERE Name = 'Poster'");
		ps.setString(1, posterFormat);
		ps.execute();
		ps.close();
		Connection.closeConnection();

		OutputFormat.posterFormat = posterFormat;
	}

	/**
	 * Updates {@link #speechFormat} in {@link OutputFormat} and database
	 * 
	 * @param speechFormat
	 * @throws SQLException
	 */
	public static void setSpeechFormat(String speechFormat) throws SQLException {
		Connection.makeConnection();
		PreparedStatement ps = Connection.prepareStatement("UPDATE Format SET Format = ? WHERE Name = 'Speech'");
		ps.setString(1, speechFormat);
		ps.execute();
		ps.close();
		Connection.closeConnection();

		OutputFormat.speechFormat = speechFormat;
	}

	/**
	 * Updates {@link #fileFormat} in {@link OutputFormat} and database
	 * 
	 * @param fileFormat
	 * @throws SQLException
	 */
	public static void setFileFormat(String fileFormat) throws SQLException {
		Connection.makeConnection();
		PreparedStatement ps = Connection.prepareStatement("UPDATE Format SET Format = ? WHERE Name = 'Out'");
		ps.setString(1, fileFormat);
		ps.execute();
		ps.close();
		Connection.closeConnection();

		OutputFormat.fileFormat = fileFormat;
	}

}
