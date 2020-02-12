package server;

/**
 * Die Klasse dient als Schnittstelle zu der Klasse DatabaseConnectorMySQL und f�hrt Vorg�nge, wie den
 * Login-Vorgang oder das erstellen einer Freundesliste aus.
 * @author Jan, Lucas
 *
 */
public class DatabaseExecuter {
	
	/**
	 * Verbindet sich mit der Datenbank und gibt ein DatabaseConnectorMySQL-Objekt zur�ck.
	 * Wenn es einen Fehler gab, wird null zur�ckgegeben.
	 * @return
	 */
	private DatabaseConnectorMySQL getCon() {
		DatabaseConnectorMySQL con = new DatabaseConnectorMySQL("localhost", 3306, "spiele_projekt", "root", "");
		if(con.getErrorMessage() == null) {
			return con;
		}
		System.out.println(con.getErrorMessage());
		return null;
	}
	
	/**
	 * Die Methode gibt 1 zur�ck, wenn der Username und das Passwort existieren.
	 * Die Methode gibt 0 zur�ck, wenn die Anmeldedaten falsch sind.
	 * Die Methode gibt -1 zur�ck, wenn es einen anderen Fehler gab.	 * 
	 * @param pUsername
	 * @param pPassword
	 * @return
	 */
	public int login(String pUsername, String pPassword) {
		DatabaseConnectorMySQL con = getCon();
		//Wenn es keinen Fehler gab
		if(con != null) {
			//Pr�fen, ob der Username mit Passwort vorliegt
			con.executeStatement("SELECT * FROM users WHERE username='" + pUsername + "' AND password = '" + pPassword + "'");
			//Pr�fen, ob es eine Fehlermeldung gab
			if(con.getErrorMessage() == null) {
				QueryResult result = con.getCurrentQueryResult();
				if(result.getRowCount() == 1) {
					con.close();
					return 1;
				}
				return 0;
			}else {
				System.out.println(con.getErrorMessage());
				con.close();
			}
		}
		return -1;
	}
	
	/**
	 * F�gt einen neuen Nutzer der Datenbank hinzu.
	 * Ist der Nutzername bereits vorhanden, wird eine Nachricht zur�ckgegeben.
	 * Gibt es einen Fehler, wird die Fehlermeldung zur�ckgegebem.
	 * Sonst wird eine Best�tigung zur�ckgegeben.
	 * @param pUsername
	 * @param pPassword
	 */
	public String register(String pUsername, String pPassword) {
		DatabaseConnectorMySQL con = getCon();
		//Wenn es keine Fehler gab
		if(con != null) {
			con.executeStatement("SELECT username FROM users WHERE username LIKE '" + pUsername + "'");
			//Pr�fen, ob es keinen Fehler gab
			if(con.getErrorMessage() == null) {
				//Pr�fen, ob der Username vergeben ist
				if(con.getCurrentQueryResult().getColumnCount() == 1) {
					con.close();
					return "Username bereits vergeben";
				}
				//Nutzernamen und Passwort eingeben
				//Not L�sung sp�ter �ndern
				con.executeStatement("SELECT user_id FROM users ORDER BY user_id DESC LIMIT 0,1");
				if(con.getErrorMessage() != null) {
					System.out.println("Fehler bei h�chster ID");
					return "Fehler beim Einfuegen des Users";
				}
				int id = Integer.parseInt(con.getCurrentQueryResult().getData()[0][0]) + 1; 
				con.executeStatement("INSERT INTO users (user_id, username, password) VALUES (" + id + ",'" + pUsername + "','" + pPassword + "')");
				con.close();
				//Pr�fen, ob es einen Fehler beim einf�gen gab
				if(con.getErrorMessage() != null) {
					System.out.println(con.getErrorMessage());
					return "Fehler beim Einfuegen des Users";
				}
				//Best�tigung des Einf�gens
				return "Username eingefuegt";
			}
		}
		return null;
	}
	
