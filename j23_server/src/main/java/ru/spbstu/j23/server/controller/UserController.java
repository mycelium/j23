package ru.spbstu.j23.server.controller;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.ExecutorService;

import ru.spbstu.j23.dto.CreateUserRequest;
import ru.spbstu.j23.dto.CreateUserResponse;
import ru.spbstu.j23.dto.GetUsersRequest;
import ru.spbstu.j23.dto.GetUsersResponse;
import ru.spbstu.j23.dto.Response;
import ru.spbstu.j23.server.manager.UserManager;
import ru.spbstu.j23.server.model.User;

public class UserController {

	UserManager userManager = UserManager.getInstance();

	public void startListening(int port, ExecutorService pool) {

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
//TODO java 22						
//						Object responseData = switch (message) {
//						case CreateUserRequest.class -> 
//							createUser((CreateUserRequest) message);
//						case GetUsersRequest.class -> 
//							getUsers((GetUsersRequest) message);
//						default ->
//							throw new IllegalArgumentException("Unexpected value: " + message);
//						};
						Object responseData = null;

						if (message instanceof CreateUserRequest) {
							responseData = createUser((CreateUserRequest) message);
						} else if (message instanceof GetUsersRequest) {
							responseData = getUsers((GetUsersRequest) message);
						} else {
							System.err.println("Unknown message");
						}

						Response response = new Response();
						response.setData(responseData);
						response.setStatusCode(200);
						out.writeObject(response);
						out.flush();

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

	private CreateUserResponse createUser(CreateUserRequest request) {
		User user = userManager.createUser(request.getLogin(), request.getEmail());
		CreateUserResponse response = new CreateUserResponse();
		response.setUserId(user.getId());
		return response;
	}

	private GetUsersResponse getUsers(GetUsersRequest request) {
		List<User> users = userManager.getUsers();

		GetUsersResponse response = new GetUsersResponse();

		response.setUsers(users.stream().map(user -> {
			ru.spbstu.j23.dto.User dtoUser = new ru.spbstu.j23.dto.User();
			dtoUser.setEmail(user.getEmail());
			dtoUser.setId(user.getId());
			dtoUser.setLogin(user.getName());
			return dtoUser;
		}).toList());

		return response;
	}

}
