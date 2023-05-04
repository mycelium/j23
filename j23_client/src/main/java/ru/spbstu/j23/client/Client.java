package ru.spbstu.j23.client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import ru.spbstu.j23.dto.CreateUserRequest;
import ru.spbstu.j23.dto.CreateUserResponse;
import ru.spbstu.j23.dto.GetUsersRequest;
import ru.spbstu.j23.dto.GetUsersResponse;


public class Client {

	public static void main(String[] args) throws InterruptedException, ExecutionException {
		String serverAddress = "localhost"; // Replace with the server's IP address if needed
		int port = 12345; // The same port number as the server

		CompletableFuture cf = CompletableFuture.allOf(Stream.generate(() -> {
			return CompletableFuture.runAsync(() -> {
				long timeStart = System.currentTimeMillis();
				try (Socket socket = new Socket(serverAddress, port)) {
					System.out.println("Connected to server: " + socket.getRemoteSocketAddress());
					ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
					out.flush();
					ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
					try {
						CreateUserRequest request = new CreateUserRequest(Thread.currentThread().getName(), "test",
								"test@test.test", 10);
						out.writeObject(request);
						Object response = in.readObject();
						if (response instanceof CreateUserResponse) {
							CreateUserResponse userResponse = (CreateUserResponse) response;
							if (userResponse.getStatusCode() == 200) {
								System.out.println(userResponse.getUserId());
							}
						}
					} catch (Exception e) {
					} finally {
						out.close();
						in.close();
					}

				} catch (UnknownHostException e) {
					System.out.println("Unknown host: " + serverAddress);
				} catch (IOException e) {
					System.out.println("Error connecting to server: " + e.getMessage());
				}
				System.out.println(Thread.currentThread().getName() + ": " + (System.currentTimeMillis() - timeStart));
			});
		}).limit(10).toArray(CompletableFuture[]::new));

		cf.get();
		
		
		try (Socket socket = new Socket(serverAddress, port)) {
			System.out.println("Connected to server: " + socket.getRemoteSocketAddress());
			ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
			ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
			try {
				GetUsersRequest request = new GetUsersRequest();
				out.writeObject(request);
				Object response = in.readObject();
				if (response instanceof GetUsersResponse) {
					GetUsersResponse userResponse = (GetUsersResponse) response;
					userResponse.getUsers().forEach(user -> 
						System.out.println(user.getId())
					);
				}
			} catch (Exception e) {
			} finally {
				out.close();
				in.close();
			}

		} catch (UnknownHostException e) {
			System.out.println("Unknown host: " + serverAddress);
		} catch (IOException e) {
			System.out.println("Error connecting to server: " + e.getMessage());
		}
		
		
//        for (int i = 0; i < 10; i++) {
//			CompletableFuture.runAsync(()->{
//				try (Socket socket = new Socket(serverAddress, port)) {
//					System.out.println("Connected to server: " + socket.getRemoteSocketAddress());
//					
//					try (ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream())) {
//						String message = "Hello, server "+Thread.currentThread().getName();
//						out.writeObject(message);
//						System.out.println("Sent message: " + message);
//					}
//				} catch (UnknownHostException e) {
//					System.out.println("Unknown host: " + serverAddress);
//				} catch (IOException e) {
//					System.out.println("Error connecting to server: " + e.getMessage());
//				}
//			});
//		}
	}
}
