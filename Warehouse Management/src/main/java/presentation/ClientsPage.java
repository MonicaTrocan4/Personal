package presentation;

import bll.ClientBLL;
import bll.TableBLL;
import model.Client;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.List;

/**
 * The ClientsPage class represents the GUI for managing clients.
 * It allows users to insert, delete, update, and list clients.
 */
public class ClientsPage extends JFrame {
    private JTextField nameField, addressField, emailField;
    private JPanel panel;
    private JButton insertClientButton, deleteClientButton, updateClientButton, goToMenuButton, listClientsButton;
    private JComboBox<Client> clientJComboBox;
    private JTable clientsTable;

    /**
     * Constructs a new ClientsPage.
     */
    public ClientsPage() {
        setTitle("Clients Management");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setMinimumSize(new Dimension(800, 600));
        setLayout(new BorderLayout());

        // Create title label
        JLabel titleLabel = new JLabel("Clients Management");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(titleLabel, BorderLayout.NORTH);

        // Create main panel with BoxLayout
        panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // Add margins

        // Create and add components to the panel
        clientJComboBox = new JComboBox<>();
        clientJComboBox.setMaximumSize(new Dimension(500, 20));

        nameField = new JTextField(15);
        nameField.setMaximumSize(new Dimension(200, 20));
        emailField = new JTextField(15);
        emailField.setMaximumSize(new Dimension(200, 20));
        addressField = new JTextField(15);
        addressField.setMaximumSize(new Dimension(200, 20));

        insertClientButton = new JButton("Insert Client");
        deleteClientButton = new JButton("Delete Client");
        updateClientButton = new JButton("Update Client");
        listClientsButton = new JButton("List Clients");
        goToMenuButton = new JButton("Back to Menu");

        // Set maximum sizes for consistent button appearance
        Dimension buttonSize = new Dimension(200, 50);
        insertClientButton.setMaximumSize(buttonSize);
        deleteClientButton.setMaximumSize(buttonSize);
        updateClientButton.setMaximumSize(buttonSize);
        listClientsButton.setMaximumSize(buttonSize);
        goToMenuButton.setMaximumSize(buttonSize);

        // Center align buttons and text fields
        insertClientButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        deleteClientButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        updateClientButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        listClientsButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        goToMenuButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        nameField.setAlignmentX(Component.CENTER_ALIGNMENT);
        addressField.setAlignmentX(Component.CENTER_ALIGNMENT);
        emailField.setAlignmentX(Component.CENTER_ALIGNMENT);
        clientJComboBox.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Add components to panel with spacing
        panel.add(clientJComboBox);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(new JLabel("Name:"));
        panel.add(nameField);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(new JLabel("Email:"));
        panel.add(emailField);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(new JLabel("Address:"));
        panel.add(addressField);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        panel.add(insertClientButton);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(deleteClientButton);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(updateClientButton);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(listClientsButton);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        panel.add(goToMenuButton);

        add(panel, BorderLayout.WEST);

        // Create table for displaying clients
        clientsTable = new JTable(new DefaultTableModel(new Object[]{"id", "name", "email", "address"}, 0));
        JScrollPane scrollPane = new JScrollPane(clientsTable);
        add(scrollPane, BorderLayout.CENTER);

        List<Client> clientList = ClientBLL.listAllClients();
        for (Client client : clientList) {
            clientJComboBox.addItem(client);
        }

        // Add action listeners for buttons
        insertClientButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Client client = new Client(nameField.getText(), emailField.getText(), addressField.getText());
                ClientBLL.insertNewClient(client);
                clientList.add(client);
                List<Client> clientList1 = ClientBLL.listAllClients();
                clientJComboBox.removeAllItems();
                for (Client client1 : clientList1) {
                    clientJComboBox.addItem(client1);
                }
            }
        });

        deleteClientButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Client client = (Client) clientJComboBox.getSelectedItem();
                ClientBLL.deleteClient(client.getId());
                clientList.remove(client);
                clientJComboBox.removeItem(client);
            }
        });

        updateClientButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Client client = (Client) clientJComboBox.getSelectedItem();
                if (!nameField.getText().isEmpty()) {
                    client.setName(nameField.getText());
                }
                if (!emailField.getText().isEmpty()) {
                    client.setEmail(emailField.getText());
                }
                if (!addressField.getText().isEmpty()) {
                    client.setAddress(addressField.getText());
                }
                ClientBLL.updateClient(client);
            }
        });

        listClientsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                List<Client> clientList1 = ClientBLL.listAllClients();

                try {
                    TableBLL.generateTableHeader(clientsTable, clientList1);
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
                try {
                    TableBLL.populateTable(clientsTable, clientList1);
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        goToMenuButton.addActionListener(new ActionListener() {
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
