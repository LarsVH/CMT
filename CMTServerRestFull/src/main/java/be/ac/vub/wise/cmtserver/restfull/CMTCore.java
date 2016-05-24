/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ac.vub.wise.cmtserver.restfull;


import be.ac.vub.wise.cmtserver.core.CMTDelegator;

import be.ac.vub.wise.cmtserver.blocks.Action;
import be.ac.vub.wise.cmtserver.blocks.ActionClient;
import be.ac.vub.wise.cmtserver.blocks.Activity;
import be.ac.vub.wise.cmtserver.blocks.Binding;
import be.ac.vub.wise.cmtserver.blocks.BindingInputFact;
import be.ac.vub.wise.cmtserver.blocks.BindingInputField;
import be.ac.vub.wise.cmtserver.blocks.BindingParameter;
import be.ac.vub.wise.cmtserver.blocks.CMTField;
import be.ac.vub.wise.cmtserver.blocks.Event;
import be.ac.vub.wise.cmtserver.blocks.Fact;
import be.ac.vub.wise.cmtserver.blocks.FactType;
import be.ac.vub.wise.cmtserver.blocks.Function;
import be.ac.vub.wise.cmtserver.blocks.IFBlock;
import be.ac.vub.wise.cmtserver.blocks.IFactType;
import be.ac.vub.wise.cmtserver.blocks.Rule;
import be.ac.vub.wise.cmtserver.blocks.Template;
import be.ac.vub.wise.cmtserver.blocks.TemplateActions;
import be.ac.vub.wise.cmtserver.blocks.TemplateHA;
import be.ac.vub.wise.cmtserver.core.Compilers;
import be.ac.vub.wise.cmtserver.db.DatabaseSQL;
import be.ac.vub.wise.cmtserver.db.DbComponent;
import be.ac.vub.wise.cmtserver.drools.DroolsComponent;
import be.ac.vub.wise.cmtserver.util.Constants;
import be.ac.vub.wise.cmtserver.util.Converter;
import be.ac.vub.wise.cmtserver.util.HelperClass;
import com.google.gson.Gson;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author Sandra
 */
public class CMTCore {
    
    private String packageRmi = Constants.PACKAGEBLOCKS;
    private String packageFacts = Constants.PACKAGEFACTS;
    private String projectTargetPath = Constants.CLASSPATH;
    private static CMTCore core = null;
    
    private CMTCore(){
    
    }
    // Singleton (LvH)
    public static CMTCore get(){
	if(core == null){
            core = new CMTCore();
	}
	return core;
    }
    
