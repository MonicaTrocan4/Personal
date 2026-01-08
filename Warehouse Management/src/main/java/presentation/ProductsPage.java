package presentation;

import bll.TableBLL;
import model.Product;
import bll.ProductBLL;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.List;

/**
 * The ProductsPage class represents the GUI for managing products.
 * It allows users to insert, update, delete, and list products.
 */
public class ProductsPage extends JFrame {
    private JTextField nameField, priceField, stockQuantityField;
    private JPanel panel;
    private JButton insertProductButton, updateProductButton, deleteProductButton, listProductsButton, backToMenuButton;
    private JTable productsTable;
    private JComboBox<Product> productsComboBox;

    /**
     * Constructs a new ProductsPage.
     */
    public ProductsPage() {
        setTitle("Products Management");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setMinimumSize(new Dimension(800, 600));
        setLayout(new BorderLayout());

        // Create title label
        JLabel titleLabel = new JLabel("Products Management");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(titleLabel, BorderLayout.NORTH);

        // Create main panel with BoxLayout
        panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // Add margins

        productsComboBox = new JComboBox<>();
        productsComboBox.setMaximumSize(new Dimension(500, 20));

        // Create and add components to the panel
        nameField = new JTextField(15);
        nameField.setMaximumSize(new Dimension(200, 20));
        priceField = new JTextField(15);
        priceField.setMaximumSize(new Dimension(200, 20));
        stockQuantityField = new JTextField(15);
        stockQuantityField.setMaximumSize(new Dimension(200, 20));

        insertProductButton = new JButton("Insert Product");
        updateProductButton = new JButton("Update Product");
        deleteProductButton = new JButton("Delete Product");
        listProductsButton = new JButton("List Products");
        backToMenuButton = new JButton("Back to Menu");

        // Set maximum sizes for consistent button appearance
        Dimension buttonSize = new Dimension(200, 50);
        insertProductButton.setMaximumSize(buttonSize);
        updateProductButton.setMaximumSize(buttonSize);
        deleteProductButton.setMaximumSize(buttonSize);
        listProductsButton.setMaximumSize(buttonSize);
        backToMenuButton.setMaximumSize(buttonSize);

        // Center align buttons and text fields
        insertProductButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        updateProductButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        deleteProductButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        listProductsButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        backToMenuButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        nameField.setAlignmentX(Component.CENTER_ALIGNMENT);
        priceField.setAlignmentX(Component.CENTER_ALIGNMENT);
        stockQuantityField.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Add components to panel with spacing
        panel.add(productsComboBox);
        panel.add(new JLabel("Name:"));
        panel.add(nameField);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(new JLabel("Price:"));
        panel.add(priceField);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(new JLabel("Stock Quantity:"));
        panel.add(stockQuantityField);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        panel.add(insertProductButton);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(updateProductButton);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(deleteProductButton);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(listProductsButton);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        panel.add(backToMenuButton);

        add(panel, BorderLayout.WEST);

        // Create table for displaying products
        productsTable = new JTable(new DefaultTableModel(new Object[]{"id", "name", "price", "stock_quantity"}, 0));
        JScrollPane scrollPane = new JScrollPane(productsTable);
        add(scrollPane, BorderLayout.CENTER);

        productsComboBox.removeAllItems();
        List<Product> productsList = ProductBLL.listAllProducts();
        for (Product product : productsList) {
            productsComboBox.addItem(product);
        }

        // Add action listeners for buttons
        insertProductButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Product product = new Product(nameField.getText(), Integer.parseInt(priceField.getText()), Integer.parseInt(stockQuantityField.getText()));
                ProductBLL.insertNewProduct(product);
                productsList.add(product);
                List<Product> productList1 = ProductBLL.listAllProducts();
                productsComboBox.removeAllItems();
                for (Product product1 : productList1) {
                    productsComboBox.addItem(product1);
                }
            }
        });

        updateProductButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Product product = (Product) productsComboBox.getSelectedItem();
                if (!nameField.getText().isEmpty()) {
                    product.setName(nameField.getText());
                }
                if (!priceField.getText().isEmpty()) {
                    product.setPrice(Integer.parseInt(priceField.getText()));
                }
                if (!stockQuantityField.getText().isEmpty()) {
                    product.setStock_quantity(Integer.parseInt(stockQuantityField.getText()));
                }
                ProductBLL.updateProduct(product);
            }
        });

        deleteProductButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Product product = (Product) productsComboBox.getSelectedItem();
                ProductBLL.deleteProduct(product.getId());
                productsList.remove(product);
                productsComboBox.removeItem(product);
            }
        });

        listProductsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                List<Product> productList = ProductBLL.listAllProducts();
                try {
                    TableBLL.generateTableHeader(productsTable, productList);
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
                try {
                    TableBLL.populateTable(productsTable, productList);
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
