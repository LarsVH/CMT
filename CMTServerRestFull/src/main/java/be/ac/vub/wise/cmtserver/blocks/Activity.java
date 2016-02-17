package be.ac.vub.wise.cmtserver.blocks;

public class Activity implements IFactType {

	private boolean custom;
	
	public void setCustom(boolean custom){
		this.custom = custom;
	}
	
	public boolean getCustom(){
		return custom;
	}
}
