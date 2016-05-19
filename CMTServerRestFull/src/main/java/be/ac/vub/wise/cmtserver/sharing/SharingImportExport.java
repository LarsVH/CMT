package be.ac.vub.wise.cmtserver.sharing;

import be.ac.vub.wise.cmtserver.blocks.Binding;
import be.ac.vub.wise.cmtserver.blocks.BindingInputBlock;
import be.ac.vub.wise.cmtserver.blocks.BindingParameter;
import be.ac.vub.wise.cmtserver.blocks.Fact;
import be.ac.vub.wise.cmtserver.blocks.FactType;
import be.ac.vub.wise.cmtserver.blocks.Function;
import be.ac.vub.wise.cmtserver.blocks.IFBlock;
import be.ac.vub.wise.cmtserver.blocks.IFactType;
import be.ac.vub.wise.cmtserver.blocks.IFunctionClass;
import be.ac.vub.wise.cmtserver.blocks.OutputHA;
import be.ac.vub.wise.cmtserver.blocks.Template;
import be.ac.vub.wise.cmtserver.blocks.TemplateActions;
import be.ac.vub.wise.cmtserver.blocks.TemplateHA;
import be.ac.vub.wise.cmtserver.core.CMTDelegator;
import be.ac.vub.wise.cmtserver.util.Converter;
import static be.ac.vub.wise.cmtserver.util.Converter.fromJSONtoListBindings;
import be.ac.vub.wise.cmtserver.util.IndexableArraySet;
import com.sun.istack.logging.Logger;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.logging.Level;
import org.json.JSONArray;
import org.json.JSONObject;
import org.kie.api.definition.type.Role;
import org.kie.api.definition.type.Role.Type;

/**
 *
 * @author lars
 */
public class SharingImportExport implements Sharing {
    
    HashMap<String,TemplateHA> eventToTemplate = new HashMap<>();

    @Override
    public JSONObject exportActivity(Template templ) {
        JSONObject resultjson = null;
        eventToTemplate = this.prepareOutputBlockIndex();
        
        try {
            resultjson = prepareTemplateSkeletonJSON(templ);
            JSONArray initJArray = new JSONArray();
            
            
            resultjson.put("init", initJArray);

        } catch (Exception ex) {
            java.util.logging.Logger.getLogger(SharingImportExport.class.getName()).log(Level.SEVERE, null, ex);
        }
        return resultjson;
    }
    // Preparing the initial JSON skeleton
        // 1. Convert the template using the standard converter
        // 2. Remove the IFBlocks in order to create nested IFBlocks
        // 3. processIFBlocks (= loop over the IFBlocks and create nested ones)
    public JSONObject prepareTemplateSkeletonJSON(Template templ) throws Exception{
        JSONObject res = Converter.fromTemplateToJSON(templ);
        res.remove("ifblocks");
        
        JSONArray nestedIFBlocks = new JSONArray();
        processIFBlocks(templ, nestedIFBlocks);
        
        res.put("ifblocks", nestedIFBlocks);
        
        return res;       
    }
    
    // Check whether or not an event is custom and therefore needs further processing
    public JSONArray processEvent(FactType event){            
        // Event is already written to JSON, we only need to write declarations if IFBlocks contain custom events
        
        if(event.isIsCustom()) // If event is custom, we have to check the IFBlocks on other custom events
           return processCustomEvent(event);      
        return null; // FIXME
    }
    
