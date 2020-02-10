package projekt_schule.client;

public class GameClient extends Client{
	
	public GameClient() {
		super("localhost", 9999);
	}

	@Override
	public void processMessage(String pMessage) {
		//Sp�ter entfernen
		System.out.println(pMessage);
	}
	
	/**
	 * F�hrt den Loginvorgang durch.
	 * @param pUsername
	 */
	public void login(String pUsername) {
		
	}
	
	/**
	 * F�hrt der Registrierungsvorgang durch.
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
	 * F�gt einen Freund hinzu.
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

}
