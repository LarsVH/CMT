/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ac.vub.wise.cmtserver.restfull;
import be.ac.vub.wise.cmtserver.core.CMTDelegator;
import be.ac.vub.wise.cmtserver.blocks.Action;
import be.ac.vub.wise.cmtserver.blocks.ActionClient;
import be.ac.vub.wise.cmtserver.blocks.ActionField;
import be.ac.vub.wise.cmtserver.blocks.ActionFieldAnno;
import be.ac.vub.wise.cmtserver.blocks.Activity;
import be.ac.vub.wise.cmtserver.blocks.Binding;
import be.ac.vub.wise.cmtserver.blocks.BindingInputFact;
import be.ac.vub.wise.cmtserver.blocks.BindingParameter;
import be.ac.vub.wise.cmtserver.blocks.EventVariables;
import be.ac.vub.wise.cmtserver.blocks.FactType;
import be.ac.vub.wise.cmtserver.blocks.Function;
import be.ac.vub.wise.cmtserver.blocks.IFBlock;
import be.ac.vub.wise.cmtserver.blocks.IFactType;
import be.ac.vub.wise.cmtserver.blocks.IFunctionClass;
import be.ac.vub.wise.cmtserver.blocks.Parameters;
import be.ac.vub.wise.cmtserver.blocks.Rule;
import be.ac.vub.wise.cmtserver.blocks.TemplateActions;
import be.ac.vub.wise.cmtserver.blocks.TemplateHA;
import be.ac.vub.wise.cmtserver.blocks.UriFactType;
import be.ac.vub.wise.cmtserver.util.Constants;
import be.ac.vub.wise.cmtserver.util.Converter;
import com.google.gson.Gson;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.json.JSONArray;
import org.json.JSONObject;
/**
 *
 * @author Sandra
 */
@Path("/cmt")
public class CMTRest {
    private static final String wsUrl = Constants.URLWS;
    
    @GET
    @Path("/resetCMT")
    @Produces("application/json")
    public Response resetCMT() {
        JSONObject obj = new JSONObject();
        CMTCore.get().resetCMT();
        obj.put("state", "ok");
        return Response.status(201).entity(obj.toString()).build() ;
    }
    
    
    @POST // input JSON {"className":<simple name>, "uriField":<fieldname>, "fields":[{"fieldName":<name>, "type":<binairy class name>}, ... ]}
    @Path("/registerFactClass")
    @Produces("application/json")
    @Consumes(MediaType.TEXT_PLAIN) 
    public Response registerFactClass(String input){
        JSONObject in = new JSONObject(input);
        CMTCore.get().registerFactClass(in);
        notify("registerFactClass", in);
        JSONObject obj = new JSONObject();
        obj.put("status", "ok");
        return Response.status(201).entity(obj.toString()).build();
    }

    @POST // input JSON {"className":<simple name>, "object":{...}}
    @Path("/addFact")
    @Produces("application/json")
    @Consumes(MediaType.TEXT_PLAIN) 
    public Response addFact(String input){
        JSONObject in = new JSONObject(input);
        JSONObject toReturn = CMTCore.get().addFact(in);
        JSONObject ret = new JSONObject();
        ret.put("object", toReturn);
        notify("newFact", ret);
        JSONObject obj = new JSONObject();
        obj.put("status", "ok");
        return Response.status(201).entity(obj.toString()).build();
    }
    
    @POST // input JSON Fact to json
    @Path("/addFactInFactFormat")
    @Produces("application/json")
    @Consumes(MediaType.TEXT_PLAIN) 
    public Response addFactInFactFormat(String input){
        JSONObject in = new JSONObject(input);
        JSONObject toNotify = CMTCore.get().addFactInFactFormat(in);
        notify("newFact", toNotify);
        JSONObject obj = new JSONObject();
        obj.put("status", "ok");
        return Response.status(201).entity(obj.toString()).build();
    }
    
