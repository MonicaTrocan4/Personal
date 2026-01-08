package bll;

import java.util.List;
import java.util.NoSuchElementException;

import dao.ClientDAO;
import model.Client;

/**
 * The ClientBLL class handles the business logic for Client operations.
 * It interacts with the ClientDAO class to perform CRUD operations.
 */
public class ClientBLL {

    private static ClientDAO clientDAO = new ClientDAO();

    /**
     * Finds a client by their ID.
     *
     * @param id the ID of the client to find
     * @return the Client object if found
     * @throws NoSuchElementException if the client with the specified ID is not found
     */
    public static Client findClientById(int id) {
        Client client = clientDAO.findById(id);
        if (client == null) {
            throw new NoSuchElementException("The client with id = " + id + " was not found!");
        }
        return client;
    }

    /**
     * Inserts a new client into the database.
     *
     * @param client the Client object to insert
     * @return the ID of the newly inserted client
     */
    public static int insertNewClient(Client client) {
        return clientDAO.insert(client);
    }

    /**
     * Deletes a client from the database by their ID.
     *
     * @param id the ID of the client to delete
     * @return the number of rows affected
     */
    public static int deleteClient(int id) {
        return clientDAO.delete(id);
    }

    /**
     * Updates an existing client in the database.
     *
     * @param client the Client object with updated information
     * @return the number of rows affected
     */
    public static int updateClient(Client client) {
        return clientDAO.update(client);
    }

    /**
     * Lists all clients in the database.
     *
     * @return a list of all Client objects
     */
    public static List<Client> listAllClients() {
        return clientDAO.findAll();
    }
}
