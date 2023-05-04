package ru.spbstu.j23.dto;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class GetUsersResponse implements Serializable {
	
	private List<User> users = new LinkedList<>();

	public List<User> getUsers() {
		return users;
	}

	public void setUsers(List<User> users) {
		this.users = users;
	}
	
}
