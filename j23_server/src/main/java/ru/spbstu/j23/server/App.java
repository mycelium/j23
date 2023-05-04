package ru.spbstu.j23.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ru.spbstu.j23.dto.CreateUserRequest;
import ru.spbstu.j23.dto.CreateUserResponse;
import ru.spbstu.j23.dto.GetUsersRequest;
import ru.spbstu.j23.dto.GetUsersResponse;
import ru.spbstu.j23.dto.User;

public class App {

	public static void main(String[] args) {
		int port = 12345; // Replace with the desired port number
		ExecutorService pool = Executors.newFixedThreadPool(10);

		createTable();

		try (ServerSocket serverSocket = new ServerSocket(port)) {
			System.out.println("Server started on port " + port);
			while (true) {
				Socket socket = serverSocket.accept();
				pool.submit(() -> {
					System.out.println("Client connected: " + socket.getRemoteSocketAddress());
					ObjectOutputStream out = null;
					ObjectInputStream in = null;
					try {
						out = new ObjectOutputStream(socket.getOutputStream());
						in = new ObjectInputStream(socket.getInputStream());
						Object message = in.readObject();
						if (message instanceof CreateUserRequest) {
							CreateUserRequest request = (CreateUserRequest) message;
							CreateUserResponse response = createUser(request);
							out.writeObject(response);
							out.flush();
						} else if (message instanceof GetUsersRequest) {
							GetUsersResponse response = new GetUsersResponse();
							response.setUsers(getUsers());
							out.writeObject(response);
							out.flush();
						} else {
							System.err.println("Invalid request format");
						}

					} catch (ClassNotFoundException e) {
						System.out.println("Error decoding message: " + e.getMessage());
					} catch (IOException e) {
						e.printStackTrace();
					} finally {
						try {
							out.close();
							in.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				});

			}
		} catch (IOException e) {
			System.out.println("Error starting server: " + e.getMessage());
		}
	}

	private static CreateUserResponse createUser(CreateUserRequest request) {

		String userID = insertUser(request.getLogin(), request.getEmail());
		return new CreateUserResponse().setMessage("Success").setStatusCode(200).setUserId(userID);
	}

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

	public static String insertUser(String name, String email) {
		String sql = "INSERT INTO users(id, name, email) VALUES(?, ?, ?)";
		String id = UUID.randomUUID().toString();
		try (Connection conn = DriverManager.getConnection(URL); PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setString(1, id);
			pstmt.setString(2, name);
			pstmt.setString(3, email);
			pstmt.executeUpdate();
			return id;
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		return null;
	}

	public static List<User> getUsers() {
		String sql = "SELECT id, name, email FROM users";
		List<User> users = new ArrayList<>();
		try (Connection conn = DriverManager.getConnection(URL);
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(sql)) {
			while (rs.next()) {
				User user = new User();
				user.setId(rs.getString("id"));
				user.setEmail(rs.getString("email"));
				user.setLogin(rs.getString("name"));
				users.add(user);
			}
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		return users;
	}

	public static void updateUser(String id, String name, String email) {
		String sql = "UPDATE users SET name = ?, email = ? WHERE id = ?";

		try (Connection conn = DriverManager.getConnection(URL); PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setString(1, name);
			pstmt.setString(2, email);
			pstmt.setString(3, id);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}

	public static void deleteUser(String id) {
		String sql = "DELETE FROM users WHERE id = ?";

		try (Connection conn = DriverManager.getConnection(URL); PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setString(1, id);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}

}
