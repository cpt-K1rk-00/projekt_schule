package server;

/**
 * Die Klasse dient als Schnittstelle zu der Klasse DatabaseConnectorMySQL und fï¿½hrt Vorgï¿½nge, wie den
 * Login-Vorgang oder das erstellen einer Freundesliste aus.
 * @author Jan, Lucas
 *
 */
public class DatabaseExecuter {
	
	PasswordAuthentication auth = new PasswordAuthentication();
	
	/**
	 * Verbindet sich mit der Datenbank und gibt ein DatabaseConnectorMySQL-Objekt zurï¿½ck.
	 * Wenn es einen Fehler gab, wird null zurï¿½ckgegeben.
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
	 * Die Methode gibt 1 zurï¿½ck, wenn der Username und das Passwort existieren.
	 * Die Methode gibt 0 zurï¿½ck, wenn die Anmeldedaten falsch sind.
	 * Die Methode gibt -1 zurï¿½ck, wenn es einen anderen Fehler gab.	 * 
	 * @param pUsername
	 * @param pPassword
	 * @return
	 */
	public int login(String pUsername, String pPassword) {
		DatabaseConnectorMySQL con = getCon();
		//Wenn es keinen Fehler gab
		if(con != null) {
			//Prï¿½fen, ob der Username mit Passwort vorliegt
			con.executeStatement("SELECT password FROM users WHERE username='" + pUsername + "'");
			//Prï¿½fen, ob es eine Fehlermeldung gab
			if(con.getErrorMessage() == null) {
				QueryResult result = con.getCurrentQueryResult();
				if(result.getRowCount() == 1) {
					con.close();
					if (auth.authenticate(pPassword.toCharArray(), result.getData()[0][0])) {
						System.out.println("lgin");
						return 1;
					}
				}
				System.out.println("not");
				return 0;
			}else {
				System.out.println(con.getErrorMessage());
				con.close();
			}
		}
		System.out.println("broke");
		return -1;
	}
	
	/**
	 * Fï¿½gt einen neuen Nutzer der Datenbank hinzu.
	 * Ist der Nutzername bereits vorhanden, wird eine Nachricht zurï¿½ckgegeben.
	 * Gibt es einen Fehler, wird die Fehlermeldung zurï¿½ckgegebem.
	 * Sonst wird eine Bestï¿½tigung zurï¿½ckgegeben.
	 * @param pUsername
	 * @param pPassword
	 */
	public String register(String pUsername, String pPassword) {
		DatabaseConnectorMySQL con = getCon();
		//Wenn es keine Fehler gab
		if(con != null) {
			con.executeStatement("SELECT username FROM users WHERE username LIKE '" + pUsername + "'");
			//Prï¿½fen, ob es keinen Fehler gab
			if(con.getErrorMessage() == null) {
				//Prï¿½fen, ob der Username vergeben ist
				if(con.getCurrentQueryResult().getColumnCount() == 1) {
					con.close();
					return "Username bereits vergeben";
				}
				//Nutzernamen und Passwort eingeben
				//Not Lï¿½sung spï¿½ter ï¿½ndern
				con.executeStatement("SELECT user_id FROM users ORDER BY user_id DESC LIMIT 0,1");
				if(con.getErrorMessage() != null) {
					System.out.println("Fehler bei hï¿½chster ID");
					return "Fehler beim Einfuegen des Users";
				}
				int id = Integer.parseInt(con.getCurrentQueryResult().getData()[0][0]) + 1; 
				con.executeStatement("INSERT INTO users (user_id, username, password, online, liga_id, liga_punkte) VALUES (" + id + ",'" + pUsername + "','" + auth.hash(pPassword.toCharArray()) +"0, 0, 0')");
				con.close();
				//Prï¿½fen, ob es einen Fehler beim einfï¿½gen gab
				if(con.getErrorMessage() != null) {
					System.out.println(con.getErrorMessage());
					return "Fehler beim Einfuegen des Users";
				}
				//Bestï¿½tigung des Einfï¿½gens
				return "Username eingefuegt";
			}
		}
		return null;
	}
	
