package ru.spbstu.j23.server;

import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ru.spbstu.j23.server.controller.UserController;

public class App {

	public static void main(String[] args) {
		int port = 12345; // Replace with the desired port number
		ExecutorService pool = Executors.newFixedThreadPool(10);
		
		UserController controller = new UserController();
		controller.startListening(port, pool);
		
	}

}
