package model;

import java.util.Date;

/**
 * The Orders class represents an order in the system.
 * It contains information about the order, such as id, client_id, product_id, client_name, product_name, order_date, quantity, and total_price.
 */
public class Orders {
    private int id;
    private int client_id;
    private int product_id;
    private String client_name;
    private String product_name;
    private Date order_date;
    private int quantity;
    private int total_price;

    /**
     * Default constructor for Orders.
     */
    public Orders() {
    }

    /**
     * Constructs a new Order with the specified id, client_id, product_id, client name, product name, order date, quantity, and total price.
     *
     * @param id          the id of the order
     * @param client_id   the id of the client
     * @param product_id  the id of the product
     * @param clientName  the name of the client
     * @param productName the name of the product
     * @param order_date  the date of the order
     * @param quantity    the quantity of the product ordered
     * @param total_price the total price of the order
     */
    public Orders(int id, int client_id, int product_id, String clientName, String productName, Date order_date, int quantity, int total_price) {
        this.id = id;
        this.client_id = client_id;
        this.product_id = product_id;
        this.client_name = clientName;
        this.product_name = productName;
        this.order_date = order_date;
        this.quantity = quantity;
        this.total_price = total_price;
    }

    /**
     * Constructs a new Order with the specified client_id, product_id, client name, product name, order date, quantity, and total price.
     *
     * @param client_id   the id of the client
     * @param product_id  the id of the product
     * @param clientName  the name of the client
     * @param productName the name of the product
     * @param order_date  the date of the order
     * @param quantity    the quantity of the product ordered
     * @param total_price the total price of the order
     */
    public Orders(int client_id, int product_id, String clientName, String productName, Date order_date, int quantity, int total_price) {
        this.client_id = client_id;
        this.product_id = product_id;
        this.client_name = clientName;
        this.product_name = productName;
        this.order_date = order_date;
        this.quantity = quantity;
        this.total_price = total_price;
    }

    /**
     * Constructs a new Order with the specified client_id, product_id, and quantity.
     *
     * @param id1 the id of the client
     * @param id2 the id of the product
     * @param q   the quantity of the product ordered
     */
    public Orders(int id1, int id2, int q) {
        this.client_id = id1;
        this.product_id = id2;
        this.quantity = q;
    }

    /**
     * Gets the id of the order.
     *
     * @return the id of the order
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the id of the order.
     *
     * @param id the new id of the order
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Gets the client_id of the order.
     *
     * @return the client_id of the order
     */
    public int getClient_id() {
        return client_id;
    }

    /**
     * Sets the client_id of the order.
     *
     * @param client_id the new client_id of the order
     */
    public void setClient_id(int client_id) {
        this.client_id = client_id;
    }

    /**
     * Gets the product_id of the order.
     *
     * @return the product_id of the order
     */
    public int getProduct_id() {
        return product_id;
    }

    /**
     * Sets the product_id of the order.
     *
     * @param product_id the new product_id of the order
     */
    public void setProduct_id(int product_id) {
        this.product_id = product_id;
    }

    /**
     * Gets the order_date of the order.
     *
     * @return the order_date of the order
     */
    public Date getOrder_date() {
        return order_date;
    }

    /**
     * Sets the order_date of the order.
     *
     * @param order_date the new order_date of the order
     */
    public void setOrder_date(Date order_date) {
        this.order_date = order_date;
    }

    /**
     * Gets the quantity of the order.
     *
     * @return the quantity of the order
     */
    public int getQuantity() {
        return quantity;
    }

    /**
     * Sets the quantity of the order.
     *
     * @param quantity the new quantity of the order
     */
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    /**
     * Gets the total_price of the order.
     *
     * @return the total_price of the order
     */
    public int getTotal_price() {
        return total_price;
    }

    /**
     * Sets the total_price of the order.
     *
     * @param total_price the new total_price of the order
     */
    public void setTotal_price(int total_price) {
        this.total_price = total_price;
    }

    /**
     * Gets the client_name of the order.
     *
     * @return the client_name of the order
     */
    public String getClient_name() {
        return client_name;
    }

    /**
     * Sets the client_name of the order.
     *
     * @param client_name the new client_name of the order
     */
    public void setClient_name(String client_name) {
        this.client_name = client_name;
    }

    /**
     * Gets the product_name of the order.
     *
     * @return the product_name of the order
     */
    public String getProduct_name() {
        return product_name;
    }

    /**
     * Sets the product_name of the order.
     *
     * @param product_name the new product_name of the order
     */
    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }

    /**
     * Returns a string representation of the order.
     *
     * @return a string representation of the order
     */
    @Override
    public String toString() {
        return "Order [" +
                "id=" + id +
                ", client_id=" + client_id +
                ", product_id=" + product_id +
                ", client_name='" + client_name + '\'' +
                ", product_name='" + product_name + '\'' +
                ", order_date=" + order_date +
                ", quantity=" + quantity +
                ", total_price=" + total_price +
                ']';
    }
}
