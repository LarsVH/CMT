package be.ac.vub.wise.cmtserver.blocks;

import java.io.Serializable;

public class Operator implements Serializable {
	
	String operator;
	String[] AvailableOperators = {"AND", "OR"};
	public String getOperator() {
		return operator;
	}
	public void setOperator(String operator) {
		this.operator = operator;
	}
	public String[] getAvailableOperators() {
		return AvailableOperators;
	}
	public void setAvailableOperators(String[] availableOperators) {
		AvailableOperators = availableOperators;
	}
	
	

}
