package projekt_schule.client;

public class User {
	
	private String username;
	private boolean online;
	private List<User> friends;
	
	
	public User(String pUsername, boolean pOnline) {
		this.username = pUsername;
		this.online = pOnline;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public boolean isOnline() {
		return online;
	}

	public void setOnline(boolean online) {
		this.online = online;
	}

}
