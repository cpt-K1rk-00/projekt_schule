package server;
/**
 * Beschreiben Sie hier die Klasse Field.
 * 
 * @author (Ihr Name) 
 * @version (eine Versionsnummer oder ein Datum)
 */
public class Field
{
    private char[][] field;
    private boolean hasWon;
    private boolean semi;
    public Field(){
        field= new char[3][3];
        for(char[] arr : field){
            for(char c : arr){
                c = '#';
            }
        }
        hasWon = false;
        semi = false;
    }
    
    public void setField(char[][] pField){
        field = pField;
    }
    
    public char[][] getField(){
        char[][] result = new char[3][3];
        for(int i = 0; i < 3; i++) {
        	for(int j = 0; j < 3; j++) {
        		result[i][j] = this.field[i][j];
        	}
        }
        return result;
    }
    
    public boolean hasWon(){
        return hasWon;
    }
    
    public boolean semi(){
        return semi;
    }
}
