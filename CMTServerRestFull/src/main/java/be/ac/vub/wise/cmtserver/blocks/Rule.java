package be.ac.vub.wise.cmtserver.blocks;

import java.io.Serializable;

public class Rule implements Serializable{
	
	String name;
	String drlRule;
	
	public Rule(){
		
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDrlRule() {
		return drlRule;
	}
	public void setDrlRule(String drlRule) {
		this.drlRule = drlRule;
	}

	
}
