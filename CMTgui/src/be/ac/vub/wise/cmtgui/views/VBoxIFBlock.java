package be.ac.vub.wise.cmtgui.views;

import javafx.scene.layout.VBox;

public class VBoxIFBlock extends VBox{

	Object functionOrEvent;
	String type;

	public Object getFunctionOrEvent() {
		return functionOrEvent;
	}

	public void setFunctionOrEvent(Object functionOrEvent) {
		this.functionOrEvent = functionOrEvent;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	
}
