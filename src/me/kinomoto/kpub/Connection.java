package me.kinomoto.kpub;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Makes connections with database
 */
public class Connection {

	/**
	 * Current open connection with database
	 */
	private static java.sql.Connection connection = null;

	/**
	 * Connection counter
	 */
	private static int openConnections = 0;

	/**
	 * Database user name
	 */
	private static final String DB_USER = "jdbc";

	/**
	 * Database user password
	 */
	private static final String DB_PASS = "jdbc";

	/**
	 * Database name
	 */
	private static final String DB_NAME = "jdbc";

	/**
	 * Database host location
	 */
	private static final String DB_HOST = "localhost";

	private Connection() {
	}

	/**
	 * Open new {@link #connection} and/or increment {@link #openConnections}. Close connection with {@link #closeConnection()}
	 * 
	 * @throws SQLException
	 */
	public static void makeConnection() throws SQLException {
		openConnections++;
		if (openConnections == 1)
			connection = DriverManager.getConnection("jdbc:mysql://" + DB_HOST + "/" + DB_NAME, DB_USER, DB_PASS);
	}

	/**
	 * Make {@link Statement} and connects to database if necessary.
	 * 
	 * @throws SQLException
	 */
	public static Statement getStatement() throws SQLException {
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
	public static PreparedStatement prepareStatement(String sql) throws SQLException {
		if (connection == null)
			makeConnection();
		return connection.prepareStatement(sql);
	}

	/**
	 * Decrement {@link #openConnections} and/or close {@link #connection}
	 */
	public static void closeConnection() {
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
}
