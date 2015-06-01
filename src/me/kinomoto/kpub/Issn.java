package me.kinomoto.kpub;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Stores ISSN – number, name and MNiSW list type.
 */
public class Issn {
	public static final int ISSN_COLUMNS_COUNT = 3;

	/**
	 * Issn number without 8th check sum character
	 */
	private int issn7;

	/**
	 * Issn in ####-###* format, where # is number and * is number or 'X'
	 */
	private String issnString;

	/**
	 * Journal name
	 */
	private String name;

	/**
	 * MNiSW list
	 */
	public enum LIST_TYPE {
		LIST_A, LIST_B, UNLISTED
	}

	/**
	 * To which MNiSW list it belongs
	 */
	private LIST_TYPE type;

	/**
	 * Issn from database.
	 */
	public Issn(int issn7, String name, String type) {
		super();
		this.issn7 = issn7;
		this.name = name;
		this.type = LIST_TYPE.valueOf(type.toUpperCase());
		updateIssnString();
	}

	/**
	 * Create new issn in database
	 * 
	 * @throws ChecksumException
	 * @throws EmptyNameException
	 * @throws WrongTypeException
	 * @throws SQLException
	 * @throws IssnZeroException
	 * @throws IssnLenghtException
	 */
	public Issn(String issn, String name, String type) throws ChecksumException, EmptyNameException, WrongTypeException, SQLException, IssnZeroException, IssnLenghtException {
		if (!Issn.checkIssn(issn)) {
			throw new ChecksumException();
		}
		this.issn7 = issnStringToIssn7(issn);

		if (name.isEmpty()) {
			throw new EmptyNameException();
		}
		this.name = name;

		try {
			this.type = LIST_TYPE.valueOf(type.replaceAll(" ", "_").toUpperCase());
		} catch (IllegalArgumentException e) {
			throw new WrongTypeException();
		}

		Connection.makeConnection();
		PreparedStatement st = Connection.prepareStatement("INSERT INTO `ISSN` (`ISSN`, `ISSNName`, `List`) VALUES (?,?,?);");
		st.setInt(1, this.issn7);
		st.setString(2, this.name);
		st.setString(3, this.type.toString());
		st.execute();
		st.close();
		Connection.closeConnection();
	}

	/**
	 * {@link String} array with list types. Used by {@link IssnAdder} and {@link IssnEditor}.
	 * 
	 * @return { ”List A”, ”List B”, ”Unlisted” }
	 */
	public static String[] getIssnTypesArray() {
		return new String[] { "List A", "List B", "Unlisted" };
	}

	/**
	 * @return {@link #issn7}
	 */
	public int getIssn7() {
		return issn7;
	}

	/**
	 * @return {@link #name}
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return {@link #type}
	 */
	public LIST_TYPE getType() {
		return type;
	}

	/**
	 * @return {@link #issnString}
	 */
	public String getIssnString() {
		updateIssnString();
		return issnString;
	}

	/**
	 * Calculate issn check sum
	 * 
	 * @param issn7
	 */
	private static String calculateCheckSum(int issn7) {
		int counter = 2;
		int sum = 0;
		int j = 0;
		int tmpIssn = issn7;
		for (int i = 8; i >= 0; i--) {
			sum += counter * (tmpIssn % 10);
			tmpIssn /= 10;
			counter += 1;
		}
		for (j = 0; j < 10; j++) {
			if ((sum + j) % 11 == 0) {
				break;
			}
		}
		return String.valueOf(j == 10 ? "X" : j);
	}

	/**
	 * Check if issn is correct
	 * 
	 * @param issn
	 * @throws IssnZeroException
	 * @throws IssnLenghtException
	 */
	public static boolean checkIssn(String issn) throws IssnZeroException, IssnLenghtException {
		try {
			if (issn.length() != 9)
				throw new IssnLenghtException();
			String checkIssn = issn.substring(8);
			int tmpIssn = Integer.valueOf(issn.substring(0, 4) + issn.substring(5, 8));
			if (tmpIssn == 0)
				throw new IssnZeroException();
			return calculateCheckSum(tmpIssn).equals(checkIssn);
		} catch (StringIndexOutOfBoundsException e) {
			throw new IssnLenghtException();
		}
	}

	/**
	 * @param issn
	 * @return {@link #issn7}
	 */
	private static int issnStringToIssn7(String issn) {
		return Integer.valueOf(issn.substring(0, 4) + issn.substring(5, 8));
	}

