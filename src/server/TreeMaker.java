package server;
/**
 * Beschreiben Sie hier die Klasse.
 * 
 * @author (Ihr Name) 
 * @version (eine Versionsnummer oder ein Datum)
 */
public class TreeMaker
{
   private Node<Field> parent;
   
   public TreeMaker(){
       setParent(new Node<Field>(new Field()));
       char[][] board = new char[3][3];
       System.out.println(board);
       for (int y = 0; y < 3; y++) {
			for (int x = 0; x < 3; x++) {
				board[y][x] = '#';
			}
       }
       this.parent.getData().setField(board);
   }
   
   /**
    * Fügt alle neue Möglichkeiten hinzu.
    */
   public void addNewLeafs(Node<Field> aktNode, char sign){
       for(int x = 0; x < 3; x++){
           for(int y = 0; y < 3; y++){
               //Neue Möglichkeit hinzufügen
               char[][] aktField = aktNode.getData().getField();
               //prüfen, ob das Feld frei ist
               if(aktField[y][x] == '#'){
                   //Zug setzen
                   aktField[y][x] = sign;
                   //Neues Feld als Kind hinzufügen, wenn nicht Sieg oder unentschieden
                   Field field = new Field();
                   field.setField(aktField);
                   //Prüfen, ob das aktuelle Feld ein weiteres Spielen möglich macht
                   if(true) {
                	  aktNode.addChild(new Node<Field>(field));
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
   
   public void travTree(Node<Field> aktNode) {
	   //Ausgabe
	   for(int i = 0; i < aktNode.getData().getField().length; i++) {
		   for(int j = 0; j < aktNode.getData().getField().length; j++) {
			  System.out.print(aktNode.getData().getField()[i][j]);
		   }
		   System.out.println();
	   }
	   System.out.println();
	   System.out.println();
	   //Rekursion
	   List<Node<Field>> children = aktNode.getChildren();
	   children.toFirst();
	   while(children.hasAccess()) {
		   travTree(children.getContent());
		   children.next();
	   }
   }
   
   public void startTrav() {
	   travTree(this.parent);
   }

   public Node<Field> getParent() {
	   return parent;
   }

   public void setParent(Node<Field> parent) {
	   this.parent = parent;
   }
   
   public static void main (String[] args) {
	  TreeMaker mkr = new TreeMaker();
	  mkr.startAddingLeafs();
	  mkr.startTrav();
   }
}