    @POST // input JSON {"className":<simple name>, "extends":<time or activity>, "activityCustom": <boolean> (if time == false),
    //"uriField":<fieldname>, "varList":[<list of strings>{"var":<label>}], "varFormat":<format>, "fields":[{"fieldName":<name>, "type":<binairy class name>}, ... ]}    
    @Path("/registerEventClass")
    @Produces("application/json")
    @Consumes(MediaType.TEXT_PLAIN) 
    public Response registerEventClass(String input){
        JSONObject in = new JSONObject(input);
        CMTCore.get().registerEventClass(in);
        notify("registerEventClass", in);
        JSONObject obj = new JSONObject();
        obj.put("status", "ok");
        return Response.status(201).entity(obj.toString()).build();
    }
    
    
    @POST // input JSON {"className":<name>, "methods":[{"method":<string of method>}]}
    @Path("/addFunctions")
    @Produces("application/json")
    @Consumes(MediaType.TEXT_PLAIN) 
    public Response addFunctions(String input){
        JSONObject in = new JSONObject(input);
        
        CMTCore.get().registerFunctionClass(in);
        JSONArray arrMethods = in.getJSONArray("methods");
        for(int i=0; i<arrMethods.length(); i++){
            JSONObject met = arrMethods.getJSONObject(i);
            notify("addFunction", met);
        }
        JSONObject obj = new JSONObject();
        obj.put("status", "ok");
        return Response.status(201).entity(obj.toString()).build();
    }
    
    @POST // input JSON {"className":<simple name>, "object":{...}}
    @Path("/addEvent")
    @Produces("application/json")
    @Consumes(MediaType.TEXT_PLAIN) 
    public Response addEvent(String input){
        JSONObject in = new JSONObject(input);
        CMTCore.get().addEvent(in);
       // notify("addEvent", in);
        JSONObject obj = new JSONObject();
        obj.put("status", "ok");
        return Response.status(201).entity(obj.toString()).build();
    }
    
    @POST // input JSON {"className":<simple name>, "fields":[{...}]}
    @Path("/addActivity")
    @Produces("application/json")
    @Consumes(MediaType.TEXT_PLAIN) 
    public Response addActivity(String input){
        JSONObject in = new JSONObject(input);
        CMTCore.get().addActivity(in);
       // notify("addEvent", in);
        JSONObject obj = new JSONObject();
        obj.put("status", "ok");
        return Response.status(201).entity(obj.toString()).build();
    }
   
    // TODO add event in event format
    
    @POST // input JSON {"name":<name>, "drlRule":<rule>}
    @Path("/addRule")
    @Produces("application/json")
    @Consumes(MediaType.TEXT_PLAIN) 
    public Response addRule(String input){
        JSONObject in = new JSONObject(input);
      
        CMTCore.get().addRule(in);
        notify("addRule", in);
        JSONObject obj = new JSONObject();
        obj.put("status", "ok");
        return Response.status(201).entity(obj.toString()).build();
    }
    
    @GET
    @Path("/getAllRules")
    @Produces("application/json")
    public Response getAllRules() {
        JSONObject obj = new JSONObject();
        HashSet<Rule> rules = CMTDelegator.get().getRules();
        JSONArray arr = new JSONArray();
        for(Rule rule : rules){
            JSONObject ob = Converter.fromRuleToJSON(rule);
            arr.put(ob);
        }
        obj.put("rules", arr);
        return Response.status(201).entity(obj.toString()).build() ;
    }
    
    @POST // input JSON of Template object
    @Path("/addTemplateActions")
    @Produces("application/json")
    @Consumes(MediaType.TEXT_PLAIN) 
    public Response addTemplateActions(String input){
        JSONObject in = new JSONObject(input);
       
        CMTCore.get().addTemplateActions(in);
        notify("addTemplateActions", in);
        JSONObject obj = new JSONObject();
        obj.put("status", "ok");
        return Response.status(201).entity(obj.toString()).build();
    }
    
    @POST // input JSON of Template object // manually
    @Path("/addTemplateHA")
    @Produces("application/json")
    @Consumes(MediaType.TEXT_PLAIN) 
    public Response addTemplateHA(String input){
        JSONObject in = new JSONObject(input);
        
        CMTCore.get().addTemplateHA(in);
        notify("addTemplateHA", in);
        JSONObject obj = new JSONObject();
        obj.put("status", "ok");
        return Response.status(201).entity(obj.toString()).build();
    }
    
    @GET
    @Path("/getAllAvailableTemplateActions")
    @Produces("application/json")
    public Response getAllAvailableTemplateActions() { // manually
        JSONObject obj = new JSONObject();
        HashSet<TemplateActions> temps = CMTDelegator.get().getAllTemplateActions();
        JSONArray arr = new JSONArray();
        for(TemplateActions temp : temps){
            JSONObject json = Converter.fromTemplateToJSON(temp);
            arr.put(json);
        }
        obj.put("templates", arr);
        return Response.status(201).entity(obj.toString()).build() ;
    }
    
