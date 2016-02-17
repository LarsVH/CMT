package be.ac.vub.wise.cmtserver.blocks;

import java.io.Serializable;

public class Binding implements Serializable{
	
	// start function or input - end input or output
	public BindingParameter startBinding;
	public BindingParameter endBinding;
	
        public Binding(){}
	
	public BindingParameter getStartBinding() {
		return startBinding;
	}
	public void setStartBinding(BindingParameter startBinding) {
		this.startBinding = startBinding;
	}
	public BindingParameter getEndBinding() {
		return endBinding;
	}
	public void setEndBinding(BindingParameter endBinding) {
		this.endBinding = endBinding;
	}
	
	

}
