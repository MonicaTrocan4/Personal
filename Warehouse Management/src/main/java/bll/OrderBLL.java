package bll;

import dao.OrderDAO;
import model.Orders;
import model.Product;

import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * The OrderBLL class handles the business logic for Order operations.
 * It interacts with the OrderDAO class to perform CRUD operations.
 */
public class OrderBLL {

    private static OrderDAO orderDAO = new OrderDAO();

    /**
     * Finds an order by its ID.
     *
     * @param id the ID of the order to find
     * @return the Orders object if found
     * @throws NoSuchElementException if the order with the specified ID is not found
     */
    public static Orders findOrderById(int id) {
        Orders orders = orderDAO.findById(id);
        if (orders == null) {
            throw new NoSuchElementException("The order with id = " + id + " was not found");
        }
        return orders;
    }

    /**
     * Deletes an order from the database by its ID.
     *
     * @param id the ID of the order to delete
     * @return the number of rows affected
     */
    public static int deleteOrder(int id) {
        return orderDAO.delete(id);
    }

    /**
     * Lists all orders in the database.
     *
     * @return a list of all Orders objects
     */
    public static List<Orders> listAllOrders() {
        return orderDAO.findAll();
    }

    /**
     * Inserts a new order into the database.
     *
     * @param order the Orders object to insert
     * @return the ID of the newly inserted order or -1 if there is insufficient stock
     */
    public static int insertNewOrder(Orders order) {
        order.setProduct_name(ProductBLL.findProductById(order.getProduct_id()).getName());
        order.setClient_name(ClientBLL.findClientById(order.getClient_id()).getName());

        if (ProductBLL.findProductById(order.getProduct_id()).getStock_quantity() < order.getQuantity()) {
            return -1;
        }
        order.setOrder_date(new Date());
        order.setTotal_price(order.getQuantity() * ProductBLL.findProductById(order.getProduct_id()).getPrice());
        Product product = ProductBLL.findProductById(order.getProduct_id());
        product.setStock_quantity(product.getStock_quantity() - order.getQuantity());

        ProductBLL.updateProduct(product);
        return orderDAO.insert(order);
    }
}
