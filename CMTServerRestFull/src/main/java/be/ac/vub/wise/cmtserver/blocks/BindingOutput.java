package be.ac.vub.wise.cmtserver.blocks;

import java.io.Serializable;

public class BindingOutput implements BindingParameter,  Serializable{
	
	public OutputHA outputObj;
	public String parameter;
	public String parType;
        
        public BindingOutput(){}
        
	public OutputHA getOutputObj() {
		return outputObj;
	}
	public void setOutputObj(OutputHA outputObj) {
		this.outputObj = outputObj;
	}
	public String getParameter() {
		return parameter;
	}
	public void setParameter(String parameter) {
		this.parameter = parameter;
	}
	public String getParType() {
		return parType;
	}
	public void setParType(String parType) {
		this.parType = parType;
	}
	
	public int indexObj = 0;
        
	public int getIndexObj() {
		return indexObj;
	}
	public void setIndexObj(int indexObj) {
		this.indexObj = indexObj;
	}
	

}
