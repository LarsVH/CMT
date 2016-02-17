package be.ac.vub.wise.cmtserver.blocks;

import java.io.Serializable;

public class BindingInputFact implements BindingParameter, Serializable{
	
	public IFactType inputObject;
	public String factId; // uriField value!!
	
	
        public BindingInputFact(){}
	
	public IFactType getInputObject() {
		return inputObject;
	}
	public void setInputObject(IFactType inputObject) {
		this.inputObject = inputObject;
	}
	public String getFactId() {
		return factId;
	}
	public void setFactId(String factId) {
		this.factId = factId;
	}
	public int indexObj;
        
	public int getIndexObj() {
		return indexObj;
	}
	public void setIndexObj(int indexObj) {
		this.indexObj = indexObj;
	}
	
	
	
	
}
