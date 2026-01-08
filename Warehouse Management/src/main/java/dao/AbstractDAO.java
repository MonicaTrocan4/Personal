package dao;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import connection.ConnectionFactory;

/**
 * The AbstractDAO class is a generic class that provides basic CRUD operations for database entities.
 *
 * @param <T> the type of the entity
 */
public class AbstractDAO<T> {
	protected static final Logger LOGGER = Logger.getLogger(AbstractDAO.class.getName());

	private final Class<T> type;

	/**
	 * Constructs an AbstractDAO for the specified entity type.
	 */
	@SuppressWarnings("unchecked")
	public AbstractDAO() {
		this.type = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
	}

	private String createSelectQuery(String field) {
		StringBuilder sb = new StringBuilder();
		sb.append("SELECT ");
		sb.append(" * ");
		sb.append(" FROM ");
		sb.append(type.getSimpleName());
		sb.append(" WHERE " + field + " =?");
		return sb.toString();
	}

	private String createInsertQuery() {
		StringBuilder sb = new StringBuilder();
		sb.append("INSERT INTO ");
		sb.append(type.getSimpleName());
		sb.append(" (");
		int firstField = 1;
		for (Field field : type.getDeclaredFields()) {
			if (firstField != 1) {
				sb.append(", ");
			}
			sb.append(field.getName());
			firstField = 0;
		}

		sb.append(") VALUES (");

		int firstValue = 1;
		for (Field field : type.getDeclaredFields()) {
			if (firstValue < 1) {
				sb.append(", ");
			}
			sb.append("?");
			firstValue = 0;
		}

		sb.append(")");
		return sb.toString();
	}

	private String createDeleteQuery() {
		StringBuilder sb = new StringBuilder();
		sb.append("DELETE FROM ");
		sb.append(type.getSimpleName());
		sb.append(" WHERE id = ?");
		return sb.toString();
	}

	private String createUpdateQuery() {
		StringBuilder sb = new StringBuilder();
		sb.append("UPDATE ");
		sb.append(type.getSimpleName());
		sb.append(" SET ");

		int firstField = 1;
		for (Field field : type.getDeclaredFields()) {
			if (!field.getName().equalsIgnoreCase("id")) {
				if (firstField != 1) {
					sb.append(", ");
				}
				sb.append(field.getName());
				sb.append(" = ?");
				firstField = 0;
			}
		}

		sb.append(" WHERE id = ?");
		return sb.toString();
	}

	private String createSelectAllQuery() {
		StringBuilder sb = new StringBuilder();
		sb.append("SELECT * FROM ");
		sb.append(type.getSimpleName());
		return sb.toString();
	}

	/**
	 * Finds and returns all entities of type T from the database.
	 *
	 * @return a list of all entities of type T
	 */
	public List<T> findAll() {
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet resultSet = null;
		List<T> items = new ArrayList<>();
		String query = createSelectAllQuery();
		try {
			connection = ConnectionFactory.getConnection();
			statement = connection.prepareStatement(query);
			resultSet = statement.executeQuery();
			items = createObjects(resultSet);

		} catch (SQLException e) {
			LOGGER.log(Level.WARNING, type.getName() + "DAO:findAll " + e.getMessage());
		} finally {
			ConnectionFactory.close(resultSet);
			ConnectionFactory.close(statement);
			ConnectionFactory.close(connection);
		}
		return items;
	}

	/**
	 * Finds and returns an entity of type T from the database by its id.
	 *
	 * @param id the id of the entity to find
	 * @return the found entity, or null if not found
	 */
	public T findById(int id) {
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet resultSet = null;
		String query = createSelectQuery("id");
		try {
			connection = ConnectionFactory.getConnection();
			statement = connection.prepareStatement(query);
			statement.setInt(1, id);
			resultSet = statement.executeQuery();

			List<T> items = createObjects(resultSet);
			if (items.isEmpty()) {
				return null;
			}
			return items.get(0);

		} catch (SQLException e) {
			LOGGER.log(Level.WARNING, type.getName() + "DAO:findById " + e.getMessage());
		} finally {
			ConnectionFactory.close(resultSet);
			ConnectionFactory.close(statement);
			ConnectionFactory.close(connection);
		}
		return null;
	}

