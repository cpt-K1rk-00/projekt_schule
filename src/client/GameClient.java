package client;

import javafx.application.Platform;

/**
* Die Klasse dient der Kommunikation zwischen dem Server und der GUI.
* In der Process Message Methode werden die Daten empfangen, die dann gegebenfalls lokal 
* gespeichert werden müssen.
*/
public class GameClient extends Client{
	
	//Gibt an, ob der Client sich eingelogt hat
	private boolean login;
	private List<User> user;;
	private League league;
	private Main gui;
	
	public GameClient(Main gui) {
		//später anpassen
		super("localhost", 9999);
		this.gui = gui;
	}

	@Override
	public void processMessage(String pMessage) {
		//Nachricht einteilen
		String[] msg = pMessage.split(":");
		//Login-Antwort
		if(msg[0].equals("1")) {
			System.out.println(1);
			//Wenn der Login erfolgreich ist
			if(msg[1].equals("Anmeldung erfolgreich")) {
				//GUI updaten
				Platform.runLater(new Runnable() {public void run() {gui.updateScene(gui.setMainMenu());}});
			//Wenn die Anmeldedaten falsch sind
			}else if(msg[1].equals("Anmeldedaten falsch")) {
				//Nachricht ausgeben
				System.out.println(2);
				Platform.runLater(new Runnable() {public void run() {gui.showError("Anmeldedaten falsch");}});
			}else if(msg[1].equals("Fehler bei Anmeldung")) {
				//Nachricht ausgeben
				Platform.runLater(new Runnable() {public void run() {gui.showError("Fehler bei Anmeldung");}});
			}
		//Registrierung-Antwort
		}else if(msg[0].equals("2")) {
			System.out.println("logindaten falsch");
		//Freunde laden
		}else if(msg[0].equals("3")) {
			
		}
	}
	
	/**
	 * Startet den Loginvorgang durch.
	 * @param pUsername, pPasswort
	 */
	public void login(String pUsername, String pPasswort) {
		this.send("LOGIN:" + pUsername + ":" + pPasswort);
	}
	
	/**
	 * Führt der Registrierungsvorgang durch.
	 * @param pUsername
	 * @param pPasswort
	 */
	public void register(String pUsername, String pPasswort) {
		
	}
	
	/**
	 *Fordert eine Liste mit Freunden an.
	 * @param username
	 */
	public void getFriends(String pUsername) {
		
	}
	
	/**
	 * Fügt einen Freund hinzu.
	 * @param pUsername
	 */
	public void addFriend(String pUsername) {
		
	}
	
	/**
	 * Entfernt einen Freund.
	 * @param pUsername
	 * @param pFriend
	 */
	public void removeFriend(String pUsername, String pFriend) {
		
	}
	
	/**
	 * Stellt eine Anfrage an einen Spieler und prüft, ob dieser online ist.
	 * @param pUsername
	 * @param secondPlayer
	 */
	public void play(String pUsername, String secondPlayer) {
		
	}
	
	/**
	 * Fügt die Namen der Datei zurück
	 * @param pUsername
	 */
	public void playRandom(String pUsername) {
		
	}
	
	/**
	 * Verlässt die Liga
	 * @param pUsername
	 */
	public void leaveLeague(String pUsername) {
		
	}
	
	/**
	 * Stellt die Anfrage eine neue Liga zu erstellen.
	 * @param pLeagueName
	 */
	public void createLeague(String pLeagueName) {
		
	}
	
	/**
	 * Stellt die Anfrage einer  Liga zu beizutreten.
	 * @param pLeagueName
	 */
	public void joinLeague(String pLeagueName) {
		
	}
	
	/**
	 * Stellt eine Anfrage auf eine Liste mit allen
	 * Mitgliedern einer Liga.
	 * @param pLeagueName
	 */
	public void loadLeague(String pLeagueName) {
		
	}
	
	/**
	 * Stellt eine Anfrage auf den Namen der Liga,
	 * in der User ist.
	 * @param pUsername
	 */
	public void getLeagueName(String pUsername) {
		
	}

	public boolean isLogin() {
		return login;
	}

	public void setLogin(boolean login) {
		this.login = login;
	}

	public List<User> getUser() {
		return user;
	}

	public void setUser(List<User> user) {
		this.user = user;
	}

	public League getLeague() {
		return league;
	}

	public void setLeague(League league) {
		this.league = league;
	}

}