	/**
	 * Die Methode gibt eine Liste mit den Namen der User, die in der
	 * Freundesliste gespeichert sind. Gibt es keine Freunde ist die Liste leer.
	 * Gibt es einen Fehler, wird null zurï¿½ckgegeben.
	 * @param pUsername
	 * @return
	 */
	public List<String> getFriends(String pUsername) {
		DatabaseConnectorMySQL con = getCon();
		List<String> names = new List<String>();
		//Wenn es keinen Fehler gab
		if(con != null) {
			//Gibt alle Freunde einers Username zurï¿½ck
			con.executeStatement("(select users.username from users, freunde where users.user_id = freunde.spielerid_1 and freunde.spielerid_2 = (SELECT DISTINCT users.user_id FROM users WHERE users.username = '" + pUsername + "')) UNION (select users.username from users, freunde where users.user_id = freunde.spielerid_2 and freunde.spielerid_1 = (SELECT DISTINCT users.user_id FROM users WHERE users.username = '" + pUsername +"'))");
			//Prï¿½fen, ob es keinen Fehler gab
			if(con.getErrorMessage() == null) {
				QueryResult result = con.getCurrentQueryResult();
				for(int i = 0; i < result.getRowCount(); i++) {
					names.append(result.getData()[i][0]);
				}
				return names;
			}else {
				System.out.println(con.getErrorMessage());
				con.close();
			}
		}
		return null;
	}
	