	private List<T> createObjects(ResultSet resultSet) {
		List<T> list = new ArrayList<>();
		Constructor[] ctors = type.getDeclaredConstructors();
		Constructor ctor = null;
		for (int i = 0; i < ctors.length; i++) {
			ctor = ctors[i];
			if (ctor.getGenericParameterTypes().length == 0)
				break;
		}
		try {
			while (resultSet.next()) {
				ctor.setAccessible(true);
				T instance = (T) ctor.newInstance();
				for (Field field : type.getDeclaredFields()) {
					String fieldName = field.getName();
					Object value = resultSet.getObject(fieldName);
					PropertyDescriptor propertyDescriptor = new PropertyDescriptor(fieldName, type);
					Method method = propertyDescriptor.getWriteMethod();
					method.invoke(instance, value);
				}
				list.add(instance);
			}
		} catch (InstantiationException | IllegalAccessException | SecurityException |
				 IllegalArgumentException | InvocationTargetException | SQLException | IntrospectionException e) {
			e.printStackTrace();
		}
		return list;
	}

	/**
	 * Inserts a new entity of type T into the database.
	 *
	 * @param t the entity to insert
	 * @return the number of rows affected
	 */
	public int insert(T t) {
		Connection connection = null;
		PreparedStatement statement = null;
		String query = createInsertQuery();
		int result = 0;
		try {
			connection = ConnectionFactory.getConnection();
			statement = connection.prepareStatement(query);

			int i = 1;
			for (Field field : type.getDeclaredFields()) {
				field.setAccessible(true);
				statement.setObject(i, field.get(t));
				i = i + 1;
			}

			result = statement.executeUpdate();
		} catch (SQLException | IllegalAccessException e) {
			LOGGER.log(Level.WARNING, type.getName() + "DAO:insertRecord " + e.getMessage());
		} finally {
			ConnectionFactory.close(statement);
			ConnectionFactory.close(connection);
		}
		return result;
	}

	/**
	 * Updates an existing entity of type T in the database.
	 *
	 * @param t the entity to update
	 * @return 0 if the update was successful, -1 otherwise
	 */
	public int update(T t) {
		Connection connection = null;
		PreparedStatement statement = null;
		String query = createUpdateQuery();

		try {
			connection = ConnectionFactory.getConnection();
			statement = connection.prepareStatement(query);

			int i = 1;
			Field idField = null;
			for (Field field : type.getDeclaredFields()) {
				field.setAccessible(true);
				if (field.getName().equalsIgnoreCase("id")) {
					idField = field; // Save the id field for the WHERE condition
				} else {
					statement.setObject(i, field.get(t));
					i++;
				}
			}

			if (idField != null) {
				idField.setAccessible(true);
				statement.setObject(i, idField.get(t)); // Set the value for the WHERE condition
			}

			statement.executeUpdate();
			return 0;
		} catch (SQLException | IllegalAccessException e) {
			LOGGER.log(Level.WARNING, type.getName() + "DAO:updateRecord " + e.getMessage());
			return -1;
		} finally {
			ConnectionFactory.close(statement);
			ConnectionFactory.close(connection);
		}
	}

	/**
	 * Deletes an entity of type T from the database by its id.
	 *
	 * @param id the id of the entity to delete
	 * @return the number of rows affected
	 */
	public int delete(int id) {
		Connection connection = null;
		PreparedStatement statement = null;
		String query = createDeleteQuery();
		int result = 0;
		try {
			connection = ConnectionFactory.getConnection();
			statement = connection.prepareStatement(query);
			statement.setObject(1, id);
			result = statement.executeUpdate();
		} catch (SQLException e) {
			LOGGER.log(Level.WARNING, type.getName() + "DAO:deleteRecord " + e.getMessage());
			result = -1;
		} finally {
			ConnectionFactory.close(statement);
			ConnectionFactory.close(connection);
		}
		return result;
	}
}
