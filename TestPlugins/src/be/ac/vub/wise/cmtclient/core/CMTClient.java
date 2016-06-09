/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ac.vub.wise.cmtclient.core;

import be.ac.vub.wise.cmtclient.blocks.ActionClient;
import be.ac.vub.wise.cmtclient.blocks.Fact;
import be.ac.vub.wise.cmtclient.blocks.FactType;
import be.ac.vub.wise.cmtclient.blocks.Function;
import be.ac.vub.wise.cmtclient.blocks.Event;
import be.ac.vub.wise.cmtclient.blocks.Rule;
import be.ac.vub.wise.cmtclient.blocks.TemplateActions;
import be.ac.vub.wise.cmtclient.blocks.TemplateHA;
import be.ac.vub.wise.cmtclient.util.Compilers;
import be.ac.vub.wise.cmtclient.util.Constants;
import be.ac.vub.wise.cmtclient.util.ConverterCoreBlocks;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.websocket.ClientEndpoint;
import javax.websocket.ContainerProvider;
import javax.websocket.DeploymentException;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author Sandra
 */
@ClientEndpoint
public class CMTClient {
    
      private static Session session;
    private static String url = Constants.URLCMT;
    private static final String wsUrl = Constants.WSCMT;
    private static final HashSet<CMTListener> listeners = new HashSet<>();
    
    public static void addListener(CMTListener lis){
        listeners.add(lis);
    }
    
    public static void startWS(){
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        try {
            URI uri = URI.create(wsUrl); // sub endpoint subscribe    
            container.connectToServer(CMTClient.class, uri);
        } catch (DeploymentException | IOException ex) {
              System.exit(-1);
        }
    }
    
    public static void setServerUrl(String url){
        CMTClient.url = url;
    }
    
