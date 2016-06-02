package be.ac.vub.wise.cmtclient.blocks;

import java.io.Serializable;

public class TemplateHA extends Template implements Serializable{
	
	public OutputHA output;

	public TemplateHA(){
		
	}
	public OutputHA getOutput() {
		return output;
	}

	public void setOutput(OutputHA output) {
		this.output = output;
	}
	
	

}
