package server;

 

/**
 * Beschreiben Sie hier die Klasse Player.
 * 
 * @author (Ihr Name) 
 * @version (eine Versionsnummer oder ein Datum)
 */
public class Player
{
    private String username;
	private String connection;
    char symbol;
    
    public Player(String pUsername, String pConnection)
    {
        username = pUsername;
        connection = pConnection;
    }
    
    public String getUsername() {
		return username;
	}

	public String getConnection() {
		return connection;
	}
	
	public char getSymbol() {
		return symbol;
	}

	public void setSymbol(char symbol) {
		this.symbol = symbol;
	}
}
