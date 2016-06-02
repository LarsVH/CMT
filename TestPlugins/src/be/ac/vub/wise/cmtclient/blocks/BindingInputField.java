package be.ac.vub.wise.cmtclient.blocks;

import java.io.Serializable;

public class BindingInputField implements BindingParameter, Serializable {
	
	public IFactType inputObject;
	public String factId;
	public CMTField field;

	
        public BindingInputField(){}
        
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

    public CMTField getField() {
        return field;
    }

    public void setField(CMTField field) {
        this.field = field;
    }
	
	public int indexObj;
        
	public int getIndexObj() {
		return indexObj;
	}
	public void setIndexObj(int indexObj) {
		this.indexObj = indexObj;
	}
	
	
	
}
