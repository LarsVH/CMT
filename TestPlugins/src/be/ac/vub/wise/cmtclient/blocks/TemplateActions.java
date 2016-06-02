package be.ac.vub.wise.cmtclient.blocks;

import java.io.Serializable;
import java.util.LinkedList;

public class TemplateActions extends Template implements Serializable{

	public LinkedList<ActionClient> actions;
	
	public TemplateActions(){
		actions = new LinkedList<ActionClient>();
	}

	public LinkedList<ActionClient> getActions() {
		return actions;
	}

	public void addAction(ActionClient action) {
		this.actions.add(action);
	}
	
	
}
