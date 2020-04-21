package server;

 
/**
 * Beschreiben Sie hier die Klasse.
 * 
 * @author (Ihr Name) 
 * @version (eine Versionsnummer oder ein Datum)
 */
public class TreeMaker
{
   private Node<Board> parent;
   
   public TreeMaker(){
       setParent(new Node<Board>(new Board()));
       char[][] board = new char[3][3];
       System.out.println(board);
       for (int y = 0; y < 3; y++) {
			for (int x = 0; x < 3; x++) {
				board[y][x] = '#';
			}
       }
       this.parent.getData().setBoard(board);
   }
   
   /**
    * F�gt alle neue M�glichkeiten hinzu.
    */
   public void addNewLeafs(Node<Board> aktNode, char sign){
       for(int x = 0; x < 3; x++){
           for(int y = 0; y < 3; y++){
               //Neue M�glichkeit hinzuf�gen
               char[][] aktBoard = aktNode.getData().getBaord();
               //pr�fen, ob das Feld frei ist
               if(aktBoard[y][x] == '#'){
                   //Zug setzen
            	   aktBoard[y][x] = sign;
                   //Neues Feld als Kind hinzuf�gen, wenn nicht Sieg oder unentschieden
                   Board board = new Board();
                   board.setBoard(aktBoard);
                   //Pr�fen, ob das aktuelle Feld ein weiteres Spielen m�glich macht
                   if(true) {
                	  aktNode.addChild(new Node<Board>(board));
                	  if(sign == 'x') {
                		  aktNode.getChildren().toLast();
                		  addNewLeafs(aktNode.getChildren().getContent(), 'o');
                	  }else {
                		  aktNode.getChildren().toLast();
                		  addNewLeafs(aktNode.getChildren().getContent(), 'x');
                	  }
                   }
               }
           }
       }
   }
   
   public void startAddingLeafs() {
	   addNewLeafs(this.parent, 'x');
   }
   
   public void travTree(Node<Board> aktNode) {
	   //Ausgabe
	   for(int i = 0; i < aktNode.getData().getBaord().length; i++) {
		   for(int j = 0; j < aktNode.getData().getBaord().length; j++) {
			  System.out.print(aktNode.getData().getBaord()[i][j]);
		   }
		   System.out.println();
	   }
	   System.out.println();
	   System.out.println();
	   //Rekursion
	   List<Node<Board>> children = aktNode.getChildren();
	   children.toFirst();
	   while(children.hasAccess()) {
		   travTree(children.getContent());
		   children.next();
	   }
   }
   
   public void startTrav() {
	   travTree(this.parent);
   }

   public Node<Board> getParent() {
	   return parent;
   }

   public void setParent(Node<Board> parent) {
	   this.parent = parent;
   }
   
   public static void main (String[] args) {
	  TreeMaker mkr = new TreeMaker();
	  mkr.startAddingLeafs();
	  mkr.startTrav();
   }
}