    // In case of a custom event
        // 1. Convert the event using the built-in event converter
    public JSONArray processCustomEvent(FactType event) {
        JSONArray resultJArray = new JSONArray();
        resultJArray.put(Converter.fromFactTypeToJSON(event));
        // Retrieving the corrseponding template
        // >> SQL: Search template responsible for "event"
        Template _t = eventToTemplate.get(event.getClassName());
        // >> SQL: check for custom event IFBlocks (! recursively!!!!)
        boolean hasCustomEvents_t = true;
        
        if(!hasCustomEvents_t)// No custom events -> converter already converted everything
            return resultJArray; 
        return null; // FIXME
    }
    // Processes the IFBlocks: delegates depending on IFBlock = event/function
        // 1. Loop over the IFBlocks
        // 2. In case of an event: 
            // 2a. Use the built-in event converter
            // 2b. Delegate to "processEvent" to determine whether it is custom and needs further processing
        // 3. In case of a function
            // 3a. Use the built-in function converter
            // 3b. SQL: check (!recursively) if the function contains any custom events
            // 3c. If needed, delegate to "processBindings"
    public void processIFBlocks(Template t, JSONArray resultBlocks) throws Exception {
        LinkedList<IFBlock> ifBlocks = t.getIfBlocks();

        for (IFBlock currBlock : ifBlocks) {
            JSONObject jIFBlock = new JSONObject();
            JSONObject jFuncEvent;
            JSONArray jRecArray = new JSONArray();

            if (currBlock.getEvent() != null) {
                jFuncEvent = Converter.fromFactTypeToJSON(currBlock.getEvent()); // <<<FIXME: dubbele conversie!
                // ^^^^ >> processEvent convert al...
                jRecArray = processEvent(currBlock.getEvent());

                jIFBlock.put("event", jFuncEvent);
                jIFBlock.put("typeBlock", "activity");

            } else if (currBlock.getFunction() != null) {
                Function func = currBlock.getFunction();
                jFuncEvent = Converter.fromFunctionToJSON(func);
                LinkedList<Binding> bindings = currBlock.getBindings();
                // >> SQL endpoint: check of Functie custom events bevat als PM
                // ??? Wat als er inderdaad geen custom events inzitten?
                // -> dan moeten we toch nog altijd de bindings processen???

                jRecArray = processBindings(bindings);

                jIFBlock.put("function", jFuncEvent);
                jIFBlock.put("typeBlock", "function");

            } else {
                jIFBlock = null;
                throw new Exception("IFBlock does not contain an Event or Function");
            }
            jIFBlock.put("declarations", jRecArray);
            resultBlocks.put(jIFBlock);
        }
    }
    // Processes function bindings
        // 1. Loop over the bindings
        // 2. Determine whether or not one of the parameters is bound to a FactType (= any) or a specific Fact
        // 3. In case of a FactType, we need to recursively process the event
        // 4. In case of a specific Fact, -> not needed **<<<<<<<< TO CHECK
    public JSONArray processBindings(LinkedList<Binding> bindings) {
        JSONArray jResBindings = null;
        for (Binding currBinding : bindings) {
            BindingParameter endBinding = currBinding.getEndBinding();
            BindingInputBlock bindingInputBlock = (BindingInputBlock) endBinding;
            IFactType inputObject = bindingInputBlock.getInputObject();
             if(inputObject instanceof FactType){
                jResBindings = processEvent((FactType) inputObject);
                // check FactType of string 'type'
                // case "activity" -> recursive processEvent
                // case "fact" -> continue
             }
             else if(inputObject instanceof Fact){
                 continue;      //**<<<<<<<< TO CHECK
                 
             }
        }
        return jResBindings;
    }