    public static void shortcutAddFunctionInCMT(Class<?> classWithFunctions){
        try {
            JSONObject json = ConverterCoreBlocks.fromFunctionClassToJSON(classWithFunctions);
            String stjson = json.toString();
            System.out.println(stjson);
            String result = stjson.trim().trim(); 
            HttpResponse<String> request = Unirest.post(url+"/addFunctions").body(result).asString();
        } catch (UnirestException ex) {
            Logger.getLogger(CMTClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static void shortcutRegisterFacttypeInCMT(Class<?> factClass, String uriField, String category){
        try {
            FactType type = ConverterCoreBlocks.fromFactClassToFactType(factClass, uriField, category);
            JSONObject json = ConverterCoreBlocks.fromFactTypeToJSON(type);
            String stjson = json.toString();
            String result = stjson.trim().trim();
            System.out.println("TP>>>> Outbound JSON: " + result);
            HttpResponse<String> request = Unirest.post(url+"/registerFactClass").body(result).asString();
        } catch (UnirestException ex) {
            Logger.getLogger(CMTClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void shortcutAddFactInCMT(Object object){ // not using with sql db!
        Fact fact = ConverterCoreBlocks.fromObjectToFactInstance(object);
        addFactInCMT(fact);
//        try {
//            JSONObject json = ConverterCoreBlocks.fromFactObjectToJSON(object);
//            String stjson = json.toString();
//            String result = stjson.trim().trim();
//            HttpResponse<String> request = Unirest.post(url+"/addFact").body(result).asString();
//        } catch (UnirestException ex) {
//            Logger.getLogger(CMTClient.class.getName()).log(Level.SEVERE, null, ex);
//        }
    }
    
    public static void addFactInCMT(Fact object){
        try {
            JSONObject json = ConverterCoreBlocks.fromFactToJSON(object);
            String stjson = json.toString();
            String result = stjson.trim().trim();
            System.out.println(" -- " + result);
            HttpResponse<String> request = Unirest.post(url+"/addFactInFactFormat").body(result).asString();
        } catch (UnirestException ex) {
            Logger.getLogger(CMTClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    // @param: format: vb. "00:00" in geval van tijd
    public static void shortcutRegisterEventInCMT(Object object, boolean isActivity, boolean isCustom, String uriField, ArrayList<String> varList, String varFormat, String category){
        FactType event = ConverterCoreBlocks.fromEventClassToFactType(object.getClass(), isActivity, isCustom, uriField, varList, varFormat, category);
        registerEventTypeInCMT(event);
    }
    
    // verander metamodel!
    public static void registerEventTypeInCMTbyExample(Event event){
        try {
            JSONObject json = ConverterCoreBlocks.fromEventToJSON(event);
            String stjson = json.toString();
            String result = stjson.trim().trim();
            HttpResponse<String> request = Unirest.post(url+"/registerEventClass").body(result).asString();
            System.out.println(request.getCode());
        } catch (UnirestException ex) {
            Logger.getLogger(CMTClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    // Convert Eventobject to JSON
    public static void shortcutAddEventInCMT(Object object){
        try {
            JSONObject json = ConverterCoreBlocks.fromFactObjectToJSON(object);
            String stjson = json.toString();
            String result = stjson.trim().trim();
            HttpResponse<String> request = Unirest.post(url+"/addEvent").body(result).asString();
        } catch (UnirestException ex) {
            Logger.getLogger(CMTClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static void addRuleInCMT(String name, String drlRule){
        JSONObject json = new JSONObject();
        try {
            json.put("name", name);
            json.put("drlRule", drlRule);
            String stjson = json.toString();
            String result = stjson.trim().trim();
            HttpResponse<String> request = Unirest.post(url+"/addRule").body(result).asString();
        } catch (JSONException ex) {
            Logger.getLogger(CMTClient.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnirestException ex) {
            Logger.getLogger(CMTClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static void addTemplateActionsInCMT(TemplateActions temp){
        JSONObject json = ConverterCoreBlocks.fromTemplateToJSON(temp);
        String stjson = json.toString();
        String result = stjson.trim().trim();
        try {
            HttpResponse<String> request = Unirest.post(url+"/addTemplateActions").body(result).asString();
            System.out.println(request.getCode());
        } catch (UnirestException ex) {
            Logger.getLogger(CMTClient.class.getName()).log(Level.SEVERE, null, ex);
        }    
    }
    
    public static void addTemplateHAInCMT(TemplateHA temp){
        JSONObject json = ConverterCoreBlocks.fromTemplateToJSON(temp);
        String stjson = json.toString();
        String result = stjson.trim().trim();
        try {
            HttpResponse<String> request = Unirest.post(url+"/addTemplateHA").body(result).asString();
            System.out.println(request.getCode());
        } catch (UnirestException ex) {
            Logger.getLogger(CMTClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static void addActionInCMT(ActionClient action){
        JSONObject json = ConverterCoreBlocks.fromActionToJSON(action);
        String stjson = json.toString();
        String result = stjson.trim().trim();
        try {
            HttpResponse<String> request = Unirest.post(url+"/addAction").body(result).asString();
        } catch (UnirestException ex) {
            Logger.getLogger(CMTClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
     /*
    all types of facts not events
    */
    public static HashSet<FactType> getAvailableFactTypes(){
        HashSet<FactType> result = new HashSet<FactType>();
        try {
            HttpResponse<String> request = Unirest.get(url+"/getAllAvailableFactTypes").asString();
            System.out.println("------- " + request.getBody().trim().trim());
            JSONObject urlRes =  new JSONObject(request.getBody().trim().trim());
            JSONArray types = urlRes.getJSONArray("types");
            for(int i = 0; i<types.length();i++){
                JSONObject type = types.getJSONObject(i);
                FactType facttype = ConverterCoreBlocks.fromJSONtoFactTypeFact(type);
                result.add(facttype);
            }
            
        } catch (UnirestException ex) {
            Logger.getLogger(CMTClient.class.getName()).log(Level.SEVERE, null, ex);
        }   catch (JSONException ex) {
                Logger.getLogger(CMTClient.class.getName()).log(Level.SEVERE, null, ex);
            }
        return result;
    }
    
    public static FactType getFactType(String className){
        FactType type = ConverterCoreBlocks.getFactType(className);
        return type;
    }
    
    public static Fact getFact(String className, String uriField, String value){ // verander fact terug
        try {
            
            HttpResponse<JsonNode> request = Unirest.get(url+"/getFact/"+className+"/"+uriField+"/"+ value).asJson();
            JSONObject urlRes = request.getBody().getObject();
            Fact fact = ConverterCoreBlocks.fromJSONtoFact(urlRes);
            return fact;
        } catch (UnirestException ex) {
            Logger.getLogger(CMTClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    public static Fact getFact(int id){ 
        try {
            
            HttpResponse<JsonNode> request = Unirest.get(url+"/getFactId/"+id).asJson();
            JSONObject urlRes = request.getBody().getObject();
            Fact fact = ConverterCoreBlocks.fromJSONtoFact(urlRes);
            return fact;
        } catch (UnirestException ex) {
            Logger.getLogger(CMTClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    public static HashSet<Fact> getFactsOfType(FactType factType){
        HashSet<Fact> result = new HashSet<Fact>();
        try {
            HttpResponse<String> request = Unirest.get(url+"/getAllFactsWithType/"+factType.getClassName()).asString();
            JSONObject urlRes = new JSONObject(request.getBody().trim().trim());
            JSONArray facts = urlRes.getJSONArray("facts");
            for(int i = 0; i<facts.length();i++){
                JSONObject factO = facts.getJSONObject(i);
                Fact fact = ConverterCoreBlocks.fromJSONtoFact(factO);
                result.add(fact);
            }
        } catch (UnirestException ex) {
            Logger.getLogger(CMTClient.class.getName()).log(Level.SEVERE, null, ex);
        }   catch (JSONException ex) {
                System.out.println("-- cause " + ex.getMessage());
                Logger.getLogger(CMTClient.class.getName()).log(Level.SEVERE, null, ex);
                
            }
        return result;
    }
    
    // {"className":<>, "type":< fact || time || activity> , "activityCustom": boolean (only if activity type), "varList": [{var:<>}, ..], "varFormat": <>, 
    // "uriField":<>, "fields":[{"fieldName":<>, "fieldType":<>}]}
    public static HashSet<FactType> getAvailableEventTypes(){
        HashSet<FactType> result = new HashSet<FactType>();
        try {
            HttpResponse<String> request = Unirest.get(url+"/getAllAvailableEventTypes").asString();
            JSONObject urlRes = new JSONObject(request.getBody().trim().trim());
            JSONArray types = urlRes.getJSONArray("types");
            for(int i = 0; i<types.length();i++){
                JSONObject type = types.getJSONObject(i);
                FactType facttype = ConverterCoreBlocks.fromJSONtoFactTypeEvent(type);
                result.add(facttype);
            }
            
        } catch (UnirestException ex) {
            Logger.getLogger(CMTClient.class.getName()).log(Level.SEVERE, null, ex);
        }   catch (JSONException ex) {
                Logger.getLogger(CMTClient.class.getName()).log(Level.SEVERE, null, ex);
            }
        return result;
    }
    
    public static HashSet<Fact> getAvailableFacts(){
        HashSet<Fact> result = new HashSet<Fact>();
        try {
            HttpResponse<String> request = Unirest.get(url+"/getAllFacts").asString();
            JSONObject urlRes = new JSONObject( request.getBody().trim().trim());
            JSONArray facts = urlRes.getJSONArray("facts");
            for(int i = 0; i<facts.length();i++){
                JSONObject factO = facts.getJSONObject(i);
                Fact fact = ConverterCoreBlocks.fromJSONtoFact(factO);
                result.add(fact);
            }
        } catch (UnirestException ex) {
            Logger.getLogger(CMTClient.class.getName()).log(Level.SEVERE, null, ex);
        }   catch (JSONException ex) {
                Logger.getLogger(CMTClient.class.getName()).log(Level.SEVERE, null, ex);
            }
        return result;
    }
    
    public static HashSet<TemplateActions> getAvailableTemplateActions(){
        HashSet<TemplateActions> result = new HashSet<TemplateActions>();
        try {
            HttpResponse<JsonNode> request = Unirest.get(url+"/getAllAvailableTemplateActions").asJson();
            JSONObject urlRes = request.getBody().getObject();
            JSONArray temps = urlRes.getJSONArray("templates");
            for(int i = 0; i<temps.length();i++){
                JSONObject tempO = temps.getJSONObject(i);
                TemplateActions temp = ConverterCoreBlocks.fromJSONtoTemplateAction(tempO);
                result.add(temp);
            }
            
        } catch (UnirestException ex) {
            Logger.getLogger(CMTClient.class.getName()).log(Level.SEVERE, null, ex);
        } catch (JSONException ex) {
            Logger.getLogger(CMTClient.class.getName()).log(Level.SEVERE, null, ex);
        }  
        return result;
    }
    
    public static HashSet<TemplateHA> getAvailableTemplateHA(){
        HashSet<TemplateHA> result = new HashSet<TemplateHA>();
        try {
            HttpResponse<String> request = Unirest.get(url+"/getAllAvailableTemplateHA").asString();
            System.out.println("antwoord -------------- " + request.getBody());
            JSONObject urlRes = new JSONObject(request.getBody());
            JSONArray temps = urlRes.getJSONArray("templates");
            for(int i = 0; i<temps.length();i++){
                JSONObject tempO = temps.getJSONObject(i);
                TemplateHA temp = ConverterCoreBlocks.fromJSONtoTemplateHA(tempO);
                result.add(temp);
            }
        } catch (UnirestException ex) {
            Logger.getLogger(CMTClient.class.getName()).log(Level.SEVERE, null, ex);
        } catch (JSONException ex) {
            Logger.getLogger(CMTClient.class.getName()).log(Level.SEVERE, null, ex);
        }  
        return result;
    }
    
    public static HashSet<Rule> getAvailableRules(){
        HashSet<Rule> result = new HashSet<Rule>();
        try {
            HttpResponse<JsonNode> request = Unirest.get(url+"/getAllRules").asJson();
            JSONObject urlRes = request.getBody().getObject();
            JSONArray arrRules = urlRes.getJSONArray("rules");
            for(int i=0; i<arrRules.length(); i++){
                JSONObject ruleO = arrRules.getJSONObject(i);
                Rule rule = ConverterCoreBlocks.fromJSONtoRule(ruleO);
                result.add(rule);
            }
        } catch (UnirestException ex) {
            Logger.getLogger(CMTClient.class.getName()).log(Level.SEVERE, null, ex);
        }   catch (JSONException ex) {  
                Logger.getLogger(CMTClient.class.getName()).log(Level.SEVERE, null, ex);
            }  
        return result;
    }
    
    public static HashSet<Function> getAvailableFunctions(){
        HashSet<Function> result = new HashSet<Function>();
        try {
            HttpResponse<JsonNode> request = Unirest.get(url+"/getAllFunctions").asJson();
            JSONObject urlRes = request.getBody().getObject();
            JSONArray arrFunc = urlRes.getJSONArray("functions");
            for(int i=0; i<arrFunc.length(); i++){
                JSONObject funcO = arrFunc.getJSONObject(i);
                Function func = ConverterCoreBlocks.fromJSONtoFunction(funcO);
                result.add(func);
            }
        } catch (UnirestException ex) {
            Logger.getLogger(CMTClient.class.getName()).log(Level.SEVERE, null, ex);
        }   catch (JSONException ex) {  
                Logger.getLogger(CMTClient.class.getName()).log(Level.SEVERE, null, ex);
            }  
        return result;
    }
    
    public static Function getFunctionWithName(String name){
        HashSet<Function> functions = getAvailableFunctions();
        for(Function func:functions){
            if(func.getName().equals(name)){
                return func;
            }
        }
        return null;
    }
    
    public static HashSet<ActionClient> getAvailableActions(){
        HashSet<ActionClient> result = new HashSet<ActionClient>();
        try {
            HttpResponse<JsonNode> request = Unirest.get(url+"/getAllActions").asJson();
            JSONObject urlRes = request.getBody().getObject();
            JSONArray arrAct = urlRes.getJSONArray("actions");
            for(int i=0; i<arrAct.length(); i++){
                JSONObject actO = arrAct.getJSONObject(i);
                ActionClient act = ConverterCoreBlocks.fromJSONtoAction(actO);
                result.add(act);
            }
        } catch (UnirestException ex) {
            Logger.getLogger(CMTClient.class.getName()).log(Level.SEVERE, null, ex);
        }   catch (JSONException ex) {  
                Logger.getLogger(CMTClient.class.getName()).log(Level.SEVERE, null, ex);
            }  
        return result;
    }
    
    public static void registerEventTypeInCMT(FactType eventType){
        try {
            JSONObject json = ConverterCoreBlocks.fromFactTypeToJSON(eventType);
            String stjson = json.toString();
            String result = stjson.trim().trim();
            HttpResponse<String> request = Unirest.post(url+"/registerEventClass").body(result).asString();
        } catch (UnirestException ex) {
            Logger.getLogger(CMTClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public static void registerFacttypeInCMT(FactType type){
        try {
            
            JSONObject json = ConverterCoreBlocks.fromFactTypeToJSON(type);
            String stjson = json.toString();
            String result = stjson.trim().trim();
            HttpResponse<String> request = Unirest.post(url+"/registerFactClass").body(result).asString();
        } catch (UnirestException ex) {
            Logger.getLogger(CMTClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public static FactType getFactTypeFactWithName(String name){
        try{
            HttpResponse<String> request = Unirest.get(url+"/getFactTypeFact/"+name).asString();
            JSONObject urlRes = new JSONObject(request.getBody());
            System.out.println("-------- return facttype " + urlRes);
            FactType facttype = ConverterCoreBlocks.fromJSONtoFactTypeFact(urlRes);
            return facttype;
        } catch (UnirestException ex) {
            Logger.getLogger(CMTClient.class.getName()).log(Level.SEVERE, null, ex);
        } catch (JSONException ex) {
              Logger.getLogger(CMTClient.class.getName()).log(Level.SEVERE, null, ex);
          }
        return null;
    }
    
    public static void createNewActivity(TemplateHA temp){
        FactType newActType = Compilers.createNewActivity(temp);
        registerEventTypeInCMT(newActType);
        Rule rule = Compilers.compileDrlRuleActivity(temp);
        addRuleInCMT(rule.getName(), rule.getDrlRule());
    }
    
    public static void createNewRule(TemplateActions temp){
        Rule rule = Compilers.compileDrlRuleActivity(temp);
        addRuleInCMT(rule.getName(), rule.getDrlRule());
    }
    
    public static String getSimpleTypeName(String name){
        if(name.contains("java")){
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
    @OnOpen
    public void onOpen(Session session) {
    this.session = session;
    }

    @OnMessage
    public void onMessage(String st) {
        System.out.println("WebSocket message Received!  " + st);
        if(st.startsWith("{")){    
            try {
                JSONObject input = new JSONObject(st);
                String action = input.getString("action");
                switch(action){
                    case "registerFactClass":
                        FactType type = ConverterCoreBlocks.fromJSONtoFactTypeFact(input.getJSONObject("object"));
                        for(CMTListener lis : listeners){
                            lis.newFactTypeAdded(type);
                        }
                        break;
                    case "newFact":
                        JSONObject objectReq = input.getJSONObject("object");
                        Fact fact = ConverterCoreBlocks.fromJSONtoFact(objectReq);
                        for(CMTListener lis : listeners){
                            lis.newFactAdded(fact);
                        }
                        break;
                    case "registerEventClass":
                        FactType type2 = ConverterCoreBlocks.fromJSONtoFactTypeEvent(input.getJSONObject("object"));
                        for(CMTListener lis : listeners){
                            lis.newEventTypeAdded(type2);
                        }
                        break;
                    case "addEvent":
                     /*   JSONObject objectReq2 = input.getJSONObject("object");
                        HttpResponse<String> request = Unirest.get(url+"/getFactTypeEvent/"+ objectReq2.getString("className")).asString();
                        System.out.println(request.getBody());
                    /*    JSONObject urlRes = new JSONObject(request.getBody());
                        //JSONObject urlRes = request.getBody().getObject();
                        FactType type3 = ConverterCoreBlocks.convertFactTypeFromEvent(urlRes.getJSONObject("object"));
                        Event event = ConverterCoreBlocks.convertEventObject(objectReq2.getString("className"),objectReq2.getJSONObject("object"), type3);
                        for(CMTListener lis : listeners){
                            lis.newEventAdded(event);
                        }
                      */  break;
                    case "addFunction":
                        Function func = ConverterCoreBlocks.fromJSONtoFunction(input.getJSONObject("object"));
                        for(CMTListener lis : listeners){
                            lis.newFunctionAdded(func);
                        }
                        break;
                    case "addRule":
                        Rule rule = ConverterCoreBlocks.fromJSONtoRule(input.getJSONObject("object"));
                        for(CMTListener lis : listeners){
                            lis.newRuleAdded(rule);
                        }
                        break;
                    case "addTemplateActions":
                        TemplateActions temp = ConverterCoreBlocks.fromJSONtoTemplateAction(input.getJSONObject("object"));
                        for(CMTListener lis : listeners){
                            lis.newTemplateActionsAdded(temp);
                        }
                        break;
                    case "addTemplateHA":
                        System.out.println("--------------- in noti lis");
                        TemplateHA temp2 = ConverterCoreBlocks.fromJSONtoTemplateHA(input.getJSONObject("object"));
                        System.out.println("--------------- in noti lis " + temp2.getName());
                        for(CMTListener lis : listeners){
                            lis.newTemplateHAAdded(temp2);
                        }
                        break;
                    case "addAction":
                        ActionClient act = ConverterCoreBlocks.fromJSONtoAction(input.getJSONObject("object"));
                        for(CMTListener lis : listeners){
                            lis.actionAdded(act);
                        }
                        break;
                    case "currentContext":
                        JSONObject objectReq3 = input.getJSONObject("object");
                        Event event4 = ConverterCoreBlocks.fromJSONtoEvent(objectReq3);
                        for(CMTListener lis : listeners){
                            lis.currentContext(event4);
                        }
                        break;
                }
                
            } catch (JSONException ex) {
                Logger.getLogger(CMTClient.class.getName()).log(Level.SEVERE, null, ex);
            } 
        }else{
            System.out.println("no json");
        }
    }

    @OnClose
    public void onClose() {
        startWS();
    }
}
