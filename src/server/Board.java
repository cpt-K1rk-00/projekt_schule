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
    private boolean hasWon = false;
    private boolean semi = false;
    
    public Board(){
    	
        board = new char[3][3];
        
        for (int y = 0; y < 3; y++) {
			for (int x = 0; x < 3; x++) {
				
				board[y][x] = '#';
			}
		}
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
    		return true;
    	}
    	return false;
    	
    }
    
    public boolean hasWon(){
        return hasWon;
    }
    
    public boolean semi(){
        return semi;
    }
}
