/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ac.vub.wise.cmtclient.util;

import be.ac.vub.wise.cmtclient.blocks.AInput;
import be.ac.vub.wise.cmtclient.blocks.AInput.Input;
import be.ac.vub.wise.cmtclient.blocks.ActionClient;
import be.ac.vub.wise.cmtclient.blocks.ActionField;
import be.ac.vub.wise.cmtclient.blocks.Binding;
import be.ac.vub.wise.cmtclient.blocks.BindingIF;
import be.ac.vub.wise.cmtclient.blocks.BindingInputFact;
import be.ac.vub.wise.cmtclient.blocks.BindingInputField;
import be.ac.vub.wise.cmtclient.blocks.BindingOutput;
import be.ac.vub.wise.cmtclient.blocks.BindingParameter;
import be.ac.vub.wise.cmtclient.blocks.CMTField;
import be.ac.vub.wise.cmtclient.blocks.CMTParameter;
import be.ac.vub.wise.cmtclient.blocks.Event;
import be.ac.vub.wise.cmtclient.blocks.EventInput;
import be.ac.vub.wise.cmtclient.blocks.Fact;
import be.ac.vub.wise.cmtclient.blocks.FactType;
import be.ac.vub.wise.cmtclient.blocks.FieldValueLimitation;
import be.ac.vub.wise.cmtclient.blocks.Function;
import be.ac.vub.wise.cmtclient.blocks.IFBlock;
import be.ac.vub.wise.cmtclient.blocks.Operator;
import be.ac.vub.wise.cmtclient.blocks.OutputHA;
import be.ac.vub.wise.cmtclient.blocks.Parameters;
import be.ac.vub.wise.cmtclient.blocks.Rule;
import be.ac.vub.wise.cmtclient.blocks.Template;
import be.ac.vub.wise.cmtclient.blocks.TemplateActions;
import be.ac.vub.wise.cmtclient.blocks.TemplateHA;
import be.ac.vub.wise.cmtclient.core.CMTClient;
import com.google.gson.Gson;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author Sandra
 */
public class ConverterCoreBlocks {
    
    private static final String url = Constants.URLCMT; 
    
