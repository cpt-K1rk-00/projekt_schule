package server;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

public class GameServer extends Server{
	
	private List<User> onlineUser;
	private DatabaseExecuter db;

	public GameServer(int pPort) {
		super(pPort);
		//Liste für User die online sind initialisieren
		onlineUser = new List<User>();
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
				onlineUser.append(new User(msg[1], true));
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
		//Freund hinzufügen
		}else if(msg[0].equals("ADD_FRIEND")) {
			
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
	public boolean userOnline(String pName) {
		onlineUser.toFirst();
		while(onlineUser.hasAccess()) {
			//Wenn der User online ist
			if(onlineUser.getContent().getUsername().equals(pName)) {
				return true;
			}
			onlineUser.next();
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
	
	public static void main (String[] args) {
		GameServer server = new GameServer(9999);
		for(;;) {}
	}

}