    @Deprecated
    // Loops over IFBlocks of a template
    // For each IFBlock:
    // - Retrieve event(FactType) xor function(Function)
    // - add fact to FactInstances
    // - add event to eventInstances
    // - add Function's to functions -> these are already resolved
    public void sortIFBlocks(LinkedList<IFBlock> ifBlocks, IndexableArraySet<String> types,
            LinkedList<FactType> factInstances, LinkedList<FactType> eventInstances, IndexableArraySet<Function> functions) throws Exception {
        for (int i = 0; i < ifBlocks.size(); i++) {
            IFBlock currBlock = ifBlocks.get(i);
            if (currBlock.getEvent() != null) { // Event can be Fact or Event
                FactType event = currBlock.getEvent();

                if (isAnnotatedAs(event, Type.FACT)) {
                    // Put the current instance in the factInstance list, so they can be assigned to the corresponding template
                    factInstances.add(event);
                } else if (isAnnotatedAs(event, Type.EVENT)) {
                    eventInstances.add(event);
                }
                //String eventType = event.getClassName();

                /**
                 * De oplossing: Schrijf zelf een converter naar JSON voor
                 * niet-template classen. Let wel op: FACTS: we gaan de
                 * instances van facts moeten bijhouden wanneer we ze in types
                 * steken >> Opl: je kan FactTypes rechtstreeks uit de DB
                 * opvragen en met Converter serializeren (je krijgt dan een
                 * "FactType" terug) >> We moeten instanties ook delen. >>
                 * Probleem: de JSON converter converteert of naar class of naar
                 * instantie. => Class + instantie nodig (of zelf converter
                 * schrijven) FUNCTIONS: kunnen parameters hebben: TO ASK: zijn
                 * die ook default Java types? Neen-> vb. Location (! deze types
                 * zijn wel altijd voorgeprogrammeerd) >> Hoe de semantiek van
                 * een functie verzenden? (java file als string??) @ask Sandra
                 * EVENTS: kunnen parameters hebben die situaties zijn ->
                 * template opvragen die die situatie definieert en deze
                 * recursief doorlopen (solved)
                 *
                 */
                /*types.add(eventType);

                // Normally, we should be able to get these fields from the database when loading the event class (Lvh)
                ArrayList<CMTField> fields = event.getFields();
                for (CMTField field : fields) {
                    String fieldType = field.getType(); // TODO: MAJOR FIX needed -> just the type is not enough to reconstruct the field
                    types.add(fieldType);                    
                }          */
            } else if (currBlock.getFunction() != null) {
                Function function = currBlock.getFunction();
                functions.add(function);

            } else {
                // Throw Error: IFBlock must always contain an Event xor Function
                throw new Exception("IFBlock does not contain an Event or Function");
            }

        }

    }

    @Deprecated
    // Checks if a Fact/Event is annotated as Type.FACT/EVENT
    public boolean isAnnotatedAs(IFactType f, Type t) {
        Type annotation = f.getClass().getAnnotation(Role.class).value();
        return annotation.equals(t);
    }
    @Deprecated
    public void processFunctions(IndexableArraySet<Function> functions, IndexableArraySet<String> types, JSONArray jFunctions) throws Exception {
        for (int i = 0; i < functions.size(); i++) {
            Function currfunc = functions.get(i);
            processParameters(currfunc.getParameters(), types);
            jFunctions.put(Converter.fromFunctionToJSON(currfunc));
            functions.markProcessed(currfunc);
        }

        // TODO: retrieve parameters and put in types; convert currfunc to JSON and mark as processed in functions
    }
    @Deprecated
    public void processParameters(LinkedHashMap<String, String> parameters, IndexableArraySet<String> types) throws Exception {
        Iterator<String> it = parameters.keySet().iterator();
        for (Map.Entry<String, String> pm : parameters.entrySet()) {
            //String pmName = pm.getKey();
            String pmType = pm.getValue();
            if (!types.isProcessed(pmType)) {
                types.add(pmType);  // TODO: include instance
            }
        }
    }
    @Deprecated
    // TODO: how to make the determination of the type more generic
    public void processTypes(IndexableArraySet<String> types, HashMap<String, TemplateHA> outputIdx, JSONArray jFacts, IndexableArraySet<Template> tmpls) {
        for (String type : types) {
            Class s = type.getClass();
            if (s.isAssignableFrom(IFactType.class)) {
                Type annotation = s.getClass().getAnnotation(Role.class).value();
                if (annotation.equals(Type.FACT)) {
                    FactType ft = CMTDelegator.get().getFactTypeWithName(type);
                    // Convert to JSON

                } else if (annotation.equals(Type.EVENT)) {
                    // Retrieve template from index of this event (is of type TemplateHA). This situation/event can be found in the OutputHA field
                    // We need the template making the event to know what IFBlocks need to be true in order for the event to be true
                    // When template found: also add it to tmpls
                    TemplateHA respTempl = outputIdx.get(type);
                    tmpls.add(respTempl);

                    // ASK Sandra for DB commands
                }

            } else if (s.isAssignableFrom(IFunctionClass.class)) {

            } else if (s.equals(java.lang.String.class)) {

            } else if (s.isPrimitive()) {// ! String is not a primitive type

            }
            // DB Operations....

        }
        // TODO
    }

