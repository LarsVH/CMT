package be.ac.vub.wise.cmtserver.blocks;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.LinkedList;

// The new situation resulting from a situation template (TemplateHA)
public class OutputHA implements Serializable{

	LinkedList<Binding> bindings;
	String name;
        
       
	
	public OutputHA(){
		bindings = new LinkedList<Binding>(); 
	}
	
	public LinkedList<Binding> getBindings(){
		return bindings;
	}
	
	public String getName(){
		return name;
	}
	
	public void setName(String name){
		this.name = name;
	}
	
	public void addBinding(Binding bind){
		bindings.add(bind);
	}
        
        public void setBindings(LinkedList<Binding> bindings){
            this.bindings = bindings;
        }
	
	
}
