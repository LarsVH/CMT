/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ac.vub.wise.cmtserver.util;

import be.ac.vub.wise.cmtserver.blocks.Action;
import be.ac.vub.wise.cmtserver.blocks.ActionClient;
import be.ac.vub.wise.cmtserver.blocks.ActionField;
import be.ac.vub.wise.cmtserver.blocks.ActionFieldAnno;
import be.ac.vub.wise.cmtserver.blocks.Activity;
import be.ac.vub.wise.cmtserver.blocks.Binding;
import be.ac.vub.wise.cmtserver.blocks.BindingIF;
import be.ac.vub.wise.cmtserver.blocks.BindingInputFact;
import be.ac.vub.wise.cmtserver.blocks.BindingInputField;
import be.ac.vub.wise.cmtserver.blocks.BindingOutput;
import be.ac.vub.wise.cmtserver.blocks.BindingParameter;
import be.ac.vub.wise.cmtserver.blocks.CMTField;
import be.ac.vub.wise.cmtserver.blocks.CMTParameter;
import be.ac.vub.wise.cmtserver.blocks.Event;
import be.ac.vub.wise.cmtserver.blocks.EventInput;
import be.ac.vub.wise.cmtserver.blocks.Fact;
import be.ac.vub.wise.cmtserver.blocks.FactType;
import be.ac.vub.wise.cmtserver.blocks.FieldValueLimitation;
import be.ac.vub.wise.cmtserver.blocks.Function;
import be.ac.vub.wise.cmtserver.blocks.IFBlock;
import be.ac.vub.wise.cmtserver.blocks.IFactType;
import be.ac.vub.wise.cmtserver.blocks.Operator;
import be.ac.vub.wise.cmtserver.blocks.OutputHA;
import be.ac.vub.wise.cmtserver.blocks.Rule;
import be.ac.vub.wise.cmtserver.blocks.Template;
import be.ac.vub.wise.cmtserver.blocks.TemplateActions;
import be.ac.vub.wise.cmtserver.blocks.TemplateHA;
import be.ac.vub.wise.cmtserver.core.CMTDelegator;
import be.ac.vub.wise.cmtserver.restfull.CMTCore;
import be.ac.vub.wise.cmtserver.sharing.IFactTypeSuggestions;
import be.ac.vub.wise.cmtserver.sharing.TemplateSuggestions;
import com.google.gson.Gson;
import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author Sandra
 */
