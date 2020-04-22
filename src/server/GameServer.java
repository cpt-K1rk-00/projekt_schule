package server;

 

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

public class GameServer extends Server {

    private DatabaseExecuter db;
    private List<Player> onlinePlayers;
    private List<Player> waitingPlayers;
    private List<Game> runningGames;

    public GameServer(int pPort) {
        super(pPort);
        // Liste fï¿½r User die online sind initialisieren
        onlinePlayers = new List<Player>();
        waitingPlayers = new List<Player>();
        runningGames = new List<Game>();
        db = new DatabaseExecuter();
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
        //Fï¿½r Login Vorgang
        if(msg[0].equals("LOGIN")) {
            //Anmeldedaten prï¿½fen
            int res = db.login(msg[1], msg[2]);
            onlinePlayers.toFirst();
            while (onlinePlayers.hasAccess()) {
            	if (onlinePlayers.getContent().getUsername().contentEquals(msg[1])) {
            		this.send(pClientIP, pClientPort, "1:Fehler bei Anmeldung");
            		return;
            	}
            	onlinePlayers.next();
            }
            //Login-erfolgreich
            if(res == 1) {
                //Zu online-Liste zufï¿½gen
                onlinePlayers.append(new Player(msg[1], pClientIP + ":" + pClientPort));
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
        //Fï¿½r Registrierungs Vorgang
        }else if(msg[0].equals("REGISTER")) {
            //Registrierung prï¿½fen
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
        //Fï¿½r Anforderung der Freunde
        }else if(msg[0].equals("GET_FRIENDS")) {
            List<String> names = db.getFriends(msg[1]);
            //Wenn es einen Fehler gab
            if(names == null) {
                this.send(pClientIP, pClientPort, "3:Fehler beim Laden der Datei");
            //Wenn es keinen Fehler gab
            }else {
                //Prï¿½fen, ob User online ist
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
        //Freund hinzufï¿½gen
        }else if(msg[0].equals("ADD_FRIEND")) {
            String result = db.addFriendship(msg[1], msg[2]);
            //Wenn die Freundschaft hinzugefuegt wurde
            if(result.equals("Freundschaft hinzugefuegt")){
                //Prï¿½fen ob neuer Freund online
                Player tmp = getPlayerIfOnline(msg[2]);
                if(tmp != null){
                    this.send(tmp.getConnection().split(":")[0], Integer.parseInt(tmp.getConnection().split(":")[1]), "3:fordere Freunde");
                }
                this.send(pClientIP, pClientPort, "3:fordere Freunde");
            }else{
                //Nachricht schicken
                this.send(pClientIP, pClientPort,"6:" + result);
            }
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
							if(isPlayerOnline(names.getContent().split(";")[0])) {
								tmp = names.getContent() + ";1";
							}else {
								tmp = names.getContent() + ";0";
							}
							result += ":" + tmp;
							names.next();
						}
						this.send(pClientIP, pClientPort, "4:Laden erfolgreich:" + leagueName + result);
					}else {
						//Fehlermeldung zurï¿½ckgeben
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
				onlinePlayers.toFirst();
				while(onlinePlayers.hasAccess()) {
					//Prï¿½fen, ob sie in der gleichen Liga spielen
					if(db.getLeagueNameFromUsername(onlinePlayers.getContent().getUsername()).equals(leagueName)){
						//User auffordern Liga zu laden
						this.send(onlinePlayers.getContent().getConnection().split(":")[0], Integer.parseInt(onlinePlayers.getContent().getConnection().split(":")[1]), "8:Lade Liga");
					}
					onlinePlayers.next();
				}
			}else {
				this.send(pClientIP, pClientPort, "8:Fehler beim Verlassen der Liga");
			}
		}else if(msg[0].equals("JOIN_LEAGUE")) {
			 //Der Liga beitreten
            if(db.joinLeague(msg[2],msg[1])) {
                this.send(pClientIP, pClientPort, "10:Liga beigetreten");
                askForMembers(msg);
            }else {
                this.send(pClientIP, pClientPort, "10:Fehler beim Beitritt der Liga");
            }
		} 
		else if(msg[0].equals("REMOVE_FRIEND")){
			//Die Verbindung herstellen
			if(db.removeFriendship(msg[1], msg[2])) {
				this.send(pClientIP, pClientPort, "5:Freund entfernt");
				//Wenn der andere User online ist
				Player exfriend = getPlayerIfOnline(msg[2]);
				if(exfriend != null) {
					this.send(onlinePlayers.getContent().getConnection().split(":")[0], Integer.parseInt(onlinePlayers.getContent().getConnection().split(":")[1]), "3:fordere Freunde");
				}
			}else {
				this.send(pClientIP, pClientPort, "5:Fehler beim Entfernen des Freundes");
			}
		}else if (msg[0].equals("START_GAME")) {
		    onlinePlayers.toFirst();
		    String playerLeague = null;
		    Player player = null;
		    while(onlinePlayers.hasAccess()) {
		    	//Wenn der User online ist
		    	if(onlinePlayers.getContent().getConnection().equals(pClientIP+":"+pClientPort)) {
		    		waitingPlayers.append(onlinePlayers.getContent());
		    		player = onlinePlayers.getContent();
		    		playerLeague = db.getLeagueNameFromUsername(onlinePlayers.getContent().getUsername());
		    		break;
		    	}
		    	onlinePlayers.next();
		    }
		    Player opponent = null;
		    waitingPlayers.toFirst();
		    while(waitingPlayers.hasAccess()) {
		    	//Wenn der User online ist
		    	if (db.getLeagueNameFromUsername(waitingPlayers.getContent().getUsername()).equals(playerLeague) && !waitingPlayers.getContent().getConnection().equals(player.getConnection())) {
		    		opponent = waitingPlayers.getContent();
		    		waitingPlayers.remove();
		    		break;
		    	}
		    	waitingPlayers.next();
		    }
		    if (opponent != null) {
		    	waitingPlayers.toLast();
		    	waitingPlayers.remove();
		    	Game newGame = new Game(player, opponent);
		    	player.setSymbol('x');
		    	opponent.setSymbol('o');
		    	runningGames.append(newGame);
	    		send(player.getConnection().split(":")[0], Integer.parseInt(player.getConnection().split(":")[1]), "PLAYER_TURN_RESPONSE:"+newGame.getWinner()+":"+newGame.getBoardAsString()+":true");
	    		send(opponent.getConnection().split(":")[0], Integer.parseInt(opponent.getConnection().split(":")[1]), "PLAYER_TURN_RESPONSE:"+newGame.getWinner()+":"+newGame.getBoardAsString()+":false");
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
		} else if (msg[0].equals("TURN")) {
		    int[] coords = {Integer.parseInt(msg[1]), Integer.parseInt(msg[2])};
		    runningGames.toFirst();
		    while(runningGames.hasAccess()) {
		    	Game game = runningGames.getContent();
		    	//aktuelles Spiel suchen
		    	if(game.getPlayers()[0].getConnection().equals(pClientIP + ":" + pClientPort) || game.getPlayers()[1].getConnection().equals(pClientIP + ":" + pClientPort)) {
		    		//Zug machen
		    		if(game.getPlayers()[0].getConnection().equals(pClientIP + ":" + pClientPort)) {
		    			System.out.println("in 1");
		    			game.turn(game.getPlayers()[0], coords);
		    			if(!game.isFinished()) {
		    				send(game.getPlayers()[0].getConnection().split(":")[0], Integer.parseInt(game.getPlayers()[0].getConnection().split(":")[1]), "PLAYER_TURN_RESPONSE:"+game.getWinner()+":"+game.getBoardAsString()+":false");
		    				send(game.getPlayers()[1].getConnection().split(":")[0], Integer.parseInt(game.getPlayers()[1].getConnection().split(":")[1]), "PLAYER_TURN_RESPONSE:"+game.getWinner()+":"+game.getBoardAsString()+":true");
		    			}
		    		}else {
		    			System.out.println("in 2");
		    			game.turn(game.getPlayers()[1], coords);
		    			if(!game.isFinished()) {
		    				send(game.getPlayers()[1].getConnection().split(":")[0], Integer.parseInt(game.getPlayers()[1].getConnection().split(":")[1]), "PLAYER_TURN_RESPONSE:"+game.getWinner()+":"+game.getBoardAsString()+":false");
		    				send(game.getPlayers()[0].getConnection().split(":")[0], Integer.parseInt(game.getPlayers()[0].getConnection().split(":")[1]), "PLAYER_TURN_RESPONSE:"+game.getWinner()+":"+game.getBoardAsString()+":true");
		    			}
		    		}
		    	}
		    	if (runningGames.getContent().isFinished()) {
		    		//change the stats
		    		db.updateStats(runningGames.getContent().getPlayers()[0], runningGames.getContent().getPlayers()[1], runningGames.getContent().getWinner());
		    		for (Player player: runningGames.getContent().getPlayers()) {
		    			if(runningGames.getContent().getWinner() == '#') {
		    				this.send(player.getConnection().split(":")[0], Integer.parseInt(player.getConnection().split(":")[1]), "PLAYER_TURN_RESPONSE:DONE:TIE:" + player.getUsername());
		    			} else if (player.getSymbol() == runningGames.getContent().getWinner()) {
		    				db.addPoints(player.getUsername());
		    				askForMembers(player.getUsername());
		    				//spieler über ausgang informieren
		    				this.send(player.getConnection().split(":")[0], Integer.parseInt(player.getConnection().split(":")[1]), "PLAYER_TURN_RESPONSE:DONE:WON:" + player.getUsername());
		    			}else {
		    				this.send(player.getConnection().split(":")[0], Integer.parseInt(player.getConnection().split(":")[1]), "PLAYER_TURN_RESPONSE:DONE:LOST:" + player.getUsername());
		    			}
		    		}
		    		runningGames.remove();
		    		
		    	}
		    	runningGames.next();
		   }
		}else if(msg[0].equals("DATAREQUEST")){
			int[] result = db.getStats(msg[1]);
			if(result == null) {
				this.send(pClientIP, pClientPort, "DATAREQUEST:ERROR");
			}else {
				String answer = "DATAREQUEST";
				for(int i = 0; i < result.length; i++) {
					answer = answer + ":" + result[i];
				}
				this.send(pClientIP, pClientPort, answer);
			}
		}else{
			// Fehlermeldung zurï¿½ckgeben
			this.send(pClientIP, pClientPort, "4:Username nicht erkannt");
		}
	}

    /**
     * Prï¿½ft, ob der Name des Spielers in der Liste mit den Spielern, die online
     * sind, enthalten ist. Ist dies der Fall, dann wird true zurï¿½ckgegeben. Sonst
     * wird false zurï¿½ckgegeben.
     * 
     * @param pName
     * @return
     */
    public boolean isPlayerOnline(String pName) {
        onlinePlayers.toFirst();
        while (onlinePlayers.hasAccess()) {
            // Wenn der User online ist
            if (onlinePlayers.getContent().getUsername().equals(pName)) {
                return true;
            }
            onlinePlayers.next();
        }
        return false;
    }

    public void askForFriends(String[] msg) {
        List<String> allPlayers = db.getFriends(msg[1]);
        allPlayers.toFirst();
        while (allPlayers.hasAccess()) {
            if (isPlayerOnline(allPlayers.getContent())) {
                Player aktPlayer = getPlayerIfOnline(allPlayers.getContent());
                // Aufforderung schicken
                this.send(aktPlayer.getConnection().split(":")[0], Integer.parseInt(aktPlayer.getConnection().split(":")[1]), "3:fordere Freunde");
            }
            allPlayers.next();
        }
    }

    public void askForMembers(String[] msg) {
        //alle User der Liga
        List<String> member = db.getMembers(db.getLeagueNameFromUsername(msg[1]));
        member.toFirst();
        while(member.hasAccess()) {
            //Wenn der User online ist
            if(isPlayerOnline(member.getContent().split(";")[0])) {
                Player aktUser = getPlayerIfOnline(member.getContent().split(";")[0]);
                this.send(aktUser.getConnection().split(":")[0], Integer.parseInt(aktUser.getConnection().split(":")[1]), "8:Lade Liga");
            }
            member.next();
        }
    }
    
    public void askForMembers(String username) {
        //alle User der Liga
        List<String> member = db.getMembers(db.getLeagueNameFromUsername(username));
        member.toFirst();
        while(member.hasAccess()) {
            //Wenn der User online ist
            if(isPlayerOnline(member.getContent().split(";")[0])) {
                Player aktUser = getPlayerIfOnline(member.getContent().split(";")[0]);
                this.send(aktUser.getConnection().split(":")[0], Integer.parseInt(aktUser.getConnection().split(":")[1]), "8:Lade Liga");
            }
            member.next();
        }
    }

    /**
     * Gibt das passende Userobjekt zu einem Usernamen zurï¿½ck. Ist der User nicht
     * online wird null zurï¿½ckgegeben.
     * 
     * @param pUsername
     * @return
     */
    public Player getPlayerIfOnline(String pUsername) {
        this.onlinePlayers.toFirst();
        while (this.onlinePlayers.hasAccess()) {
            if (this.onlinePlayers.getContent().getUsername().equals(pUsername)) {
                return this.onlinePlayers.getContent();
            }
            this.onlinePlayers.next();
        }
        return null;
    }

    @Override
    /**
     * Online-Status bei Freunden aktualisieren.
     */
    public void processClosingConnection(String pClientIP, int pClientPort) {
        this.onlinePlayers.toFirst();
        while(onlinePlayers.hasAccess()){
            if(onlinePlayers.getContent().getConnection().equals(pClientIP + ":" + pClientPort)){
                onlinePlayers.remove();
                break;
            }
            onlinePlayers.next();
        }
        onlinePlayers.toFirst();
        while(onlinePlayers.hasAccess()){
            this.send(onlinePlayers.getContent().getConnection().split(":")[0], Integer.parseInt(onlinePlayers.getContent().getConnection().split(":")[1]),"3:fordere Freunde");
            this.send(onlinePlayers.getContent().getConnection().split(":")[0], Integer.parseInt(onlinePlayers.getContent().getConnection().split(":")[1]),"8:Lade Liga");
            onlinePlayers.next();
        }
        //remove from waiting list
        this.waitingPlayers.toFirst();
        while(waitingPlayers.hasAccess()) {
        	if(waitingPlayers.getContent().getConnection().equals(pClientIP + ":" + pClientPort)) {
        		waitingPlayers.remove();
        		break;
        	}
        }
    }

    /**
     * ï¿½ndert die Liga-Headline.
     */
    public void changeLeagueHeadline() {

    }

    public static void main(String[] args) {
        GameServer server = new GameServer(9999);
        for (;;) {
        }
    }

}
