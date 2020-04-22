package server;

public class DataAnalyzer {
	
	public static int[] analyzer(String[][] data, String userID) {
		//wins, defeats, ties, total
		int[] result = new int[4];
		result[3] = data.length;
		for(String[] akt : data) {
			if(akt[0].equals(userID)){
				if(akt[2].equals("1")) {
					result[0] += 1;
				}else if(akt[2].equals("2")) {
					result[1] += 1;
				}else {
					result[2] += 1;
				}
			}else {
				if(akt[2].equals("1")) {
					result[1] += 1;
				}else if(akt[2].equals("2")) {
					result[0] += 1;
				}else {
					result[2] += 1;
				}
			}
		}
		return result;
	}

}
