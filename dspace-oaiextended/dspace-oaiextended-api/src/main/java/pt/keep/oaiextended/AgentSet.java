package pt.keep.oaiextended;

public class AgentSet {
	private String name;
	private boolean isVirtual = false;
	
	
	public AgentSet (String name) {
		this.name = name;
		this.isVirtual = false;
	}
	
	public AgentSet (String name, boolean isVirtual) {
		this.name = name;
		this.isVirtual = isVirtual;
	}
	
	public boolean isVirtual () {
		return this.isVirtual;
	}
	
	public String getName ()  {
		return this.name;
	}
}
