package bll;

import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.List;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

/**
 * The TableBLL class provides methods for generating table headers and populating tables with data using reflection.
 */
public class TableBLL {

    /**
     * Generates the table header based on the fields of the objects in the provided list.
     *
     * @param table   the JTable to set the headers on
     * @param objects the list of objects to use for generating the headers
     * @throws SQLException if a database access error occurs
     */
    public static void generateTableHeader(JTable table, List<?> objects) throws SQLException {
        if (objects.isEmpty()) {
            return;
        }

        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setColumnCount(0);
        model.setRowCount(0);

        // Assuming the first object contains the required fields for header generation
        Object firstObject = objects.get(0);
        Field[] fields = firstObject.getClass().getDeclaredFields();
        String[] headers = new String[fields.length];

        for (int i = 0; i < fields.length; i++) {
            headers[i] = fields[i].getName();
        }

        model.setColumnIdentifiers(headers);
    }

    /**
     * Populates the table with data from the provided list of objects.
     *
     * @param table   the JTable to populate
     * @param objects the list of objects to use for populating the table
     * @throws SQLException if a database access error occurs
     */
    public static void populateTable(JTable table, List<?> objects) throws SQLException {
        if (objects.isEmpty()) {
            return;
        }

        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0);

        for (Object obj : objects) {
            Field[] fields = obj.getClass().getDeclaredFields();
            Object[] rowData = new Object[fields.length];

            for (int i = 0; i < fields.length; i++) {
                fields[i].setAccessible(true);
                try {
                    rowData[i] = fields[i].get(obj);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }

            model.addRow(rowData);
        }
    }
}
