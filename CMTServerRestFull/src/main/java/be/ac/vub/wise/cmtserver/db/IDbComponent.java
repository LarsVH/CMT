package be.ac.vub.wise.cmtserver.db;

import be.ac.vub.wise.cmtserver.blocks.Action;
import be.ac.vub.wise.cmtserver.blocks.FactType;
import be.ac.vub.wise.cmtserver.blocks.Function;
import be.ac.vub.wise.cmtserver.blocks.IFactType;
import be.ac.vub.wise.cmtserver.blocks.IFunctionClass;
import be.ac.vub.wise.cmtserver.blocks.Rule;
import be.ac.vub.wise.cmtserver.blocks.Template;
import be.ac.vub.wise.cmtserver.blocks.TemplateActions;
import be.ac.vub.wise.cmtserver.blocks.TemplateHA;
import java.rmi.RemoteException;
import java.util.HashSet;
import java.util.List;



public interface IDbComponent {
	
	public HashSet<Template> getAvailableContextForms();
	public boolean addContextForm(Template form);
    public boolean removeContextForm(Template form);
    public HashSet<TemplateHA> getAvailableTemplateHA();
    public HashSet<TemplateActions> getAvailableTemplateActions();
    
    public HashSet<IFactType> getFacts();
    public IFactType getFact(String className, String uriField, String value);
    public HashSet<FactType> getAvailableFactTypes();
    public void registerEventType(FactType type);
    public HashSet<FactType> getAvailableEventTypes();
    public FactType getFactTypeWithName(String name);
    public HashSet<IFactType> getFactsWithType(String classNamed);
    public void registerFactType(FactType type); // also for events -- just keeps the classname
    public void addFact(IFactType fact);
    public boolean removeFact(IFactType fact);
    public void addFunction(Function function);
    public HashSet<Function> getFunctions();
    
    public boolean addAction(Action action);
    public boolean removeAction(Action action);
    public HashSet<Action> getActions(); 
    public Action getAction(String className);
    
    public void addRule(Rule rule);
    public HashSet<Rule> getRules();
    public Rule getRule(String name);
    
    public boolean restartDb();
    public void closeDb();
}
