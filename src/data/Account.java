package data;

public class Account {
	private String username;
	private String password;
	private String name;
	private String email;
	
	public Account(String username, String password, String name, String email) {
		//this.ID = IDcount++;
		this.username = username;
		this.password = password;
		this.name = name;
		this.email = email;
	}
	
	public String getUsername() {
		return username;
	}
	
	public String getPassword() {
		return password;
	}
	
	public String getName() {
		return name;
	}
	
	public String getEmail() {
		return email;
	}
	
}
