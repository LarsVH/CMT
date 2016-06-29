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
import be.ac.vub.wise.cmtserver.blocks.CMTField;
import be.ac.vub.wise.cmtserver.blocks.Event;
import be.ac.vub.wise.cmtserver.blocks.Fact;
import be.ac.vub.wise.cmtserver.blocks.FactType;
import be.ac.vub.wise.cmtserver.blocks.Function;
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
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
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
    // Overload for importer
    public void registerFactClass(FactType factType){
        JSONObject jFactType = Converter.fromFactTypeToJSON(factType);
        generateSourceAndCompileFactClass(jFactType);
        CMTDelegator.get().registerFactType(factType);
    }
    
    // Creates *.java file to be inserted in Drools (LvH)
    // P.e. Person, Location
    public void registerFactClass(JSONObject json) {
        generateSourceAndCompileFactClass(json);
        FactType type = Converter.fromJSONtoFactTypeFact(json);
        CMTDelegator.get().registerFactType(type);  // = Put FactType in db
    }

    private void generateSourceAndCompileFactClass(JSONObject json) throws JSONException {
        if (!json.getString("className").contains("java")) {
            String className = HelperClass.toUppercaseFirstLetter(json.getString("className"));
            String uriField = json.getString("uriField");
            JSONArray arrFields = json.getJSONArray("fields");
            if (checkUriField(uriField, arrFields)) {
                String source = "package " + packageFacts + "; "
                        + "import " + packageRmi + "UriFactType; import org.kie.api.definition.type.Role; import org.kie.api.definition.type.Role.Type; "
                        + "import java.io.Serializable; import " + packageRmi + "IFactType; "
                        + "import org.apache.commons.lang3.builder.EqualsBuilder; import org.apache.commons.lang3.builder.HashCodeBuilder; "
                        + "@Role(Type.FACT) @UriFactType(id = \"" + uriField + "\") "
                        + "public class " + className + " implements IFactType, Serializable { ";
                for (int i = 0; i < arrFields.length(); i++) {
                    JSONObject ob = arrFields.getJSONObject(i);
                    String type = ob.getString("fieldType");
                    String fieldName = ob.getString("fieldName");

                    if (!type.contains("java")) {
                        String[] splitLastPoint = type.split("\\.");
                        int z = splitLastPoint.length;
                        String simpleClassName = type;
                        if (z > 0) {
                            simpleClassName = splitLastPoint[z - 1];
                        }
                        if (checkTypeClassPath(simpleClassName)) {
                            //String okType = StringUtils.capitalize(type);
                            source = addSetterGetters(source, fieldName, Constants.PACKAGEFACTS + "." + simpleClassName);
                        }
                    } else {
                        source = addSetterGetters(source, fieldName, type);
                    }
                }
                String capClassName = StringUtils.capitalize(className);
                source += getStringEqualsEtc(uriField, capClassName) + "}";
                HelperClass.compile(source, className, Constants.PACKAGEFACTSSLASH);
            }
        }
    }
    
    /**
     * Strategie: zie addFieldsToEventType
     * @param type
     * @param fields 
     */
    public void addFieldsToFactTypeFact(FactType type, ArrayList<CMTField> fields){
        CMTDelegator delegator = CMTDelegator.get();
        String className = type.getClassName();
        
        System.out.println("DEBUG -- CMTCore -- addFieldsToFactTypeFact "
                + "-- #fields to add: " + fields.size() + " -- FactType: " + type.getClassName());
        
        // 1. SQL: Add fields to the database (needed in step 4)
        //----------------------------------------------------------------------
        delegator.addFactTypeFields(type, fields);     
        
        // 2. Remove all facts of a factType from Drools
        //----------------------------------------------------------------------
        //droolsRemoveFactsOfFactType(type);
        
        // 3. Remove *.java and *.class file of FactTypeEvent
        //----------------------------------------------------------------------
        File pathJavaSource = new File(Constants.JAVAFILEPATH
                + Constants.PACKAGEFACTSSLASH + File.separator + className + ".java");
        File pathClassSource = new File(Constants.CLASSPATH
                + Constants.PACKAGEFACTSSLASH + File.separator + className + ".class");
        boolean deleted = false;
        if (pathJavaSource.exists()) {
            deleted = pathJavaSource.delete();
        }
        if (pathClassSource.exists()) {
            deleted = pathClassSource.delete() && deleted;
        }
        if (!deleted) {
            System.out.println("CMTCORE>>>> addFieldsToFactTypeFact: " + className + " -- NOT DELETED!!!");
        }
        
        // 4. Retrieve FactType (with added fields from db) and compile
        //----------------------------------------------------------------------
        FactType updatedFactType = delegator.getFactTypeWithName(className);
        System.out.println("CMTCORE>>>> addFieldsToFactTypeFact: \n "
                + "retrieved updated factType from db, #fields: " + updatedFactType.getFields().size());
        JSONObject jUpdatedFactType = Converter.fromFactTypeToJSON(updatedFactType);  // (lowpriority) fix dat sourcegenerator werkt met FactType ipv JSON
        generateSourceAndCompileFactClass(jUpdatedFactType);
        
        // 5. Re-add Facts to Drools
        //----------------------------------------------------------------------
        //droolsAddFactsOfFactType(type);
    }
   
    public JSONObject addFact(JSONObject json){
        IFactType fact = Converter.fromJSONFactObjectToObject(json, false);
        
        CMTDelegator.get().addFact(fact);
        JSONObject resultFact = Converter.fromFactToJSON(Converter.fromObjectToFactInstance(fact));
        return resultFact;
    }
    
    // Overload voor default CMT 
    public JSONObject addFactInFactFormat(JSONObject json){ 
            Fact fact = Converter.fromJSONtoFact(json);
        addFactInFactFormat(fact);
        return json;
    }

    public void addFactInFactFormat(Fact fact) {
        if(!CMTDelegator.get().getDbComponentVersion().equals("SQL")){
            IFactType obj = Converter.fromFactToObject(fact);
            if(obj!=null)
                CMTDelegator.get().addFact(obj);
        }else{  // SQL
            CMTDelegator.get().addFactInFactFrom(fact);
        }
    }
    
    
    
    // input JSON {"className":<name>, "extends":<time or activity>, "activityCustom": <boolean> (if time == false),
    //"uriField":<fieldname>, "varList":[<list of strings>{"var":<label>}], "varFormat":<format>, "fields":[{"fieldName":<name>, "type":<simpleClassName>}, ... ]}    
    public void registerEventClass(JSONObject json) {
        System.out.println(" json registerevent -- " + json);
        if (generateSourceAndCompileEventClass(json)) {
            FactType type = Converter.fromJSONtoFactTypeEvent(json);
            CMTDelegator.get().registerEventType(type); // = put Event in database
        }
    }
    // Overload for importer
    public void registerEventClass(FactType eventType){
        System.out.println(">>> CMTCORE -- RegisterEventClass: " + eventType.getClassName());
        JSONObject jEventType = Converter.fromFactTypeToJSON(eventType);
        Boolean sourceCompiled =  generateSourceAndCompileEventClass(jEventType);
        if(!sourceCompiled)
            System.out.println(">>> CMTCORE -- RegisterEventClass  -- !! Source not generated/compiled!!");
        else {
            CMTDelegator.get().registerEventType(eventType);
        }
    }
    
    // Overload for "registerEventClass"
    private boolean generateSourceAndCompileEventClass(JSONObject json){
        return generateSourceAndCompileEventClass(json, false);
    }    
    /**
     * @param json: Event in JSON format to generate source/compile for
     * @param merging: true when using this function for merging (merged FactType is already in db),
     * False otherwise (creating brand new EventClass)
     * @return (for "registerEventClass": true if both IF-statements passed
     * @throws JSONException 
     */
    private boolean generateSourceAndCompileEventClass(JSONObject json, boolean merging) throws JSONException {
        System.out.println("CMTCore>>>> generateSourceAndCompileEventClass -- IN, merging=" + merging);
        String className = HelperClass.toUppercaseFirstLetter(json.getString("className"));
        if (CMTDelegator.get().getFactTypeWithName(className) == null || merging) { // In case of merging, factType is already in db (TO REFACTOR)
            String typeEvent = json.getString("type");
            String extendsClass = "";
            boolean custom = false;
            boolean isActivity = false;
            switch (typeEvent) {
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
            if (checkUriField(uriField, arrFields) || uriField.isEmpty()) {
                String source = "package " + Constants.PACKAGEEVENTS + ";"
                        + "import " + Constants.PACKAGEBLOCKS + "UriFactType; import org.kie.api.definition.type.Role; import org.kie.api.definition.type.Role.Type; "
                        + "import java.io.Serializable; import " + Constants.PACKAGEBLOCKS + "IFactType; "
                        + "import org.apache.commons.lang3.builder.EqualsBuilder; import org.apache.commons.lang3.builder.HashCodeBuilder; "
                        + "import be.ac.vub.wise.cmtserver.blocks.EventVariables;  import be.ac.vub.wise.cmtserver.blocks.Time; import be.ac.vub.wise.cmtserver.blocks.Activity;"
                        + "@Role(Type.EVENT) @UriFactType(id = \"" + uriField + "\") ";

                if (varList.length() == 0) { // then format -- todo add exception if both are empty
                    if (varFormat.isEmpty()) {
                        source += "@EventVariables(list = \"\", format=\"\")";
                    } else {
                        source += "@EventVariables(list = \"\", format=\"format\")";
                    }
                } else {
                    source += "@EventVariables(list = \"list\", format=\"\")";
                }

                source += " public class " + className + " extends " + extendsClass + " { ";

                if (varList.length() == 0) {
                    if (varFormat.isEmpty()) {
                        source += " public " + className + "(){"; // constructor

                    } else {
                        source += " java.lang.String format = \"\"; "
                                + " public " + className + "(){" // constructor
                                + " this.format = \"" + varFormat + "\"; ";
                    }
                    if (isActivity) {
                        source += " super.setCustom(" + custom + "); } ";
                    } else {
                        source += "}";
                    }
                } else {
                    source += " public java.util.LinkedList<String> list = null; "
                            + " public " + className + "(){" // constructor
                            + " this.list = new java.util.LinkedList<String>(); ";
                    if (isActivity) {
                        source += " super.setCustom(" + custom + "); ";
                    }
                    for (int i = 0; i < varList.length(); i++) {
                        JSONObject ob = varList.getJSONObject(i);
                        String var = ob.getString("var");
                        source += " this.list.add(\"" + var + "\"); "; //populate list
                    }

                    source += "}";
                }

                System.out.println(" arrFields length " + arrFields.length());
                for (int i = 0; i < arrFields.length(); i++) {
                    JSONObject ob = arrFields.getJSONObject(i);
                    String type = ob.getString("fieldType");
                    String fieldName = ob.getString("fieldName");
                    if (!type.contains("java")) {
                        String[] splitLastPoint = type.split("\\.");
                        int z = splitLastPoint.length;
                        String simpleClassName = type;
                        if (z > 0) {
                            simpleClassName = splitLastPoint[z - 1];
                        }
                        if (checkTypeClassPath(simpleClassName)) {
                            //String okType = StringUtils.capitalize(type);
                            source = addSetterGetters(source, fieldName, Constants.PACKAGEFACTS + "." + simpleClassName);
                        }
                    } else {
                        source = addSetterGetters(source, fieldName, type);
                    }
                }

                source += "}";
                HelperClass.compile(source, className, Constants.PACKAGEEVENTSSLASH);             

                // For "registerEventClass" -> "registerEventType" only allowed
                //when this if clause is reached
                return true;    
            }
        }
        return false;
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
    
     /**
     * Strategie: . 
     * 1. SQL: Voeg gewoon de extra fields to in SQL (FactType blijft bestaan (in SQL))
     * 2. Drools: verwijder alle FactTypes uit Drools ---> CHECK MET SANDRA OF DIT WEL NODIG IS
     * 3. Verwijder facttypename.java/.class files 
     * 4. Haal nieuw FactType uit de DB (zie 1) en hercompileer met de compiler
     * 5. Voeg Facts terug toe aan Drools --> ---> CHECK MET SANDRA OF DIT WEL NODIG IS
     * @param type
     * @param fields
     */
    public void addFieldsToEventType(FactType type, ArrayList<CMTField> fields) {
        CMTDelegator delegator = CMTDelegator.get();
        String className = type.getClassName();
        
        // 1. SQL: Add fields to the database (needed in step 4)
        //----------------------------------------------------------------------
        delegator.addEventTypeFields(type, fields);        
        
        // 2. Remove all facts of a factType from Drools
        //----------------------------------------------------------------------
       // droolsRemoveFactsOfFactType(type);    // normaal gezien niet nodig

        // 3. Remove *.java and *.class file of FactTypeEvent
        //----------------------------------------------------------------------
        File pathJavaSource = new File(Constants.JAVAFILEPATH
                + Constants.PACKAGEEVENTSSLASH + File.separator + className + ".java");
        File pathClassSource = new File(Constants.CLASSPATH
                + Constants.PACKAGEEVENTSSLASH + File.separator + className + ".class");
        boolean deleted = false;
        if (pathJavaSource.exists()) {
            deleted = pathJavaSource.delete();
        }
        if (pathClassSource.exists()) {
            deleted = pathClassSource.delete() && deleted;
        }
        if (!deleted) {
            System.out.println("CMTCORE>>>> addFieldsToFactTypeEvent: " + className + " -- NOT DELETED!!!");
        }
        
        // 4. Retrieve FactType (with added fields from db) and compile
        //----------------------------------------------------------------------
        FactType updatedFactType = delegator.getFactTypeWithName(className);
        System.out.println("CMTCORE>>>> addFieldsToFactTypeEvent: \n "
                + "retrieved updated factType from db, #fields: " + updatedFactType.getFields().size());
        JSONObject jUpdatedFactType = Converter.fromFactTypeToJSON(updatedFactType);  // (lowpriority) fix dat sourcegenerator werkt met FactType ipv JSON
        generateSourceAndCompileEventClass(jUpdatedFactType, true);
        
        // 5. Re-add Facts to Drools
        //----------------------------------------------------------------------
        //droolsAddFactsOfFactType(type);   // normaal gezien niet nodig
    }

    private void  droolsRemoveFactsOfFactType(FactType type) {
        CMTDelegator delegator = CMTDelegator.get();
        String className = type.getClassName();
        HashSet<Fact> facts = delegator.getFactsInFactVersionWithType(className);
        System.out.println("CMTCORE>>>> removeFactTypeDrools: 1. found " +
                facts.size() + "facts");
        DroolsComponent drools = DroolsComponent.getDroolsComponent();
        for (Fact fact : facts) {
            drools.removeFact(fact);
        }
    }
    
    private void droolsAddFactsOfFactType(FactType type){
        CMTDelegator delegator = CMTDelegator.get();
        DroolsComponent drools = DroolsComponent.getDroolsComponent();
        String className = type.getClassName();
        HashSet<Fact> facts = delegator.getFactsInFactVersionWithType(className);
        for(Fact fact: facts){
            drools.addFact(fact);
        }
    }
    
    public void registerFunctionClass(JSONObject json) {

        String className = json.getString("encapClass");
        JSONArray mets = json.getJSONArray("methods");
        ArrayList<Function> funclist = new ArrayList<Function>();
        String source = "package " + Constants.PACKAGEFUNCTIONS + ";"
                + " import be.ac.vub.wise.cmtserver.blocks.IFunctionClass; import be.ac.vub.wise.cmtserver.blocks.Parameters; import java.io.Serializable; "
                + " public class " + className + " implements IFunctionClass, Serializable{ ";

        for (int i = 0; i < mets.length(); i++) {
            String method = "";
            JSONObject ob = mets.getJSONObject(i);
            Function fu = Converter.fromJSONtoFunction(ob);
            // fu.setEncapClass(className);
            funclist.add(fu);
            String methodName = ob.getString("methodName");
            String methodBody = ob.getString("methodBody");
            JSONArray arrPars = ob.getJSONArray("pars");

            fu.setName(methodName);
            method += "@Parameters(parameters = \"";
            for (int b = 0; b < arrPars.length(); b++) {
                JSONObject objPar = getParIndex(b, arrPars);
                String namePar = objPar.getString("parName");
                method += namePar + " ";
            }
            
            method += "\") ";
            method += "public static boolean " + methodName + " (";
            for (int a = 0; a < arrPars.length(); a++) {
                JSONObject obPar = getParIndex(a, arrPars);
                String parName = obPar.getString("parName");
                String parType = obPar.getString("parType");
                if (!parType.contains("java")) {
                    String[] splitLastPoint = parType.split("\\.");
                    int z = splitLastPoint.length;
                    String simpleClassName = parType;
                    String finalBinaryTypeName = "";
                    if (z > 0) {
                        simpleClassName = splitLastPoint[z - 1];
                    }
                    if (checkTypeClassPath(simpleClassName)) {
                        if (isEvent(simpleClassName)) {
                            finalBinaryTypeName = Constants.PACKAGEEVENTS + "." + simpleClassName;
                        } else {
                            finalBinaryTypeName = Constants.PACKAGEFACTS + "." + simpleClassName;
                        }
                        method += " " + finalBinaryTypeName + " " + parName;
                        if (a != arrPars.length() - 1) {
                            method += ",";
                        }
                    }
                } else {
                    method += " " + parType + " " + parName;
                    if (a != arrPars.length() - 1) {
                        method += ",";
                    }
                }
            }
            method += " ){" + methodBody + "} ";
            source += "  " + method + "  ";
            fu.setBody(method);
            CMTDelegator.get().addFunction(fu);
        }
        source += "}";
        HelperClass.compile(source, className, Constants.PACKAGEFUNCTIONSSLASH);
        Class<?> cl;
        try {
            URLClassLoader l = new URLClassLoader(new URL[]{new File(Constants.CLASSPATH+"/").toURI().toURL()}, Thread.currentThread().getContextClassLoader());
              // l.loadClass(Constants.PACKAGERMI + "IFunctionClass");
            cl = Class.forName(Constants.PACKAGEFUNCTIONS + "."+ className, true, l);
            Object object = cl.newInstance();
            //  IFunctionClass func = (IFunctionClass) object;
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
    
    //TODO (from Sandra): Deze methode roep je aan als je een function importeerd en de gebruiker hem nog niet heeft:
public void addFunction(JSONObject json){
        String className = json.getString("encapClass");
        String methodName = json.getString("methodName");
        Function fu = Converter.fromJSONtoFunction(json);
        fu.setEncapClass("C"+methodName);
        String source = "package "+ Constants.PACKAGEFUNCTIONS+ ";"
                        + " import be.ac.vub.wise.cmtserver.blocks.IFunctionClass; import be.ac.vub.wise.cmtserver.blocks.Parameters; import java.io.Serializable; "
                        + " public class " + "C"+methodName + " implements IFunctionClass, Serializable{ ";
        source += fu.getBody();
        source += "}";
        HelperClass.compile(source, className, Constants.PACKAGEFUNCTIONSSLASH);
         Class<?> cl;
        try {
            URLClassLoader l = new URLClassLoader(new URL[]{new File(Constants.CLASSPATH+"\\").toURI().toURL()}, Thread.currentThread().getContextClassLoader());
              // l.loadClass(Constants.PACKAGERMI + "IFunctionClass");
            cl = Class.forName(Constants.PACKAGEFUNCTIONS + "."+ className, true, l);        
        Object object = cl.newInstance();
      //  IFunctionClass func = (IFunctionClass) object;
        CMTDelegator.get().addFunction(fu);
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
       addTemplateHA(temp);
   }
   // Overload for importer
   public void addTemplateHA(TemplateHA tmpl){
       System.out.println("DEBUG -- CMTCore -- ResultJSON of template: \n "+ Converter.fromTemplateToJSON(tmpl));
       
       CMTDelegator.get().addTemplate(tmpl);
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
            Action action = Converter.fromJSONtoAction(json);
            System.out.println(" ----- " + CMTDelegator.get().getAction(className));
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
