package be.ac.vub.wise.cmtserver.db;

import be.ac.vub.wise.cmtserver.db.DatabaseManager.DbSource;
import be.ac.vub.wise.cmtserver.blocks.Action;
import be.ac.vub.wise.cmtserver.blocks.ActionClient;
import be.ac.vub.wise.cmtserver.blocks.CMTField;
import be.ac.vub.wise.cmtserver.blocks.EventInput;
import be.ac.vub.wise.cmtserver.blocks.Fact;
import be.ac.vub.wise.cmtserver.blocks.FactType;
import be.ac.vub.wise.cmtserver.blocks.Function;
import be.ac.vub.wise.cmtserver.blocks.IFactType;
import be.ac.vub.wise.cmtserver.blocks.IFunctionClass;
import be.ac.vub.wise.cmtserver.blocks.Rule;
import be.ac.vub.wise.cmtserver.blocks.Template;
import be.ac.vub.wise.cmtserver.blocks.TemplateActions;
import be.ac.vub.wise.cmtserver.blocks.TemplateHA;
import be.ac.vub.wise.cmtserver.blocks.UriFactType;
import be.ac.vub.wise.cmtserver.util.Constants;
import be.ac.vub.wise.cmtserver.util.HelperClass;
import com.db4o.activation.ActivationPurpose;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;



import org.kie.api.definition.type.Role;
import org.kie.api.definition.type.Role.Type;

public class DbComponent implements IDbComponent{
	
	private static DbComponent comp= null;
	private IDatabaseConnector database;
	
	private DbComponent(){
		
		database = DatabaseManager.getDatabaseManager(DbSource.Db4o, null);
		
                Collection<Object> result = database.read(AvailableFactClasses.class, "").getResults();
                Collection<Object> resultEv = database.read(AvailableEventClasses.class, "").getResults();
              
                if(result == null || result.size()==0){
                  
                    AvailableFactClasses ob = new AvailableFactClasses();
                    database.create(ob);
                    database.commit();
                }
                if(resultEv == null || resultEv.size()==0){
                    
                    AvailableEventClasses ob = new AvailableEventClasses();
                    database.create(ob);
                    database.commit();
                }
	}
	
	public static DbComponent getDbComponent(){
		if(comp == null){
			comp = new DbComponent();
		}
		return comp;
	}
	

	@Override
	public HashSet<Template> getAvailableContextForms() {
		HashSet<Template> forms = new HashSet<Template>();
		Collection<Object> result = database.read(Template.class, "").getResults();
		if(result != null){
			for(Object obj : result){
				if(obj instanceof Template){
					Template form = (Template) obj;
					
					forms.add(form);
				}
			}
			return forms;
		}
		
		return null;
	}

        @Override
	public HashSet<TemplateActions> getAvailableTemplateActions() {
		HashSet<TemplateActions> forms = new HashSet<TemplateActions>();
		Collection<Object> result = database.read(TemplateActions.class, "").getResults();
		if(result != null){
			for(Object obj : result){
				if(obj instanceof TemplateActions){
					TemplateActions form = (TemplateActions) obj;
					
					forms.add(form);
				}
			}
			return forms;
		}
		
		return null;
	}
        
        @Override
	public HashSet<TemplateHA> getAvailableTemplateHA() {
		HashSet<TemplateHA> forms = new HashSet<TemplateHA>();
		Collection<Object> result = database.read(TemplateHA.class, "").getResults();
		if(result != null){
			for(Object obj : result){
				if(obj instanceof TemplateHA){
					TemplateHA form = (TemplateHA) obj;
					
					forms.add(form);
				}
			}
                         
			return forms;
		}
		
		return null;
	}
        
	@Override
	public boolean addContextForm(Template form) {
	
		//database.create(form.getIfBlocks());
		database.create(form);
		
		database.commit();
                
		return true;
	}

	@Override
	public boolean removeContextForm(Template form) {
		Template formFromDb = (Template) database.read(Template.class, "name == \""+form.getName()+"\"").getFirst(); 
		if(formFromDb != null){
			database.delete(formFromDb);
			return true;
		}
		return false;
	}