	/**
	 * Die Methode gibt eine Liste mit den Namen der User, die in der
	 * Freundesliste gespeichert sind. Gibt es keine Freunde ist die Liste leer.
	 * Gibt es einen Fehler, wird null zur�ckgegeben.
	 * @param pUsername
	 * @return
	 */
	public List<String> getFriends(String pUsername) {
		DatabaseConnectorMySQL con = getCon();
		List<String> names = new List<String>();
		//Wenn es keinen Fehler gab
		if(con != null) {
			//Gibt alle Freunde einers Username zur�ck
			con.executeStatement("(select users.username from users, freunde where users.user_id = freunde.spielerid_1 and freunde.spielerid_2 = (SELECT DISTINCT users.user_id FROM users WHERE users.username = '" + pUsername + "')) UNION (select users.username from users, freunde where users.user_id = freunde.spielerid_2 and freunde.spielerid_1 = (SELECT DISTINCT users.user_id FROM users WHERE users.username = '" + pUsername +"'))");
			//Pr�fen, ob es keinen Fehler gab
			if(con.getErrorMessage() == null) {
				QueryResult result = con.getCurrentQueryResult();
				for(int i = 0; i < result.getRowCount(); i++) {
					names.append(result.getData()[i][0]);
				}
				con.close();
				return names;
			}else {
				System.out.println(con.getErrorMessage());
				con.close();
			}
		}
		return null;
	}
	
	
	/**
	 * Die f�gt eine neue Freundschaft der Datenbank hinzu und
	 * es wird eine Best�tigung zur�ckgegeben
	 * Sind beide bereits befreundet, wird eine Meldung zur�ckgegeben.
	 * Ist der Fehler unbekannt, wird null zur�ckgegeben.
	 * @param pUsername1
	 * @param pUsername2
	 * @return
	 */
	public String addFriendship(String pUsername1, String pUsername2) {
		DatabaseConnectorMySQL con = getCon();
		//Pr�fen, ob es einen Fehler mit der Verbindung gab
		if(con != null) {
			//Pr�fen, ob die User bereits miteinander befreundet sind
			con.executeStatement("(SELECT users.username FROM users, freunde WHERE users.user_id = freunde.spielerid_1 AND " + 
								"((freunde.spielerid_1 = (SELECT users.user_id FROM users WHERE users.username = '" + pUsername1 +"') " + 
								"AND freunde.spielerid_2 = (SELECT users.user_id FROM users WHERE users.username = '" + pUsername2 +"')) OR " +
								"(freunde.spielerid_1 = (SELECT users.user_id FROM users WHERE users.username = '" + pUsername2 + "') " + 
								"AND freunde.spielerid_2 = (SELECT users.user_id FROM users WHERE users.username = '" + pUsername1 + "')))) " + 
								"UNION (SELECT users.username FROM users, freunde WHERE users.user_id = freunde.spielerid_2  " + 
								"AND ((freunde.spielerid_1 = (SELECT users.user_id FROM users WHERE users.username = '" + pUsername1 + "')" + 
								"AND freunde.spielerid_2 = (SELECT users.user_id FROM users WHERE users.username = '" + pUsername2 + "')) " + 
								"OR (freunde.spielerid_1 = (SELECT users.user_id FROM users WHERE users.username = '" + pUsername2 + "')  " + 
								"AND freunde.spielerid_2 = (SELECT users.user_id FROM users WHERE users.username = '" + pUsername1 + "'))))");
			//Pr�fen, ob es keinen Fehler gab
			if(con.getErrorMessage() == null) {
				//Pr�fen, ob die Freundschaft bereits existiert
				if(con.getCurrentQueryResult().getRowCount() == 2) {
					con.close();
					return "Freundschaft existiert bereits";
				}
				//Neue Freundschaft anlegen
				con.executeStatement("INSERT INTO freunde (spielerid_1, spielerid_2) VALUES ((SELECT user_id FROM users WHERE username = '" + pUsername1 + 
						"'), (SELECT user_id FROM users WHERE username = '" + pUsername2 + "'))");
				con.close();
				if(con.getErrorMessage() == null) {
					return "Freundschaft hinzugef�gt";
				}
				System.out.println(con.getErrorMessage());
				return "Fehler beim Laden der Freundschaft";
			}
		}
		return null;
	}
	
	/**
	 * Die Methode gibt eine Liste mit allen Namen der vorhandenden Ligen zur�ck.
	 * Gibt es einen Fehler, dann wird null zur�ckgegeben.
	 * @return
	 */
	public List<String> getLeagues() {
		DatabaseConnectorMySQL con = getCon();
		List<String> names = new List<String>();
		//Wenn es keinen Fehler gab
		if(con != null) {
			//Gibt eine Liste mit allen Ligen zur�ck
			con.executeStatement("SELECT name FROM ligen");
			//Pr�fen, ob es keinen Fehler gab
			if(con.getErrorMessage() == null) {
				QueryResult result = con.getCurrentQueryResult();
				for(int i = 0; i < result.getRowCount(); i++) {
					names.append(result.getData()[i][0]);
				}
				con.close();
				return names;
			}else {
				System.out.println(con.getErrorMessage());
				con.close();
			}
		}
		return null;
	}
	
	/**
	 * Gibt die Mitglieder einer Liga zur�ck.
	 * Wenn die Liga nicht existiert wird null zur�ckgegeben.
	 * Wenn es keine mitglieber gibt, dann wird dies zur�ck gegeben.
	 * @param pLeagueName
	 * @return
	 */
	public List<String> getMembers(String pLeagueName) {
		DatabaseConnectorMySQL con = getCon();
		List<String> names = new List<String>();
		//Wenn es keinen Fehler gab
		if(con.getErrorMessage() == null) {
			//Gibt eine Liste mit allen Mitgliedern einer Liga zur�ck
			con.executeStatement("SELECT username, liga_punkte FROM users WHERE liga_id = (SELECT liga_id FROM ligen WHERE ligen.name = '" + pLeagueName + "') ORDER BY liga_punkte DESC");
			if(con.getErrorMessage() == null) {
				QueryResult result = con.getCurrentQueryResult();
				for(int i = 0; i < result.getRowCount(); i++){
					names.append(result.getData()[i][0] + ":" + result.getData()[i][1]);
				}
				con.close();
				return names;
			}else {
				System.out.println(con.getErrorMessage());
				con.close();
			}
		}
		return null;
	}
	
	/**
	 * Gibt ein Array mit den Siegen, Niederlagen und Unentschieden zur�ck.
	 * Existiert der Username nicht, wird null zur�ck gegeben.
	 * (Evlt. als Diagramm darstellen)
	 * @param pUsername
	 * @return
	 */
	public int[] getStats(String pUsername) {
		//Sp�ter hinzuf�gen
		return null;
	}
	
	public static void main (String[] args) {
		DatabaseExecuter ex = new DatabaseExecuter();
		System.out.println(ex.login("killer123", "killer123"));
	}
}
