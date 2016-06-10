package be.ac.vub.wise.cmtserver.db;

import be.ac.vub.wise.cmtserver.blocks.Action;
import be.ac.vub.wise.cmtserver.blocks.CMTField;
import be.ac.vub.wise.cmtserver.blocks.FactType;
import be.ac.vub.wise.cmtserver.blocks.Fact;
import be.ac.vub.wise.cmtserver.blocks.Function;
import be.ac.vub.wise.cmtserver.blocks.IFactType;
import be.ac.vub.wise.cmtserver.blocks.IFunctionClass;
import be.ac.vub.wise.cmtserver.blocks.Rule;
import be.ac.vub.wise.cmtserver.blocks.Template;
import be.ac.vub.wise.cmtserver.blocks.TemplateActions;
import be.ac.vub.wise.cmtserver.blocks.TemplateHA;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;



public interface IDbComponent {
	
    public HashSet<Template> getAvailableContextForms();
    public boolean addContextForm(Template form);
    public boolean removeContextForm(Template form);
    public HashSet<TemplateHA> getAvailableTemplateHA();
    public HashSet<TemplateActions> getAvailableTemplateActions();
    
    public HashSet<IFactType> getFacts();
    public Fact getFact(int sqlid);
    public Fact getFactInFactForm(String className, String uriField, String value);
    public HashSet<Fact> getFactsInFactVersions() ;
    public IFactType getFact(String className, String uriField, String value);
    public HashSet<FactType> getAvailableFactTypes();
    public void registerEventType(FactType type);
    public void addEventTypeFields(FactType type, ArrayList<CMTField> fields);
    public HashSet<FactType> getAvailableEventTypes();
    public FactType getFactTypeWithName(String name);
    public HashSet<IFactType> getFactsWithType(String classNamed);
    public HashSet<Fact> getFactsInFactVersionWithType(String classNamed);
    public void registerFactType(FactType type); // also for events -- just keeps the classname
    @Deprecated // Still working though
    public void addFactTypeFields(FactType type, ArrayList<CMTField> fields);
    public void addFact(IFactType fact);
    public void addFactinFactForm(Fact fact);
    public boolean removeFact(IFactType fact);
    public void addFunction(Function function);
    public HashSet<Function> getFunctions();
    public ArrayList<FactType> getCustomEventsUsedInTemplate(int templateId);
    
    public boolean addAction(Action action);
    public boolean removeAction(Action action);
    public HashSet<Action> getActions(); 
    public Action getAction(String className);
    
    public void addRule(Rule rule);
    public HashSet<Rule> getRules();
    public Rule getRule(String name);
    public void addRule(Rule rule, Template temp);
    
    public boolean restartDb();
    public void closeDb();
    public Template getTemplateOfSituation(String situationName);
    
    public ArrayList<String> getLievensteinMatchesFactTypes(String strToMatch, int threshold);
}
