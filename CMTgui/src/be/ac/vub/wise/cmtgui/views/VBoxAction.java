package be.ac.vub.wise.cmtgui.views;



import be.ac.vub.wise.cmtclient.blocks.ActionClient;
import javafx.scene.layout.VBox;

public class VBoxAction extends VBox {
	
	private ActionClient action;

	public ActionClient getAction() {
		return action;
	}

	public void setAction(ActionClient action) {
		this.action = action;
	}
	
	

}
