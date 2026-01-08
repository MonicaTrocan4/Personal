package connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The ConnectionFactory class is responsible for managing database connections.
 * It implements the Singleton pattern to ensure that only one instance of the connection factory exists.
 */
public class ConnectionFactory {

	private static final Logger LOGGER = Logger.getLogger(ConnectionFactory.class.getName());
	private static final String DRIVER = "com.mysql.cj.jdbc.Driver";
	private static final String DBURL = "jdbc:mysql://localhost:3306/gestiuneComenzi";
	private static final String USER = "root";
	private static final String PASS = "floricele1";

	private static ConnectionFactory singleInstance = new ConnectionFactory();

	/**
	 * Private constructor to load the database driver and prevent instantiation.
	 */
	private ConnectionFactory() {
		try {
			Class.forName(DRIVER);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Creates a new connection to the database.
	 *
	 * @return a new Connection object
	 */
	private Connection createConnection() {
		Connection connection = null;
		try {
			connection = DriverManager.getConnection(DBURL, USER, PASS);
		} catch (SQLException e) {
			LOGGER.log(Level.WARNING, "An error occurred while trying to connect to the database");
			e.printStackTrace();
		}
		return connection;
	}

	/**
	 * Gets a connection to the database using the singleton instance.
	 *
	 * @return a Connection object
	 */
	public static Connection getConnection() {
		return singleInstance.createConnection();
	}

	/**
	 * Closes the given Connection object.
	 *
	 * @param connection the Connection object to be closed
	 */
	public static void close(Connection connection) {
		if (connection != null) {
			try {
				connection.close();
			} catch (SQLException e) {
				LOGGER.log(Level.WARNING, "An error occurred while trying to close the connection");
			}
		}
	}

	/**
	 * Closes the given Statement object.
	 *
	 * @param statement the Statement object to be closed
	 */
	public static void close(Statement statement) {
		if (statement != null) {
			try {
				statement.close();
			} catch (SQLException e) {
				LOGGER.log(Level.WARNING, "An error occurred while trying to close the statement");
			}
		}
	}

	/**
	 * Closes the given ResultSet object.
	 *
	 * @param resultSet the ResultSet object to be closed
	 */
	public static void close(ResultSet resultSet) {
		if (resultSet != null) {
			try {
				resultSet.close();
			} catch (SQLException e) {
				LOGGER.log(Level.WARNING, "An error occurred while trying to close the ResultSet");
			}
		}
	}
}