    @GET
    @Path("/getAllAvailableTemplateHA")
    @Produces("application/json")
    public Response getAllAvailableTemplateHA() { // manually
        JSONObject obj = new JSONObject();
        HashSet<TemplateHA> temps = CMTDelegator.get().getAllTemplateHA();
        
        JSONArray arr = new JSONArray();
        for(TemplateHA temp : temps){
            
            JSONObject json = Converter.fromTemplateToJSON(temp);
            arr.put(json);
        }
        obj.put("templates", arr);
        return Response.status(201).entity(obj.toString()).build() ;
    }
    
    @POST // input {"className":<>, "fields":[{"fieldName": <>, "varList":<[{"var":<string>}, ...]>, "varFormat":<format>} field type can only be string!]}
    @Path("/addAction")
    @Produces("application/json")
    @Consumes(MediaType.TEXT_PLAIN) 
    public Response addAction(String input){
        JSONObject in = new JSONObject(input);
        
        CMTCore.get().addAction(in);
        notify("addAction", in);
        JSONObject obj = new JSONObject();
        obj.put("status", "ok");
        return Response.status(201).entity(obj.toString()).build();
    }
    
    @GET
    @Path("/getAllActions")
    @Produces("application/json")
    public Response getAllActions() {
        JSONObject obj = new JSONObject();
        HashSet<Action> actions = CMTDelegator.get().getAllActions();
        JSONArray arr = new JSONArray();
        for(Action action : actions){
            ActionClient actCl = Converter.fromActionObjectToActionClient(action);
            System.out.println(" actCl size fields " + actCl.getFields().size());
            JSONObject jsonAct = Converter.fromActionToJSON(actCl);
            arr.put(jsonAct);

        }
        obj.put("actions",arr );
        return Response.status(201).entity(obj.toString()).build() ;
    }
    
    
    @GET // ONLY FACTS  {"className":<>, "type":< fact || time || activity> , "activityCustom": boolean (only if activity type), "varList": [{var:<>}, ..], "varFormat": <>, 
    // "uriField":<>, "fields":[{"fieldName":<>, "fieldType":<>}]}
    @Path("/getAllAvailableFactTypes")
    @Produces("application/json")
    public Response getAllAvailableFactTypes() {
        JSONObject obj = new JSONObject();
        HashSet<FactType> types = CMTDelegator.get().getAvailableFactTypes();
        JSONArray arr = new JSONArray();
        for(FactType fact : types){
            JSONObject ob = Converter.fromFactTypeToJSON(fact);
            arr.put(ob);
        }
        obj.put("types", arr);
        return Response.status(201).entity(obj.toString()).build() ;
    }
    
    @GET // ONLY EVENTS  {"className":<>, "type":< fact || time || activity> , "activityCustom": boolean (only if activity type), "varList": [{var:<>}, ..], "varFormat": <>, 
    // "uriField":<>, "fields":[{"fieldName":<>, "fieldType":<>}]}
    @Path("/getAllAvailableEventTypes")
    @Produces("application/json")
    public Response getAllAvailableEventTypes() {
        JSONObject obj = new JSONObject();
        HashSet<FactType> types = CMTDelegator.get().getAvailableEventTypes();
        JSONArray arr = new JSONArray();
        for(FactType fact : types){
            JSONObject ob = Converter.fromFactTypeToJSON(fact);
            arr.put(ob);
        }
        obj.put("types", arr);
        return Response.status(201).entity(obj.toString()).build() ;
    }
    
    @GET // {"className":<>, "type":< fact || time || activity> , "activityCustom": boolean (only if activity type), "varList": [{var:<>}, ..], "varFormat": <>, 
    // "uriField":<>, "fields":[{"fieldName":<>, "fieldType":<>}]}
    @Path("/getFactTypeEvent/{className}")
    @Produces("application/json")
    public Response getFactTypeEvent(@PathParam("className") String name) {
        
        FactType type = CMTDelegator.get().getFactTypeWithName(Constants.PACKAGEEVENTS +"."+name);
        JSONObject ob = Converter.fromFactTypeToJSON(type);
        
        return Response.status(201).entity(ob.toString()).build() ;
    }
    