    /*
    class including the functions
    */
    public static JSONObject fromFunctionClassToJSON(Class<?> functionClass){
        JSONObject result = new JSONObject();
        String packname = functionClass.getPackage().getName();
        String packurl = packname.replace(".","/");
	String classUri = System.getProperty("user.dir")+"/src/" + packurl +"/" + functionClass.getSimpleName() + ".java";
        try {
            String res = FileUtils.readFileToString(new File(classUri), System.getProperty("file.encoding"));
            ArrayList<String> methods = parseJavaFileMethods(res);
            // get body for every method + method name + 
            Method[] meths = functionClass.getMethods();
            JSONArray arr = new JSONArray();
            for(String st : methods){
                JSONObject met = new JSONObject();
                String body = parseMethodsForBody(st);
                String methName = getMethodName(functionClass, st);
                met.put("methodBody", body);
                met.put("methodName", methName);
               met.put("sqlId", 0);
                met.put("encapClass", functionClass.getSimpleName());
                // parameters parNames + parTypes
                JSONArray arrParNames = new JSONArray();
                for(Method meth : meths){
                    if(meth.getName().equals(methName)){
                        Parameters parAnno = meth.getAnnotation(Parameters.class);
                        String parsNames = parAnno.parameters();
                        String[] splits = parsNames.split("\\s");
                        Class<?>[] parTypes = meth.getParameterTypes();
                        for(int a=0; a<splits.length; a++){
                            JSONObject parNameO = new JSONObject();
                            parNameO.put("index", a);
                            parNameO.put("parName", splits[a]);    
                            parNameO.put("parType",  getSimpleName(parTypes[a].getName()));
                            parNameO.put("sqlId", 0);
                            arrParNames.put(parNameO);
                        }
                    }
                }
                met.put("pars", arrParNames);
                arr.put(met);
            }
            result.put("methods", arr);
            result.put("encapClass", functionClass.getSimpleName());
           
        } catch (IOException ex) {
            Logger.getLogger(ConverterCoreBlocks.class.getName()).log(Level.SEVERE, null, ex);
        } catch (JSONException ex) {
            Logger.getLogger(ConverterCoreBlocks.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result;
    }
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
                // java in then leaf full nameTODO !! ! 
                String type = field.getType().getName();
                if(!name.contains("java")){
                    
                }
                
                JSONObject ob = new JSONObject();
                ob.put("fieldName", name);
                ob.put("fieldType", type);
                arr.put(ob);
            }
            result.put("fields", arr);
            
        } catch (JSONException ex) {
            Logger.getLogger(ConverterCoreBlocks.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result;
    }
    /*
    short cut for sending facts and events to cmt of registered facttypes
    */
    // JSON {"className":< simple name>, "object":{...}} // also ok for events
    public static JSONObject fromFactObjectToJSON(Object object){
        Fact fact = fromObjectToFactInstance(object);
        
        JSONObject result = fromFactToJSON(fact);
//        String type = object.getClass().getSimpleName();
//        Gson gson = new Gson();
//        String jsonob = gson.toJson(object);
//        try {
//            JSONObject ob = new JSONObject(jsonob);
//            result.put("className", type);
//            result.put("object", ob);
//        } catch (JSONException ex) {
//            Logger.getLogger(ConverterCoreBlocks.class.getName()).log(Level.SEVERE, null, ex);
//        }
        return result;
    }
    
    public static Fact fromObjectToFactInstance(Object object){ 
        
        FactType type = CMTClient.getFactTypeFactWithName(object.getClass().getSimpleName());
        Fact fact = new Fact(object.getClass().getSimpleName(), type.getUriField());
        fact.setFields(type.getFields());
        Field[] fields = object.getClass().getDeclaredFields();
        for(Field field : fields){
            try {
                Object val = field.get(object);
                for(CMTField cmtField : fact.getFields()){
                    if(cmtField.getName().equals(field.getName())){
                        if(!(field.getType().getName().contains("java") || field.getType().getName().contains("String") )){
                        // check if fact then convert to fact
                      //  FactType typeFact = CMTClient.getFactTypeFactWithName(val.getClass().getSimpleName());
                       
                           // Fact valFact = fromObjectToFactInstance(val); // call CMT for fact
                            FactType typeObj = CMTClient.getFactType(cmtField.getType());
                            
                            Fact valFact = null;
                            try {
                                valFact = CMTClient.getFact(typeObj.getClassName(), typeObj.getUriField(), val.getClass().getDeclaredField(typeObj.getUriField()).get(val).toString());
                            } catch (NoSuchFieldException ex) {
                                Logger.getLogger(ConverterCoreBlocks.class.getName()).log(Level.SEVERE, null, ex);
                            } catch (SecurityException ex) {
                                Logger.getLogger(ConverterCoreBlocks.class.getName()).log(Level.SEVERE, null, ex);
                            }
                            cmtField.setValue(valFact);
                        }else{
                            cmtField.setValue(val);
                        }
                    }
                }
            } catch (IllegalArgumentException ex) {
                Logger.getLogger(ConverterCoreBlocks.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IllegalAccessException ex) {
                Logger.getLogger(ConverterCoreBlocks.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return fact;
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
                    Logger.getLogger(ConverterCoreBlocks.class.getName()).log(Level.SEVERE, null, ex);
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
                Logger.getLogger(ConverterCoreBlocks.class.getName()).log(Level.SEVERE, null, ex);
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
                    fieldO.put("varList", "[]"); 
                }
                fieldO.put("varFormat", field.getFormat());
                arr.put(fieldO);
            }
            result.put("fields", arr);
        } catch (JSONException ex) {
            Logger.getLogger(ConverterCoreBlocks.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
        return result;
    }
    
    public static Fact fromJSONtoFact(JSONObject json){
        try {
            
            Fact factObject = new Fact(json.getString("className"), json.getString("uriField"));
            ArrayList<CMTField> fields = new ArrayList<>();
            factObject.setId(json.getInt("sqlId"));
            JSONArray arrfields = json.getJSONArray("fields");
            for(int i=0; i< arrfields.length();i++){
                JSONObject jsonField = arrfields.getJSONObject(i);
                CMTField fi = fromJSONtoCMTField(jsonField);
                System.out.println("------ in conv " + fi.getName());
                fields.add(fi);
            }
            factObject.setFields(fields);
            return factObject;
        
        } catch (JSONException ex) {
           Logger.getLogger(ConverterCoreBlocks.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    public static FactType getFactType(String className){
        try {
            
            HttpResponse<String> request = Unirest.get(url+"/getFactTypeFact/"+ className).asString();

            JSONObject factTypeO = new JSONObject(request.getBody());
            // String className, String type, String uriField, ArrayList<CMTField> field
            String classNameO = factTypeO.getString("className");
            String uriField = factTypeO.getString("uriField");
            ArrayList<CMTField> fields = new ArrayList<CMTField>();
            JSONArray fieldsFactType = factTypeO.getJSONArray("fields");
            for( int i = 0; i< fieldsFactType.length(); i++){
                JSONObject obField = fieldsFactType.getJSONObject(i);
                CMTField field = new CMTField(obField.getString("fieldName"), obField.getString("fieldType"));
                fields.add(field);
            }
            FactType factType = new FactType(classNameO, "fact", uriField, fields);
            return factType;
        } catch (UnirestException ex) {
           Logger.getLogger(ConverterCoreBlocks.class.getName()).log(Level.SEVERE, null, ex);
        } catch (JSONException ex) {
           Logger.getLogger(ConverterCoreBlocks.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    public static FactType fromJSONtoFactTypeEvent(JSONObject object){
        
        try {
            String name = object.getString("className"); // simple name
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
            String typeClass = object.getString("type");
   
            FactType facttype = new FactType(name, typeClass, uriField, fields);
            facttype.setVarFormat(varFormat);
            facttype.setVarList(varList);
            if(typeClass.equals("activity")){
                facttype.setIsCustom(object.getBoolean("isCustom"));
            }
            return facttype;
        } catch (JSONException ex) {
            Logger.getLogger(ConverterCoreBlocks.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    public static FactType fromJSONtoFactTypeFact(JSONObject object){
        
        try {
            String name = object.getString("className"); // simple name
            //String typeClass = object.getString("type");
            ArrayList<String> varList = new ArrayList<String>(); // is empty when fact type
            
            String varFormat = "";
            String uriField = object.getString("uriField");
            JSONArray arrFields = object.getJSONArray("fields");
            ArrayList<CMTField> fields = new ArrayList<CMTField>();
            for(int a=0; a<arrFields.length();a++){
                JSONObject fieldO = arrFields.getJSONObject(a);
                CMTField field = ConverterCoreBlocks.fromJSONtoCMTField(fieldO);
                fields.add(field);
            }
            FactType facttype = new FactType(name, "fact", uriField, fields);
            facttype.setVarFormat(varFormat);
            facttype.setVarList(varList);
            facttype.setCategory(object.getString("category"));
            return facttype;
        } catch (JSONException ex) {
            Logger.getLogger(ConverterCoreBlocks.class.getName()).log(Level.SEVERE, null, ex);
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

public static Function fromJSONtoFunction(JSONObject object) {
        try {
            Function func = new Function();
            func.setName(object.getString("methodName"));
            func.setEncapClass(object.getString("encapClass"));
            func.setSql_id(object.getInt("sqlId"));
            func.setBody(object.getString("body"));
            ArrayList<CMTParameter> pars = new ArrayList<>();
            JSONArray arrPars = object.getJSONArray("pars");
            HashMap<Integer, CMTParameter> indexFunctions = new HashMap<Integer, CMTParameter>();
            for (int a = 0; a < arrPars.length(); a++) {
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
            Logger.getLogger(ConverterCoreBlocks.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public static JSONObject fromFunctionToJSON(Function func) {
        if (func != null) {
            JSONObject result = new JSONObject();
            try {
                result.put("encapClass", func.getEncapClass());
                result.put("methodName", func.getName());
                result.put("sqlId", func.getSql_id());
                result.put("body", func.getBody());
                ArrayList<CMTParameter> parameters = func.getParameters();
                JSONArray arrPars = new JSONArray();
                for (CMTParameter p : parameters) {
                    JSONObject parObj = new JSONObject();
                    parObj.put("parName", p.getParName());
                    parObj.put("parType", p.getType());
                    parObj.put("index", p.getPosition());
                    parObj.put("sqlId", p.getSql_id());
                    arrPars.put(parObj);
                }
                result.put("pars", arrPars);
            } catch (JSONException ex) {
                Logger.getLogger(ConverterCoreBlocks.class.getName()).log(Level.SEVERE, null, ex);
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
            return rule;
        } catch (JSONException ex) {
            Logger.getLogger(ConverterCoreBlocks.class.getName()).log(Level.SEVERE, null, ex);
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
        } catch (JSONException ex) {
            Logger.getLogger(ConverterCoreBlocks.class.getName()).log(Level.SEVERE, null, ex);
        }
        temp = (TemplateActions) fillTemplateLS(temp, object);
        return temp;
    }
    
    public static TemplateHA fromJSONtoTemplateHA(JSONObject object){ // convert manually 
        TemplateHA temp = new TemplateHA();
        try{
            temp.setOutput(fromJSONtoOutputHA(object.getJSONObject("output")));
        } catch (JSONException ex) {
            Logger.getLogger(ConverterCoreBlocks.class.getName()).log(Level.SEVERE, null, ex);
        }
        temp = (TemplateHA) fillTemplateLS(temp, object);
        return temp;
    }
    
    public static ActionClient fromJSONtoAction(JSONObject object){
        try {
            String className = object.getString("className");
            ArrayList<ActionField> fields = new ArrayList<ActionField>();
            JSONArray arrFields = object.getJSONArray("fields");
            for(int a=0; a<arrFields.length(); a++){
                JSONObject fieldO = arrFields.getJSONObject(a);
                String fieldName = fieldO.getString("fieldName");
                JSONArray arrList = fieldO.getJSONArray("varList");
                ArrayList<String> varList = new ArrayList<String>();
                for(int b=0; b<arrList.length(); b++){
                    JSONObject varO = arrList.getJSONObject(b);
                    varList.add(varO.getString("var"));
                }
                String varFormat = fieldO.getString("varFormat");
                ActionField actF = new ActionField(fieldName, varList, varFormat);
                fields.add(actF);
            }
            ActionClient act = new ActionClient(className, fields);
            return act;
        } catch (JSONException ex) {
            Logger.getLogger(ConverterCoreBlocks.class.getName()).log(Level.SEVERE, null, ex);
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
  
    private static Template fillTemplateLS(Template temp, JSONObject json){
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
            Logger.getLogger(ConverterCoreBlocks.class.getName()).log(Level.SEVERE, null, ex);
        }
        return temp;
    }
    
    public static  IFBlock fromJSONtoIFBlock(JSONObject json){
        IFBlock ifBlock = new IFBlock();
        try{
            JSONObject jsonIfBlock = json;
            String typeBlock = jsonIfBlock.getString("typeBlock");
            ifBlock.setType(typeBlock);
            switch(typeBlock){
                case "function":
                    ifBlock.setFunction(fromJSONtoFunction(jsonIfBlock.getJSONObject("function")));
                    
                    break;
                case "activity":
                    ifBlock.setEvent(fromJSONtoFactTypeEvent(jsonIfBlock.getJSONObject("event")));
                    break;
            }
            JSONArray arrBindings = jsonIfBlock.getJSONArray("bindings");
            LinkedList<Binding> bindings = fromJSONtoListBindings(arrBindings);
            ifBlock.setBindings(bindings);
        } catch (JSONException ex) {
                Logger.getLogger(ConverterCoreBlocks.class.getName()).log(Level.SEVERE, null, ex);
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
                Logger.getLogger(ConverterCoreBlocks.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    public static CMTField fromJSONtoCMTField(JSONObject json){
        try{
            CMTField field = new CMTField(json.getString("fieldName"), json.getString("fieldType"));
            field.setIsVar(json.getBoolean("input"));
            field.setFormat(json.getString("format"));
            field.setSql_id(json.getInt("sqlId"));
            System.out.println("----------- id field " + json.getInt("sqlId"));
            JSONArray opt = json.getJSONArray("options");
            for(int i =0; i<opt.length(); i++){
                field.addOption(opt.getString(i));
            }
            JSONObject fieldValue = json.getJSONObject("fieldValue");
            System.out.println("------ pr " + fieldValue.toString());
            if(fieldValue.has("fact")){
                field.setValue(fromJSONtoFact(fieldValue.getJSONObject("fact")));
            }else{
                if(fieldValue.has("string")){
                    field.setValue(fieldValue.getString("string"));
                }else{
                    Gson gson = new Gson();
                  //  field.setValue(gson.fromJson(fieldValue.getString("gson"), Class.forName(fieldValue.getString("className"))));
                }
            }
            return field;
        } catch (JSONException ex) {
                Logger.getLogger(ConverterCoreBlocks.class.getName()).log(Level.SEVERE, null, ex);
        } 
        return null;
    }
    
    public static JSONObject fromTemplateToJSON(Template temp){
        JSONObject result = new JSONObject();
        try {
            LinkedList<IFBlock> ifblocks = temp.getIfBlocks();
            LinkedList<String> operators = temp.getOperators();
            JSONArray arrIfBlocks = new JSONArray();
            for(int i=0; i<ifblocks.size();i++){
                IFBlock ifblock = ifblocks.get(i);
                JSONObject obj = new JSONObject();
                obj.put("index", i);
                if(ifblock.getFunction() != null){
                    JSONObject func = fromFunctionToJSON(ifblock.getFunction());
                    obj.put("function", func);
                    obj.put("typeBlock", "function");
                }
                if(ifblock.getEvent() != null){
                    JSONObject event = fromFactTypeToJSON(ifblock.getEvent());
                    obj.put("event", event);
                    obj.put("typeBlock", "activity");
                }
                LinkedList<Binding> bindings = ifblock.getBindings();
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
            if(temp.getClass().isAssignableFrom(TemplateHA.class)){
                result.put("tempType", "TemplateHA");
                result.put("output", fromOutputHAToJSON(((TemplateHA)temp).getOutput()));
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
                }
            }
        } catch (JSONException ex) {
                Logger.getLogger(ConverterCoreBlocks.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result;
    }
    
    private static JSONObject fromBindingParameterToJSON(BindingParameter par){
        JSONObject result = new JSONObject();
        try {
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
                        Logger.getLogger(ConverterCoreBlocks.class.getName()).log(Level.SEVERE, null, ex);
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
                        bindFact.setFactId(json.getString("factId"));
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
                        Logger.getLogger(ConverterCoreBlocks.class.getName()).log(Level.SEVERE, null, ex);
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
                fLim.put("operator", lim.getOperator());
                fLim.put("value", lim.getValue());
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
                        Logger.getLogger(ConverterCoreBlocks.class.getName()).log(Level.SEVERE, null, ex);
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
                        Logger.getLogger(ConverterCoreBlocks.class.getName()).log(Level.SEVERE, null, ex);
                    }
        return result;
    }
    
    public static JSONObject fromOutputHAToJSON(OutputHA output){
        JSONObject result = new JSONObject();
        try{
            result.put("name", output.getName());
            result.put("bindings", fromListBindingsToJSON(output.getBindings()));
        } catch (JSONException ex) {
            Logger.getLogger(ConverterCoreBlocks.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result;
    }
    
    public static OutputHA fromJSONtoOutputHA(JSONObject json){
        OutputHA result = new OutputHA();
        try{
            result.setName(json.getString("name"));
            result.setBindings(fromJSONtoListBindings(json.getJSONArray("bindings")));
        } catch (JSONException ex) {
            Logger.getLogger(ConverterCoreBlocks.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result;
    }
    
    private static JSONArray fromListBindingsToJSON(LinkedList<Binding> bindings){
        JSONArray arrBindings = new JSONArray();
        try{
                for(int ii=0; ii<bindings.size();ii++){
                    JSONObject objBind = new JSONObject();
                    objBind.put("index", ii);
                    Binding binding = bindings.get(ii);
                    BindingParameter startBind = binding.getStartBinding();
                    BindingParameter endBind = binding.getEndBinding();
                    objBind.put("startBinding", fromBindingParameterToJSON(startBind));
                    objBind.put("endBinding", fromBindingParameterToJSON(endBind));
                    arrBindings.put(objBind);
                }
        } catch (JSONException ex) {
                        Logger.getLogger(ConverterCoreBlocks.class.getName()).log(Level.SEVERE, null, ex);
                    }
        return arrBindings;
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
            Logger.getLogger(ConverterCoreBlocks.class.getName()).log(Level.SEVERE, null, ex);
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
                        Logger.getLogger(ConverterCoreBlocks.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result;
    }
    
    public static JSONObject fromFactTypeToJSON(FactType facttype){
        JSONObject result = new JSONObject();
        try{
            result.put("className", facttype.getClassName());
            result.put("type", facttype.getType());
            System.out.println("-------------- get type in to json" +facttype.getType());
             if(facttype.getType().equals(Constants.ACTIVITY)){
                result.put("extends",Constants.EXTENDSACTIVITY );
            }
            if(facttype.getType().equals(Constants.TIME)){
                result.put("extends",Constants.EXTENDSTIME);
            }
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
                        Logger.getLogger(ConverterCoreBlocks.class.getName()).log(Level.SEVERE, null, ex);
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
                System.out.println(" ------ val cmt field " + field.getValue().getClass().getSimpleName());
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
            Logger.getLogger(ConverterCoreBlocks.class.getName()).log(Level.SEVERE, null, ex);
        }
        return objField;
    }
    
    public static FactType fromFactClassToFactType(Class<?> factClass, String uriField, String category){
        ArrayList<CMTField> cmtfields = new ArrayList<CMTField>();
        Field[] fields = factClass.getDeclaredFields();
        for(Field field : fields){
            CMTField cmtfield = new CMTField(field.getName(), getSimpleName(field.getType().getName()));
            cmtfields.add(cmtfield);
        }
        FactType type = new FactType(factClass.getSimpleName(), "fact", uriField, cmtfields);
        type.setCategory(category);
        return type;
    }
    
    public static FactType fromEventClassToFactType(Class<?> eventClass, boolean isActivity, boolean isCustom, String uriField, ArrayList<String> varList, String varFormat, String category){
        ArrayList<CMTField> cmtfields = new ArrayList<CMTField>();
        Field[] fields = eventClass.getDeclaredFields();
        for(Field field : fields){
            AInput anno = field.getAnnotation(AInput.class);
            if(anno != null){
                CMTField cmtfield = new CMTField(field.getName(), field.getType().getName());
                if(anno.input() == Input.Variable){
                    cmtfield.setIsVar(true);
                    cmtfield.setFormat(anno.format());
                    String[] ops = anno.options();
                    for(String option : ops){
                        cmtfield.addOption(option);
                    }
                }else{
                    cmtfield.setIsVar(false);
                }
                cmtfields.add(cmtfield);
            }
        }
        String typeEvent = "activity";
        if(!isActivity){
            typeEvent = "time";
        }
        
        FactType type = new FactType(eventClass.getSimpleName(), typeEvent, uriField, cmtfields);
        type.setCategory(category);
        type.setIsCustom(isCustom);
        type.setVarList(varList);
        type.setVarFormat(varFormat);
        
        return type;
    }
    
   
    
    public static String getSimpleName(String name){
        if(!name.contains("java")){
                    String[] splitLastPoint = name.split("\\.");
                    int z = splitLastPoint.length;
                    String simpleClassName = name;
                    if(z>0){
                        simpleClassName= splitLastPoint[z-1];
                    }
                    return simpleClassName;
        }
        return name;
    }
  public static String getSimpleNameAll(String name){
                    String[] splitLastPoint = name.split("\\.");
                    int z = splitLastPoint.length;
                    String simpleClassName = name;
                    if(z>0){
                        simpleClassName= splitLastPoint[z-1];
                    }
                    return simpleClassName;
  
    }
    
    public static String toUppercaseFirstLetter(String st){
       String sub1l = st.substring(0, 1);
        String up1l = sub1l.toUpperCase();
        String ret = up1l + st.substring(1);
        return ret;
    }
}
