package be.ac.vub.wise.cmtgui.views;

import java.io.Serializable;

import be.ac.vub.wise.cmtgui.util.PositionCmtCircle;




import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class CmtCircle extends Circle implements Serializable{
	
	private boolean enabled = false;
	private Object obj; 
	private String parameter;
	private PositionCmtCircle position;
	private String typeOfParameter;
	private int indexFunction;
	private int indexInput;
	private int indexOfCrInVB;
	
	public boolean getEnabled() {
		return enabled;
	}
	
	public void setEnabled(boolean enabled) {
		if(enabled){
			 this.setStyle("-fx-fill: #004D40;");
		}
		this.enabled = enabled;
	}
	
	public Object getObj() {
		return obj;
	}
	
	public void setObj(Object obj) {
		this.obj = obj;
	}
	
	public String getParameter() {
		return parameter;
	}
	
	public void setParameter(String parameter) {
		this.parameter = parameter;
	}
	
	public PositionCmtCircle getPosition() {
		return position;
	}
	
	public void setPosition(PositionCmtCircle position) {
		this.position = position;
	}

	public String getTypeOfParameter() {
		return typeOfParameter;
	}

	public void setTypeOfParameter(String typeOfParameter) {
		this.typeOfParameter = typeOfParameter;
	}
	
	public void setDefaultColor(){
		this.setStyle("-fx-fill: #80CBC4;");
		enabled = false;
	}

	public int getIndexFunction() {
		return indexFunction;
	}

	public void setIndexFunction(int indexFunction) {
		this.indexFunction = indexFunction;
	}

	public int getIndexInput() {
		return indexInput;
	}

	public void setIndexInput(int indexInput) {
		this.indexInput = indexInput;
	}

	public int getIndexOfCrInVB() {
		return indexOfCrInVB;
	}

	public void setIndexOfCrInVB(int indexOfCrInVB) {
		this.indexOfCrInVB = indexOfCrInVB;
	}
	

}
