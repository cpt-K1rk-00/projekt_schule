package server;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

public class GameServer extends Server{
	
	private List<Player> onlinePlayers;
	private List<Player> waitingPlayers;
	private List<Game> runningGames;
	private DatabaseExecuter db;

	public GameServer(int pPort) {
		super(pPort);
		//Liste für User die online sind initialisieren
		onlinePlayers = new List<Player>();
		db = new DatabaseExecuter();
		System.out.println("start");
	}

	@Override
	/**
	 * Bei neuer Verbindung keine Reaktion.
	 */
	public void processNewConnection(String pClientIP, int pClientPort) {
		//Nichts passiert
	}

	@Override
	/**
	 * Antwort nach Protokoll (siehe onenote).
	 */
	public void processMessage(String pClientIP, int pClientPort, String pMessage) {
		System.out.println("msg:" + pMessage);
		String[] msg = pMessage.split(":");
		//Für Login Vorgang
		if(msg[0].equals("LOGIN")) {
			//Anmeldedaten prüfen
			int res = db.login(msg[1], msg[2]);
			//Login-erfolgreich
			if(res == 1) {
				//Zu online-Liste zufügen
				System.out.println(new Integer(res).toString());
				onlinePlayers.append(new Player(msg[1], pClientIP+":"+pClientPort));
				this.send(pClientIP, pClientPort, "1:Anmeldung erfolgreich:" + msg[1]);
			//Anmeldedaten falsch
			}else if(res == 0) {
				System.out.println(new Integer(res).toString());
				this.send(pClientIP, pClientPort, "1:Anmeldedaten falsch");
			//Anderer Fehler
			}else if(res == -1) {
				System.out.println(new Integer(res).toString());
				this.send(pClientIP, pClientPort, "1:Fehler bei Anmeldung");
			}
		//Für Registrierungs Vorgang
		}else if(msg[0].equals("REGISTER")) {
			System.out.println("in registrierugn");
			//Registrierung prüfen
			String res = db.register(msg[1], msg[2]);
			System.out.println(res);
			//Wenn die Registrierung erfolgreich war
			if(res.equals("Username eingefuegt")) {
				this.send(pClientIP, pClientPort, "2:Registrierung erfolgreich:" + msg[1] + ":" + msg[2]);
			//Wenn der Nutzername vergeben ist
			}else if(res.equals("Username bereits vergeben")) {
				this.send(pClientIP, pClientPort, "2:Nutzername bereits vergeben");
			//Wenn es einen Fehler gibt
			}else if(res.equals("Fehler beim Einfuegen des Users")) {
				this.send(pClientIP, pClientPort, "2:Fehler bei Registrierung");
			}
		//Für Anforderung der Freunde
		}else if(msg[0].equals("GET_FRIENDS")) {
			System.out.println("get friends");
			List<String> names = db.getFriends(msg[1]);
			System.out.println(names);
			//Wenn es einen Fehler gab
			if(names == null) {
				this.send(pClientIP, pClientPort, "3:Fehler beim Laden der Datei");
			//Wenn es keinen Fehler gab
			}else {
				//Prüfen, ob User online ist
				String result = "";
				names.toFirst();
				List<String> sortedList = new List<String>();
				while(names.hasAccess()) {
					if(isPlayerOnline(names.getContent())) {
						sortedList.toFirst();
						sortedList.insert(names.getContent()+ ";1");
					}else {
						sortedList.append(names.getContent() + ";0");
					}
					names.next();
				}
				sortedList.toFirst();
				while(sortedList.hasAccess()) {
					result += ":" + sortedList.getContent();
					sortedList.next();
				}
				this.send(pClientIP, pClientPort, "3:Laden erfolgreich" + result);
			
			}
		//Freund hinzufügen
		}else if(msg[0].equals("ADD_FRIEND")) {
			
		//Mitglieder der Liga laden
		}else if(msg[0].equals("LOAD_LEAGUE")) {
			System.out.println("Ligamitglieder laden");
			//Username laden
			String leagueName = db.getLeagueNameFromUsername(msg[1]);
			//Wenn es einen Namen gibt
			if(leagueName != null) {
				if(leagueName.equals("keine Liga")) {
					this.send(pClientIP, pClientPort, "4:Laden erfolgreich:" + leagueName);
				}else {
					List<String> names = db.getMembers(leagueName);
					//Wenn es keine Fehler gab
					if(names != null) {
						//Prüfen, ob die User online sind
						String result = "";
						names.toFirst();
						List<String> sortedList = new List<String>();
						while(names.hasAccess()) {
							if(isPlayerOnline(names.getContent())) {
								sortedList.toFirst();
								sortedList.insert(names.getContent()+ ";1");
							}else {
								sortedList.append(names.getContent() + ";0");
							}
							names.next();
						}
						sortedList.toFirst();
						while(sortedList.hasAccess()) {
							result += ":" + sortedList.getContent();
							sortedList.next();
						}
						this.send(pClientIP, pClientPort, "4:Laden erfolgreich:" + leagueName + result);
					}else {
						//Fehlermeldung zurückgeben
						this.send(pClientIP, pClientPort, "4:Fehler beim Laden der Mitglieder");
					}
				}
			}else {
				//Fehlermeldung zurückgeben
				this.send(pClientIP, pClientPort, "4:Username nicht erkannt");
			}
		} else if (msg[0].equals("START_GAME")) {
		    onlinePlayers.toFirst();
		while(onlinePlayers.hasAccess()) {
			//Wenn der User online ist
			if(onlinePlayers.getContent().getConnection().equals(pClientIP+":"+pClientPort)) {
				waitingPlayers.append(onlinePlayers.getContent());
				break;
			}
			onlinePlayers.next();
		}
		int numberOfWaitingPlayers = 0;
		waitingPlayers.toFirst();
		while(waitingPlayers.hasAccess()) {
			//Wenn der User online ist
			numberOfWaitingPlayers++;
			waitingPlayers.next();
		}
		if (numberOfWaitingPlayers >= 2) {
		waitingPlayers.toFirst();
		Player player1 = waitingPlayers.getContent();
		waitingPlayers.next();
		Player player2 = waitingPlayers.getContent();
		Game newGame = new Game(player1, player2);
		runningGames.append(newGame);
		  }
		
		} else if (msg[0].equals("TURN")) {
		    int[] coords = {Integer.parseInt(msg[1]), Integer.parseInt(msg[2])};
		    runningGames.toFirst();
		while(runningGames.hasAccess()) {
			//Wenn der User online ist
			if (runningGames.getContent().getPlayers()[0].getConnection().equals(pClientIP+":"+pClientPort)) {
			    runningGames.getContent().turn(runningGames.getContent().getPlayers()[0], coords);
			    send(runningGames.getContent().getPlayers()[0].getConnection().split(":")[0],
			    Integer.parseInt(runningGames.getContent().getPlayers()[0].getConnection().split(":")[1]),
			    "PLAYER_TURN_RESPONSE:"+msg[1]+":"+msg[2]);
			    send(runningGames.getContent().getPlayers()[1].getConnection().split(":")[0],
			    Integer.parseInt(runningGames.getContent().getPlayers()[0].getConnection().split(":")[1]),
			    "PLAYER_TURN_RESPONSE:"+msg[1]+":"+msg[2]);
			    break;
			 }
			 if (runningGames.getContent().getPlayers()[1].getConnection().equals(pClientIP+":"+pClientPort)) {
			    runningGames.getContent().turn(runningGames.getContent().getPlayers()[1], coords);
			    send(runningGames.getContent().getPlayers()[0].getConnection().split(":")[0],
			    Integer.parseInt(runningGames.getContent().getPlayers()[0].getConnection().split(":")[1]),
			    "PLAYER_TURN_RESPONSE:"+msg[1]+":"+msg[2]);
			    send(runningGames.getContent().getPlayers()[1].getConnection().split(":")[0],
			    Integer.parseInt(runningGames.getContent().getPlayers()[0].getConnection().split(":")[1]),
			    "PLAYER_TURN_RESPONSE:"+msg[1]+":"+msg[2]);
			    break;
			 }
	
			runningGames.next();
		}
		  }
	}
	
	/**
	 * Prüft, ob der Name des Spielers in der Liste mit den 
	 * Spielern, die online sind, enthalten ist. Ist dies der
	 * Fall, dann wird true zurückgegeben. Sonst wird false
	 * zurückgegeben.
	 * @param pName
	 * @return
	 */
	public boolean isPlayerOnline(String pName) {
		onlinePlayers.toFirst();
		while(onlinePlayers.hasAccess()) {
			//Wenn der User online ist
			if(onlinePlayers.getContent().getUsername().equals(pName)) {
				return true;
			}
			onlinePlayers.next();
		}
		return false;
	}

	@Override
	/**
	 * Online-Status bei Freunden aktualisieren.
	 */
	public void processClosingConnection(String pClientIP, int pClientPort) {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * Ändert die Liga-Headline.
	 */
	public void changeLeagueHeadline() {
		
	}
	
	public static void main (String[] args) {
		GameServer server = new GameServer(9999);
		for(;;) {}
	}

}
