package server;

import java.util.Random;

/**
 * Beschreiben Sie hier die Klasse Game.
 * 
 * @author Jan, Lucas
 * @version 13.02.2020
 */
public class Game {

	Field board = new Field();
	Player[] players = new Player[2];

	public Game(Player pPlayer1, Player pPlayer2) {

		for (int y = 0; y < 3; y++) {
			for (int x = 0; x < 3; x++) {
				board[y][x] = '#';
			}
		}
		players[0] = pPlayer1;
		players[1] = pPlayer2;
	}

	public boolean turn(Player currentPlayer, int[] move) {
		if (this.board[move[1]][move[0]] == '#') {
			this.board[move[1]][move[0]] = currentPlayer.symbol;
			return true;
		}
		return false;
	}

	public static String checkResult(char[][] board, Player currentPlayer) {
		boolean win = false;
		boolean tie = false;
		for (int y = 0; y < 3; y++) {
			int tmp = 0;
			for (int x = 0; x < 3; x++) {
				if (board[y][x] == currentPlayer.symbol) {
					tmp++;
				}
			}
			if (tmp == 3) {
				win = true;
			}
		}
		for (int x = 0; x < 3; x++) {
			int tmp = 0;
			for (int y = 0; y < 3; y++) {
				if (board[y][x] == currentPlayer.symbol) {
					tmp++;
				}
			}
			if (tmp == 3) {
				win = true;
			}
		}
		for (int y = 0; y < 3; y++) {
			int tmp = 0;
			if (board[y][y] == currentPlayer.symbol) {
				tmp++;
			}
			if (tmp == 3) {
				win = true;
			}
		}
		for (int y = 0; y < 3; y++) {
			int tmp = 0;
			if (board[y][2 - y] == currentPlayer.symbol) {
				tmp++;
			}
			if (tmp == 3) {
				win = true;
			}
		}
		if (!win) {
			int tmp = 0;
			for (int y = 0; y < 3; y++) {
				for (int x = 0; x < 3; x++) {
					if (board[y][x] == currentPlayer.symbol) {
						tmp++;
					}
				}
			}
			if (tmp == 9) {
				tie = true;
			}
		}
		if (win)
			return "WIN";
		if (tie)
			return "TIE";
		return null;
	}
	
	public Player[] getPlayers() {
	    return players;
	   }
}