    public boolean resetCMT(){
        // remove rules
        DroolsComponent.getDroolsComponent().resetDrools();
        if(!CMTDelegator.get().getDbComponentVersion().equals("SQL")){
                DbComponent.getDbComponent().resetDb();
        }else{
            DatabaseSQL.getDbComponent().resetDb();
        }
        ArrayList<File> dicToDelete = new ArrayList<>();
        File dicEv = new File(Constants.JAVAFILEPATH + Constants.PACKAGEEVENTSSLASH);
        File dicFacts = new File(Constants.JAVAFILEPATH + Constants.PACKAGEFACTSSLASH);
        File dicFunc = new File(Constants.JAVAFILEPATH + Constants.PACKAGEFUNCTIONSSLASH);
        File dicAct = new File(Constants.JAVAFILEPATH + Constants.PACKAGEACTIONSSLASH);
        File cdicEv = new File(Constants.CLASSPATH + Constants.PACKAGEEVENTSSLASH);
        File cdicFacts = new File(Constants.CLASSPATH + "/" + Constants.PACKAGEFACTSSLASH);
        File cdicFunc = new File(Constants.CLASSPATH + "/" + Constants.PACKAGEFUNCTIONSSLASH);
        File cdicAct = new File(Constants.CLASSPATH + "/" + Constants.PACKAGEFACTSSLASH);
        dicToDelete.add(dicEv);
        dicToDelete.add(dicFacts);
        dicToDelete.add(dicFunc);
        dicToDelete.add(dicAct);
        dicToDelete.add(cdicEv);
        dicToDelete.add(cdicFacts);
        dicToDelete.add(cdicFunc);
        dicToDelete.add(cdicAct);
        
        for(File file : dicToDelete){
            if(file.exists()){
                try {
                    FileUtils.cleanDirectory(file);
                } catch (IOException ex) {
                    Logger.getLogger(CMTCore.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        
        
        return true;
    }
    // Creates *.java file to be inserted in Drools (LvH)
    // P.e. Person, Location
    public void registerFactClass(JSONObject json){
        if(!json.getString("className").contains("java")){
        String className = HelperClass.toUppercaseFirstLetter(json.getString("className"));
        String uriField = json.getString("uriField");
        JSONArray arrFields = json.getJSONArray("fields");
        if(checkUriField(uriField, arrFields)){
            String source = "package "+ packageFacts+ "; "
                                    + "import " + packageRmi +"UriFactType; import org.kie.api.definition.type.Role; import org.kie.api.definition.type.Role.Type; "
                                    + "import java.io.Serializable; import " + packageRmi +"IFactType; "
                                    + "import org.apache.commons.lang3.builder.EqualsBuilder; import org.apache.commons.lang3.builder.HashCodeBuilder; "
                                    + "@Role(Type.FACT) @UriFactType(id = \""+ uriField+"\") "

                                    + "public class " +className + " implements IFactType, Serializable { ";
            for(int i = 0; i< arrFields.length(); i++){
                JSONObject ob = arrFields.getJSONObject(i);
                String type = ob.getString("fieldType");
                String fieldName = ob.getString("fieldName");
                
                if(!type.contains("java")){
                    String[] splitLastPoint = type.split("\\.");
                    int z = splitLastPoint.length;
                    String simpleClassName = type;
                    if(z>0){
                        simpleClassName= splitLastPoint[z-1];
                    }
                    if(checkTypeClassPath(simpleClassName)){
                        //String okType = StringUtils.capitalize(type);
                        source = addSetterGetters(source, fieldName, Constants.PACKAGEFACTS+"."+simpleClassName);
                    }
                }else{
                    source = addSetterGetters(source, fieldName, type);
                }
            }
            String capClassName = StringUtils.capitalize(className);
            source += getStringEqualsEtc(uriField, capClassName) + "}";
            HelperClass.compile(source, className, Constants.PACKAGEFACTSSLASH);
        }
        }
            FactType type = Converter.fromJSONtoFactTypeFact(json);
            CMTDelegator.get().registerFactType(type);
        
    }
    
    public JSONObject addFact(JSONObject json){
        IFactType fact = Converter.fromJSONFactObjectToObject(json, false);
        
        CMTDelegator.get().addFact(fact);
        JSONObject resultFact = Converter.fromFactToJSON(Converter.fromObjectToFactInstance(fact));
        return resultFact;
    }
    
    public JSONObject addFactInFactFormat(JSONObject json){ 
            Fact fact = Converter.fromJSONtoFact(json);
            if(!CMTDelegator.get().getDbComponentVersion().equals("SQL")){
                IFactType obj = Converter.fromFactToObject(fact);
                if(obj!=null)
                    CMTDelegator.get().addFact(obj);
            }else{
                CMTDelegator.get().addFactInFactFrom(fact);
            }
        return json;
    }
    
    
    
    // input JSON {"className":<name>, "extends":<time or activity>, "activityCustom": <boolean> (if time == false),
    //"uriField":<fieldname>, "varList":[<list of strings>{"var":<label>}], "varFormat":<format>, "fields":[{"fieldName":<name>, "type":<simpleClassName>}, ... ]}    
    
    public void registerEventClass(JSONObject json){
        System.out.println(" json re " + json);
        String className = HelperClass.toUppercaseFirstLetter(json.getString("className"));
        if(CMTDelegator.get().getFactTypeWithName(className) == null){
        String typeEvent = json.getString("type");
        String extendsClass = "";
        boolean custom = false;
        boolean isActivity = false;
        switch(typeEvent){
            case "activity":
                 extendsClass = "be.ac.vub.wise.cmtserver.blocks.Activity";
                 isActivity = true;
                 custom = json.getBoolean("isCustom");
                 break;
            case "time":
                extendsClass = "import be.ac.vub.wise.cmtserver.blocks.Time";
                break;
        }
        
        String uriField = json.getString("uriField");
        JSONArray varList = json.getJSONArray("varList"); // populate linkedList in constructor  -- if empty then format is ok
        String varFormat = json.getString("varFormat");
        JSONArray arrFields = json.getJSONArray("fields");
        if(checkUriField(uriField, arrFields) || uriField.isEmpty()){
            String source = "package "+ Constants.PACKAGEEVENTS+ ";"
                                    + "import " + Constants.PACKAGEBLOCKS +"UriFactType; import org.kie.api.definition.type.Role; import org.kie.api.definition.type.Role.Type; "
                                    + "import java.io.Serializable; import " + Constants.PACKAGEBLOCKS +"IFactType; "
                                    + "import org.apache.commons.lang3.builder.EqualsBuilder; import org.apache.commons.lang3.builder.HashCodeBuilder; "
                                    + "import be.ac.vub.wise.cmtserver.blocks.EventVariables;  import be.ac.vub.wise.cmtserver.blocks.Time; import be.ac.vub.wise.cmtserver.blocks.Activity;"
                                    + "@Role(Type.EVENT) @UriFactType(id = \""+ uriField+"\") ";
            
            if(varList.length() == 0 ){ // then format -- todo add exception if both are empty
                if(varFormat.isEmpty()){
                    source += "@EventVariables(list = \"\", format=\"\")";
                }else{
                    source += "@EventVariables(list = \"\", format=\"format\")";
                }
            }else{
                source += "@EventVariables(list = \"list\", format=\"\")";
            }
            
            source += " public class " +className + " extends "+extendsClass+" { ";
            
            if(varList.length() == 0){ 
                if(varFormat.isEmpty()){
                    source += " public "+ className+"(){"; // constructor
                            
                }else{
                    source += " java.lang.String format = \"\"; "
                            + " public "+ className+"(){" // constructor
                            + " this.format = \""+varFormat+"\"; ";
                }
                if(isActivity){
                    source += " super.setCustom("+custom+"); } ";
                } else{
                    source +="}";
                }        
            }else{
                source += " public java.util.LinkedList<String> list = null; "
                        + " public "+ className+"(){" // constructor
                        + " this.list = new java.util.LinkedList<String>(); ";
                if(isActivity){
                    source += " super.setCustom("+custom+"); ";
                }
                for(int i = 0; i<varList.length(); i++){
                    JSONObject ob = varList.getJSONObject(i);
                    String var = ob.getString("var");
                    source += " this.list.add(\""+var+"\"); "; //populate list
                }
                
                source +="}";
            }
            
            System.out.println(" arrFields length " + arrFields.length());
            for(int i = 0; i< arrFields.length(); i++){
                JSONObject ob = arrFields.getJSONObject(i);
                String type = ob.getString("fieldType");
                String fieldName = ob.getString("fieldName");
                if(!type.contains("java")){
                    String[] splitLastPoint = type.split("\\.");
                    int z = splitLastPoint.length;
                    String simpleClassName = type;
                    if(z>0){
                        simpleClassName= splitLastPoint[z-1];
                    }
                    if(checkTypeClassPath(simpleClassName)){
                        //String okType = StringUtils.capitalize(type);
                        source = addSetterGetters(source, fieldName, Constants.PACKAGEFACTS+"."+simpleClassName);
                    }
                }else{
                    source = addSetterGetters(source, fieldName, type);
                }
            }
            
            source += "}";
            HelperClass.compile(source, className, Constants.PACKAGEEVENTSSLASH);
            FactType type = Converter.fromJSONtoFactTypeEvent(json);
            CMTDelegator.get().registerEventType(type);
                    
        }
        }
    }
    
   
    //input JSON {"className":<name>, "object":{...}}
    public JSONObject addEvent(JSONObject json){
      IFactType event = Converter.fromJSONFactObjectToObject(json, true);
      CMTDelegator.get().addEvent(event);
        
      FactType eventType = CMTDelegator.get().getFactTypeWithName(event.getClass().getSimpleName());
      boolean isActivity =false;
      if(eventType.getType().equals("activity")){
          isActivity = true;
      }
      Event eventReturn = Converter.fromObjectToEvent(event, isActivity, eventType.isIsCustom(), eventType.getUriField(), eventType.getVarList(), eventType.getVarFormat());
      return Converter.fromEventToJSON(eventReturn);
    }
    
    public JSONObject addActivity(JSONObject json){
        Activity event = Converter.fromJSONtoActivityInstance(json);
        System.out.println(" ---------- add act " + event.getClass().getSimpleName());
        CMTDelegator.get().addEvent(event);
       FactType eventType = CMTDelegator.get().getFactTypeWithName(event.getClass().getSimpleName());
      boolean isActivity =false;
      if(eventType.getType().equals("activity")){
          isActivity = true;
      }
      Event eventReturn = Converter.fromObjectToEvent(event, isActivity, eventType.isIsCustom(), eventType.getUriField(), eventType.getVarList(), eventType.getVarFormat());
      return Converter.fromEventToJSON(eventReturn);
    }
    
    public void registerFunctionClass(JSONObject json){
      
        String className = json.getString("encapClass");
        JSONArray mets = json.getJSONArray("methods");
        ArrayList<Function> funclist = new ArrayList<Function>();
        String source = "package "+ Constants.PACKAGEFUNCTIONS+ ";"
                        + " import be.ac.vub.wise.cmtserver.blocks.IFunctionClass; import be.ac.vub.wise.cmtserver.blocks.Parameters; import java.io.Serializable; "
                        + " public class " + className + " implements IFunctionClass, Serializable{ ";
        
        for(int i = 0; i<mets.length(); i++){
            
            
            JSONObject ob = mets.getJSONObject(i);
            Function fu = Converter.fromJSONtoFunction(ob);
           // fu.setEncapClass(className);
            funclist.add(fu);
            CMTDelegator.get().addFunction(fu);
        
            
            String methodName = ob.getString("methodName");
            String methodBody = ob.getString("methodBody");
            JSONArray arrPars = ob.getJSONArray("pars");
            fu.setName(methodName);
            source += "@Parameters(parameters = \"" ; 
            for(int b=0; b<arrPars.length();b++){
                JSONObject objPar = getParIndex(b, arrPars);
                String namePar = objPar.getString("parName");
                source += namePar + " ";
            }        
            source +="\") ";
            source += "public static boolean " +methodName+" (";
           
            for(int a=0;a<arrPars.length();a++){
                JSONObject obPar = getParIndex(a, arrPars);
               
                String parName = obPar.getString("parName");
                String parType = obPar.getString("parType");
                System.out.println(">>>>>>>>" + parType);
               
                if(!parType.contains("java")){  // No default Java type parameter
                   
                    String[] splitLastPoint = parType.split("\\.");
                    System.out.println("2>>>>>>> " + splitLastPoint[0]);
                    int z = splitLastPoint.length;
                    String simpleClassName = parType;
                    String finalBinaryTypeName = "";
                    if(z>0){
                        simpleClassName= splitLastPoint[z-1];
                    }
                    
                    if(checkTypeClassPath(simpleClassName)){
                        if(isEvent(simpleClassName)){
                            finalBinaryTypeName = Constants.PACKAGEEVENTS + "." + simpleClassName;
                        }else{
                            finalBinaryTypeName = Constants.PACKAGEFACTS + "." +simpleClassName;
                        }
                        
                        source += " "+finalBinaryTypeName+" " +parName;
                        if(a != arrPars.length()-1){
                            source += ",";
                        }
                    }
                }else{
                    System.out.println("5>>>>>>> ELSE " + parType);
                    source += " " +parType + " " + parName;
                    if(a != arrPars.length()-1){
                        source += ",";
                    }
                }
            }
            source +=  " ){" + methodBody + "} ";
        }
        
        source += "}";
        
        System.out.println("4>>>>>>>>>>>> \n" + source);
        
        HelperClass.compile(source, className, Constants.PACKAGEFUNCTIONSSLASH);
         Class<?> cl;
        try {
            URLClassLoader l = new URLClassLoader(new URL[]{new File(Constants.CLASSPATH+"/").toURI().toURL()}, Thread.currentThread().getContextClassLoader());
              // l.loadClass(Constants.PACKAGERMI + "IFunctionClass");
            cl = Class.forName(Constants.PACKAGEFUNCTIONS + "."+ className, true, l);
         
        Object object = cl.newInstance();
      //  IFunctionClass func = (IFunctionClass) object;
        
        for(Function fun : funclist){
            CMTDelegator.get().addFunction(fun);
        }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(CMTCore.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            Logger.getLogger(CMTCore.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(CMTCore.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MalformedURLException ex) {
            Logger.getLogger(CMTCore.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private JSONObject getParIndex(int i, JSONArray pars){
        for(int a=0; a<pars.length(); a++){
            JSONObject obPar = pars.getJSONObject(a);
            if(obPar.getInt("index") == i){
                return obPar;
            }
        }
        return null;
    }
    
    public void addRule(JSONObject json){
        String ruleName = json.getString("name");
        String rule = json.getString("drlRule");
        CMTDelegator.get().addRule(ruleName, rule);
    }
    
   public void addTemplateActions(JSONObject json){ 
       
       TemplateActions temp = Converter.fromJSONtoTemplateAction(json);
       CMTDelegator.get().addTemplate(temp);
   }
   
   public void addTemplateHA(JSONObject json){ 
       
       TemplateHA temp = Converter.fromJSONtoTemplateHA(json);
       
        CMTDelegator.get().addTemplate(temp);
   }
   
   
   // {"fieldName": <>, "varList":<[{"var":<string>}, ...]>, "varFormat":<format>} field type can only be string!
   public void addAction(JSONObject json){
       String className = json.getString("className");
       JSONArray fields = json.getJSONArray("fields");
       String source = "package "+ Constants.PACKAGEACTIONS+ "; "
                        + "import be.ac.vub.wise.cmtserver.blocks.Action; import be.ac.vub.wise.cmtserver.blocks.ActionFieldAnno; import java.io.Serializable; "
                        + " public class " +className + " implements Action, Serializable { "
                        + " public " +className+ "(){ populateLists(); } ";
       String bodyPopulateLists = "";                 
       for(int i = 0; i<fields.length(); i++){
           JSONObject ob = fields.getJSONObject(i);
           JSONArray varList = ob.getJSONArray("varList");
           String varFormat = ob.getString("varFormat");
           String name = ob.getString("fieldName");
           String capName = StringUtils.capitalize(name);
           
           if(varList.length() == 0){ 
                
                if(varFormat.isEmpty()){
                    source += "@ActionFieldAnno(list=\"\", format=\"format"+ capName+"\")" 
                            + " public String " + name + " = \"\";" // constructor
                            + " public String format"+capName+ " = \"String\" ; ";
                    source = addSetterGettersActions(source, name, "String");
                }else{
                    source += "@ActionFieldAnno(list=\"\", format=\"format"+ capName+"\")" 
                            + " public String " + name + " = \"\";" // constructor
                            + " public String format"+capName+ " = \""+varFormat+"\" ; ";
                    source = addSetterGettersActions(source, name, "String");
                }
                     
            }else{
                source += "@ActionFieldAnno(list=\" list"+capName+"\", format=\"\")"
                            + " public String " + name + " = \"\";" // constructor
                            + " public java.util.ArrayList<String> list"+capName+ " = null ; ";
                            
                    source = addSetterGettersActions(source, name, "String");
                    
                    bodyPopulateLists += " this.list"+capName+" = new java.util.ArrayList<String>(); ";
                    for(int a = 0; a<varList.length() ; a++){
                        JSONObject varO = varList.getJSONObject(a);
                        String var = varO.getString("var");
                        bodyPopulateLists += " this.list"+capName+ ".add(\""+var+"\"); ";
                    }
            }
        }   
        source += " public void populateLists(){ "+ bodyPopulateLists+" } }";
        HelperClass.compile(source, className, Constants.PACKAGEACTIONSSLASH);
        try {
            Class<?> cl = Class.forName(Constants.PACKAGEACTIONS + "."+ className);
            Object object = cl.newInstance();
            Action action = (Action) object;
            CMTDelegator.get().addAction(action);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(CMTCore.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            Logger.getLogger(CMTCore.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(CMTCore.class.getName()).log(Level.SEVERE, null, ex);
        }
              
    }
   
   public Rule compileAndAddRule(JSONObject json){ // filled in tremplate so also create new activity here!! 
       Template temp = null;
       String type = json.getString("tempType");
       switch (type){
           case "TemplateActions":
               temp = Converter.fromJSONtoTemplateAction(json);
               break;
           case "TemplateHA":
               temp=Converter.fromJSONtoTemplateHA(json);
               break;
       }
       if(temp != null){
           if(!CMTDelegator.get().getDbComponentVersion().equals("SQL")){
               if(temp instanceof TemplateHA){
                   Compilers.createNewActivity((TemplateHA)temp); 
               }
               Rule rule = Compilers.compileDrlRuleActivity(temp);
                CMTDelegator.get().addRule(rule.getName(), rule.getDrlRule());
                return rule;
            }else{
               // temp is ingevulde template
               System.out.println("------------------------------------------------ " + temp.getClass().getSimpleName());
               if(temp instanceof TemplateHA){
                   Compilers.createNewActivity((TemplateHA)temp); 
               }
               Rule rule = Compilers.compileDrlRuleActivity(temp);
               CMTDelegator.get().addRule(rule, temp);
               return rule;
           }
           
       }
       return null;
   }
   
   public void notifyFromDroolsEvent(Object object){
       
       Gson gson = new Gson();
       String json = gson.toJson(object);
       JSONObject result = new JSONObject();
       result.put("className", object.getClass().getSimpleName());
       result.put("object", json);
       // achterhalen currentContext of niet 
       if(object instanceof Activity ){
           Activity act = (Activity) object;
           if(act.getCustom()){
            CMTRest.notify("currentContext", result);
           }
       }else{
           if(object instanceof Action){
               ActionClient act = Converter.fromActionObjectToActionClient((Action)object);
               JSONObject res = Converter.fromActionToJSON(act);
               CMTRest.notify("actionInvoked", res);
           }
       }
       
   }
   
 

    private String addSetterGetters(String source, String fieldName, String type){
        String capField = StringUtils.capitalize(fieldName);
        String result = source;
                    result += " public " + type + " " + fieldName + " ;" 
                            + " public void set"+capField+"("+type+" " + fieldName +"){"
                            + " this."+fieldName+" = " +fieldName+ ";} "
                            + " public " + type + " get"+capField+"(){"
                            + " return this."+fieldName+";} ";
        return result;
    }
    
    private String addSetterGettersActions(String source, String fieldName, String type){
        String capField = StringUtils.capitalize(fieldName);
        String result = source;
                    result += " public void set"+capField+"("+type+" " + fieldName +"){"
                            + " this."+fieldName+" = " +fieldName+ ";} "
                            + " public " + type + " get"+capField+"(){"
                            + " return this."+fieldName+";} ";
        return result;
    }
    
    private boolean checkUriField(String uriField, JSONArray arrFields){
        for(int i = 0; i< arrFields.length(); i++){
            JSONObject ob = arrFields.getJSONObject(i);
            if(ob.getString("fieldName").equals(uriField)){
                return true;
            }
        }
        return false;
    }
    
    private boolean checkTypeClassPath(String type){
        
        String classUri = projectTargetPath +"/"+Constants.PACKAGEFACTSSLASH+"/"+type + ".class";   // In case of a fact
        
        String classUriEv = projectTargetPath +"/"+Constants.PACKAGEEVENTSSLASH+"/"+type + ".class";    // In case of an event
               
        if(Files.exists(new File(classUri).toPath()) || Files.exists(new File(classUriEv).toPath())  ){
            System.out.println("3>>>>" + "TRUE");
            return true;
        } return false;
    }
    
    private boolean isEvent(String type){
      
        String packToFoldEv = Constants.PACKAGEEVENTS.replaceAll(".", "/");
        String classUriEv = projectTargetPath +"/"+packToFoldEv+"/"+type + ".class";
        if(Files.exists(new File(classUriEv).toPath())  ){
            return true;
        } return false;
    }
    
    private String getStringEqualsEtc(String uriField, String classname){
        String source = "@Override\n" 
                        + " public int hashCode(){ " 
                        + " return new HashCodeBuilder().append(this."+uriField+").toHashCode();}"
                        + "@Override\n" 
                        + " public boolean equals(Object obj){" 
                        + " if (obj instanceof "+classname+" == false){"
                        + " return false;}"
                        + " if (this == obj){"
                        + " return true;}"
                        + " final "+classname+" otherObject = ("+classname+") obj;"
                        + " return new EqualsBuilder().append(this."+uriField+", otherObject."+uriField+").isEquals();} "
                        + "@Override\n" 
                        + " public String toString(){" 
                        + " return this."+uriField+" ;} ";
        return source;
    
    }
    
    
}
