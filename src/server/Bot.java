package server;

 


/**
 * Beschreiben Sie hier die Klasse Bot.
 * 
 * @author Lucas
 */
public class Bot extends Player
{
    public Bot()
    {
    	super("Bot", null);
    }
    
    void public turn(Board board) {
    	for (int y = 0; y < 3; y++) {
    		for (int x = 0; x < 3; x++) {
    			if board[y][x] == '#' {
    					board[y][x] = 'o';
    			}
    		}
    	}
    }
}
