package server;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

public class GameServer extends Server {

	private List<User> onlineUser;
	private DatabaseExecuter db;
	private List<Player> onlinePlayers;
	private List<Player> waitingPlayers;
	private List<Game> runningGames;

	public GameServer(int pPort) {
		super(pPort);
		// Liste f�r User die online sind initialisieren
		onlineUser = new List<User>();
		onlinePlayers = new List<Player>();
		waitingPlayers = new List<Player>();
		runningGames = new List<Game>();
		db = new DatabaseExecuter();
		System.out.println("start");
	}

	@Override
	/**
	 * Bei neuer Verbindung keine Reaktion.
	 */
	public void processNewConnection(String pClientIP, int pClientPort) {
		// Nichts passiert
	}

	@Override
	/**
	 * Antwort nach Protokoll (siehe onenote).
	 */
	public void processMessage(String pClientIP, int pClientPort, String pMessage) {
		
		String[] msg = pMessage.split(":");
		//F�r Login Vorgang
		if(msg[0].equals("LOGIN")) {
			//Anmeldedaten pr�fen
			int res = db.login(msg[1], msg[2]);
			//Login-erfolgreich
			if(res == 1) {
				//Zu online-Liste zuf�gen
				onlineUser.append(new User(msg[1], true, pClientIP, pClientPort));
				this.send(pClientIP, pClientPort, "1:Anmeldung erfolgreich:" + msg[1]);
				//alle Ligamitglieder und Freunde auffordern neu zu laden
				this.askForFriends(msg);
				this.askForMembers(msg);
			//Anmeldedaten falsch
			}else if(res == 0) {
				this.send(pClientIP, pClientPort, "1:Anmeldedaten falsch");
			//Anderer Fehler
			}else if(res == -1) {
				this.send(pClientIP, pClientPort, "1:Fehler bei Anmeldung");
			}
		//F�r Registrierungs Vorgang
		}else if(msg[0].equals("REGISTER")) {
			//Registrierung pr�fen
			String res = db.register(msg[1], msg[2]);
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
		//F�r Anforderung der Freunde
		}else if(msg[0].equals("GET_FRIENDS")) {
			List<String> names = db.getFriends(msg[1]);
			//Wenn es einen Fehler gab
			if(names == null) {
				this.send(pClientIP, pClientPort, "3:Fehler beim Laden der Datei");
			//Wenn es keinen Fehler gab
			}else {
				//Pr�fen, ob User online ist
				String result = "";
				names.toFirst();
				List<String> sortedList = new List<String>();
				while(names.hasAccess()) {
					if(userOnline(names.getContent())) {
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
		//Freund hinzuf�gen
		}else if(msg[0].equals("ADD_FRIEND")) {
			
		//Mitglieder der Liga laden
		}else if(msg[0].equals("LOAD_LEAGUE")) {
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
						String result = "";
						names.toFirst();
						while(names.hasAccess()) {
							String tmp = "";
							if(userOnline(names.getContent().split(";")[0])) {
								tmp = names.getContent() + ";1";
							}else {
								tmp = names.getContent() + ";0";
							}
							result += ":" + tmp;
							names.next();
						}
						this.send(pClientIP, pClientPort, "4:Laden erfolgreich:" + leagueName + result);
					}else {
						//Fehlermeldung zur�ckgeben
						this.send(pClientIP, pClientPort, "4:Fehler beim Laden der Mitglieder");
					}
				}
			}
		}else if(msg[0].equals("LEAVE_LEAGUE")) {
			//Die Verbindung aufstellen
			String leagueName = db.leaveLeague(msg[1]);
			if(leagueName != null) {
				this.send(pClientIP, pClientPort, "8:Liga verlassen");
				//Alle Mitglieder der Liga aktualisieren
				onlineUser.toFirst();
				while(onlineUser.hasAccess()) {
					//Pr�fen, ob sie in der gleichen Liga spielen
					if(db.getLeagueNameFromUsername(onlineUser.getContent().getUsername()).equals(leagueName)){
						//User auffordern Liga zu laden
						this.send(onlineUser.getContent().getIp(), onlineUser.getContent().getPort(), "8:Lade Liga");
					}
					onlineUser.next();
				}
			}else {
				this.send(pClientIP, pClientPort, "8:Fehler beim Verlassen der Liga");
			}
		}else if(msg[0].equals("REMOVE_FRIEND")){
			//Die Verbindung herstellen
			if(db.removeFriendship(msg[1], msg[2])) {
				this.send(pClientIP, pClientPort, "5:Freund entfernt");
				//Wenn der andere User online ist
				User exfriend = getOnlineUser(msg[2]);
				if(exfriend != null) {
					this.send(exfriend.getIp(), exfriend.getPort(), "3:fordere Freunde");
				}
			}else {
				this.send(pClientIP, pClientPort, "5:Fehler beim Entfernen des Freundes");
			}
		}else if (msg[0].equals("START_GAME")) {
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
		}else if(msg[0].equals("GET_ALL_LEAGUES")) {
			List<String> names = db.getLeagues();
			if(names != null) {
				String result = "";
				names.toFirst();
				while(names.hasAccess()) {
					if(!names.getContent().equals("keine Liga")) {
						result += names.getContent() + ":";
					}
					names.next();
				}
				this.send(pClientIP, pClientPort, "12:Laden erfolgreich:" + result);
			}else {
				this.send(pClientIP, pClientPort, "12:Fehler beim laden der Ligen");
			}
		} else if(msg[0].equals("JOIN_LEAGUE")){
			System.out.println(1);
			//Der Liga beitreten
			if(db.joinLeague(msg[1])) {
				this.send(pClientIP, pClientPort, "10:Liga beigetreten");
			}else {
				this.send(pClientIP, pClientPort, "10:Fehler beim Beitritt der Liga");
			}
		}else if (msg[0].equals("TURN")) {
		    int[] coords = {Integer.parseInt(msg[1]), Integer.parseInt(msg[2])};
		    runningGames.toFirst();
		    while(runningGames.hasAccess()) {
		    	for (Player player: runningGames.getContent().getPlayers()) {
			    	if (player.getConnection().equals(pClientIP+":"+pClientPort)) {
			    		runningGames.getContent().turn(player, coords);
			    		for (Player player_tmp: runningGames.getContent().getPlayers()) {
				    		send(player_tmp.getConnection().split(":")[0], Integer.parseInt(runningGames.getContent().getPlayers()[0].getConnection().split(":")[1]),
				    				"PLAYER_TURN_RESPONSE:"+runningGames.getContent().getWinner()+":"+runningGames.getContent().getBoardAsString());
			    		}
			    		break;
			    	}
			    	break;
		    	}
		    	runningGames.next();
		   }
		}else{
			// Fehlermeldung zur�ckgeben
			this.send(pClientIP, pClientPort, "4:Username nicht erkannt");
		}
	}

