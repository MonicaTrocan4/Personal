package model;

/**
 * The Product class represents a product in the system.
 * It contains information about the product, such as id, name, price, and stock quantity.
 */
public class Product {
    private int id;
    private String name;
    private int price;
    private int stock_quantity;

    /**
     * Default constructor for Product.
     */
    public Product() {
    }

    /**
     * Constructs a new Product with the specified id, name, price, and stock quantity.
     *
     * @param id             the id of the product
     * @param name           the name of the product
     * @param price          the price of the product
     * @param stock_quantity the stock quantity of the product
     */
    public Product(int id, String name, int price, int stock_quantity) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.stock_quantity = stock_quantity;
    }

    /**
     * Constructs a new Product with the specified name, price, and stock quantity.
     *
     * @param name           the name of the product
     * @param price          the price of the product
     * @param stock_quantity the stock quantity of the product
     */
    public Product(String name, int price, int stock_quantity) {
        this.name = name;
        this.price = price;
        this.stock_quantity = stock_quantity;
    }

    /**
     * Gets the id of the product.
     *
     * @return the id of the product
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the id of the product.
     *
     * @param id the new id of the product
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Gets the name of the product.
     *
     * @return the name of the product
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the product.
     *
     * @param name the new name of the product
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the price of the product.
     *
     * @return the price of the product
     */
    public int getPrice() {
        return price;
    }

    /**
     * Sets the price of the product.
     *
     * @param price the new price of the product
     */
    public void setPrice(int price) {
        this.price = price;
    }

    /**
     * Gets the stock quantity of the product.
     *
     * @return the stock quantity of the product
     */
    public int getStock_quantity() {
        return stock_quantity;
    }

    /**
     * Sets the stock quantity of the product.
     *
     * @param stock_quantity the new stock quantity of the product
     */
    public void setStock_quantity(int stock_quantity) {
        this.stock_quantity = stock_quantity;
    }

    /**
     * Returns a string representation of the product.
     *
     * @return a string representation of the product
     */
    @Override
    public String toString() {
        return "Product [id=" + id + ", name=" + name + ", price=" + price + ", stock_quantity=" + stock_quantity + "]";
    }
}
