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
		//Liste f�r User die online sind initialisieren
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
		//F�r Login Vorgang
		if(msg[0].equals("LOGIN")) {
			//Anmeldedaten pr�fen
			int res = db.login(msg[1], msg[2]);
			//Login-erfolgreich
			if(res == 1) {
				//Zu online-Liste zuf�gen
				System.out.println(new Integer(res).toString());
				onlineUser.append(new User(msg[1], true));
				this.send(pClientIP, pClientPort, "1:Anmeldung erfolgreich");
			//Anmeldedaten falsch
			}else if(res == 0) {
				System.out.println(new Integer(res).toString());
				this.send(pClientIP, pClientPort, "1:Anmeldedaten falsch");
			//Anderer Fehler
			}else if(res == -1) {
				System.out.println(new Integer(res).toString());
				this.send(pClientIP, pClientPort, "1:Fehler bei Anmeldung");
			}
		//F�r Registrierungs Vorgang
		}else if(msg[0].equals("REGISTER")) {
			System.out.println("in registrierugn");
			//Registrierung pr�fen
			String res = db.register(msg[1], msg[2]);
			System.out.println(res);
			//Wenn die Registrierung erfolgreich war
			if(res.equals("Username eingef�gt")) {
				System.out.println(res);
				this.send(pClientIP, pClientPort, "2:Registrierung erfolgreich:" + msg[1] + ":" + msg[2]);
			//Wenn der Nutzername vergeben ist
			}else if(res.equals("Username eingefuegt")) {
				System.out.println(res);
				this.send(pClientIP, pClientPort, "2:Nutzername bereits vergeben");
			//Wenn es einen Fehler gibt
			}else if(res.equals("Fehler beim Einfuegen des Users")) {
				System.out.println(res);
				this.send(pClientIP, pClientPort, "2:Fehler bei Registrierung");
			}
		}
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
		for(;;) {
			
		}
	}

}
