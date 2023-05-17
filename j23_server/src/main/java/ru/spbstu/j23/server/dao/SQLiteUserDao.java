package ru.spbstu.j23.server.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import ru.spbstu.j23.server.manager.UserDAO;
import ru.spbstu.j23.server.model.User;

public class SQLiteUserDao implements UserDAO {

	private static final String URL = "jdbc:sqlite:users.db";
	
	
	public static void createTable() {
		String sql = "CREATE TABLE IF NOT EXISTS users (\n" + " id TEXT PRIMARY KEY,\n" + " name TEXT NOT NULL,\n"
				+ " email TEXT NOT NULL \n" + ");";

		try (Connection conn = DriverManager.getConnection(URL); Statement stmt = conn.createStatement()) {
			stmt.execute(sql);
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}

	public List<User> getUsers() {
		String sql = "SELECT id, name, email FROM users";
		List<User> users = new ArrayList<>();
		try (Connection conn = DriverManager.getConnection(URL);
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(sql)) {
			while (rs.next()) {
				User user = new User();
				user.setId(rs.getString("id"));
				user.setEmail(rs.getString("email"));
				user.setName(rs.getString("name"));
				users.add(user);
			}
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		return users;
	}

	@Override
	public User updateUser(String id, String name, String email) {
		String sql = "UPDATE users SET name = ?, email = ? WHERE id = ?";

		try (Connection conn = DriverManager.getConnection(URL); PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setString(1, name);
			pstmt.setString(2, email);
			pstmt.setString(3, id);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		return getUser(id);
	}

	@Override
	public User createUser(String name, String email) {
		String sql = "INSERT INTO users(id, name, email) VALUES(?, ?, ?)";
		String id = UUID.randomUUID().toString();
		try (Connection conn = DriverManager.getConnection(URL); PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setString(1, id);
			pstmt.setString(2, name);
			pstmt.setString(3, email);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		return getUser(id);
	}

	@Override
	public User deleteUser(String id) {
		String sql = "DELETE FROM users WHERE id = ?";
		User user = getUser(id);
		try (Connection conn = DriverManager.getConnection(URL); PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setString(1, id);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		return user;
	}

	@Override
	public User getUser(String id) {
		String sql = "SELECT id, name, email FROM users WHERE id=?";
		User user = new User();
		try (Connection conn = DriverManager.getConnection(URL);
			    PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setString(1, id);
			ResultSet rs = pstmt.executeQuery(sql);
			rs.next();
			user
				.setId(rs.getString("id"))
				.setEmail(rs.getString("email"))
				.setName(rs.getString("name"));
			if (rs.next()) {
				//TODO multiple users with same id!!!
			}
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		return user;
	}
	
	
	private static SQLiteUserDao instance;
	private static Object monitor = new Object();
	
	private SQLiteUserDao() {
		super();
		createTable();
	}
	
	public static SQLiteUserDao getInstance() {
		if (instance == null) {
			synchronized (monitor) {
				if (instance == null) {
					instance = new SQLiteUserDao();
				}
			}
		}
		return instance;
	}

}
