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
	
    public Bot()
    {
    	super("Bot", null);
    }
    
    void public turn(Board board) {
    	mkr.startAddingLeafs();
    	outcome = new List<Board>()[9];
    	startTravTree();
    	board = countWins();
	}
    
    public Board countWins() {
    	int[] wins = new Int[9];
    	for (int i = 0; i < 9; i++) {
    		int wins[i] = 0;
	    	outcome[i].toFirst();
	    	while (outcome[i].hasAccess()) {
	    		if (outcome[i].getContent().checkResult() == 'o') {
	    			wins[i]++;
	    		}
	    		outcome.next();
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
    					return children.getContent().getBoard();
    				}
    	    		i++;
    	    		chldren.next();
    	    	}
    		}
    	}
    }
    
    public void startTrav() {
    	List<Node<Board>> children = mkr.parent.getChildren();
    	children.toFirst();
    	int i = 0
    	while (children.hasAccess()) {
    		travTree(children.getContent());
    		i++;
    		chldren.next();
    	}
    }
    
    public void travTree(Node<Board> aktNode) {
 	   List<Node<Board>> children = aktNode.getChildren();
 	   children.toFirst();
 	   while(children.hasAccess()) {
 		   travTree(children.getContent());
 		   children.next();
 	   }
 	   outcome[i] = aktNode.getData().getBoard();
 	   
 	   
    }
}