	/**
	 * Calculate issn check sum and returns issn number in #######* format.
	 */
	private String issn7toIssn8withChecksum() {
		return String.format("%07d", issn7) + "" + calculateCheckSum(issn7);
	}

	/**
	 * Make new {@link #issnString} from {@link #issn7}
	 */
	private void updateIssnString() {
		StringBuilder tmp = new StringBuilder(issn7toIssn8withChecksum());
		tmp.insert(4, "-");
		issnString = tmp.toString();
	}

	/**
	 * @return
	 *         ”List A” for LIST A, ”List B” for LIST B, ”Unlisted” for UNLISTED
	 */
	public String getTypeString() {
		switch (type) {
		case LIST_A:
			return "List A";
		case LIST_B:
			return "List B";
		case UNLISTED:
			return "Unlisted";
		default:
			return "";
		}
	}

	/**
	 * Update issn comboboxes in {@link ArticleView}
	 */
	public static void updateIssnArticleModel() {
		ArticleView v = ArticleView.getRef();
		if (v != null)
			v.updateIssn();
	}

	/**
	 * Update issn number in {@link #issn7} and database. Fires {@link #updateIssnString()} and {@link #updateIssnArticleModel()}.
	 * 
	 * @throws SQLException
	 */
	public void setIssn(String issn) throws SQLException {
		int issn7 = Integer.valueOf(issn.substring(0, 4) + issn.substring(5, 8));
		Connection.makeConnection();
		PreparedStatement st = Connection.prepareStatement("UPDATE `ISSN` SET ISSN = ? WHERE ISSN = " + this.issn7);
		st.setInt(1, issn7);
		st.execute();
		st.close();
		st = Connection.prepareStatement("UPDATE `Article` SET ISSN = ? WHERE ISSN = " + this.issn7);
		st.setInt(1, issn7);
		st.execute();

		this.issn7 = issn7;
		updateIssnString();
		updateIssnArticleModel();

		st.close();
		Connection.closeConnection();
	}

	/**
	 * Update {@link #type} uses {@link LIST_TYPE#valueOf(String)} can throw exception from it.
	 * 
	 * @throws SQLException
	 */
	public void setType(String type) throws SQLException {
		LIST_TYPE t = LIST_TYPE.valueOf(type.replaceAll(" ", "_").toUpperCase());
		Connection.makeConnection();
		PreparedStatement st = Connection.prepareStatement("UPDATE `ISSN` SET List = ? WHERE ISSN = " + this.issn7);
		st.setString(1, t.toString());
		st.execute();

		this.type = t;

		st.close();
		Connection.closeConnection();
	}

	/**
	 * Update {@link #name} and database.
	 * 
	 * @throws SQLException
	 */
	public void setName(String name) throws SQLException {
		Connection.makeConnection();
		PreparedStatement st = Connection.prepareStatement("UPDATE `ISSN` SET ISSNName = ? WHERE ISSN = " + this.issn7);
		st.setString(1, name);
		st.execute();

		this.name = name;

		updateIssnArticleModel();
		st.close();
		Connection.closeConnection();
	}

	/**
	 * Deletes {@link Issn} from database, use {@link IssnModel#removeIssn(Issn)} instead.
	 * 
	 * @throws SQLException
	 */
	public void removeIssn() throws SQLException {
		Connection.makeConnection();
		PreparedStatement st = Connection.prepareStatement("DELETE FROM `ISSN` WHERE ISSN = " + this.issn7);
		st.execute();
		st.close();
		st = Connection.prepareStatement("UPDATE Article SET ISSN = 0 WHERE ISSN = " + this.issn7);
		st.execute();
		st.close();
		Connection.closeConnection();
	}

	/**
	 * threw when checksum of issn number is invalid
	 */
	public static class ChecksumException extends Exception {
		private static final long serialVersionUID = 9170012356898489333L;
	}

	/**
	 * threw when issn name is empty
	 */
	public static class EmptyNameException extends Exception {
		private static final long serialVersionUID = -6617341848568809034L;
	}

	/**
	 * threw when {@link #type} can not be cast to {@link LIST_TYPE}
	 */
	public static class WrongTypeException extends Exception {
		private static final long serialVersionUID = -3917341848523452134L;
	}

	/**
	 * threw when issn is equal to 0000-0000
	 */
	public static class IssnZeroException extends Exception {
		private static final long serialVersionUID = -3519947551622293077L;
	}

	/**
	 * threw when issn string is not 9 character long
	 */
	public static class IssnLenghtException extends Exception {
		private static final long serialVersionUID = 398924281182939380L;
	}

}