	/**
	 * Die Methode bekommt den Nutzernamen zweier Freunde ï¿½bergeben.
	 * Wenn das Entfernen der Freundschaft erfolgreich war, dann 
	 * wird true zurï¿½ckgegeben, sonst wird false zurï¿½ckgegeben.
	 * @param pUsername
	 * @param pFriend
	 * @return
	 */
	public boolean removeFriendship(String pUsername, String pFriend) {
		DatabaseConnectorMySQL con = getCon();
		//Wenn es keinen Fehler gab
		if(con != null) {
			//Freundschaft lï¿½schen
			con.executeStatement("DELETE FROM freunde WHERE spielerid_2 = (SELECT users.user_id FROM users WHERE users.username LIKE '" + pUsername + "') AND spielerid_1 = (SELECT users.user_id FROM users WHERE users.username LIKE '" + pFriend + "')");
			con.executeStatement("DELETE FROM freunde WHERE spielerid_1 = (SELECT users.user_id FROM users WHERE users.username LIKE '" + pUsername + "') AND spielerid_2 = (SELECT users.user_id FROM users WHERE users.username LIKE '" + pFriend + "')");
			//Prï¿½fen, ob es keinen Fehler gab
			if(con.getErrorMessage() == null) {
				return true;
			}
			System.out.println(con.getErrorMessage());
		}
		return false;
		
	}
	
	
	/**
	 * Die fï¿½gt eine neue Freundschaft der Datenbank hinzu und
	 * es wird eine Bestï¿½tigung zurï¿½ckgegeben
	 * Sind beide bereits befreundet, wird eine Meldung zurï¿½ckgegeben.
	 * Ist der Fehler unbekannt, wird null zurï¿½ckgegeben.
	 * @param pUsername1
	 * @param pUsername2
	 * @return
	 */
	 public String addFriendship(String pUsername1, String pUsername2) {
	        DatabaseConnectorMySQL con = getCon();
	        //Pr�fen, ob es einen Fehler mit der Verbindung gab
	        if(con != null) {
	            //Prüfen, ob der neue User existiert
	            con.executeStatement("SELECT username FROM users WHERE username = '" + pUsername2 + "'");
	            System.out.println(con.getCurrentQueryResult().getRowCount());
	            if(con.getCurrentQueryResult().getRowCount() == 0){
	                return "User existiert nicht";
	            }
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
	                    return "Freundschaft hinzugefuegt";
	                }
	                System.out.println(con.getErrorMessage());
	                return "Fehler beim Laden der Freundschaft";
	            }
	        }
	        return "Fehler beim Hinzufuegen der Freundschaft";
	    }
	
	/**
	 * Die Methode gibt eine Liste mit allen Namen der vorhandenden Ligen zurï¿½ck.
	 * Gibt es einen Fehler, dann wird null zurï¿½ckgegeben.
	 * @return
	 */
	public List<String> getLeagues() {
		DatabaseConnectorMySQL con = getCon();
		List<String> names = new List<String>();
		//Wenn es keinen Fehler gab
		if(con != null) {
			//Gibt eine Liste mit allen Ligen zurï¿½ck
			con.executeStatement("SELECT name FROM ligen");
			//Prï¿½fen, ob es keinen Fehler gab
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
	 * Gibt die Mitglieder einer Liga zurï¿½ck.
	 * Wenn die Liga nicht existiert wird null zurï¿½ckgegeben.
	 * Wenn es keine mitglieber gibt, dann wird dies zurï¿½ck gegeben.
	 * @param pLeagueName
	 * @return
	 */
	public List<String> getMembers(String pLeagueName) {
		DatabaseConnectorMySQL con = getCon();
		List<String> names = new List<String>();
		//Wenn es keinen Fehler gab
		if(con.getErrorMessage() == null) {
			//Gibt eine Liste mit allen Mitgliedern einer Liga zurï¿½ck
			con.executeStatement("SELECT username, liga_punkte FROM users WHERE liga_id = (SELECT liga_id FROM ligen WHERE ligen.name = '" + pLeagueName + "') ORDER BY liga_punkte DESC");
			if(con.getErrorMessage() == null) {
				QueryResult result = con.getCurrentQueryResult();
				for(int i = 0; i < result.getRowCount(); i++){
					names.append(result.getData()[i][0] + ";" + result.getData()[i][1]);
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
	 * Gibt die Liga zu einen Username zurï¿½ck.
	 * Kombination aus verschiedenen Methoden.
	 * @param pUsername
	 * @return
	 */
	public String getLeagueNameFromUsername(String pUsername){
		DatabaseConnectorMySQL con = getCon();
		//Wenn es keine Fehlermeldung gibt
		if(con.getErrorMessage() == null) {
			//Gibt den Namen der Liga in der User spielt zurï¿½ck
			con.executeStatement("SELECT ligen.name FROM ligen, users WHERE ligen.liga_id = users.liga_id AND users.username = '" + pUsername + "'");
			//Wenn es keine Fehlermeldung gab
			if(con.getErrorMessage() == null) {
				//Den Liganamen zurï¿½ckgeben
				return con.getCurrentQueryResult().getData()[0][0];
			}else {
				System.out.println(con.getErrorMessage());
			}
		}
		return null;
	}
	
	public boolean joinLeague(String pLeagueName) {
		DatabaseConnectorMySQL con = getCon();
		//Wenn es keine Fehlermeldung gibt
		if(con.getErrorMessage() == null) {
			con.executeStatement("UPDATE `users` SET `liga_id` = '1' WHERE `users`.`user_id` = (SELECT `liga_id` FROM `ligen` WHERE ligen.name LIKE'" + pLeagueName + "')");
			//Wenn es keine Fehlermeldung gab
			if(con.getErrorMessage() == null) {
				System.out.println(2);
				return true;
			}else {
				System.out.println(con.getErrorMessage());
			}
		}
		return false;
	}
	
	/**
	 * Verlï¿½sst eine Liga, in der sich der User befindet.
	 * Gibt den Namen der Liga zurï¿½ck, damit deren Ansicht aktualisiert werden
	 * kann.
	 * @param pUsername
	 */
	public String leaveLeague(String pUsername) {
		DatabaseConnectorMySQL con = getCon();
		//Wenn es keine Fehlermeldung gab
		String leagueName = getLeagueNameFromUsername(pUsername);
		con.executeStatement("UPDATE users SET liga_id= 0 WHERE users.username = '" + pUsername + "'");
		if(con.getErrorMessage() == null) {
			//Erfolg zurï¿½ckgeben
			return leagueName;
		}else {
			System.out.println(con.getErrorMessage());
		}
		return null;
	}
	
	/**
	 * Gibt ein Array mit den Siegen, Niederlagen und Unentschieden zurï¿½ck.
	 * Existiert der Username nicht, wird null zurï¿½ck gegeben.
	 * (Evlt. als Diagramm darstellen)
	 * @param pUsername
	 * @return
	 */
	public int[] getStats(String pUsername) {
		//Spï¿½ter hinzufï¿½gen
		return null;
	}
	
	public static void main (String[] args) {
		DatabaseExecuter ex = new DatabaseExecuter();
		ex.leaveLeague("killer123");
	}
}
