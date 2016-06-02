package be.ac.vub.wise.cmtclient.blocks;

import java.io.Serializable;
import java.util.LinkedList;

public class IFBlock implements Serializable{
	
	// function or time or activity
	String type;
	// if bindings 0 dan eg isInMeeting() in drl
	LinkedList<Binding>  bindings;
	FactType event;
	Function function;
	
	public IFBlock(){
		bindings = new LinkedList<Binding>();
	}
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public LinkedList<Binding> getBindings() {
		return bindings;
	}
        public void setBindings(LinkedList<Binding> bindings) {
            this.bindings = bindings;
	}
	public void addBinding(Binding binding) {
		this.bindings.add(binding);
	}

    public FactType getEvent() {
        return event;
    }

    public void setEvent(FactType event) {
        this.event = event;
    }

    public Function getFunction() {
        return function;
    }

    public void setFunction(Function function) {
        this.function = function;
    }
	
        
	
	

}
