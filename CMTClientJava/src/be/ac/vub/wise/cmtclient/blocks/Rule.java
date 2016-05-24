package be.ac.vub.wise.cmtclient.blocks;

import java.io.Serializable;

public class Rule implements Serializable{
	
	String name;
	String drlRule;
        int sql_id = 0;
	
	public Rule(){
		
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDrlRule() {
		return drlRule;
	}
	public void setDrlRule(String drlRule) {
		this.drlRule = drlRule;
	}

    public int getSql_id() {
        return sql_id;
    }

    public void setSql_id(int sql_id) {
        this.sql_id = sql_id;
    }

        
	
}
