package server;

 


/**
 * Beschreiben Sie hier die Klasse Bot.
 * 
 * @author Lucas
 */
public class Bot extends Player
{
	TreeMaker mkr = new TreeMaker();
    public Bot()
    {
    	super("Bot", null);
    	mkr.startAddingLeafs();
    }
    
    void public turn(Board board) {
    	for (int y = 0; y < 3; y++) {
    		for (int x = 0; x < 3; x++) {
    			if mkr.getData().getBaord()[y][x] == '#' {
    					board[y][x] = 'o';
    			}
    		}
    	}
    }
}
