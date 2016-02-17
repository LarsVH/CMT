package be.ac.vub.wise.cmtgui.views;

import be.ac.vub.wise.cmtclient.blocks.BindingInfo;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Line;

public class CmtLine extends Line{
	
	private BindingInfo info;
	private int indexInOutput =-1;
	private int indexInInput =-1;
	private int indexInFunction=-1;
	private int indexCrS = -1;
	private int indexCrT = -1;
	

	public BindingInfo getInfo() {
		return info;
	}

	public void setInfo(BindingInfo info) {
		this.info = info;
	}

	public int getIndexInOutput() {
		return indexInOutput;
	}

	public void setIndexInOutput(int indexInOutput) {
		this.indexInOutput = indexInOutput;
	}

	public int getIndexInInput() {
		return indexInInput;
	}

	public void setIndexInInput(int indexInInput) {
		this.indexInInput = indexInInput;
	}

	public int getIndexInFunction() {
		return indexInFunction;
	}

	public void setIndexInFunction(int indexInFunction) {
		this.indexInFunction = indexInFunction;
	}

	public int getIndexCrS() {
		return indexCrS;
	}

	public void setIndexCrS(int indexCrS) {
		this.indexCrS = indexCrS;
	}

	public int getIndexCrT() {
		return indexCrT;
	}

	public void setIndexCrT(int indexCrT) {
		this.indexCrT = indexCrT;
	}

	
	
}
