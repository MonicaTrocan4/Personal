package bll;

import dao.LogDAO;
import model.Log;

import java.sql.SQLException;
import java.util.List;

/**
 * The LogBLL class handles the business logic for Log operations.
 * It interacts with the LogDAO class to perform CRUD operations.
 */
public class LogBLL {

    private static LogDAO logDAO = new LogDAO();

    /**
     * Lists all logs in the database.
     *
     * @return a list of all Log objects
     * @throws SQLException if a database access error occurs
     */
    public static List<Log> listAllLogs() throws SQLException {
        return LogDAO.listLogs();
    }

    /**
     * Inserts a new log into the database.
     *
     * @param log the Log object to insert
     * @return the ID of the newly inserted log
     * @throws SQLException if a database access error occurs
     */
    public static int insertNewLog(Log log) throws SQLException {
        return LogDAO.insertNewLog(log);
    }
}
