package server;
/**
 * Beschreiben Sie hier die Klasse DecisionMaker.
 * 
 * @author (Ihr Name) 
 * @version (eine Versionsnummer oder ein Datum)
 */
public class TreeMaker
{
   private Node<Field> parent;
   
   public TreeMaker(){
       setParent(new Node(new Field()));
   }
   
   /**
    * F�gt alle neue M�glichkeiten hinzu.
    */
   public void addNewLeafs(Node<Field> aktNode, char sign){
       for(int x = 0; x < 3; x++){
           for(int y = 0; y < 3; y++){
               //Neue M�glichkeit hinzuf�gen
               char[][] aktField = aktNode.getData().getField();
               //pr�fen, ob das Feld frei ist
               if(aktField[y][x] == '#'){
                   //Zug setzen
                   aktField[y][x] = sign;
                   //Neues Feld als Kind hinzuf�gen, wenn nicht Sieg oder unentschieden
                   Field field = new Field();
                   field.setField(aktField);
                   aktNode.addChild(new Node<Field>(field));
               }
           }
       }
       
   }

public Node<Field> getParent() {
	return parent;
}

public void setParent(Node<Field> parent) {
	this.parent = parent;
}
}
