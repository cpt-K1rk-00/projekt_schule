 

import java.util.Random;

/**
 * Beschreiben Sie hier die Klasse Game.
 * 
 * @author Lucas
 * @version 29.03.2020
 */
public class Game {

	Board board = new Board();
	Player[] players = new Player[2];

	public Game(Player pPlayer1, Player pPlayer2) {
		players[0] = pPlayer1;
		players[1] = pPlayer2;
		players[0].symbol = 'x';
		players[1].symbol = 'o';
	}

	public boolean turn(Player currentPlayer, int[] move) {
		return this.board.setField(move[0], move[1], currentPlayer.symbol);
	}
	
	public Player[] getPlayers() {
	    return players;
	   }
	
	public char getWinner() {
		return board.getWinner();
	}
	
	public boolean isFinished() {
		return board.isFinished();
	}
	
	public boolean isTied() {
		return board.isTied();
	}
	
	public String getBoardAsString() {
		String ret = "";
		for (int y = 0; y < 3; y++) {
			for (int x = 0; x < 3; x++) {
				ret += board.getField(x, y);
			}
		}
		return ret;
	}
}
