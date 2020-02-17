package client;

import javafx.application.Platform;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;

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
	private String username;
	
	public GameClient(Main gui) {
		//später anpassen
		super("localhost", 9999);
		this.gui = gui;
	}

	@Override
	public void processMessage(String pMessage) {
		//Nachricht einteilen
		String[] msg = pMessage.split(":");
		System.out.println(msg[1]);
		//Login-Antwort
		if(msg[0].equals("1")) {
			System.out.println(1);
			//Wenn der Login erfolgreich ist
			if(msg[1].equals("Anmeldung erfolgreich")) {
				//GUI updaten
				Platform.runLater(new Runnable() {public void run() {gui.updateScene(gui.setMainMenu());}});
				//Die Freundedaten laden
				this.getFriends(msg[2]);
				this.loadLeague(msg[2]);
			//Wenn die Anmeldedaten falsch sind
			}else if(msg[1].equals("Anmeldedaten falsch")) {
				//Nachricht ausgeben
				System.out.println(2);
				Platform.runLater(new Runnable() {public void run() {gui.showError("Anmeldedaten falsch");}});
			//Wenn es einen Fehler gab
			}else if(msg[1].equals("Fehler bei Anmeldung")) {
				//Nachricht ausgeben
				Platform.runLater(new Runnable() {public void run() {gui.showError("Fehler bei Anmeldung");}});
			}
		//Registrierung-Antwort
		}else if(msg[0].equals("2")) {
			System.out.println("In registrierung : ");
			//Bei erfolgreicher Registrierung
			if(msg[1].equals("Registrierung erfolgreich")) {
				System.out.println("Registrierung erfolgreich");
				//Login durchführen
				login(msg[2],msg[3]);
			//Wenn der Nutzername bereits vergeben ist
			}else if(msg[1].equals("Nutzername bereits vergeben")) {
				System.out.println("Username bereits vergeben");
				Platform.runLater(new Runnable() {public void run() {gui.showError("Nutzername bereits vergeben");}});
			//Wenn es einen Fehler gab
			}else if(msg[1].equals("Fehler beim Einfuegen des Users")) {
				System.out.println("Fehler beim Einfuegen des Users");
				Platform.runLater(new Runnable() {public void run() {gui.showError("Fehler bei Registrierung");}});
			}
		//Freunde laden
		}else if(msg[0].equals("3")) {
			System.out.println("in freunde anfordern");
			if(msg[1].equals("Laden erfolgreich")) {
				List<User> user = new List<User>();
				//Die Freundesliste in der GUI aktualieren
				for(int i = 2; i < msg.length; i++) {
					System.out.println(msg[i]);
					String[] tmp = msg[i].split(";");
					if(tmp[1].equals("1")) {
						user.append(new User(tmp[0], true));
					}else {
						user.append(new User(tmp[0], false));
					}
				}
				//GUI aktualiseren
				Platform.runLater(new Runnable() {
					public void run() {
						ScrollPane scroll = new ScrollPane(gui.getFriends(user));
						scroll.setHbarPolicy(ScrollBarPolicy.NEVER);;
						scroll.setMaxHeight(gui.getY() * 0.6);
						scroll.setMinHeight(gui.getY() * 0.6);
						scroll.setStyle("-fx-background:POWDERBLUE; -fx-background-color:transparent;");
						scroll.setBorder(null);
						gui.getRoot().setRight(scroll);
					}
				});
			}else {
				Platform.runLater(new Runnable(){public void run() { gui.showError("Freunde konnten nicht geladen werden");}});
			}
		//Ligamitglieder lagen
		}else if(msg[0].equals("4")){
			System.out.println("in liga anfordern");
			//wenn es keine Fehler gab
			if(msg[1].equals("Laden erfolgreich")) {
				//Prüfen, ob er einer Liga beigetreten ist
				if(msg[2].equals("keine Liga")) {
					Platform.runLater(new Runnable() {
						public void run() {
							gui.getHeadlineLeague().setText("keine Liga");
						}
					});
				}
			}
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
		this.send("REGISTER:" + pUsername + ":" + pPasswort);
	}
	
	/**
	 *Fordert eine Liste mit Freunden an.
	 * @param username
	 */
	public void getFriends(String pUsername) {
		this.send("GET_FRIENDS:" + pUsername);
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
	public void loadLeague(String pUsername) {
		this.send("LOAD_LEAGUE:" + pUsername);
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