	/**
	 * Pr�ft, ob der Name des Spielers in der Liste mit den Spielern, die online
	 * sind, enthalten ist. Ist dies der Fall, dann wird true zur�ckgegeben. Sonst
	 * wird false zur�ckgegeben.
	 * 
	 * @param pName
	 * @return
	 */
	public boolean userOnline(String pName) {
		onlineUser.toFirst();
		while (onlineUser.hasAccess()) {
			// Wenn der User online ist
			if (onlineUser.getContent().getUsername().equals(pName)) {
				return true;
			}
			onlineUser.next();
		}
		return false;
	}

	public void askForFriends(String[] msg) {
		List<String> allUsers = db.getFriends(msg[1]);
		allUsers.toFirst();
		while (allUsers.hasAccess()) {
			if (userOnline(allUsers.getContent())) {
				User aktUser = getOnlineUser(allUsers.getContent());
				// Aufforderung schicken
				this.send(aktUser.getIp(), aktUser.getPort(), "3:fordere Freunde");
			}
			allUsers.next();
		}
	}

	public void askForMembers(String[] msg) {
		//alle User der Liga
		List<String> member = db.getMembers(db.getLeagueNameFromUsername(msg[1]));
		member.toFirst();
		while(member.hasAccess()) {
			//Wenn der User online ist
			if(userOnline(member.getContent().split(";")[0])) {
				User aktUser = getOnlineUser(member.getContent().split(";")[0]);
				this.send(aktUser.getIp(), aktUser.getPort(), "8:Lade Liga");
			}
			member.next();
		}
	}

	/**
	 * Gibt das passende Userobjekt zu einem Usernamen zur�ck. Ist der User nicht
	 * online wird null zur�ckgegeben.
	 * 
	 * @param pUsername
	 * @return
	 */
	public User getOnlineUser(String pUsername) {
		this.onlineUser.toFirst();
		while (this.onlineUser.hasAccess()) {
			if (this.onlineUser.getContent().getUsername().equals(pUsername)) {
				return this.onlineUser.getContent();
			}
			this.onlineUser.next();
		}
		return null;
	}

	@Override
	/**
	 * Online-Status bei Freunden aktualisieren.
	 */
	public void processClosingConnection(String pClientIP, int pClientPort) {
		// TODO Auto-generated method stub

	}

	/**
	 * �ndert die Liga-Headline.
	 */
	public void changeLeagueHeadline() {

	}

	public static void main(String[] args) {
		GameServer server = new GameServer(9999);
		for (;;) {
		}
	}

}
