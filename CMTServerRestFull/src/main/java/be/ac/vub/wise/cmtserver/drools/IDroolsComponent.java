package be.ac.vub.wise.cmtserver.drools;

import be.ac.vub.wise.cmtserver.blocks.IFactType;
import be.ac.vub.wise.cmtserver.blocks.IFunctionClass;
import java.rmi.RemoteException;
import java.util.HashSet;
import java.util.List;



public interface IDroolsComponent {

    public boolean resetDrools();
    public String getRulesDRL();
    public List<String> getRuleNames();
    public String getRule(String name);
	
   
	public boolean addFact(IFactType fact);
    public boolean addRule(String rule); 
    
    public boolean removeFact(IFactType fact);
   
	
}
