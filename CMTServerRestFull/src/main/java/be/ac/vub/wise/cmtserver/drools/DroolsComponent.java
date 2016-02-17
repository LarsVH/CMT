package be.ac.vub.wise.cmtserver.drools;


import be.ac.vub.wise.cmtserver.blocks.Action;
import be.ac.vub.wise.cmtserver.blocks.Activity;
import be.ac.vub.wise.cmtserver.restfull.CMTCore;
import be.ac.vub.wise.cmtserver.blocks.IFactType;
import be.ac.vub.wise.cmtserver.blocks.IFunctionClass;
import be.ac.vub.wise.cmtserver.blocks.UriFactType;
import be.ac.vub.wise.cmtserver.util.Constants;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.ReleaseId;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.api.conf.EventProcessingOption;
import org.kie.api.definition.rule.Rule;
import org.kie.api.definition.type.Role;
import org.kie.api.definition.type.Role.Type;
import org.kie.api.event.rule.DebugRuleRuntimeEventListener;
import org.kie.api.event.rule.ObjectDeletedEvent;
import org.kie.api.event.rule.ObjectInsertedEvent;
import org.kie.api.event.rule.ObjectUpdatedEvent;
import org.kie.api.event.rule.RuleRuntimeEventListener;
import org.kie.api.io.ResourceType;
import org.kie.api.logger.KieRuntimeLogger;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.ObjectFilter;
import org.kie.api.runtime.rule.FactHandle;

public class DroolsComponent implements IDroolsComponent {

	private KieSession kSession;
	KieServices ks; 
	KieContainer kContainer;
	int versionCounter = 0;
	private static DroolsComponent drools = null;
	String drlPath = Constants.PATHDRL;
	
	
	private DroolsComponent(){
		
		ks = KieServices.Factory.get();
		
		ReleaseId id = makeKieModuleVersion(null);
		
		kContainer = ks.newKieContainer(id);
                
		kSession = kContainer.newKieSession();
                kSession.addEventListener(new RuleRuntimeEventListener() {

                    @Override
                    public void objectInserted(ObjectInsertedEvent oie) { // ni mooi om naar CMT core te forwarden ma gene tijd nu! 
                        Object obj = oie.getObject();
                        CMTCore.get().notifyFromDroolsEvent(obj);
                        if(obj instanceof Action){
                            Field[] fields = obj.getClass().getDeclaredFields();
                            for(Field f : fields){
                                try {
                                    System.out.println(" field val in drools notify " + f.getName() + "  " + f.get(obj));
                                } catch (IllegalArgumentException ex) {
                                    Logger.getLogger(DroolsComponent.class.getName()).log(Level.SEVERE, null, ex);
                                } catch (IllegalAccessException ex) {
                                    Logger.getLogger(DroolsComponent.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            }
                        }
                    }

                    @Override
                    public void objectUpdated(ObjectUpdatedEvent oue) {
                        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                    }

                    @Override
                    public void objectDeleted(ObjectDeletedEvent ode) {
                        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                    }
                });
                kSession.addEventListener( new DebugRuleRuntimeEventListener());
                KieRuntimeLogger logger = ks.getLoggers().newConsoleLogger(kSession);
	}
	
	public static DroolsComponent getDroolsComponent(){
		if(drools == null){
			drools = new DroolsComponent();
		}
		return drools;
	}
	
        public boolean resetDrools(){
            // remove rules files source + target
            try {
		File file = new File(drlPath);
		BufferedWriter output = new BufferedWriter(new FileWriter(file,false));
		output.write("package rules");
		output.close();
	    } catch ( IOException e ) {
		e.printStackTrace();
            }
            // set drools == null
            drools = null;
            // new Drools
            getDroolsComponent();
            return true;
        }


	@Override
	public String getRulesDRL(){
		try {
			FileInputStream rules = new FileInputStream(drlPath);
			BufferedReader reader = new BufferedReader(new InputStreamReader(rules));
			StringBuilder out = new StringBuilder();
			String newLine = System.getProperty("line.separator");
			String line;
			while ((line = reader.readLine()) != null) {
				out.append(line);
			    out.append(newLine);
			}
		return out.toString();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public List<String> getRuleNames(){
		List<String> names = new LinkedList<String>();
		Collection<Rule> rules= kContainer.getKieBase().getKiePackage("rules").getRules();
		Iterator<Rule> it = rules.iterator();
		while(it.hasNext()){
			Rule rule = it.next();
			names.add(rule.getName());
		}
		
		return names;
	}

	@Override
	public String getRule(String name) {
		final Scanner scanner = new Scanner(drlPath);
		String rule = "";
		boolean stop = true;
		while (stop) {
		   final String lineFromFile = scanner.nextLine();
		   if(lineFromFile.contains(name)) { 
			  rule += lineFromFile + "\n";
			   boolean end = true;
			   while(end){
				   String nextLine = scanner.nextLine();
				   rule += nextLine + "\n";
				   if(nextLine.contains("end")){
					   end = false;
					   stop = false;
				   }
			   }
			   
		   }
		}
		
		return rule;
	}

	

	@Override
	public boolean addFact(IFactType fact) {
              
                kSession.insert(fact);
                kSession.fireAllRules();
                
                return true;
            
        }

	@Override
	public boolean addRule(String rule) {
		ReleaseId relId = makeKieModuleVersion(rule);
		kContainer.updateToVersion(relId);
		kSession.fireAllRules();
		return true;
	}

	@Override
	public boolean removeFact(final IFactType fact) {
		
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
					}else{
						return false;
					}
				} catch (NoSuchFieldException | SecurityException e1) {
					return false;
				} catch (IllegalArgumentException e) {
					return false;
				} catch (IllegalAccessException e) {
					return false;
				}
				
				ObjectFilter filter = new ObjectFilter() {
				    @Override
				        public boolean accept( Object object ) {
				            return object.getClass().equals( fact.getClass() );
				        }
				    };
				    
				    Collection<? extends Object> results = kSession.getObjects( filter );
				    Field field ;
				    for(Object obj : results){
				    	try {
							field = obj.getClass().getDeclaredField(id);
				    	} catch (NoSuchFieldException | SecurityException e) {
							return false;
						}
				    	
				    	try {
				    		
							Object objectField = field.get(obj);
							if(objectField instanceof String){
								String objectFieldValue = (String) objectField;
								if(objectFieldValue.equals(factidvalue)){
									FactHandle handle = kSession.getFactHandle(obj);
									if(handle != null){
										kSession.delete(handle);
										return true;
									}else{
										return false;
									}
								}
							}else{
								return false;
							}
						} catch (IllegalArgumentException
								| IllegalAccessException e) {
							return false;
						}
				    }
			}
		}
		return false;
	}
	
