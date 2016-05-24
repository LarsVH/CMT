package be.ac.vub.wise.cmtserver.blocks;

import java.io.Serializable;

public class BindingInputField implements BindingInputBlock, Serializable {
	
	public IFactType inputObject;
	public String factId;
	public CMTField field;

	
        public BindingInputField(){}
        
        @Override
	public IFactType getInputObject() {
		return inputObject;
	}
        @Override
	public void setInputObject(IFactType inputObject) {
		this.inputObject = inputObject;
	}
        @Override
	public String getFactId() {
		return factId;
	}
        @Override
	public void setFactId(String factId) {
		this.factId = factId;
	}

    public CMTField getField() {
        return field;
    }

    public void setField(CMTField field) {
        this.field = field;
    }
	
	public int indexObj = 0;
        
        @Override
	public int getIndexObj() {
		return indexObj;
	}
	public void setIndexObj(int indexObj) {
		this.indexObj = indexObj;
	}
	
	
	
}
