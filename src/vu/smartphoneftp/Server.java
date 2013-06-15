package vu.smartphoneftp;

public class Server {

	private int _id, port;
	private String title, host, username, password;
	
	// Empty constructor
	public Server() {}
	
	/**
	 * constructor
	 *
	 * @param title
	 * @param host
	 * @param port
	 * @param username
	 * @param password
	 */
	public Server(String title, String host, int port, String username,
			String password) {
		this.title = title;
		this.host = host;
		this.port = port;
		this.username = username;
		this.password = password;
	}
	
	/**
	 * constructor
	 *
	 * @param _id
	 * @param title
	 * @param host
	 * @param port
	 * @param username
	 * @param password
	 */
	public Server(int _id, String title, String host, int port,
			String username, String password) {
		this._id = _id;
		this.title = title;
		this.host = host;
		this.port = port;
		this.username = username;
		this.password = password;
	}

	public int get_id() {
		return _id;
	}
	
	public void set_id(int _id) {
		this._id = _id;
	}
	
	public int getPort() {
		return port;
	}
	
	public void setPort(int port) {
		this.port = port;
	}
	
	public String getTitle() {
		return title;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getHost() {
		return host;
	}
	
	public void setHost(String host) {
		this.host = host;
	}
	
	public String getUsername() {
		return username;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	
	public String getPassword() {
		return password;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
	
	@Override
	public String toString() {
		return getTitle();
	}
}