package presentation;

import bll.*;
import model.Client;
import model.Log;
import model.Orders;
import model.Product;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.List;

/**
 * The OrdersPage class represents the GUI for managing orders.
 * It allows users to create, cancel, and list orders.
 */
public class OrdersPage extends JFrame {
    private JPanel panel;
    private JComboBox<Client> clientCombo;
    private JComboBox<Product> productCombo;
    private JButton createOrderButton, cancelOrderButton, listOrdersButton, backToMenuButton;
    private JTable ordersTable;
    private JTextField quantityField, cancelOrderIdField;

    /**
     * Constructs a new OrdersPage.
     */
    public OrdersPage() {
        setTitle("Orders Management");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setMinimumSize(new Dimension(1200, 600));
        setLayout(new BorderLayout());

        // Create title label
        JLabel titleLabel = new JLabel("Orders Management");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(titleLabel, BorderLayout.NORTH);

        // Create main panel with BoxLayout
        panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // Add margins

        // Create and add components to the panel
        clientCombo = new JComboBox<>();
        clientCombo.setMaximumSize(new Dimension(500, 20));
        productCombo = new JComboBox<>();
        productCombo.setMaximumSize(new Dimension(500, 20));

        List<Client> clientList = ClientBLL.listAllClients();
        List<Product> productList = ProductBLL.listAllProducts();
        for (Product product : productList) {
            productCombo.addItem(product);
        }
        for (Client client : clientList) {
            clientCombo.addItem(client);
        }
        List<Orders> ordersList = OrderBLL.listAllOrders();

        quantityField = new JTextField(15);
        quantityField.setMaximumSize(new Dimension(200, 20));
        cancelOrderIdField = new JTextField(15);
        cancelOrderIdField.setMaximumSize(new Dimension(200, 20));

        createOrderButton = new JButton("Create Order");
        cancelOrderButton = new JButton("Cancel Order");
        listOrdersButton = new JButton("List Orders");
        backToMenuButton = new JButton("Back to Menu");

        // Set maximum sizes for consistent button appearance
        Dimension buttonSize = new Dimension(200, 50);
        createOrderButton.setMaximumSize(buttonSize);
        cancelOrderButton.setMaximumSize(buttonSize);
        listOrdersButton.setMaximumSize(buttonSize);
        backToMenuButton.setMaximumSize(buttonSize);

        // Center align buttons and text fields
        createOrderButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        cancelOrderButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        listOrdersButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        backToMenuButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        clientCombo.setAlignmentX(Component.CENTER_ALIGNMENT);
        productCombo.setAlignmentX(Component.CENTER_ALIGNMENT);
        quantityField.setAlignmentX(Component.CENTER_ALIGNMENT);
        cancelOrderIdField.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Add components to panel with spacing
        panel.add(new JLabel("Client:"));
        panel.add(clientCombo);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(new JLabel("Product:"));
        panel.add(productCombo);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(new JLabel("Quantity:"));
        panel.add(quantityField);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(new JLabel("Cancel Order ID:"));
        panel.add(cancelOrderIdField);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        panel.add(createOrderButton);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(cancelOrderButton);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(listOrdersButton);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        panel.add(backToMenuButton);

        add(panel, BorderLayout.WEST);

        // Create table for displaying orders
        ordersTable = new JTable(new DefaultTableModel(new Object[]{"id", "client_id", "product_id", "client_name", "product_name", "order_date", "quantity", "total_price"}, 0));
        JScrollPane scrollPane = new JScrollPane(ordersTable);
        add(scrollPane, BorderLayout.CENTER);

        // Add action listeners for buttons
        createOrderButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Product product = (Product) productCombo.getSelectedItem();
                Client client = (Client) clientCombo.getSelectedItem();

                Orders order = new Orders(client.getId(), product.getId(), Integer.parseInt(quantityField.getText()));
                int success = OrderBLL.insertNewOrder(order);
                if (success != -1) {
                    product.setStock_quantity(product.getStock_quantity() - Integer.parseInt(quantityField.getText()));
                }
                ordersList.add(order);

                Log log = new Log(order.getId(), client.getName(), order.getQuantity(), order.getOrder_date());
                try {
                    LogBLL.insertNewLog(log);
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        cancelOrderButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int cancelId = Integer.parseInt(cancelOrderIdField.getText());

                Orders order = OrderBLL.findOrderById(cancelId);
                Product product = ProductBLL.findProductById(order.getProduct_id());
                product.setStock_quantity(product.getStock_quantity() + order.getQuantity());
                ProductBLL.updateProduct(product);

                OrderBLL.deleteOrder(cancelId);
                ordersList.remove(order);
            }
        });

        listOrdersButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                List<Orders> ordersList2 = OrderBLL.listAllOrders();

                try {
                    TableBLL.generateTableHeader(ordersTable, ordersList2);
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
                try {
                    TableBLL.populateTable(ordersTable, ordersList2);
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