	private ReleaseId  makeKieModuleVersion(String rule){
		
		if(rule != null){
			try {
				File file = new File(drlPath);
				BufferedWriter output = new BufferedWriter(new FileWriter(file,true));
				output.write("\n" + rule);
				output.close();
			} catch ( IOException e ) {
				e.printStackTrace();
			}
		}
		
		ReleaseId id = ks.newReleaseId( "org.contextBox", "ContextBoxServer", Integer.toString(versionCounter) );
		versionCounter += 1;
		
		try {	
			
			KieFileSystem kfs2 = ks.newKieFileSystem();
			kfs2.generateAndWritePomXML(id);
			
			KieModuleModel kModuleModel2 = ks.newKieModuleModel();
			kModuleModel2.newKieBaseModel("rules").addPackage("rules").setDefault(true).setEventProcessingMode(EventProcessingOption.STREAM ).newKieSessionModel("ksession").setDefault(true);
			kfs2.writeKModuleXML(kModuleModel2.toXML());

			FileInputStream rules = new FileInputStream(drlPath);
                        StringBuilder builder = new StringBuilder();
                        int ch;
                        while((ch = rules.read()) != -1){
                            builder.append((char)ch);
                        }
                        String drl = builder.toString();
                        System.out.println(drl);
                        System.out.println("--------------------- rules file " + rules.available());
                        System.out.println(ks.getResources().newInputStreamResource(rules).getInputStream().available());
			//ks.getResources().newInputStreamResource(rules).setResourceType(ResourceType.DRL)
                        kfs2.write("src/main/resources/rules/rules.drl", drl);
                      
			KieBuilder kb2 = ks.newKieBuilder(kfs2).buildAll();
                        
			org.junit.Assert.assertFalse(
					kb2.getResults().getMessages(org.kie.api.builder.Message.Level.ERROR).toString(),
					kb2.getResults().hasMessages(org.kie.api.builder.Message.Level.ERROR));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException ex) {
                Logger.getLogger(DroolsComponent.class.getName()).log(Level.SEVERE, null, ex);
            }
		return id;
	}
	
	
	
	
	
}
