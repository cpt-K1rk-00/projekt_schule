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
