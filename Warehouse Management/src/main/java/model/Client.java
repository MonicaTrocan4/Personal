package model;

/**
 * The Client class represents a client in the system.
 * It contains information about the client, such as id, name, email, and address.
 */
public class Client {
    private int id;
    private String name;
    private String email;
    private String address;

    /**
     * Constructs a new Client with the specified id, name, email, and address.
     *
     * @param id      the id of the client
     * @param name    the name of the client
     * @param email   the email of the client
     * @param address the address of the client
     */
    public Client(int id, String name, String email, String address) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.address = address;
    }

    /**
     * Constructs a new Client with the specified name, email, and address.
     *
     * @param name    the name of the client
     * @param email   the email of the client
     * @param address the address of the client
     */
    public Client(String name, String email, String address) {
        this.name = name;
        this.email = email;
        this.address = address;
    }

    /**
     * Default constructor for Client.
     */
    public Client() {
    }

    /**
     * Gets the id of the client.
     *
     * @return the id of the client
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the id of the client.
     *
     * @param id the new id of the client
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Gets the name of the client.
     *
     * @return the name of the client
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the client.
     *
     * @param name the new name of the client
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the email of the client.
     *
     * @return the email of the client
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the email of the client.
     *
     * @param email the new email of the client
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Gets the address of the client.
     *
     * @return the address of the client
     */
    public String getAddress() {
        return address;
    }

    /**
     * Sets the address of the client.
     *
     * @param address the new address of the client
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * Returns a string representation of the client.
     *
     * @return a string representation of the client
     */
    @Override
    public String toString() {
        return "Client [id=" + id + ", name=" + name + ", address=" + address + ", email=" + email + "]";
    }
}
