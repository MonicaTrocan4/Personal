package model;

import java.util.Date;

/**
 * The Log class represents a log entry in the system.
 * It contains information about the log entry, such as id, client name, amount, and date.
 */
public record Log(int id, String clientName, int amount, Date date) {
}
