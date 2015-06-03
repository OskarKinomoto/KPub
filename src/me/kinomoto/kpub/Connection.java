package me.kinomoto.kpub;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.prefs.Preferences;

/**
 * Makes connections with database
 */
public class Connection {

	/**
	 * Current open connection with database
	 */
	private static volatile java.sql.Connection connection = null;

	/**
	 * Connection counter
	 */
	private static volatile int openConnections = 0;

	/**
	 * Database default user name
	 */
	private static final String DB_USER = "jdbc";

	/**
	 * Database default user password
	 */
	private static final String DB_PASS = "jdbc";

	/**
	 * Database default name
	 */
	private static final String DB_NAME = "jdbc";

	/**
	 * Database default host location
	 */
	private static final String DB_HOST = "localihost";

	/**
	 * Store credentials at pc preferences
	 */
	private static Preferences prefs = Preferences.userNodeForPackage(Connection.class);

	private Connection() {
	}

	/**
	 * Open new {@link #connection} and/or increment {@link #openConnections}. Close connection with {@link #closeConnection()}
	 * 
	 * @throws SQLException
	 */
	public static synchronized void makeConnection() throws SQLException {
		openConnections++;
		if (openConnections == 1)
			connection = DriverManager.getConnection("jdbc:mysql://" + getHost() + "/" + getDB() + "?useUnicode=yes&characterEncoding=UTF-8", getName(), getPass());
	}

	/**
	 * Make {@link Statement} and connects to database if necessary.
	 * 
	 * @throws SQLException
	 */
	public static synchronized Statement getStatement() throws SQLException {
		if (connection == null)
			makeConnection();
		return connection.createStatement();
	}

	/**
	 * Make {@link PreparedStatement} and connects to database if necessary.
	 * 
	 * @param sql
	 * @throws SQLException
	 */
	public static synchronized PreparedStatement prepareStatement(String sql) throws SQLException {
		if (connection == null)
			makeConnection();
		return connection.prepareStatement(sql);
	}

	/**
	 * Decrement {@link #openConnections} and/or close {@link #connection}
	 */
	public static synchronized void closeConnection() {
		if (connection == null) {
			openConnections = 0;
			return;
		}
		if (openConnections > 0)
			openConnections--;
		if (openConnections == 0) {
			try {
				connection.close();
			} catch (SQLException e) {
				// nth to be done
			} finally {
				connection = null;
			}
		}
	}

	public static boolean testConnection() {
		try {
			makeConnection();
			return true;
		} catch (SQLException e) {
			return false;
		} finally {
			closeConnection();
		}
	}

	public static String getHost() {
		return prefs.get("DB_HOST", DB_HOST);
	}

	public static void setHost(String host) {
		prefs.put("DB_HOST", host);
	}

	public static String getName() {
		return prefs.get("USER_NAME", DB_USER);
	}

	public static void setName(String name) {
		prefs.put("USER_NAME", name);
	}

	public static String getPass() {
		return prefs.get("USER_PASS", DB_PASS);
	}

	public static void setPass(String pass) {
		prefs.put("USER_PASS", pass);
	}

	public static String getDB() {
		return prefs.get("DB_NAME", DB_NAME);
	}

	public static void setDB(String db) {
		prefs.put("DB_NAME", db);
	}

	public static void restart() {
		try {
			if (connection != null)
				connection.close();
		} catch (SQLException e) {
		}
		openConnections = 0;
	}

}
