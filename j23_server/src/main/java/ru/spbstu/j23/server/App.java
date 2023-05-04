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
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ru.spbstu.j23.dto.CreateUserRequest;
import ru.spbstu.j23.dto.CreateUserResponse;

public class App {

	public static void main(String[] args) {
		int port = 12345; // Replace with the desired port number
		ExecutorService pool = Executors.newFixedThreadPool(10);

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
		return new CreateUserResponse().setMessage("Success").setStatusCode(200)
				.setUserId(UUID.randomUUID().toString());
	}
	
	
	private static final String URL = "jdbc:sqlite:sample.db";

    public static void createTable() {
        String sql = "CREATE TABLE IF NOT EXISTS users (\n"
                + " id INTEGER PRIMARY KEY AUTOINCREMENT,\n"
                + " name TEXT NOT NULL,\n"
                + " email TEXT NOT NULL UNIQUE\n"
                + ");";

        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    
    public static void insertUser(String name, String email) {
        String sql = "INSERT INTO users(name, email) VALUES(?, ?)";

        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setString(2, email);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void getUsers() {
        String sql = "SELECT id, name, email FROM users";

        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                System.out.println(rs.getInt("id") + "\t" +
                        rs.getString("name") + "\t" +
                        rs.getString("email"));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void updateUser(int id, String name, String email) {
        String sql = "UPDATE users SET name = ?, email = ? WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setString(2, email);
            pstmt.setInt(3, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void deleteUser(int id) {
        String sql = "DELETE FROM users WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
	
}
