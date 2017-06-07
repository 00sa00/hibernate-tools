package org.hibernate.tool.hbm2x.hbm2hbmxml.IdBagTest;

import java.util.ArrayList;
import java.util.List;

public class User {
	private String name;
	private List<Group> groups = new ArrayList<Group>();
	
	User() {}
	
	public User(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
	

	void setName(String name) {
		this.name = name;
	}

	public List<Group> getGroups() {
		return groups;
	}
	
	void setGroups(List<Group> groups) {
		this.groups = groups;
	}
	
}
