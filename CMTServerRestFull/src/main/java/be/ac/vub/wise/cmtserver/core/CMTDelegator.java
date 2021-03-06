/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ac.vub.wise.cmtserver.core;

import be.ac.vub.wise.cmtserver.db.DbComponent;
import be.ac.vub.wise.cmtserver.db.IDbComponent;
import be.ac.vub.wise.cmtserver.drools.DroolsComponent;
import be.ac.vub.wise.cmtserver.drools.IDroolsComponent;
import be.ac.vub.wise.cmtserver.blocks.Action;
import be.ac.vub.wise.cmtserver.blocks.FactType;
import be.ac.vub.wise.cmtserver.blocks.Function;
import be.ac.vub.wise.cmtserver.blocks.Fact;
import be.ac.vub.wise.cmtserver.blocks.IFactType;
import be.ac.vub.wise.cmtserver.blocks.IFunctionClass;
import be.ac.vub.wise.cmtserver.blocks.Rule;
import be.ac.vub.wise.cmtserver.blocks.Template;
import be.ac.vub.wise.cmtserver.blocks.TemplateActions;
import be.ac.vub.wise.cmtserver.blocks.TemplateHA;
import be.ac.vub.wise.cmtserver.db.DatabaseSQL;
import be.ac.vub.wise.cmtserver.util.Converter;
import java.util.ArrayList;
import java.util.HashSet;

/**
 *
 * @author Sandra
 */
public class CMTDelegator {
    
    private IDroolsComponent drools;
    private IDbComponent dbComp;
    
    private static CMTDelegator delegator = null;
    
    private CMTDelegator(){
        drools = DroolsComponent.getDroolsComponent();
        dbComp = DatabaseSQL.getDbComponent();
        // init drools add facts??
        
    }
    
    public static CMTDelegator get(){
		if(delegator == null){
                    delegator = new CMTDelegator();	
		}
		return delegator;
	}
            
    public String getDbComponentVersion(){
        if (dbComp instanceof DatabaseSQL){
            return "SQL";
        }
        return "DB4O";
    }
    public void registerFactType(FactType type){
        
        dbComp.registerFactType(type);
    }
    
    public void registerEventType(FactType type){
        dbComp.registerEventType(type);
    }
    
    public void addFact(IFactType fact){
        dbComp.addFact(fact);	
        drools.addFact(fact);
       
    }
    
    public void addFactInFactFrom(Fact fact){
        dbComp.addFactinFactForm(fact);
       // IFactType factInObject = Converter.fromFactToObject(fact);
       // drools.addFact(factInObject);
    }

    public void addEvent(IFactType event){
      	
        drools.addFact(event);
       
    }
    
    public void addFunction(Function function ){
        dbComp.addFunction(function);
    } 
    
    public HashSet<FactType> getAvailableFactTypes(){
        return dbComp.getAvailableFactTypes();
    }
    
    public HashSet<FactType> getAvailableEventTypes(){
        return dbComp.getAvailableEventTypes();
    }
    
    public HashSet<IFactType> getAllFacts(){
        return dbComp.getFacts();
    }
    
     public HashSet<Fact> getAllFactsFact(){
        return dbComp.getFactsInFactVersions();
    }
    
    public HashSet<Function> getAllFunctions(){
        return dbComp.getFunctions();
    }
    
    public void addRule(String name, String rule){
        drools.addRule(rule);
        Rule ruleObj = new Rule();
        ruleObj.setName(name);
        ruleObj.setDrlRule(rule);
        dbComp.addRule(ruleObj);
    }
    
    public HashSet<Rule> getRules(){
        return dbComp.getRules();
    }
    
    public void addTemplate(Template temp){
        dbComp.addContextForm(temp);
    }
    
    public HashSet<TemplateActions> getAllTemplateActions(){
        return dbComp.getAvailableTemplateActions();
    }
    
    public HashSet<TemplateHA> getAllTemplateHA(){
        return dbComp.getAvailableTemplateHA();
    }
    
    public void addAction(Action action){
        dbComp.addAction(action);
    }
    
    public HashSet<Action> getAllActions(){
        return dbComp.getActions();
    }
    
    public Action getAction(String className){
        return dbComp.getAction(className);
    }
    
    public FactType getFactTypeWithName(String name){
        return dbComp.getFactTypeWithName(name);
    }
    
    public IFactType getFact(String className, String uriField, String value){
        return dbComp.getFact(className, uriField, value);
    }
    
    public Fact getFactInFactForm(String className, String uriField, String value){
        return dbComp.getFactInFactForm(className, uriField, value);
    }
    
    public Fact getFact(int sqlid){
        return dbComp.getFact(sqlid);
    }
    public HashSet<IFactType> getFactsWithType(String className){
        return dbComp.getFactsWithType(className);
    }
    
    public HashSet<Fact> getFactsWithTypeInFactForm(String classname){
        return dbComp.getFactsInFactVersionWithType(classname);
    }
    
    public void addRule(Rule rule, Template temp){
        dbComp.addRule(rule, temp);
        drools.addRule(rule.getDrlRule());
    }
    
    public Template getTemplateOfSituation(String situationName){
        return  dbComp.getTemplateOfSituation(situationName);
    }
    
    public ArrayList<FactType> getCustomEventsUsedInTemplate(int tempid){
        return dbComp.getCustomEventsUsedInTemplate(tempid);
    }
}
