package presentation;

import bll.LogBLL;
import bll.TableBLL;
import model.Log;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.List;

/**
 * The LogsPage class represents the GUI for managing logs.
 * It allows users to list logs and navigate back to the main menu.
 */
public class LogsPage extends JFrame {
    private JPanel panel;
    private JTable logsTable;
    private JButton listLogsButton, backToMenuButton;

    /**
     * Constructs a new LogsPage.
     */
    public LogsPage() {
        setTitle("Logs Management");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setMinimumSize(new Dimension(800, 600));
        setLayout(new BorderLayout());

        // Create title label
        JLabel titleLabel = new JLabel("Logs Management");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(titleLabel, BorderLayout.NORTH);

        // Create main panel with BoxLayout
        panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // Add margins

        // Create and add components to the panel
        listLogsButton = new JButton("List Logs");
        backToMenuButton = new JButton("Back to Menu");

        // Set maximum sizes for consistent button appearance
        Dimension buttonSize = new Dimension(200, 50);
        listLogsButton.setMaximumSize(buttonSize);
        backToMenuButton.setMaximumSize(buttonSize);

        // Center align buttons
        listLogsButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        backToMenuButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Add components to panel with spacing
        panel.add(listLogsButton);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(backToMenuButton);

        add(panel, BorderLayout.WEST);

        // Create table for displaying logs
        logsTable = new JTable(new DefaultTableModel(new Object[]{"id", "clientName", "amount", "date"}, 0));
        JScrollPane scrollPane = new JScrollPane(logsTable);
        add(scrollPane, BorderLayout.CENTER);

        // Add action listeners for buttons
        listLogsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    List<Log> logList = LogBLL.listAllLogs();
                    TableBLL.generateTableHeader(logsTable, logList);
                    TableBLL.populateTable(logsTable, logList);
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        backToMenuButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new MenuPage();
                dispose();
            }
        });

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }
}
