package be.ac.vub.wise.cmtgui.views;


import be.ac.vub.wise.cmtclient.blocks.ActionClient;
import be.ac.vub.wise.cmtclient.blocks.Binding;
import be.ac.vub.wise.cmtclient.blocks.IFactType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class CmtLabel extends Label{
	
	public IFactType obj;
	public boolean isFact;
	public ActionClient action;
	public boolean isFilled;
	
	public ComboBox box = null;
	public String format;
	public TextField tf = null;

        public Binding binding = null;

    public Binding getBinding() {
        return binding;
    }

    public void setBinding(Binding binding) {
        this.binding = binding;
    }
	public IFactType getObj() {
		return obj;
	}

	public void setObj(IFactType obj) {
		this.obj = obj;
	}

	public boolean isFact() {
		return isFact;
	}

	public void setFact(boolean isFact) {
		this.isFact = isFact;
	}

	public ActionClient getAction() {
		return action;
	}

	public void setAction(ActionClient action) {
		this.action = action;
	}

	public boolean isFilled() {
		return isFilled;
	}

	public void setFilled(boolean isFilled) {
		this.isFilled = isFilled;
	}

	public ComboBox getBox() {
		return box;
	}

	public void setBox(ComboBox box) {
		this.box = box;
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public TextField getTf() {
		return tf;
	}

	public void setTf(TextField tf) {
		this.tf = tf;
	}
	
	

}
