package be.ac.vub.wise.cmtclient.blocks;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.LinkedList;

public class Template implements Serializable{
	
	public String name;
//	public LinkedHashMap<IFBlock, Operator> ifBlocks;
	public LinkedList<IFBlock> ifBlocks;
	public LinkedList<String> operators;
	
	
	public Template(){
		this.ifBlocks = new LinkedList<IFBlock>(); 
		this.operators = new LinkedList<String>();
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public LinkedList<IFBlock> getIfBlocks() {
		return ifBlocks;
	}
	public void addIfBlock(IFBlock ifBlock, Operator op) {
		this.ifBlocks.add(ifBlock);
		operators.add(op.getOperator());
	}
	
	public LinkedList<String> getOperators() {
		return operators;
	}
       
	


}
