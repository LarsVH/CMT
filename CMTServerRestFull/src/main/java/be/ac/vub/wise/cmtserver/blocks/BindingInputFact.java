package be.ac.vub.wise.cmtserver.blocks;

import java.io.Serializable;

public class BindingInputFact implements BindingInputBlock, Serializable{
	
	public IFactType inputObject; // is a FactType or a Fact
	public String factId; // uriField value!! -> vb."Vince" -> to determine the instance in case of a Fact
	
	
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
	public int indexObj = 0;
        
	public int getIndexObj() {
		return indexObj;
	}
	public void setIndexObj(int indexObj) {
		this.indexObj = indexObj;
	}
	
	
	
	
}
