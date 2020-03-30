package server;
/**
 * Beschreiben Sie hier die Klasse Field.
 * 
 * @author Jan, Lucas
 * @version (eine Versionsnummer oder ein Datum)
 */
public class Board
{
    private char[][] board;
    private char winner = '-';
    
    public Board(){
    	
        board = new char[3][3];
        
        for (int y = 0; y < 3; y++) {
			for (int x = 0; x < 3; x++) {
				
				board[y][x] = '#';
			}
		}
    }
    
public void setBoard(char[][] pBoard){
    	
        board = pBoard;
    }
    
    public char[][] getBaord(){
    	
        char[][] result = new char[3][3];
        
        for(int y = 0; y < 3; y++) {
        	for(int x = 0; x < 3; x++) {
        		
        		result[y][x] = this.board[y][x];
        	}
        }
        return result;
    }
    
    public boolean setField(int x, int y, char symbol) {
    	if (board[y][x] == '#') {
    		board[y][x] = symbol;
    		winner = checkResult();
    		return true;
    	}
    	return false;
    }
    
    public char getField(int x, int y) {
    	return board[y][x];
    }
    
    public boolean isFinished(){
        return winner != '-';
    }
    
    public boolean isTied(){
        return winner != '#';
    }
    
    public char getWinner() {
    	return winner;
    }
    
    private char checkResult() {
    	
    	for (int y = 0; y < 3; y++) {
    		for (int x = 1; x < 3; x++) {
    			if (board[y][x] != board[y][0]) {
    				break;
    			}
    			if (x == 2 && board[y][0] != '#') {
    				return board[y][0];
    			}
    		}
    	}
    	
    	for (int x = 0; x < 3; x++) {
    		for (int y = 1; y < 3; y++) {
    			if (board[y][x] != board[0][x]) {
    				break;
    			}
    			if (y ==2 && board[0][x] != '#') {
    	    		return board[0][x];
    			}
    		}
    	}
    	
    	for (int i = 1; i < 3; i++) {
    		if (board[i][i] != board[0][0]) {
    			break;
    		}
    		if (i == 2 && board[0][0] != '#') {
    			return board[0][0];
    		}
    	}
    	
    	for (int i = 1; i < 3; i++) {
    		if (board[i][2-i] != board[0][0]) {
    			break;
    		}
    		if (i == 2 && board[0][2] != '#') {
    			return board[0][2];
    		}
    	}
    	
    	for (int y = 0; y < 3; y++) {
    		for (int x = 0; x < 3; x++) {
    			if (board[y][x] == '#') {
    				return '-';
    			}
    		}
    	}
    	
    	return '#';
    }
}