    @GET // {"className":<>, "type":< fact || time || activity> , "activityCustom": boolean (only if activity type), "varList": [{var:<>}, ..], "varFormat": <>, 
    // "uriField":<>, "fields":[{"fieldName":<>, "fieldType":<>}]}
    @Path("/getFactTypeFact/{className}")
    @Produces("application/json")
    public Response getFactTypeFact(@PathParam("className") String name) {
       
        FactType type = CMTDelegator.get().getFactTypeWithName(name);
        
        JSONObject ob = Converter.fromFactTypeToJSON(type);
        
        return Response.status(201).entity(ob.toString()).build() ;
    }
    
    @GET // {"className":<>, "type":< fact || time || activity> , "activityCustom": boolean (only if activity type), "varList": [{var:<>}, ..], "varFormat": <>, 
    // "uriField":<>, "fields":[{"fieldName":<>, "fieldType":<>}]}
    @Path("/getFact/{className}/{uriField}/{uriValue}")
    @Produces("application/json")
    public Response getFact(@PathParam("className") String className, @PathParam("uriField") String uriField, @PathParam("uriValue") String uriValue) {
        IFactType fact = CMTDelegator.get().getFact(Constants.PACKAGEFACTS +"."+className, uriField, uriValue);
        JSONObject ob = Converter.fromFactToJSON(Converter.fromObjectToFactInstance(fact));
        return Response.status(201).entity(ob.toString()).build() ;
    }
    
    @GET 
    @Path("/getAllFactsWithType/{className}")
    @Produces("application/json")
    public Response getAllFactsWithType(@PathParam("className") String className) {
        JSONObject obj = new JSONObject();
        HashSet<IFactType> facts = CMTDelegator.get().getFactsWithType(className);
        JSONArray arr = new JSONArray();
        for(IFactType fact : facts){
            JSONObject ob = Converter.fromFactToJSON(Converter.fromObjectToFactInstance(fact));
            arr.put(ob);
        }
        obj.put("facts", arr);
        return Response.status(201).entity(obj.toString()).build() ;
    }
    
    @GET // {"className" , "uriField" , "object"}
    @Path("/getAllFacts")
    @Produces("application/json")
    public Response getAllFacts() {
        JSONObject obj = new JSONObject();
        HashSet<IFactType> facts = CMTDelegator.get().getAllFacts();
        JSONArray arr = new JSONArray();
        for(IFactType fact : facts){
            JSONObject ob = Converter.fromFactToJSON(Converter.fromObjectToFactInstance(fact));
            arr.put(ob);
        }
        obj.put("facts", arr);
        return Response.status(201).entity(obj.toString()).build() ;
    }
    
    @GET
    @Path("/getAllFunctions")
    @Produces("application/json")
    public Response getAllFunctions() {
        JSONObject obj = new JSONObject();
        HashSet<Function> functions = CMTDelegator.get().getAllFunctions();
        JSONArray arrMets = new JSONArray();
        for(Function func : functions){
            JSONObject funcOb = Converter.fromFunctionToJSON(func);
            arrMets.put(funcOb);
            
        }

        obj.put("functions", arrMets);
        return Response.status(201).entity(obj.toString()).build() ;
    }
    
    @POST // input JSON Filled in Template
    @Path("/compileAndAddRule")
    @Produces("application/json")
    @Consumes(MediaType.TEXT_PLAIN) 
    public Response compileAndAddRule(String input){
        JSONObject in = new JSONObject(input);
        Rule rule = CMTCore.get().compileAndAddRule(in);
        notify("newRule", Converter.fromRuleToJSON(rule));
        JSONObject obj = new JSONObject();
        obj.put("status", "ok");
        return Response.status(201).entity(obj.toString()).build();
    }
    
    
    public static void notify(String action, JSONObject ob){
        JSONObject pub = new JSONObject();
        pub.put("action", action);
        pub.put("object", ob);
        try {
            HttpResponse<String> request = Unirest.post(wsUrl).body(pub.toString()).asString();
            System.out.println(" object send : " + ob);
        } catch (UnirestException ex) {
            Logger.getLogger(CMTRest.class.getName()).log(Level.SEVERE, null, ex);
        }
    
    }
    
}
