 

public class Main {
	
	public static void main (String[] args) {
		DatabaseExecuter ex = new DatabaseExecuter();
		List<String> tmp = ex.getMembers("keine Liga");
		tmp.toFirst();
		while(tmp.hasAccess()) {
			System.out.println(tmp.getContent());
			tmp.next();
		}
	}
}
