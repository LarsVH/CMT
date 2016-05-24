package be.ac.vub.wise.cmtserver.blocks;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.LinkedList;

public class Template implements Serializable{
	
	public String name;
//	public LinkedHashMap<IFBlock, Operator> ifBlocks;
	public LinkedList<IFBlock> ifBlocks;
	public LinkedList<String> operators;
	public String category = "";
        public int sql_id = 0;
	
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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getSql_id() {
        return sql_id;
    }

    public void setSql_id(int sql_id) {
        this.sql_id = sql_id;
    }
       
	


}
