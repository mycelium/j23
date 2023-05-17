package ru.spbstu.j23.server.manager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ru.spbstu.j23.server.dao.SQLiteUserDao;
import ru.spbstu.j23.server.model.User;

public class UserManager {

	private UserDAO userDao = SQLiteUserDao.getInstance();

	Map<String, User> usercache = new HashMap<>();


	public User createUser(String name, String email) {
		return userDao.createUser(name, email);
		// TODO sent email
	}

	public List<User> getUsers() {
		return userDao.getUsers();
	}
	
	
	
	private static UserManager instance;
	private static Object monitor = new Object();
	private UserManager() {
		super();
	}

	public static UserManager getInstance() {
		if (instance == null) {
			synchronized (monitor) {
				if (instance == null) {
					instance = new UserManager();
				}
			}
		}
		return instance;
	}
}
