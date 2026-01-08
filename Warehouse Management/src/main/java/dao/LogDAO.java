package dao;

import connection.ConnectionFactory;
import model.Log;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.sql.Date;
import java.util.List;
import java.util.logging.Logger;

/**
 * The LogDAO class provides methods for accessing and manipulating log entries in the database.
 */
public class LogDAO {
    protected static final Logger LOGGER = Logger.getLogger(LogDAO.class.getName());

    /**
     * Inserts a new log entry into the database.
     *
     * @param log the log entry to be inserted
     * @return 0 if the insertion was successful
     * @throws SQLException if a database access error occurs
     */
    public static int insertNewLog(Log log) throws SQLException {
        String query = "INSERT INTO Log (clientName, amount, date) VALUES (?, ?, ?)";
        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, log.clientName());
            statement.setDouble(2, log.amount());
            java.sql.Date sqlDate = new java.sql.Date(log.date().getTime());
            statement.setDate(3, sqlDate);
            statement.executeUpdate();
        }
        return 0;
    }

    /**
     * Retrieves all log entries from the database.
     *
     * @return a list of all log entries
     * @throws SQLException if a database access error occurs
     */
    public static List<Log> listLogs() throws SQLException {
        String query = "SELECT id, clientName, amount, date FROM Log";
        List<Log> logsList = new ArrayList<>();

        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String client_name = resultSet.getString("clientName");
                int amount = resultSet.getInt("amount");
                Date date = resultSet.getDate("date");

                Log log = new Log(id, client_name, amount, date);
                logsList.add(log);
            }
        }

        return logsList;
    }
}
