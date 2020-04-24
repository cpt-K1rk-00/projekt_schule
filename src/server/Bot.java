package server;

 


/**
 * Beschreiben Sie hier die Klasse Bot.
 * 
 * @author Lucas
 */
public class Bot extends Player
{
	TreeMaker mkr = new TreeMaker();
	List<Board>[] outcome = null;
	int i = 0;
	
    public Bot()
    {
    	super("Bot", null);
    }
    
    public void turn(Board board) {
    	mkr.startAddingLeafs();
    	outcome = new List<Board>()[9];
    	startTrav();
    	board = countWins();
	}
    
    public Board countWins() {
    	int[] wins = new int[9];
    	for (int i = 0; i < 9; i++) {
    		wins[i] = 0;
	    	outcome[i].toFirst();
	    	while (outcome[i].hasAccess()) {
	    		if (outcome[i].getContent().checkResult() == 'o') {
	    			wins[i]++;
	    		}
	    		outcome[i].next();
	    	}
    	}
    	int max = 0;
    	for (int i = 0; i < 9; i++) {
    		if (wins[i] > wins[max]) {
    			max = i;
    		}
    	}
    	for (int y = 0; y < 3; y++) {
    		for (int x = 0; x < 3; x++) {
    			Board turn = null;
    			
    			int i = 0;
    			List<Node<Board>> children = mkr.parent.getChildren();
    			while (children.hasAccess()) {
    				if (i == max) {
    					return children.getContent().getData();
    				}
    	    		i++;
    	    		children.next();
    	    	}
    		}
    	}
    }
    
    public void startTrav() {
    	List<Node<Board>> children = mkr.parent.getChildren();
    	children.toFirst();
    	int i = 0;
    	while (children.hasAccess()) {
    		travTree(children.getContent());
    		i++;
    		children.next();
    	}
    }
    
    public void travTree(Node<Board> aktNode) {
 	   List<Node<Board>> children = aktNode.getChildren();
 	   children.toFirst();
 	   while(children.hasAccess()) {
 		   travTree(children.getContent());
 		   children.next();
 	   }
 	   outcome[i].append( aktNode.getData());
 	   
 	   
    }
}
