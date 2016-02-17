package be.ac.vub.wise.cmtserver.blocks;

import java.io.Serializable;

public class BindingIF implements BindingParameter,  Serializable{
	
    public String ifParameter;

    public BindingIF(){}
	public String getIfParameter() {
		return ifParameter;
	}
	public void setIfParameter(String ifParameter) {
		this.ifParameter = ifParameter;
	}
	
        public int indexObj;
        
	public int getIndexObj() {
		return indexObj;
	}
	public void setIndexObj(int indexObj) {
		this.indexObj = indexObj;
	}

}