public class Converter {
    /*
    shortcut when client defines new facttype in cmt
    */
    public static JSONObject fromFactClassToJSON(Class<?> factClass, String uriField){
        JSONObject result = new JSONObject();
        try {
            result.put("className", factClass.getSimpleName());
            result.put("uriField", uriField);
            JSONArray arr = new JSONArray();
            Field[] fields = factClass.getDeclaredFields();
            for(Field field : fields){
                String name = field.getName();
                String type = field.getType().getName();
                JSONObject ob = new JSONObject();
                ob.put("fieldName", name);
                ob.put("fieldType", type);
                arr.put(ob);
            }
            result.put("fields", arr);
            
        } catch (JSONException ex) {
            Logger.getLogger(Converter.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result;
    }
    /*
    short cut for sending facts and events to cmt of registered facttypes
    */
    // JSON {"className":< simple name>, "object":{...}} // also ok for events
    public static JSONObject fromFactObjectToJSON(Object object){
        JSONObject result = new JSONObject();
        String type = object.getClass().getSimpleName();
        Gson gson = new Gson();
        String jsonob = gson.toJson(object);
        try {
            JSONObject ob = new JSONObject(jsonob);
            result.put("className", type);
            result.put("object", ob);
        } catch (JSONException ex) {
            Logger.getLogger(Converter.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result;
    }
    
    
    public static Event fromObjectToEvent(Object object, boolean isActivity, boolean isCustom, String uriField, ArrayList<String> varList, String varFormat){
        Event event = new Event();
        event.setClassName(object.getClass().getSimpleName());
        event.setUriField(uriField);
        if(isActivity){
            event.setExtend("activity");
        }else{
            event.setExtend("time");
        }
        event.setIsCustom(isCustom);
        event.setVarList(varList);
        event.setVarFormat(varFormat);
        ArrayList<CMTField> fieldsEvent = new ArrayList<>();
        Field[] fields = object.getClass().getDeclaredFields();
        for(Field f : fields){
            CMTField field = new CMTField(f.getName(), f.getType().getName());
            try {
                Object val = f.get(object);
                field.setValue(val);
                if(f.getName().equals(uriField)){
                    event.setValueUriField(val.toString());
                }
            } catch (IllegalArgumentException ex) {
                    Logger.getLogger(Converter.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IllegalAccessException ex) {
                    Logger.getLogger(Converter.class.getName()).log(Level.SEVERE, null, ex);
            }
            fieldsEvent.add(field);
        }
   
        event.setFields(fieldsEvent);
        return event;
    }
    
    
    public static JSONObject fromEventToJSON(Event event){
        if(event !=null){
            JSONObject result = new JSONObject();
            String className = event.getClassName();
            String extend = "";
            boolean isActivity = false;
                if(event.getExtend().equals("activity")){
                    isActivity = true;
                }
            if(isActivity){
                extend = "be.ac.vub.wise.cmtserver.rmi.Activity";
            }else{
                extend = "be.ac.vub.wise.cmtserver.rmi.Time";
            }
            String varFormat = event.getVarFormat();
            JSONArray arrList = new JSONArray();
            for(String st : event.getVarList()){
                JSONObject ob = new JSONObject();
                try {
                    ob.put("var", st);
                } catch (JSONException ex) {
                    Logger.getLogger(Converter.class.getName()).log(Level.SEVERE, null, ex);
                }
                arrList.put(ob);
            }
            JSONArray arrFields = new JSONArray();
            ArrayList<CMTField> fields = event.getFields();
            for(CMTField f : fields){
                JSONObject ob = fromCMTFieldToJSON(f);
                arrFields.put(ob);
            }
            try {
                result.put("className", className);
                result.put("extends", extend);
                result.put("activityCustom", event.isCustom());
                result.put("uriField", event.getUriField());
                result.put("varList", arrList);
                result.put("varFormat", varFormat);
                result.put("fields", arrFields);
            } catch (JSONException ex) {
                Logger.getLogger(Converter.class.getName()).log(Level.SEVERE, null, ex);
            }
            return result;
        }
        return null;
    }
    //input {"className":<simple className>, "fields":[{"fieldName": <>, "varList":<[{"var":<string>}, ...]>, "varFormat":<format>} field type can only be string!]}
    public static JSONObject fromActionToJSON(ActionClient action){
        JSONObject result = new JSONObject();
        try {
            result.put("className", action.getName());
            ArrayList<ActionField> fields = action.getFields();
            JSONArray arr = new JSONArray();
            for(ActionField field : fields){
                JSONObject fieldO = new JSONObject();
                fieldO.put("fieldName", field.getName());
                if(field.getVarList()!=null){
                    ArrayList<String> varList = field.getVarList();
                    JSONArray varListO = new JSONArray();
                    for(String var : varList){
                        JSONObject varO = new JSONObject();
                        varO.put("var", var);
                        varListO.put(varO);
                    }
                    fieldO.put("varList", varListO); 
                }else{
                    fieldO.put("varList", new JSONArray()); 
                }
                fieldO.put("varFormat", field.getFormat());
                fieldO.put("value", field.getValue());
                arr.put(fieldO);
            }
            result.put("fields", arr);
        } catch (JSONException ex) {
            Logger.getLogger(Converter.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
        return result;
    }
    
    public static Fact fromJSONtoFact(JSONObject json){
        try {
            
            String clName = json.getString("className");
            String uriField = json.getString("uriField");
          
            // if urifield is empty look up 
            if(uriField.isEmpty()){
                FactType type = CMTDelegator.get().getFactTypeWithName(clName);
                if(type == null){
                    uriField = "";
                }
                else {                
                uriField = type.getUriField();
                }
            }
            
            Fact factObject = new Fact(json.getString("className"), uriField);
            factObject.setId(json.getInt("sqlId"));
            ArrayList<CMTField> fields = new ArrayList<>();
            JSONArray arrfields = json.getJSONArray("fields");
            for(int i=0; i< arrfields.length();i++){
                JSONObject jsonField = arrfields.getJSONObject(i);
                
                fields.add(fromJSONtoCMTField(jsonField));
            }
            factObject.setFields(fields);
            if(!CMTDelegator.get().getDbComponentVersion().equals("SQL")){
                IFactType db = CMTDelegator.get().getFact(clName, uriField, factObject.getUriValue());

                if(db !=null){
                    Fact toret = fromObjectToFactInstance(db);
                    return toret;
                }
            }
            return factObject;
        
        } catch (JSONException ex) {
           Logger.getLogger(Converter.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    
    
    public static FactType fromJSONtoFactTypeEvent(JSONObject object){
        
        try {
            String name = object.getString("className"); // simple name
       
            FactType dbType = CMTDelegator.get().getFactTypeWithName(name);
           
            if(dbType ==null){
           // String name = object.getString("className"); // simple name
            JSONArray arrList = object.getJSONArray("varList");
            ArrayList<String> varList = new ArrayList<String>();
            for(int b=0; b<arrList.length(); b++){
                JSONObject varO = arrList.getJSONObject(b);
                String var = varO.getString("var");
                varList.add(var);
            }
            String varFormat = object.getString("varFormat");
            String uriField = object.getString("uriField");
            JSONArray arrFields = object.getJSONArray("fields");
            ArrayList<CMTField> fields = new ArrayList<CMTField>();
            for(int a=0; a<arrFields.length();a++){
                JSONObject fieldO = arrFields.getJSONObject(a);
                fields.add(fromJSONtoCMTField(fieldO));
            }
            String extend = object.getString("extends");
            String typeClass = "";
            switch(extend){
                case Constants.EXTENDSACTIVITY:
                    typeClass = "activity";
                   break;
                case Constants.EXTENDSTIME:
                    typeClass = "time";
                    break;
            }
            FactType facttype = new FactType(name, typeClass, uriField, fields);
            facttype.setCategory(object.getString("category"));
            facttype.setVarFormat(varFormat);
            facttype.setVarList(varList);
            if(typeClass.equals("activity")){
                facttype.setIsCustom(object.getBoolean("isCustom"));
            }
            return facttype;
            }
            return dbType;
        } catch (JSONException ex) {
            Logger.getLogger(Converter.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    public static FactType fromJSONtoFactTypeFact(JSONObject object){
      
        try {
            String name = object.getString("className"); // simple name
       
            FactType dbType = CMTDelegator.get().getFactTypeWithName(name);
           
            if(dbType ==null){
            
            ArrayList<String> varList = new ArrayList<String>(); // is empty when fact type
            
            String varFormat = "";
            String uriField = object.getString("uriField");
            JSONArray arrFields = object.getJSONArray("fields");
            ArrayList<CMTField> fields = new ArrayList<CMTField>();
            for(int a=0; a<arrFields.length();a++){
                JSONObject fieldO = arrFields.getJSONObject(a);
                String fieldName = fieldO.getString("fieldName");
                String fieldType = fieldO.getString("fieldType");
                fields.add(new CMTField(fieldName, fieldType));
            }
            FactType facttype = new FactType(name, "fact", uriField, fields);
            facttype.setVarFormat(varFormat);
            facttype.setVarList(varList);
            facttype.setCategory(object.getString("category"));
            return facttype; 
            }
           
             return dbType;
        } catch (JSONException ex) {
            Logger.getLogger(Converter.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    
    public static Event fromJSONtoEvent(String className, JSONObject object, FactType facttype){
            
            Event event = new Event();
            event.setClassName(className);
            event.setExtend(facttype.getType());
            event.setIsCustom(facttype.isIsCustom());
            event.setUriField("");
            event.setVarList(facttype.getVarList());
            event.setVarFormat(facttype.getVarFormat());
            
            return event;
     
    }

    public static Function fromJSONtoFunction(JSONObject object){
        try {
            Function func = new Function();
            func.setName(object.getString("methodName"));
            func.setEncapClass(object.getString("encapClass"));
            func.setSql_id(object.getInt("sqlId"));
            ArrayList<CMTParameter> pars = new ArrayList<>();
            JSONArray arrPars = object.getJSONArray("pars");
            HashMap<Integer,CMTParameter> indexFunctions = new HashMap<Integer, CMTParameter>();
            for(int a= 0; a<arrPars.length(); a++){
                JSONObject parO = arrPars.getJSONObject(a);
                CMTParameter p = new CMTParameter();
                p.setParName(parO.getString("parName"));
                p.setType(parO.getString("parType"));
                p.setPosition(parO.getInt("index"));  
                p.setSql_id(parO.getInt("sqlId"));
                pars.add(p);
            }
            
            func.setParameters(pars);
            return func;
        } catch (JSONException ex) {
            Logger.getLogger(Converter.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    public static JSONObject fromFunctionToJSON(Function func){
        if(func != null){
            JSONObject result = new JSONObject();
            try {
                result.put("encapClass", func.getEncapClass());
                result.put("methodName", func.getName());
                result.put("sqlId", func.getSql_id());
                ArrayList<CMTParameter> parameters = func.getParameters();
                JSONArray arrPars = new JSONArray();
                for(CMTParameter p : parameters){
                    JSONObject parObj = new JSONObject();
                    parObj.put("parName", p.getParName());
                    parObj.put("parType", p.getType());
                    parObj.put("index", p.getPosition());
                    parObj.put("sqlId", p.getSql_id());
                    arrPars.put(parObj);
                }
                result.put("pars", arrPars);
            } catch (JSONException ex) {
                Logger.getLogger(Converter.class.getName()).log(Level.SEVERE, null, ex);
            }
            return result;
        }
        return null;
    }

    public static Rule fromJSONtoRule(JSONObject object){
        try {
            Rule rule = new Rule();
            rule.setName(object.getString("name"));
            rule.setDrlRule(object.getString("drlRule"));
            rule.setSql_id(object.getInt("sqlId"));
            return rule;
        } catch (JSONException ex) {
            Logger.getLogger(Converter.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    public static TemplateActions fromJSONtoTemplateAction(JSONObject object){
        TemplateActions temp = new TemplateActions();
        try{
            JSONArray arrActions = object.getJSONArray("actions");
            for(int i=0; i<arrActions.length();i++){
                JSONObject obAct = arrActions.getJSONObject(i);
                ActionClient act = fromJSONtoAction(obAct);
                temp.addAction(act);
            }
            String ruleName = object.getString("ruleName");
            temp.setRuleName(ruleName);
        } catch (JSONException ex) {
            Logger.getLogger(Converter.class.getName()).log(Level.SEVERE, null, ex);
        }
        temp = (TemplateActions) fillTemplateLS(temp, object);
        return temp;
    }
    
    public static TemplateHA fromJSONtoTemplateHA(JSONObject object){ // convert manually 
        TemplateHA temp = new TemplateHA();
        try{
            temp.setOutput(fromJSONtoOutputHA(object.getJSONObject("output")));
        } catch (JSONException ex) {
            Logger.getLogger(Converter.class.getName()).log(Level.SEVERE, null, ex);
        }
        temp = (TemplateHA) fillTemplateLS(temp, object);
        return temp;
    }
    
    public static ActionClient fromJSONtoAction(JSONObject object){
        try {
            String className = object.getString("className");
            Action actionObject = CMTDelegator.get().getAction(className);
            if(actionObject != null){
                ActionClient actCl = null;
                if(!CMTDelegator.get().getDbComponentVersion().equals("SQL")){
                    actCl = fromActionObjectToActionClient(actionObject);
                }else{
                    actCl = (ActionClient) actionObject;
                }
                
                JSONArray arrFields = object.getJSONArray("fields");
                System.out.println(" -------------------------------- fields arr json ");
                for(int i=0;i<arrFields.length();i++){
                    JSONObject jsonField = arrFields.getJSONObject(i);
                    for(ActionField f : actCl.getFields()){
                        if(f.getName().equals(jsonField.getString("fieldName"))){
                            f.setValue(jsonField.getString("value"));
                        }
                    }
                }
                return actCl;
            }else{
                if(!CMTDelegator.get().getDbComponentVersion().equals("SQL")){
                    // TODO
                }else{
                    
                    ArrayList<ActionField> fi = new ArrayList<>();
                    JSONArray arrFields = object.getJSONArray("fields");
                    System.out.println(" -------------------------------- fields arr json " + arrFields.length());
                    
                    for(int i=0;i<arrFields.length();i++){
                        JSONObject jsonF = arrFields.getJSONObject(i);
                        ArrayList<String> ops = new ArrayList<>();
                        JSONArray opsjson = jsonF.getJSONArray("varList");
                        for(int ii = 0; ii<opsjson.length();ii++){
                            JSONObject var = opsjson.getJSONObject(ii);
                            ops.add(var.getString("var"));
                        }
                        ActionField field = new ActionField(jsonF.getString("fieldName"), ops, jsonF.getString("varFormat"));
                        fi.add(field);
                    }
                    System.out.println(" -------------------------------- fields arr fi " + fi.size());
                     ActionClient cl = new ActionClient(className, fi);
                     return cl;
                }
            
            }
        } catch (JSONException ex) {
            Logger.getLogger(Converter.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    public static ActionClient fromActionObjectToActionClient(Action object){
        try{
        ArrayList<ActionField> actFields = new ArrayList();
            System.out.println("int " + object.getClass().getDeclaredFields().length);
            for(Field field : object.getClass().getDeclaredFields()){
                System.out.println(" " + field.getName());
                System.out.println(object.getClass().getDeclaredField("listStatus").get(object).getClass());
               
            }
        for(Field field : object.getClass().getDeclaredFields()){
            System.out.println(field.getName());
            if(field.getAnnotation(ActionFieldAnno.class) != null){
                ActionFieldAnno anno = field.getAnnotation(ActionFieldAnno.class);
                ArrayList<String> varList = new ArrayList<>();
                String format = "";
                if(!anno.list().equals("")){
                    System.out.println(" anno list " + anno.list());
                    ArrayList<String> varL = (ArrayList<String>)object.getClass().getDeclaredField(anno.list()).get(object);
                    varList = varL;
                }
                if(!anno.format().equals("")){
                    format = (String) object.getClass().getDeclaredField(anno.format()).get(object);
                }
              
                
                ActionField actF = new ActionField(field.getName(), varList, format);
                if(field.get(object) != null){
                    actF.setValue(field.get(object).toString());
                }
                actFields.add(actF);
            }
        }
        ActionClient res = new ActionClient(object.getClass().getSimpleName(), actFields);
        return res;
        } catch (NoSuchFieldException ex) {
            Logger.getLogger(Converter.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SecurityException ex) {
            Logger.getLogger(Converter.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(Converter.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(Converter.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    private static String getMethodName(Class<?> className, String method){
        Method[] mets = className.getDeclaredMethods();
        for(Method met : mets){
            if(method.contains(met.getName())){
                return met.getName();
            }
        }
        return "";
    }
    
    private static ArrayList<String> parseJavaFileMethods(String strToParse){
        ArrayList<String> result = new ArrayList<String>();
        ArrayList<Integer> parAnnoPositions = getIndexes("@Parameters", strToParse);
        for(int i = 0; i< parAnnoPositions.size()-1; i++){
            String method = strToParse.substring(parAnnoPositions.get(i), (parAnnoPositions.get(i+1))-2);
            result.add(method);
        }
        String methodLast = strToParse.substring(parAnnoPositions.get(parAnnoPositions.size()-1), strToParse.lastIndexOf("}") -1);
            result.add(methodLast);
        return result;
    }
    private static String parseMethodsForBody(String strToParse){
        int firstBracked = strToParse.indexOf("{");
        int lastIndex = strToParse.lastIndexOf("}");
        return strToParse.substring(firstBracked +1, lastIndex);
    }
    
    private static  ArrayList<Integer> getIndexes(String str, String toParse){
        ArrayList<Integer> publicPositions = new ArrayList<Integer>();
        int first = toParse.indexOf(str); // class
        publicPositions.add(first);
        int stateIndex = first;
        while(stateIndex != -1){
            stateIndex = toParse.indexOf(str, stateIndex +1);
            if(stateIndex != -1){
                publicPositions.add(stateIndex);
            }
        }
        return publicPositions;
    }
   
    public static Template fillTemplateLS(Template temp, JSONObject json){
        try {
            JSONArray jsonIfBlocks = json.getJSONArray("ifblocks");
            JSONArray jsonOperators = json.getJSONArray("operators");
            temp.setName(json.getString("name"));
            temp.setSql_id(json.getInt("sqlId"));
            if(json.has("category")){
            temp.setCategory(json.getString("category"));
            }else{
                temp.setCategory("");
            }
            int stop = jsonIfBlocks.length();
            ArrayList<Operator> ops = new ArrayList<Operator>();
            for(int iii = 0; iii<jsonOperators.length(); iii ++){
                for(int iiii=0; iiii<jsonOperators.length(); iiii++){
                    JSONObject opObj = jsonOperators.getJSONObject(iiii);
                    if(opObj.getInt("index") == iii){
                        Operator op = new Operator();
                        op.setOperator(opObj.getString("operator"));
                        ops.add(op);
                    }
                }
            }
            for(int i=0;i<stop;i++){
                for(int ii=0;ii<jsonIfBlocks.length();ii++){
                    JSONObject obj = jsonIfBlocks.getJSONObject(ii);
                    if(obj.getInt("index") == i){
                        IFBlock block = fromJSONtoIFBlock(obj);
                        temp.addIfBlock(block, ops.get(i));
                    }
                }
            }
            
        } catch (JSONException ex) {
            Logger.getLogger(Converter.class.getName()).log(Level.SEVERE, null, ex);
        }
        return temp;
    }
    
    public static  IFBlock fromJSONtoIFBlock(JSONObject json){
        IFBlock ifBlock = new IFBlock();
        try{
            JSONObject jsonIfBlock = json;
            String typeBlock = jsonIfBlock.getString("typeBlock");
            switch(typeBlock){
                case "function":
                    ifBlock.setFunction(fromJSONtoFunction(jsonIfBlock.getJSONObject("function")));
                    ifBlock.setType(typeBlock);
                    break;
                case "activity":
                    ifBlock.setEvent(fromJSONtoFactTypeEvent(jsonIfBlock.getJSONObject("event")));
                    ifBlock.setType(typeBlock);
                    break;
            }
            JSONArray arrBindings = jsonIfBlock.getJSONArray("bindings");
            LinkedList<Binding> bindings = fromJSONtoListBindings(arrBindings);
            ifBlock.setBindings(bindings);
        } catch (JSONException ex) {
                Logger.getLogger(Converter.class.getName()).log(Level.SEVERE, null, ex);
        }
        return ifBlock;
    }
    
    public static Event fromJSONtoEvent(JSONObject json){
        try{
            Event event = new Event();
            event.setClassName(json.getString("className"));
            event.setExtend(json.getString("extends"));
            event.setIsCustom(json.getBoolean("activityCustom"));
            event.setUriField(json.getString("uriField"));
            event.setVarFormat(json.getString("varFormat"));
            JSONArray arrList = json.getJSONArray("varList");
            JSONArray arrFields = json.getJSONArray("fields");
            ArrayList<String> varList = new ArrayList<String>();
            ArrayList<CMTField> fields = new ArrayList<CMTField>();
            for(int i=0; i<arrList.length();i++){
                JSONObject objList = arrList.getJSONObject(i);
                varList.add(objList.getString("var"));
            }
            for(int i = 0; i<arrFields.length(); i++){
                JSONObject objField = arrFields.getJSONObject(i);
                fields.add(fromJSONtoCMTField(objField));
            }
            event.setFields(fields);
            event.setVarList(varList);
            return event;
        } catch (JSONException ex) {
                Logger.getLogger(Converter.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    public static Activity fromJSONtoActivityInstance(JSONObject json){
        try {
            Class cl = Class.forName(Constants.PACKAGEEVENTS+ "."+HelperClass.toUppercaseFirstLetter(json.getString("className")));
            Object obj = cl.newInstance();
            ArrayList<CMTField> fields = new ArrayList<>();
            JSONArray arrFields = json.getJSONArray("fields");
            Field[] fobj = cl.getDeclaredFields();
            for(int i=0;i<arrFields.length();i++){
                CMTField f = fromJSONtoCMTField(arrFields.getJSONObject(i));
                if(f != null){
                    for(Field objf : fobj){
                        if(objf.getName().equals(f.getName())){
                            objf.set(obj, f.getValue());
                        }
                    }
                }
            }
            if(obj instanceof Activity){
                return (Activity) obj;
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Converter.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            Logger.getLogger(Converter.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(Converter.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    public static CMTField fromJSONtoCMTField(JSONObject json){
        try{
            CMTField field = new CMTField(json.getString("fieldName"), json.getString("fieldType"));
            field.setIsVar(json.getBoolean("input"));
            field.setFormat(json.getString("format"));
            field.setSql_id(json.getInt("sqlId"));
            JSONArray opt = json.getJSONArray("options");
            for(int i =0; i<opt.length(); i++){
                field.addOption(opt.getString(i));
            }
            JSONObject fieldValue = json.getJSONObject("fieldValue");
            System.out.println("  fieldValue json : " + fieldValue);
            
            if(fieldValue.has("fact")){
               
                field.setValue(fromJSONtoFact(fieldValue.getJSONObject("fact")));
            }else{
                if(fieldValue.has("string")){
               
                    field.setValue(fieldValue.getString("string"));
                }else{
                    if(fieldValue.has("gson")){
                    Gson gson = new Gson();
                    field.setValue(gson.fromJson(fieldValue.getString("gson"), Class.forName(fieldValue.getString("className"))));
                    }
                }
            }
            return field;
        } catch (JSONException ex) {
                Logger.getLogger(Converter.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Converter.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    public static JSONObject fromTemplateToJSON(Template temp){
        JSONObject result = new JSONObject();
        
        try {
            LinkedList<IFBlock> ifblocks = temp.getIfBlocks();
            LinkedList<String> operators = temp.getOperators();
            JSONArray arrIfBlocks = new JSONArray();
            for(int i=0; i<ifblocks.size();i++){        // Loop over the IFBlocks
                IFBlock ifblock = ifblocks.get(i);
                JSONObject obj = new JSONObject();
                obj.put("index", i);                    // Assing (volgnummer) index to each block
                if(ifblock.getFunction() != null){      // Dispatch on block type: Function
                    JSONObject func = fromFunctionToJSON(ifblock.getFunction());
                    obj.put("function", func);
                    obj.put("typeBlock", "function");
                }
                if(ifblock.getEvent() != null){         // Dispatch on block type: Event
                    JSONObject event = fromFactTypeToJSON(ifblock.getEvent());
                    obj.put("event", event);
                    obj.put("typeBlock", "activity");
                }
                LinkedList<Binding> bindings = ifblock.getBindings();
                System.out.println("------- in conv " + bindings.size());
                JSONArray arrBindings = fromListBindingsToJSON(bindings);
                obj.put("bindings", arrBindings);
                
                arrIfBlocks.put(obj);
            }
            JSONArray arrOperators = new JSONArray();
            for(int ii=0;ii<operators.size();ii++){
                String op = operators.get(ii);
                JSONObject obj = new JSONObject();
                obj.put("operator", op);
                obj.put("index", ii);
                arrOperators.put(obj);
            }
            result.put("ifblocks", arrIfBlocks);
            result.put("operators", arrOperators);
            result.put("name", temp.getName());
            result.put("category", temp.getCategory());
            result.put("sqlId", temp.getSql_id());
            if(temp.getClass().isAssignableFrom(TemplateHA.class)){ // Determine subclass: TemplateHA or TemplateActions
                result.put("tempType", "TemplateHA");
                result.put("output", fromOutputHAToJSON(((TemplateHA)temp).getOutput())); // Cast to subclass and convert
            }else{
                if(temp.getClass().isAssignableFrom(TemplateActions.class)){
                    result.put("tempType", "TemplateActions");
                    TemplateActions tempAct = (TemplateActions) temp;
                    LinkedList<ActionClient> actions = tempAct.getActions();
                    JSONArray arrActions = new JSONArray();
                    for(ActionClient act : actions){
                        JSONObject actObj = fromActionToJSON(act);
                        arrActions.put(actObj);
                    }
                    result.put("actions", arrActions);
                    result.put("ruleName", tempAct.getRuleName());
                }
            }
        } catch (JSONException ex) {
                Logger.getLogger(Converter.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result;
    }
      private static JSONObject fromBindingParameterToJSON(BindingParameter par){
        JSONObject result = new JSONObject();
        try {
            System.out.println("----------- par type " + par.getClass().getSimpleName());
            if(par instanceof BindingInputFact){
                BindingInputFact bindFact = (BindingInputFact) par;
                result.put("type", "BindingInputFact");
                result.put("indexObj", bindFact.getIndexObj());
                if(bindFact.getInputObject() instanceof Fact){
                    result.put("factId", bindFact.getFactId());
                    result.put("inputObjectType", "fact");
                    result.put("inputObject", fromFactToJSON((Fact)bindFact.getInputObject()) );
                }else{
                    if(bindFact.getInputObject() instanceof FactType){
                    result.put("inputObjectType", "facttype");
                    result.put("inputObject", fromFactTypeToJSON((FactType)bindFact.getInputObject()) );
                    }else{
                        if(bindFact.getInputObject() instanceof EventInput){
                            result.put("inputObjectType", "eventinput");
                            result.put("inputObject", fromEventInputToJSON((EventInput)bindFact.getInputObject()));
                        }
                    }
                }
            }else{
                if(par instanceof BindingInputField){
                    BindingInputField bindFact = (BindingInputField) par;
                    result.put("type", "BindingInputField");
                    result.put("indexObj", bindFact.getIndexObj());
                    if(bindFact.getInputObject() instanceof Fact){
                        result.put("inputObjectType", "fact");
                        result.put("inputObject", fromFactToJSON((Fact)bindFact.getInputObject()) );
                        result.put("factId", bindFact.getFactId());
                    }else{
                        if(bindFact.getInputObject() instanceof FactType){
                        result.put("inputObjectType", "facttype");
                        result.put("inputObject", fromFactTypeToJSON((FactType)bindFact.getInputObject()) );
                    }else{
                        if(bindFact.getInputObject() instanceof EventInput){
                            result.put("inputObjectType", "eventinput");
                            result.put("inputObject", fromEventInputToJSON((EventInput)bindFact.getInputObject()));
                        }
                    }
                    }
                    CMTField field = bindFact.getField();
                    JSONObject objField = fromCMTFieldToJSON(field);
                    result.put("CMTField", objField);
                }else{
                    if(par instanceof BindingIF){
                        BindingIF bind = (BindingIF) par;
                        result.put("type", "BindingIF");
                        result.put("ifParameter", bind.getIfParameter());
                        result.put("indexObj", bind.getIndexObj());
                        return result;
                    }else{
                        if(par instanceof BindingOutput){
                            BindingOutput ou = (BindingOutput)par;
                            result.put("type", "BindingOutput");
                          //  result.put("outputObject", fromOutputHAToJSON(ou.getOutputObj()));
                            result.put("parName", ou.getParameter());
                            result.put("parType", ou.getParType());
                        }
                    }
                }
            }
        } catch (JSONException ex) {
                        Logger.getLogger(Converter.class.getName()).log(Level.SEVERE, null, ex);
                    }
        return result;
    }
    
    public static BindingParameter fromJSONtoBindingParameter(JSONObject json){
        try{
            String type = json.getString("type");
            switch(type){
                case "BindingInputFact":
                    BindingInputFact bindFact = new BindingInputFact();
                    
                    bindFact.setIndexObj(json.getInt("indexObj"));
                    String inputObjectType = json.getString("inputObjectType");
                    if(inputObjectType.equals("fact")){
                        bindFact.setInputObject(fromJSONtoFact(json.getJSONObject("inputObject")));
                       // bindFact.setFactId(json.getString("factId"));
                    }else{
                        if(inputObjectType.equals("facttype")){
                            JSONObject obType = json.getJSONObject("inputObject");
                            if(obType.has("extends")){
                                bindFact.setInputObject(fromJSONtoFactTypeEvent(obType));
                            }else{
                                bindFact.setInputObject(fromJSONtoFactTypeFact(obType));
                            }
                        }else{
                            if(inputObjectType.equals("eventinput")){
                                bindFact.setInputObject(fromJSONtoEventInput(json.getJSONObject("inputObject")));
                            }
                        }
                    }
                    return bindFact;
                case "BindingInputField":
                    BindingInputField bindField = new BindingInputField();
                    bindField.setIndexObj(json.getInt("indexObj"));
                    String inputObjectTypeField = json.getString("inputObjectType");
                    if(inputObjectTypeField.equals("fact")){
                        bindField.setFactId(json.getString("factId"));
                        bindField.setInputObject(fromJSONtoFact(json.getJSONObject("inputObject")));
                    }else{
                        if(inputObjectTypeField.equals("facttype")){
                            JSONObject obType = json.getJSONObject("inputObject");
                            if(obType.has("extends")){
                                bindField.setInputObject(fromJSONtoFactTypeEvent(obType));
                            }else{
                                
                                bindField.setInputObject(fromJSONtoFactTypeFact(obType));
                            }
                        }else{
                            if(inputObjectTypeField.equals("eventinput")){
                                bindField.setInputObject(fromJSONtoEventInput(json.getJSONObject("inputObject")));
                            }
                        }
                    }
                    bindField.setField(fromJSONtoCMTField(json.getJSONObject("CMTField")));
                    return bindField;
                case "BindingIF":
                    BindingIF bindif = new BindingIF();
                    bindif.setIfParameter(json.getString("ifParameter"));
                    bindif.setIndexObj(json.getInt("indexObj"));
                    return bindif;
                case "BindingOutput":
                    BindingOutput bindOut = new BindingOutput();
                    bindOut.setParameter(json.getString("parName"));
                    bindOut.setParType(json.getString("parType"));
                   // bindOut.setOutputObj(fromJSONtoOutputHA(json.getJSONObject("outputObject")));
                    return bindOut;
            }
        } catch (JSONException ex) {
                        Logger.getLogger(Converter.class.getName()).log(Level.SEVERE, null, ex);
                    }
        return null;
    }
    
    public static JSONObject fromEventInputToJSON(EventInput input){
        JSONObject result = new JSONObject();
        try{
            result.put("className", input.getClassName());
            JSONArray arrLimits = new JSONArray();
            for(FieldValueLimitation lim: input.getLimitations()){
                JSONObject fLim = new JSONObject();
                fLim.put("fieldName", lim.getFieldName());
                if( lim.getOperator() == null){
                    fLim.put("operator", "");
                }else{
                    fLim.put("operator", lim.getOperator());
                }
                if(lim.getValue() == null){
                    fLim.put("value", "");
                }else{
                    fLim.put("value", lim.getValue());
                }
                arrLimits.put(fLim);
            }
            result.put("fieldsLim", arrLimits);
            JSONArray fields = new JSONArray();
            for(CMTField f: input.getFields()){
                JSONObject fj = fromCMTFieldToJSON(f);
                fields.put(fj);
            }
            result.put("fields", fields);
            
        } catch (JSONException ex) {
                        Logger.getLogger(Converter.class.getName()).log(Level.SEVERE, null, ex);
                    }
        return result;
    }
    
    public static EventInput fromJSONtoEventInput(JSONObject json){
        EventInput result = new EventInput();
        try{
            result.setClassName(json.getString("className"));
            JSONArray arrFields = json.getJSONArray("fieldsLim");
            for(int i=0;i<arrFields.length();i++){
                JSONObject field = arrFields.getJSONObject(i);
                FieldValueLimitation lim = new FieldValueLimitation();
                lim.setFieldName(field.getString("fieldName"));
                lim.setOperator(field.getString("operator"));
                lim.setValue(field.getString("value"));
                result.addLimitation(lim);
            }
            JSONArray fields = json.getJSONArray("fields");
            for(int a=0;a<fields.length();a++){
                CMTField f = fromJSONtoCMTField(fields.getJSONObject(a));
                result.addField(f);
            }
        } catch (JSONException ex) {
                        Logger.getLogger(Converter.class.getName()).log(Level.SEVERE, null, ex);
                    }
        return result;
    }
    /*
    private static JSONObject fromBindingParameterToJSON(BindingParameter par){
        JSONObject result = new JSONObject();
        try {
            if(par instanceof BindingInputFact){
                BindingInputFact bindFact = (BindingInputFact) par;
                result.put("type", "BindingInputFact");
                result.put("factId", bindFact.getFactId());
                result.put("indexObj", bindFact.getIndexObj());
                if(bindFact.getInputObject() instanceof Fact){
                    result.put("inputObjectType", "fact");
                    result.put("inputObject", fromFactToJSON((Fact)bindFact.getInputObject()) );
                }else{
                    result.put("inputObjectType", "facttype");
                  
                    result.put("inputObject", fromFactTypeToJSON((FactType)bindFact.getInputObject()) );
                }
            }else{
                if(par instanceof BindingInputField){
                    BindingInputField bindFact = (BindingInputField) par;
                    result.put("type", "BindingInputField");
                    result.put("factId", bindFact.getFactId());
                    result.put("indexObj", bindFact.getIndexObj());
                    if(bindFact.getInputObject() instanceof Fact){
                        result.put("inputObjectType", "fact");
                        result.put("inputObject", fromFactToJSON((Fact)bindFact.getInputObject()) );
                    }else{
                        result.put("inputObjectType", "facttype");
                        result.put("inputObject", fromFactTypeToJSON((FactType)bindFact.getInputObject()) );
                    }
                    CMTField field = bindFact.getField();
                    JSONObject objField = fromCMTFieldToJSON(field);
                    result.put("CMTField", objField);
                }else{
                    if(par instanceof BindingIF){
                        BindingIF bind = (BindingIF) par;
                        result.put("type", "BindingIF");
                        result.put("ifParameter", bind.getIfParameter());
                        result.put("indexObj", bind.getIndexObj());
                        return result;
                    }else{
                        if(par instanceof BindingOutput){
                            BindingOutput ou = (BindingOutput)par;
                            result.put("type", "BindingOutput");
                          //  result.put("outputObject", fromOutputHAToJSON(ou.getOutputObj()));
                            result.put("parName", ou.getParameter());
                            result.put("parType", ou.getParType());
                        }
                    }
                }
            }
        } catch (JSONException ex) {
                        Logger.getLogger(Converter.class.getName()).log(Level.SEVERE, null, ex);
                    }
        
        return result;
    }
    
    public static BindingParameter fromJSONtoBindingParameter(JSONObject json){
        try{
            String type = json.getString("type");
            switch(type){
                case "BindingInputFact":
                    BindingInputFact bindFact = new BindingInputFact();
                    bindFact.setFactId(json.getString("factId"));
                    bindFact.setIndexObj(json.getInt("indexObj"));
                    String inputObjectType = json.getString("inputObjectType");
                    if(inputObjectType.equals("fact")){
                        bindFact.setInputObject(fromJSONtoFact(json.getJSONObject("inputObject")));
                    }else{
                        if(inputObjectType.equals("facttype")){
                            JSONObject obType = json.getJSONObject("inputObject");
                            // check of ge hier input object zet
                            if(obType.has("extends")){
                                bindFact.setInputObject(fromJSONtoFactTypeEvent(obType));
                            }else{
                                 FactType ft = fromJSONtoFactTypeFact(obType);
                                
                                bindFact.setInputObject(ft);
                            }
                        }
                    }
                    return bindFact;
                case "BindingInputField":
                    BindingInputField bindField = new BindingInputField();
                    bindField.setFactId(json.getString("factId"));
                    bindField.setIndexObj(json.getInt("indexObj"));
                    String inputObjectTypeField = json.getString("inputObjectType");
                    
                    if(inputObjectTypeField.equals("fact")){
                         
                        bindField.setInputObject(fromJSONtoFact(json.getJSONObject("inputObject")));
                    }else{
                        if(inputObjectTypeField.equals("facttype")){
                            JSONObject obType = json.getJSONObject("inputObject");
                            if(obType.has("extends")){
                                bindField.setInputObject(fromJSONtoFactTypeEvent(obType));
                            }else{
                                FactType ty = fromJSONtoFactTypeFact(obType);
        
                                bindField.setInputObject(ty);
                            }
                        }
                    }
                    bindField.setField(fromJSONtoCMTField(json.getJSONObject("CMTField")));
                    return bindField;
                case "BindingIF":
                    BindingIF bindif = new BindingIF();
                    bindif.setIfParameter(json.getString("ifParameter"));
                    bindif.setIndexObj(json.getInt("indexObj"));
                    return bindif;
                case "BindingOutput":
                    BindingOutput bindOut = new BindingOutput();
                    bindOut.setParameter(json.getString("parName"));
                    bindOut.setParType(json.getString("parType"));
                 //   bindOut.setOutputObj(fromJSONtoOutputHA(json.getJSONObject("outputObject")));
                    return bindOut;
            }
        } catch (JSONException ex) {
                        Logger.getLogger(Converter.class.getName()).log(Level.SEVERE, null, ex);
                    }
        return null;
    }
    */
    
    public static JSONObject fromOutputHAToJSON(OutputHA output){
        JSONObject result = new JSONObject();
        try{
            result.put("name", output.getName());
            result.put("bindings", fromListBindingsToJSON(output.getBindings()));
        } catch (JSONException ex) {
            Logger.getLogger(Converter.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result;
    }
    
    public static OutputHA fromJSONtoOutputHA(JSONObject json){
        OutputHA result = new OutputHA();
        try{
            result.setName(json.getString("name"));
            result.setBindings(fromJSONtoListBindings(json.getJSONArray("bindings")));
        } catch (JSONException ex) {
            Logger.getLogger(Converter.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result;
    }
    
    public static JSONArray fromListBindingsToJSON(LinkedList<Binding> bindings){
        JSONArray arrBindings = new JSONArray();
        try{
                for(int ii=0; ii<bindings.size();ii++){
                    Binding binding = bindings.get(ii);
                    JSONObject objBind = fromBindingToJSON(binding, ii);  
                    arrBindings.put(objBind);
                }
        } catch (JSONException ex) {
                        Logger.getLogger(Converter.class.getName()).log(Level.SEVERE, null, ex);
                    }
        return arrBindings;
    }
    
    // (LvH)
    // Per binding converter
    // Needed for exporter
    public static JSONObject fromBindingToJSON(Binding binding, int index) {
        JSONObject objBind = new JSONObject();
        objBind.put("index", index);

                    BindingParameter startBind = binding.getStartBinding();
                    BindingParameter endBind = binding.getEndBinding();
                    System.out.println("--- start " + startBind.getIndexObj());
                    System.out.println("--- end " + endBind.getIndexObj());
                    objBind.put("startBinding", fromBindingParameterToJSON(startBind));
                    objBind.put("endBinding", fromBindingParameterToJSON(endBind));

        return objBind;
    }
    
    public static LinkedList<Binding> fromJSONtoListBindings(JSONArray arrbindings){
        LinkedList<Binding> bindings = new LinkedList<Binding>();
        try{
            for(int i= 0; i<arrbindings.length();i++){
                for(int ii = 0; ii<arrbindings.length();ii++){
                    JSONObject ob = arrbindings.getJSONObject(ii);
                    if(ob.getInt("index") == i){
                        Binding binding = new Binding();
                        binding.setStartBinding(fromJSONtoBindingParameter(ob.getJSONObject("startBinding")));
                        binding.setEndBinding(fromJSONtoBindingParameter(ob.getJSONObject("endBinding")));
                        bindings.add(binding);
                    }
                }
            }
        } catch (JSONException ex) {
            Logger.getLogger(Converter.class.getName()).log(Level.SEVERE, null, ex);
        }
        return bindings;
    }
    
    public static JSONObject fromFactToJSON(Fact fact){
        JSONObject result = new JSONObject();
        try{
            result.put("className", fact.getClassName());
            result.put("uriField", fact.getUriField());
            result.put("sqlId", fact.getId());
            ArrayList<CMTField> fields = fact.getFields();
            JSONArray arrFields = new JSONArray();
            for(CMTField field : fields){
                arrFields.put(fromCMTFieldToJSON(field));
            }
            result.put("fields", arrFields);
        } catch (JSONException ex) {
                        Logger.getLogger(Converter.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result;
    }
    
    public static JSONObject fromFactTypeToJSON(FactType facttype){
        JSONObject result = new JSONObject();
       
        try{
            result.put("className", facttype.getClassName());
            result.put("type", facttype.getType());
            result.put("isCustom", facttype.isIsCustom());
            result.put("uriField", facttype.getUriField());
            result.put("varFormat", facttype.getVarFormat());
            result.put("category", facttype.getCategory());
            ArrayList<String> varList = facttype.getVarList();
            JSONArray varListjson = new JSONArray();
            for(String st : varList){
                JSONObject ob = new JSONObject();
                ob.put("var", st);
                varListjson.put(ob);
            }
            result.put("varList", varListjson);
            ArrayList<CMTField> fields = facttype.getFields();
            JSONArray arrFields = new JSONArray();
            for(CMTField field : fields){
                arrFields.put(fromCMTFieldToJSON(field));
            }
            result.put("fields", arrFields);
        } catch (JSONException ex) {
                        Logger.getLogger(Converter.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result;
    }
    
    private static JSONObject fromCMTFieldToJSON(CMTField field){
        JSONObject objField = new JSONObject();
        try{
            
            objField.put("fieldName", field.getName());
            objField.put("fieldType", field.getType());
            objField.put("input", field.isIsVar());
            objField.put("format", field.getFormat());
            objField.put("sqlId", field.getSql_id());
            JSONArray arrOptions = new JSONArray();
            for(String option : field.getOptions()){
                arrOptions.put(option);
            }
            objField.put("options", arrOptions);
            JSONObject fieldValue = new JSONObject();
            if(field.getValue()!=null){
               
            if(field.getValue().getClass().isAssignableFrom(Fact.class)){
                Fact fact = (Fact) field.getValue();
                fieldValue.put("fact", fromFactToJSON(fact));
            }else{
                if(field.getValue().getClass().getSimpleName().equals("String")){
                    fieldValue.put("string", field.getValue().toString());
                }else{ // can cause a bug with non java vm classes
                    Gson gson = new Gson();
                    fieldValue.put("gson", gson.toJson(field.getValue()));
                    fieldValue.put("className", field.getValue().getClass().getName());
                }
            }
            
            }
            objField.put("fieldValue", fieldValue);
        }catch (JSONException ex) {
            Logger.getLogger(Converter.class.getName()).log(Level.SEVERE, null, ex);
        }
        return objField;
    }
    
    public static Fact fromObjectToFactInstance(Object object){
        
        FactType type = CMTDelegator.get().getFactTypeWithName(object.getClass().getSimpleName());
        Fact fact = new Fact(object.getClass().getSimpleName(), type.getUriField());
        ArrayList<CMTField> factFields = new ArrayList<>();
        Field[] fields = object.getClass().getDeclaredFields();
        for(Field field : fields){
            try {
                Object val = field.get(object);
                
                for(CMTField cmtField : type.getFields()){
                    if(cmtField.getName().equals(field.getName())){
                        CMTField factField = new CMTField(cmtField.getName(), cmtField.getType());
                        // check if fact then convert to fact
                        FactType typeFact = CMTDelegator.get().getFactTypeWithName(val.getClass().getSimpleName());
                        if(typeFact != null){
                            Fact valFact = fromObjectToFactInstance(val);
                          
                            factField.setValue(valFact);
                        }else{
                          
                            factField.setValue(val);
                        }
                        factFields.add(factField);
                    }
                }
            } catch (IllegalArgumentException ex) {
                Logger.getLogger(Converter.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IllegalAccessException ex) {
                Logger.getLogger(Converter.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        fact.setFields(factFields);
        return fact;
    }
    
    public static IFactType fromFactToObject(Fact fact){
        try{
            
        String factType = fact.getClassName();
        
        FactType typeFact = CMTDelegator.get().getFactTypeWithName(factType);
        IFactType factObject = CMTDelegator.get().getFact(typeFact.getClassName(), typeFact.getUriField() , fact.getUriValue());
        if(factObject == null){ 
        Class<?> cl = Class.forName(Constants.PACKAGEFACTS + "."+factType);
        Object obj = cl.newInstance();
        Field[] clFields = cl.getDeclaredFields();
        ArrayList<CMTField> fields = fact.getFields();
        for(Field f : clFields){
            for(CMTField field : fields){
                if(field.getName().equals(f.getName())){
                   if(!(f.getType().getName().contains("java") || f.getType().getName().contains("String") )){
                        // check if fact then convert to fact
                           
                            Object obf =  fromFactToObject((Fact)field.getValue());
                            f.set(obj, obf);
                            
                        }else{
                            f.set(obj, field.getValue());
                        }  
                }
            }
        }
        return (IFactType) obj;
        }
        return factObject;
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(CMTCore.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            Logger.getLogger(CMTCore.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(CMTCore.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
   
    public static IFactType fromJSONFactObjectToObject(JSONObject json, boolean isEvent){
        String type = json.getString("className");
        
        JSONObject obj = json.getJSONObject("object");
        Gson gson = new Gson();
        if(isEvent){
            type = Constants.PACKAGEEVENTS+"."+type;
        }else{
            type = Constants.PACKAGEFACTS+"."+type;
        }
        try{
            URLClassLoader classLoader = URLClassLoader.newInstance(new URL[] { new File( Constants.CLASSPATH).toURI().toURL() }, IFactType.class.getClassLoader());
            classLoader.loadClass("be.ac.vub.wise.cmtserver.blocks.IFactType");
            Object ob = gson.fromJson(obj.toString(), Class.forName(type, true, classLoader));
             Class<?>[] inter = ob.getClass().getInterfaces();
            for(Class<?> cl:inter){
                System.out.println("--- inter : " + cl.getSimpleName());
            }
            //IFactType f = (IFactType) ob;
            if(ob instanceof IFactType){
               
                return (IFactType) ob;
            }
            
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(CMTCore.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MalformedURLException ex) {
            Logger.getLogger(CMTCore.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SecurityException ex) {
            Logger.getLogger(Converter.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(Converter.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    public static JSONObject fromRuleToJSON(Rule rule){
        JSONObject ob = new JSONObject();
        ob.put("name", rule.getName());
        ob.put("drlRule",rule.getDrlRule());
        ob.put("sqlId", rule.getSql_id());
        return ob;
    
    }
    
    public static JSONArray fromEventTypeListToJSON(ArrayList<FactType> eventTypes){
        JSONArray jEventTypes = new JSONArray();
        for(int i=0; i<jEventTypes.length(); i++){
            jEventTypes.put(i, fromFactTypeToJSON(eventTypes.get(i)));
        }        
        return jEventTypes;
    }
    
    public static ArrayList<FactType> fromJSONToEventTypeList(JSONArray jEventTypes){
        ArrayList<FactType> eventTypes = new ArrayList<>();
        for(int i=0; i<jEventTypes.length(); i++){
            eventTypes.add(fromJSONtoFactTypeEvent(jEventTypes.getJSONObject(i)));
        }
        return eventTypes;
    }
     
    @Deprecated
    public static JSONObject fromSuggestionsToJSON(HashMap<String, HashMap<Integer, String>> suggestions){
        JSONObject out = new JSONObject();
        
        JSONArray jTypes = new JSONArray();
        for(HashMap.Entry<String, HashMap<Integer, String>> currType: suggestions.entrySet()){
            JSONObject jCurrType = new JSONObject();
            jCurrType.put("type", currType.getKey());
            HashMap<Integer, String> suggs = currType.getValue();
            JSONArray jSuggs = new JSONArray();
            for(HashMap.Entry<Integer, String> currSugg: suggs.entrySet()){
                JSONObject jCurrSugg = new JSONObject();
                jCurrSugg.put("id", currSugg.getKey());
                jCurrSugg.put("sugtype", currSugg.getValue());
                jSuggs.put(jCurrSugg);
            }
            jCurrType.put("suggestions", jSuggs);
        }
        
        out.put("types", jTypes);
        return out;
    }
    
    @Deprecated
    // The id of the chosen suggestion will be set to -1
    public static HashMap<String, HashMap<Integer, String>> fromJSONToSuggestions(JSONObject jSuggestions){
        HashMap<String, HashMap<Integer, String>> result = new HashMap<>();
        JSONArray jTypes = jSuggestions.getJSONArray("types");        

        for(int i=0; i<jTypes.length(); i++){
            JSONObject currType = jTypes.getJSONObject(i);
            Integer chosenSuggestionId = -1;
            if(currType.has("chosensuggestion"))
                chosenSuggestionId = currType.getInt("chosensuggestion");
            
            JSONArray jSuggs = currType.getJSONArray("suggestions");
            HashMap<Integer, String> suggs = new HashMap<>();
            for(int j=0; j<jSuggs.length(); j++){
                JSONObject currSugg = jSuggs.getJSONObject(j);
                if(chosenSuggestionId == j)
                    suggs.put(-1, currSugg.getString("sugtype"));   // if the client has chosen a suggestion, set the id of that suggestion to -1;
                else               
                    suggs.put(currSugg.getInt("id"), currSugg.getString("sugtype")); // else, just add its regular id
            }
            result.put(currType.getString("type"), suggs);
        }      
        return result;
    }
    
    /**
     * !! WARNING: Only works for IFactType subclasses: FactType, Fact, EventInput
     * @param iFType
     * @return { type: facttype/fact/eventinput, data : (default converter)
     */
    private static JSONObject fromIFactTypeToJSON(IFactType iFType){
        JSONObject jIFactType = new JSONObject();
        if(iFType instanceof FactType){
            jIFactType.put("type", "facttype");
            jIFactType.put("data", fromFactTypeToJSON((FactType) iFType));
        } else if(iFType instanceof Fact){
            jIFactType.put("type", "fact");
            jIFactType.put("data",fromFactToJSON((Fact) iFType));
        } else if(iFType instanceof EventInput){
            jIFactType.put("type", "eventinput");
            jIFactType.put("data",fromEventInputToJSON((EventInput) iFType));
        } else {
            System.out.println("ERROR -- Converter -- fromIFactTypeToJSON --"
                    + " IFactType not of type FactType, Fact or EventInput -- returning empty JSONObject...");
            jIFactType.put("type", "unknown");     
        }
        
        return jIFactType;       
    }
    /**
     *  !! WARNING: only for JSONObjects generated by fromIFactType to JSON
     * @param jIFType
     * @return 
     */
    private static IFactType fromJSONToIFactType(JSONObject jIFType){
        IFactType result;
        String type = jIFType.getString("type");
        JSONObject data = jIFType.getJSONObject("data");
        switch(type){
            case "facttype":
                if(data.has("extends")){
                    result = fromJSONtoFactTypeEvent(data);
                } else {
                    result = fromJSONtoFactTypeFact(data);
                }
                    break;
            case "fact":
                result = fromJSONtoFact(data);
                break;
            case "eventinput":
                result = fromJSONtoEventInput(data);
                break;
            default: 
                System.out.println("ERROR -- Converter -- FromJSONToIFactType -- cannot determine type of jIFType -- returning null");
                result = null;
                break;              
        }        
        return result;
    }
    
    public static JSONObject fromIFactTypeSuggestionsToJSON(IFactTypeSuggestions iftSuggs){
        JSONObject result = new JSONObject();
           
        result.put("index", iftSuggs.getIndex());
        IFactType importIFactType = iftSuggs.getImportIFactType();
        JSONObject jImportIFactType = fromIFactTypeToJSON(importIFactType);
        result.put("iFactType", jImportIFactType);
        
        ArrayList<FactType> suggestions = iftSuggs.getSuggestions();
        if (suggestions != null) {
            JSONArray jSuggestions = new JSONArray();
            for (int i=0; i < suggestions.size(); i++) {
                FactType currSugg = suggestions.get(i);
                JSONObject jFactType = fromFactTypeToJSON(currSugg);
                jSuggestions.put(i, jFactType);
            }
            result.put("suggestions", jSuggestions);
        }
        IFactType chosenSuggestion = iftSuggs.getChosenSuggestion();
        if(chosenSuggestion != null){
            JSONObject jChosenSugg = fromIFactTypeToJSON(chosenSuggestion);
            result.put("chosenSuggestion", jChosenSugg);
        }
        FactType eventType = iftSuggs.getEventType();
        if(eventType != null){
            JSONObject jEventType = fromFactTypeToJSON(eventType);
            result.put("eventType", jEventType);
        }        
        return result;
    } 
    
    public static IFactTypeSuggestions fromJSONToIFactTypeSuggestions(JSONObject jIFTSuggs){
        IFactTypeSuggestions result = new IFactTypeSuggestions();
        result.setIndex(jIFTSuggs.getInt("index"));
        JSONObject jImportIFactType = jIFTSuggs.getJSONObject("iFactType");
        result.setImportIFactType(fromJSONToIFactType(jImportIFactType));        
        
        if(jIFTSuggs.has("suggestions")){
            ArrayList<FactType> suggestions = new ArrayList<>();
            JSONArray jSuggs = jIFTSuggs.getJSONArray("suggestions");            
            for(int i=0; i < jSuggs.length(); i++){
                JSONObject jCurrSugg = jSuggs.getJSONObject(i);                
                FactType currSugg;
                if(jCurrSugg.has("extends")){
                    currSugg = fromJSONtoFactTypeEvent(jCurrSugg);
                } else {
                    currSugg = fromJSONtoFactTypeFact(jCurrSugg);
                }
                suggestions.add(currSugg);
            }
            result.setSuggestions(suggestions);
        }
        if(jIFTSuggs.has("chosenSuggestion")){
            JSONObject jChosenSugg = jIFTSuggs.getJSONObject("chosenSuggestion");
            result.setChosenSuggestion(fromJSONToIFactType(jChosenSugg));
        }
        if(jIFTSuggs.has("eventType")){
            JSONObject jEventType = jIFTSuggs.getJSONObject("eventType");
            FactType eventType;
            if(jEventType.has("extends")){
                eventType = fromJSONtoFactTypeEvent(jEventType);
            } else {
                eventType = fromJSONtoFactTypeFact(jEventType);
            }
            result.setEventType(eventType);
        }    
    return result;
    }
    
    public static JSONObject fromTemplateSuggestionsToJSON (TemplateSuggestions tmplSuggs){
        JSONObject result = new JSONObject();
        
        result.put("recursionLevel", tmplSuggs.getRecursionLevel());
        
        result.put("template", fromTemplateToJSON(tmplSuggs.getTemplate()));
        
        if (tmplSuggs.getIndexToSuggestions() != null) {
            JSONArray jIdxToSuggestions = new JSONArray();
            
            HashMap<Integer, IFactTypeSuggestions> indexToSuggestions = tmplSuggs.getIndexToSuggestions();
            for (Map.Entry<Integer, IFactTypeSuggestions> entry : indexToSuggestions.entrySet()) {
                JSONObject jIdxToSugg = new JSONObject();
                jIdxToSugg.put("index", entry.getKey());
                jIdxToSugg.put("suggestions", fromIFactTypeSuggestionsToJSON(entry.getValue()));
                jIdxToSuggestions.put(jIdxToSugg);
            }
            result.put("indexToSuggestions", jIdxToSuggestions);
        }
        if(tmplSuggs.getEventType() != null){
            result.put("eventType", fromFactTypeToJSON(tmplSuggs.getEventType()));
        }
        return result;
    }
    
    public static TemplateSuggestions fromJSONToTemplateSuggestions(JSONObject jTmplSuggs){
       TemplateSuggestions result = new TemplateSuggestions();
       
       result.setRecursionLevel(jTmplSuggs.getInt("recursionLevel"));
       result.setTemplate(fromJSONtoTemplateHA(jTmplSuggs.getJSONObject("template")));
       
       if(jTmplSuggs.has("indexToSuggestions")){
           HashMap<Integer, IFactTypeSuggestions> indexToSuggestions = new HashMap<>();
           JSONArray jIdxToSuggestions = jTmplSuggs.getJSONArray("indexToSuggestions");
           for(int i=0; i < jIdxToSuggestions.length(); i++){
               JSONObject jIdxToSugg = jIdxToSuggestions.getJSONObject(i);
               Integer index = jIdxToSugg.getInt("index");
               IFactTypeSuggestions iftSuggs = fromJSONToIFactTypeSuggestions(jIdxToSugg.getJSONObject("suggestions"));
               indexToSuggestions.put(index, iftSuggs);
           }
           result.setIndexToSuggestions(indexToSuggestions);           
       }
       
       if(jTmplSuggs.has("eventType")){
           JSONObject jEventType = jTmplSuggs.getJSONObject("eventType");
           FactType eventType;
           if(jEventType.has("extends")){
                eventType = fromJSONtoFactTypeEvent(jEventType);
            } else {
                eventType = fromJSONtoFactTypeFact(jEventType);
            }
           result.setEventType(eventType);
       }    
       return result;
    }
    
    public static JSONArray fromTemplateSuggestionsListToJSON(ArrayList<TemplateSuggestions> tmplSuggs){
        JSONArray jTmplSuggs = new JSONArray();
        
        for(int i=0; i<tmplSuggs.size(); i++){
            TemplateSuggestions currTmplSuggs = tmplSuggs.get(i);
            JSONObject jTemplateSuggestions = fromTemplateSuggestionsToJSON(currTmplSuggs);
            jTmplSuggs.put(jTemplateSuggestions);
        }
    return jTmplSuggs;
    }
    public static ArrayList<TemplateSuggestions> fromJSONToTemplateSuggestionsList(JSONArray jTmplSuggs){
        ArrayList<TemplateSuggestions> result = new ArrayList<>();
        for(int i=0; i < jTmplSuggs.length(); i++){
            JSONObject jCurr = jTmplSuggs.getJSONObject(i);
            result.add(fromJSONToTemplateSuggestions(jCurr));
        }        
        return result;
    }
}