    @Deprecated
    // Future: Don't load all templates, but just query the db4O using native queries: http://www.ibm.com/developerworks/library/j-db4o2/
    // Creates a quick access hash map mapping Situations on the templates they are generated by
    public HashMap<String, TemplateHA> prepareOutputBlockIndex() {
        HashMap<String, TemplateHA> results = new HashMap<>();

        HashSet<TemplateHA> tempsHA = CMTDelegator.get().getAllTemplateHA();
        tempsHA.stream().forEach((tmplHA) -> {
            String outputEvent = tmplHA.getOutput().getName();
            results.put(outputEvent, tmplHA);
        });

        return results;
    }

    @Deprecated
    public HashSet<Class> retrieveTemplateHAClasses() {
        HashSet<TemplateHA> temps = CMTDelegator.get().getAllTemplateHA();
        HashSet<Class> res = new HashSet<>();
        temps.stream().forEach((tmplHA) -> {
            res.add(tmplHA.getClass());
        });
        return res;
    }

    // TODO --------------------------------------------------------------------
    @Override
    public void importTemplateRule(JSONObject json) {
        String templatetype = (String) json.get("tempType");
        switch (templatetype) {
            case "TemplateHA":
                OutputHA result = new OutputHA();
                JSONObject output = json.getJSONObject("output");

                // What fromJSONtoOutputHA does 
                result.setName(output.getString("name"));
                result.setBindings(fromJSONtoListBindings(output.getJSONArray("bindings"))); // Not necessary (initially) for the matching

                // Getting IFBlocks and operators
                JSONArray jsonIfBlocks = json.getJSONArray("ifblocks");
                JSONArray jsonOperators = json.getJSONArray("operators"); // The operators between the blocks: AND/OR/(NOT)

                // Iterating over IF blocks
                for (int i = 0; i < jsonIfBlocks.length(); i++) {
                    JSONObject currIfBlock = jsonIfBlocks.getJSONObject(i);
                    String typeBlock = currIfBlock.getString("typeBlock");
                    String searchKey;
                    switch (typeBlock) {
                        case "function":
                            // In case of function -> retrieve function blocks from db/only compare with function blocks
                            searchKey = currIfBlock.getJSONObject("function").getString("methodName");

                            break;
                        case "activity":
                            //  // In case of activity -> retrieve activity blocks from db/only compare with activity blocks
                            searchKey = currIfBlock.getJSONObject("event").getString("className");

                            break;
                    }

                }

                // TODO: check fillTemplateLS code --> deze werkt voor zowel HA als actiontemplates -> hou hier rekening mee (codeduplicatie)
                // TODO: vraag databasecommando's aan Sandra
                //TemplateHA tmplha = Converter.fromJSONtoTemplateHA(json);
                break;
            case "TemplateActions":
                TemplateActions tmplactions = Converter.fromJSONtoTemplateAction(json);
                break;
            default:
                Logger.getLogger(SharingImportExport.class.getClass()).log(Level.SEVERE, "importFilledTemplateRule -- TemplateSubclass unknown");
                break;
        }

        // TODO: over template lopen: type template bepalen via key "tempType" -> moet equal zijn aan "TemplateHA" of "TemplateActions"
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
