package client;

 

import com.mysql.jdbc.UpdatableResultSet;
import com.sun.org.apache.bcel.internal.classfile.Node;
import com.sun.org.apache.bcel.internal.generic.GETFIELD;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

/**
* Die Klasse dient der Kommunikation zwischen dem Server und der GUI.
* In der Process Message Methode werden die Daten empfangen, die dann gegebenfalls lokal 
* gespeichert werden m�ssen.
*/
public class GameClient extends Client{
	
	//Gibt an, ob der Client sich eingelogt hat
	private boolean login;
	private List<User> user;;
	private String league;
	private Main gui;
	private String username;
	
	public GameClient(Main gui) {
		//sp�ter anpassen
		super("localhost", 9999);
		this.gui = gui;
		this.login = false;
	}

	@Override
	public void processMessage(String pMessage) {
		//Nachricht einteilen
		String[] msg = pMessage.split(":");
		//Login-Antwort
		if(msg[0].equals("1")) {
			//Wenn der Login erfolgreich ist
			if(msg[1].equals("Anmeldung erfolgreich")) {
				//GUI updaten
				Platform.runLater(new Runnable() {public void run() {gui.updateScene(gui.setMainMenu());}});
				//Die Freundedaten laden
				this.username = msg[2];
				this.getFriends(msg[2]);
				this.loadLeague(msg[2]);
				this.login = true;
			//Wenn die Anmeldedaten falsch sind
			}else if(msg[1].equals("Anmeldedaten falsch")) {
				//Nachricht ausgeben
				Platform.runLater(new Runnable() {public void run() {gui.showError("Anmeldedaten falsch");}});
			//Wenn es einen Fehler gab
			}else if(msg[1].equals("Fehler bei Anmeldung")) {
				//Nachricht ausgeben
				Platform.runLater(new Runnable() {public void run() {gui.showError("Fehler bei Anmeldung");}});
			}
		//Registrierung-Antwort
		}else if(msg[0].equals("2")) {
			//Bei erfolgreicher Registrierung
			if(msg[1].equals("Registrierung erfolgreich")) {
				//Login durchf�hren
				login(msg[2],msg[3]);
			//Wenn der Nutzername bereits vergeben ist
			}else if(msg[1].equals("Nutzername bereits vergeben")) {
				Platform.runLater(new Runnable() {public void run() {gui.showError("Nutzername bereits vergeben");}});
			//Wenn es einen Fehler gab
			}else if(msg[1].equals("Fehler beim Einfuegen des Users")) {
				Platform.runLater(new Runnable() {public void run() {gui.showError("Fehler bei Registrierung");}});
			}
		//Freunde laden
		}else if(msg[0].equals("3")) {
			if(msg[1].equals("Laden erfolgreich")) {
				List<User> user = new List<User>();
				//Die Freundesliste in der GUI aktualieren
				for(int i = 2; i < msg.length; i++) {

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
			}else if(msg[1].equals("fordere Freunde")) {
				//Wenn der User online ist
				if(login) {
					getFriends(this.username);
				}
			}else {
				Platform.runLater(new Runnable(){public void run() { gui.showError("Freunde konnten nicht geladen werden");}});
			}
		//Ligamitglieder lagen
		}else if(msg[0].equals("4")){
			//wenn es keine Fehler gab
			if(msg[1].equals("Laden erfolgreich")) {
				//Pr�fen, ob er einer Liga beigetreten ist
				if(msg[2].equals("keine Liga")) {
					this.league = "keine Liga";
					changeHeadline("keine Liga");
				//Wenn der User in einer Liga spielt
				}else {
					changeHeadline(msg[2]);
					this.league = msg[2];
					//Ligadaten in Liste speichern
					List<String> result = new List<String>();
					for(int i = 3; i < msg.length; i++) {
						result.append(msg[i]);
					}
					VBox leagueView = gui.createLeagueView(result);
					//GUI aktualiseren
					Platform.runLater(new Runnable() {public void run() {gui.getRoot().setCenter(null);gui.getRoot().setCenter(leagueView);}});
				}
			}else if(msg[1].equals("Lade Liga")) {
				//Wenn bereits eingelogt
				if(login) {
					this.loadLeague(this.username);
				}
			}
		}else if(msg[0].equals("5")) {
			//Wenn erfolgreich
			if(msg[1].equals("Freund entfernt")) {
				//Freunde neu laden
				this.getFriends(this.username);
			}else if(msg[1].equals("Fehler beim Enftfernen des Freundes")) {
				Platform.runLater(new Runnable() {
					public void run() {
						//Fehlermeldung ausgeben
						gui.showError("Fehler beim Entfernen des Freundes");
					}
				});
			}
		}else if(msg[0].equals("6")) {
			Platform.runLater(new Runnable(){ public void run(){gui.showError(msg[1]);}});
		}else if(msg[0].equals("8")) {
			if(msg[1].equals("Fehler beim Verlassen der Liga")) {
				Platform.runLater(new Runnable() {public void run() {gui.showError("Fehler beim Verlassen der Liga");}});
			//Liga neu Laden
			}else if(msg[1].equals("Lade Liga")) {
				this.loadLeague(this.username);
			//GUI aktualisieren
			}else {
				changeHeadline("keine Liga");
				Platform.runLater(new Runnable() {public void run() {gui.getRoot().setCenter(null);}});
			}
		}else if(msg[0].equals("10")) {
			if(msg[1].equals("Liga beigetreten")){
				this.loadLeague(this.username);
			}else {
				Platform.runLater(new Runnable(){public void run() {gui.showError("Fehler beim Beitritt der Liga");}});
			}
		}else if(msg[0].equals("12")) {
			//Wenn das Laden erfolgreich war
			if(msg[1].equals("Laden erfolgreich")) {
				
				List<String> tmp = new List<String>();
				for(int i = 2; i < msg.length; i++) {
					tmp.append(msg[i]);
				}
				Platform.runLater(new Runnable() {
					public void run() {
						gui.getRoot().setCenter(gui.createLeagueList(tmp));
					}
				});
			}else {
				Platform.runLater(new Runnable() {
					public void run() {
						gui.getRoot().setCenter(gui.createLeagueList(null));
					}
				});
			}
		}
		if (msg[0].contentEquals("PLAYER_TURN_RESPONSE")) {
			if(msg[1].equals("DONE")) {
				Platform.runLater(new Runnable() {
					public void run() {
						Alert alert = new Alert(AlertType.INFORMATION);
						alert.setTitle("tictactoe");
						if(msg[2].equals("WON")) {
							alert.setContentText("YOU WON AND EARNED 3 POINTS");
						}else if(msg[2].equals("LOST")) {
							alert.setContentText("YOUR OPPONENT WON AND EARNED 3 POINTS");
						}else {
							alert.setContentText("TIE");
						}
						alert.showAndWait();
						gui.updateScene(gui.setMainMenu());
					}
				});
			}else {
				System.out.println(pMessage);
				String winner = msg[1];
				String boardAsString = msg[2];
				boolean yourTurn = msg[3].equals( "true");
				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						gui.updateScene(gui.setGameScene(boardAsString, msg[1], yourTurn));
					}
				});
			}
		}
	}
	
	public void sendTurn(int pX, int pY) {
		send("TURN:"+pX+":"+pY);
	}
	
	public void sendStartGame() {
		send("START_GAME");
	}
	
	public void changeHeadline(String headline) {
		Platform.runLater(new Runnable() {
			public void run() {
				Label headlineLabel = new Label(headline);
				headlineLabel.setFont(new Font("Cambria", 50));
				headlineLabel.setMinHeight(gui.getY() * 0.2);
				headlineLabel.setMaxHeight(gui.getY() * 0.2);
				headlineLabel.setMinWidth(gui.getX() * 0.7);
				headlineLabel.setMaxWidth(gui.getX() * 0.7);
				GridPane pane = new GridPane();
				pane = new GridPane();
				pane.setAlignment(Pos.CENTER);
				pane.add(headlineLabel, 0, 0);
				pane.add(gui.getHeadlineFriends(), 1, 0);
				gui.getRoot().setTop(pane);
			}
				
		});
	}
	
	/**
	 * Startet den Loginvorgang durch.
	 * @param pUsername, pPasswort
	 */
	public void login(String pUsername, String pPasswort) {
		this.username = pUsername;
		this.send("LOGIN:" + pUsername + ":" + pPasswort);
	}
	
	/**
	 * F�hrt der Registrierungsvorgang durch.
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
	 * F�gt einen Freund hinzu.
	 * @param pUsername
	 */
	public void addFriend(String pUsername) {
		this.send("ADD_FRIEND:" + this.username + ":" + pUsername);
	}
	
	/**
	 * Entfernt einen Freund.
	 * @param pUsername
	 * @param pFriend
	 */
	public void removeFriend(String pUsername, String pFriend) {
		this.send("REMOVE_FRIEND:" + pUsername + ":" + pFriend);
	}
	
	/**
	 * Stellt eine Anfrage an einen Spieler und pr�ft, ob dieser online ist.
	 * @param pUsername
	 * @param secondPlayer
	 */
	public void play(String pUsername, String secondPlayer) {
		
	}
	
	/**
	 * F�gt die Namen der Datei zur�ck
	 * @param pUsername
	 */
	public void playRandom(String pUsername) {
		
	}
	
	/**
	 * Verl�sst die Liga
	 * @param pUsername
	 */
	public void leaveLeague() {
		this.send("LEAVE_LEAGUE:" + this.username);
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
		System.out.println("Liga breiten Anfrage");
		this.send("JOIN_LEAGUE:" + this.getUsername() + ":" + pLeagueName);
	}
	
	/**
	 * Stellt eine Anfrage auf eine Liste mit allen
	 * Mitgliedern einer Liga.
	 * @param pLeagueName
	 */
	public void loadLeague(String pUsername) {
		this.send("LOAD_LEAGUE:" + pUsername);
	}
	
	public void getAllLeagues() {
		this.send("GET_ALL_LEAGUES");
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

	public String getLeague() {
		return league;
	}

	public void setLeague(String league) {
		this.league = league;
	}
	
	public String getUsername() {
		return this.username;
	}

}