	@Override
	public HashSet<IFactType> getFacts() {
		HashSet<IFactType> facts = new HashSet<IFactType>();
		Collection<Object> result = database.read(IFactType.class, "").getResults();
		if(result != null){
			for(Object obj : result){
				if(obj instanceof IFactType){
						try{
							IFactType fact = (IFactType) obj;
							// get id check of al in lijst! 
                                                        if(!(obj.getClass().isAssignableFrom(FactType.class)) && !(obj.getClass().isAssignableFrom(EventInput.class))
                                                                && !(obj.getClass().isAssignableFrom(Fact.class))){
                                                        
                                                            System.out.println(" -- in db " + obj.getClass().getSimpleName());
							String id = fact.getClass().getAnnotation(UriFactType.class).id();
							Field f = fact.getClass().getDeclaredField(id);
							Object ob = f.get(fact);
							if(ob != null){
								if(!ob.toString().equals("")){
									facts.add(fact);
								}
							}
							
                                                        }

					} catch (SecurityException | IllegalArgumentException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (NoSuchFieldException ex) {
                                        Logger.getLogger(DbComponent.class.getName()).log(Level.SEVERE, null, ex);
                                    } catch (IllegalAccessException ex) {
                                        Logger.getLogger(DbComponent.class.getName()).log(Level.SEVERE, null, ex);
                                    }		}
			}
			return facts;
		}
		
		return null;
	}
        
         @Override
        public IFactType getFact(String className, String uriField, String value){
            try { 
                System.out.println(" get fact " + className);
                String clNameSimple = HelperClass.getSimpleNameAll(className);
                IFactType fact = (IFactType) database.read(Class.forName(Constants.PACKAGEFACTS+"."+clNameSimple), uriField +" == \""+value+"\"").getFirst();
                return fact;
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(DbComponent.class.getName()).log(Level.SEVERE, null, ex);
            }
            return null;
        }
        
        @Override
        public void registerFactType(FactType type){
            database.create(type);
            FactType typef = (FactType)database.read(FactType.class, "className == \""+type.getClassName()+"\"").getFirst(); 
           
            database.commit();
            
        }
        
        @Override
        public void registerEventType(FactType type){
            database.create(type);
            database.commit();
        }
        
        @Override
        public HashSet<FactType> getAvailableFactTypes() {
            HashSet<FactType> factTypes = new HashSet<FactType>();
            Collection<Object> result = database.read(FactType.class, "type == \"fact\"").getResults(); 
            for(Object ob : result){
                if(ob instanceof FactType){
                    factTypes.add((FactType)ob);
                }
            }
            
            return factTypes;    
        }
        
        @Override
        public HashSet<FactType> getAvailableEventTypes() {
            HashSet<FactType> eventTypes = new HashSet<FactType>();
            Collection<Object> result = database.read(FactType.class, "type == \"time\"").getResults(); 
            for(Object ob : result){
                if(ob instanceof FactType){
                    eventTypes.add((FactType)ob);
                }
            }
            Collection<Object> resultAct = database.read(FactType.class, "type == \"activity\"").getResults(); 
            for(Object ob2 : resultAct){
                if(ob2 instanceof FactType){
                    eventTypes.add((FactType)ob2);
                }
            }
            return eventTypes;
        }
        
        @Override
        public FactType getFactTypeWithName(String name){
             FactType type = (FactType)database.read(FactType.class, "className == \""+name+"\"").getFirst(); 
           // FactType type = (FactType) (database.readByExample(new FactType(name,null, null, null))).get(0); // returns list but since name is unique id facttype it can only be one
            return type;
        }
        
        
        
        @Override
        public HashSet<IFactType> getFactsWithType(String className){
            HashSet<IFactType> facts = new HashSet<IFactType>();
            Collection<Object> result = null;
            try {
                result = database.read(Class.forName(Constants.PACKAGEFACTS+"."+className), "").getResults();
                
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(DbComponent.class.getName()).log(Level.SEVERE, null, ex);
            }
		if(result != null){
			for(Object obj : result){
				if(obj instanceof IFactType){
                                    facts.add((IFactType) obj);
				}
			}
                }
            return facts;
        
        }
        
	@Override
	public void addFact(IFactType fact) {
		
		database.create(fact);
		
		database.commit();
	}
        
        @Override
        public void addFactinFactForm(Fact fact){
        
        }

	@Override
	public boolean removeFact(IFactType fact) {
		
		Type annotation= fact.getClass().getAnnotation(Role.class).value();
		if(annotation.equals(Type.FACT)){
			String id = fact.getClass().getAnnotation(UriFactType.class).id();
			if(id != null){
				
				String factidvalue;
				try {
					Field factField = fact.getClass().getDeclaredField(id);
					Object factFieldValue = factField.get(fact);
					if(factFieldValue != null && factFieldValue instanceof String){
						factidvalue = (String) factFieldValue;
						
						Collection<Object> results =  database.read(IFactType.class,  "").getResults();
						for(Object obj : results){
							if(fact.getClass().isInstance(obj)){
								String objId = (String) obj.getClass().getField(id).get(obj);
								if(objId.equals(factFieldValue)){
									database.delete(obj);
									database.commit();
									return true;
								}
							}
						}				
					}
				} catch (NoSuchFieldException | SecurityException e1) {
					return false;
				} catch (IllegalArgumentException e) {
					return false;
				} catch (IllegalAccessException e) {
					return false;
				}
			}
		} return false;	
	}

	@Override
	public boolean restartDb() {
		database.closeDb();
		comp = null;
		DbComponent.getDbComponent();
		return true;
	}
        
        public boolean resetDb(){
            database.resetDb();
            return true;
        }

	@Override
	public void addFunction(Function function) {
		database.create(function);
		database.commit();
                database.closeAndOpenDb();
                Collection<Object> result = database.read(Function.class, "").getResults();
                
	}



	@Override
	public HashSet<Function> getFunctions() {
		HashSet<Function> functions = new HashSet<Function>();
		Collection<Object> result = database.read(Function.class, "").getResults();
		for(Object obj : result){
			if(obj instanceof Function){
				Function fct = (Function) obj;
				functions.add(fct);
				
			}
		}
		return functions;
	}

	@Override
	public boolean addAction(Action action) {
		database.create(action);
		database.commit();
		return true;
	}

	@Override
	public boolean removeAction(Action action) {
		Collection<Object> result = database.read(action.getClass(), "").getResults();
		for(Object obj : result){
			database.delete(obj);
		}
		return true;
	}

	@Override
	public HashSet<Action> getActions() {
		HashSet<Action> actions = new HashSet<Action>();
		Collection<Object> result = database.read(Action.class, "").getResults();
		for(Object obj : result){
			if(obj instanceof Action && !(obj instanceof ActionClient)){
				Action fct = (Action) obj;
				actions.add(fct);
				
			}
		}
		return actions;
	}
        @Override
        public Action getAction(String className){
            
            try {
                Action res =(Action) database.read(Class.forName(Constants.PACKAGEACTIONS+"."+ HelperClass.getSimpleNameAll(className)), "").getFirst();
                return res;
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(DbComponent.class.getName()).log(Level.SEVERE, null, ex);
            }
            return null;
        }

	@Override
	public void addRule(Rule rule) {
		database.create(rule);
		database.commit();
		
	}

	@Override
	public HashSet<Rule> getRules() {
		HashSet<Rule> rules = new HashSet<Rule>();
		Collection<Object> result = database.read(Rule.class, "").getResults();
		if(result != null){
			for(Object obj : result){
				if(obj instanceof Rule){
					Rule rule = (Rule) obj;
					rules.add(rule);
				}
			}
			return rules;
		}
		
		return null;
	}

	@Override
	public Rule getRule(String name) {
		Rule rule = (Rule) database.read(Rule.class, "name == \""+name+"\"").getFirst(); 
		return rule;
	}

	public void closeDb(){
		database.closeDb();
	}

    @Override
    public HashSet<Fact> getFactsInFactVersions() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Fact getFact(int sqlid) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public HashSet<Fact> getFactsInFactVersionWithType(String classNamed) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Fact getFactInFactForm(String className, String uriField, String value) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void addRule(Rule rule, Template temp) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Template getTemplateOfSituation(String situationName) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ArrayList<FactType> getCustomEventsUsedInTemplate(int templateId) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ArrayList<String> getLievensteinMatchesFactTypes(String strToMatch, int threshold) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
