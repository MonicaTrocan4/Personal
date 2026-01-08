package bll;

import dao.ProductDAO;
import model.Product;

import java.util.List;
import java.util.NoSuchElementException;

/**
 * The ProductBLL class handles the business logic for Product operations.
 * It interacts with the ProductDAO class to perform CRUD operations.
 */
public class ProductBLL {

    private static ProductDAO productDAO = new ProductDAO();

    /**
     * Finds a product by its ID.
     *
     * @param id the ID of the product to find
     * @return the Product object if found
     * @throws NoSuchElementException if the product with the specified ID is not found
     */
    public static Product findProductById(int id) {
        Product product = productDAO.findById(id);
        if (product == null) {
            throw new NoSuchElementException("The product with id = " + id + " was not found!");
        }
        return product;
    }

    /**
     * Inserts a new product into the database.
     *
     * @param product the Product object to insert
     * @return the ID of the newly inserted product
     */
    public static int insertNewProduct(Product product) {
        return productDAO.insert(product);
    }

    /**
     * Deletes a product from the database by its ID.
     *
     * @param id the ID of the product to delete
     * @return the number of rows affected
     */
    public static int deleteProduct(int id) {
        return productDAO.delete(id);
    }

    /**
     * Updates an existing product in the database.
     *
     * @param product the Product object with updated information
     * @return the number of rows affected
     */
    public static int updateProduct(Product product) {
        return productDAO.update(product);
    }

    /**
     * Lists all products in the database.
     *
     * @return a list of all Product objects
     */
    public static List<Product> listAllProducts() {
        return productDAO.findAll();
    }
}
