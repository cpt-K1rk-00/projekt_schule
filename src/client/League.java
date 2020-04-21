package client;

 

public class League {
	
	private String name;
	private List<User> members;
	
	private League(String pName, List<User> pMembers) {
		this.name = pName;
		this.members = pMembers;
	}

}
