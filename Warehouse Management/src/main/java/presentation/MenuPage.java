package presentation;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * The MenuPage class represents the main menu of the Order Management System.
 * It allows navigation to different pages: Clients, Products, Orders, and Logs.
 */
public class MenuPage extends JFrame {

    /**
     * Constructs a new MenuPage.
     */
    public MenuPage() {
        setTitle("Order Management");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setMinimumSize(new Dimension(640, 480));
        setLayout(new BorderLayout());

        // Add title
        JLabel titleLabel = new JLabel("Order Management System");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(titleLabel, BorderLayout.NORTH);

        // Create panel for buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // margins

        // Create and add buttons
        JButton button1 = new JButton("Clients Page");
        JButton button2 = new JButton("Products Page");
        JButton button3 = new JButton("Orders Page");
        JButton button4 = new JButton("Logs Page");
        JButton button5 = new JButton("Exit");

        // Set fixed sizes for buttons
        Dimension buttonSize = new Dimension(200, 50);
        button1.setMaximumSize(buttonSize);
        button2.setMaximumSize(buttonSize);
        button3.setMaximumSize(buttonSize);
        button4.setMaximumSize(buttonSize);
        button5.setMaximumSize(buttonSize);

        // Center align buttons in the panel
        button1.setAlignmentX(Component.CENTER_ALIGNMENT);
        button2.setAlignmentX(Component.CENTER_ALIGNMENT);
        button3.setAlignmentX(Component.CENTER_ALIGNMENT);
        button4.setAlignmentX(Component.CENTER_ALIGNMENT);
        button5.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Add buttons to panel with spacing
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 40)));
        buttonPanel.add(button1);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        buttonPanel.add(button2);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        buttonPanel.add(button3);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        buttonPanel.add(button4);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        buttonPanel.add(button5);

        add(buttonPanel, BorderLayout.CENTER);

        // Add action listeners for buttons
        button1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                new ClientsPage();
            }
        });

        button2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                new ProductsPage();
            }
        });

        button3.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                new OrdersPage();
            }
        });

        button4.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                new LogsPage();
            }
        });

        button5.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    /**
     * The main method to launch the MenuPage.
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MenuPage());
    }
}
