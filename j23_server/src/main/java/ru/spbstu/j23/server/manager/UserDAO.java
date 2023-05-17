package ru.spbstu.j23.server.manager;

import java.util.List;

import ru.spbstu.j23.server.model.User;

public interface UserDAO {
	
	public User createUser(String name, String email);
	public User getUser(String id);
	public User deleteUser(String id);
	public User updateUser(String id, String name, String email);
	public List<User> getUsers();
}
