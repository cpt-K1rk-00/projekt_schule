 

public class User {
	
	private String username;
	private boolean online;
	private String ip;
	private int port;
	
	
	public User(String pUsername, boolean pOnline, String pIp, int pPort) {
		this.username = pUsername;
		this.online = pOnline;
		this.setIp(pIp);
		this.setPort(pPort);
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

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

}