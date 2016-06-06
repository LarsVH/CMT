package be.ac.vub.wise.cmtserver.sharing;

import be.ac.vub.wise.cmtserver.blocks.Binding;
import be.ac.vub.wise.cmtserver.blocks.BindingIF;
import be.ac.vub.wise.cmtserver.blocks.BindingInputBlock;
import be.ac.vub.wise.cmtserver.blocks.BindingInputFact;
import be.ac.vub.wise.cmtserver.blocks.BindingInputField;
import be.ac.vub.wise.cmtserver.blocks.BindingOutput;
import be.ac.vub.wise.cmtserver.blocks.BindingParameter;
import be.ac.vub.wise.cmtserver.blocks.CMTField;
import be.ac.vub.wise.cmtserver.blocks.CMTParameter;
import be.ac.vub.wise.cmtserver.blocks.EventInput;
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
import java.util.ArrayList;
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
    public JSONArray processEvent(IFactType event) throws Exception{            
        //Event is already written to JSON, we only need to write declarations if IFBlocks contain custom events
        
        // If event is (a FactType and custom) or the event is of type "EventInput" (= event is custom anyway)
        // => we have to check the IFBlocks on other custom events
        if((event instanceof FactType && ((FactType)event).isIsCustom()) ||
                event instanceof EventInput){
            return processCustomEvent(event);  
        }
       return new JSONArray(); // Event not custom? => return empty array
    }
    
    // In case of a custom event
    public JSONArray processCustomEvent(IFactType event) throws Exception {         // Waarom een array teruggeven als er maar 1 element in terecht komt?
        JSONArray jResultArray = new JSONArray();
        
        // Retrieve the class name (cast from IFactType to FactType/EventInput)
        String eventClassName;
        if (event instanceof FactType) {
            eventClassName = ((FactType) event).getClassName();
        } else if (event instanceof EventInput) {
            eventClassName = ((EventInput) event).getClassName();
        } else {
            throw new Exception(SharingImportExport.class.getName()
                    + "processCustomEvent -- event not of type 'FactType' nor 'EventInput'");
        }

        // Retrieving the corrseponding template
        // >> SQL: Search template responsible for "event"                      // <<< We moeten event zelf ook nog converten: mapping event->template nodig bij importeren
        Template _t = new Template(); // temp fix
        
        //System.out.println("2>>>> Retrieved template: '" + _t.getName() + "' from eventToTemplate");
        
        // >> SQL: check for custom event IFBlocks (! recursively!!!!)
        boolean hasCustomEvents_t = false;

        if (hasCustomEvents_t) {
            jResultArray.put(prepareTemplateSkeletonJSON(_t));
        } else // No custom events -> converter already converted everything
        {
            return jResultArray;    // !! We might still have to convert the template using the built-in converter
        }
        return jResultArray; // (Empty)
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

        for (int i=0; i<ifBlocks.size(); i++) {
              
            IFBlock currBlock = ifBlocks.get(i);
            JSONObject jIFBlock = new JSONObject();
            JSONObject jFuncEvent;
            JSONArray jRecArray;
            
            System.out.println("0>>>>> CurrIFBLock type: " + currBlock.getType());
            
            jIFBlock.put("index", i);
            
            if (currBlock.getEvent() != null) {
                jFuncEvent = Converter.fromFactTypeToJSON(currBlock.getEvent());
                //jRecArray = processEvent(currBlock.getEvent()); // FIXME: MAJOR BUG <<< JRecArray wordt op 145 overschreven

                jIFBlock.put("event", jFuncEvent);
                jIFBlock.put("typeBlock", "activity");

            } else if (currBlock.getFunction() != null) {
                Function func = currBlock.getFunction();
                jFuncEvent = Converter.fromFunctionToJSON(func);
            
                jIFBlock.put("function", jFuncEvent);
                jIFBlock.put("typeBlock", "function");    

            } else {
                jIFBlock = null;
                throw new Exception("IFBlock does not contain an Event or Function");
            }     
            LinkedList<Binding> bindings = currBlock.getBindings();
            jRecArray = processBindings(bindings); 
            jIFBlock.put("bindings", jRecArray);
            
            resultBlocks.put(jIFBlock);
        }
    }
    // Processes function bindings
        // 1. Loop over the bindings
        // 2. Determine whether or not one of the parameters is bound to a FactType (= any) or a specific Fact
        // 3. In case of a FactType, we need to recursively process the event
        // 4. In case of a specific Fact, -> not needed **<<<<<<<< TO CHECK
    // Returns a JSONarray (incl. required declarations) for key "bindings:"
    public JSONArray processBindings(LinkedList<Binding> bindings) throws ClassNotFoundException, Exception {
        JSONArray jResBindings = new JSONArray();
        for (int i=0; i < bindings.size(); i++) {            
            Binding currBinding = bindings.get(i);
            JSONObject objBind;   
         
            objBind = Converter.fromBindingToJSON(currBinding, i);
            
            JSONArray declarations = new JSONArray();

            BindingParameter endBinding = currBinding.getEndBinding();
            BindingInputBlock bindingInputBlock = (BindingInputBlock) endBinding;
            IFactType inputObject = bindingInputBlock.getInputObject();
            
             // DEBUG
            System.out.println("1>>>>> processBindings, currBinding = " + bindingParameterType(endBinding));
            
            // InputObject is ALWAYS an event (a function can never be in the endbindings)
             if(inputObject instanceof FactType){
                FactType inputObjectEvent = (FactType) inputObject;
                
                System.out.println("1a>>>>>> Handling InputObjectFactType: " + inputObjectEvent.getClassName() +
                        "\n -- isCustom: " + inputObjectEvent.isIsCustom());
                boolean isCustom = inputObjectEvent.isIsCustom();
                if(isCustom){                
                 declarations = processEvent(inputObjectEvent);
                }
                else {
                    // inputObject is not custom -> no declarations needed
                    // => declarations: []
                }
             }
             else if(inputObject instanceof Fact){
                Fact inputObjectFact = (Fact) inputObject;
                System.out.println("1b>>>>>> Handling InputObjectFactType: " + inputObjectFact.getClassName());
                          
                 continue;      //**<<<<<<<< TO CHECK
                 
             }
             // ASK SANDRA: wat is het verschil tussen een "custom FactType" en "EventInput"
             // In case the inputObject is of type "EventInput"
                // "EventInput" = corresponding 
             else if(inputObject instanceof EventInput){
                 declarations = processEvent(inputObject);
             }
             objBind.put("declarations", declarations);
             jResBindings.put(objBind);
        }
        return jResBindings;
    }

    @Deprecated
    public String bindingParameterType(BindingParameter par) {
        if (par instanceof BindingInputFact) {
            BindingInputFact bindFact = (BindingInputFact) par;
            if (bindFact.getInputObject() instanceof Fact) {
                return Fact.class.getName();
            } else if (bindFact.getInputObject() instanceof FactType) {
                return FactType.class.getName();
            } else if (bindFact.getInputObject() instanceof EventInput) {
                return EventInput.class.getName();
            }
        } else if (par instanceof BindingInputField) {
            BindingInputField bindFact = (BindingInputField) par;
            if (bindFact.getInputObject() instanceof Fact) {
                return Fact.class.getName();
            } else if(bindFact.getInputObject() instanceof FactType){
                return FactType.class.getName();
            } else if(bindFact.getInputObject() instanceof EventInput){
                return EventInput.class.getName();                                
            }
        }
        else {
            if(par instanceof BindingIF){
                return BindingIF.class.getName();
            } else if(par instanceof BindingOutput){
                return BindingOutput.class.getName();
            }
        }
        return "Type Unknown"; // Error: class unknown
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
    public void processParameters(ArrayList<CMTParameter> parameters, IndexableArraySet<String> types) throws Exception {
        for (CMTParameter pm: parameters) {
            //String pmName = pm.getKey();            
            String pmType = pm.getType();
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

        // Loop over de templates en check de IFBlocks
        // Map de ifBlock types dan op de template en steek ze in de HashMap
        HashSet<TemplateHA> tempsHA = CMTDelegator.get().getAllTemplateHA();
        tempsHA.stream().forEach((tmplHA) -> {       
            
            String outputEvent = tmplHA.getOutput().getName();
            
            System.out.println("3>>>>>>>>> Outputname: " + outputEvent);
            
            
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
    private final HashMap<String, LinkedList<String>> resolvedTypes = new HashMap<>();
    private final double levenshteinTreshold = 0.5;
    @Override
    public Template importTemplateRule(JSONObject jInput) {
        
        Template resTmpl = new Template();
        
        String tmplType = jInput.getString("tempType");
               switch (tmplType){       //TODO
           case "TemplateActions":
               //resTmpl = Converter.fromJSONtoTemplateAction(json); //TODO
               break;
           case "TemplateHA":
               TemplateHA tHA = new TemplateHA();
               //resTmpl=Converter.fromJSONtoTemplateHA(json);
               tHA.setOutput(Converter.fromJSONtoOutputHA(jInput.getJSONObject("output")));
               resTmpl = tHA;
               break;
        }
        JSONArray jIFBlocks = jInput.getJSONArray("ifblocks");
        JSONArray jOperators = jInput.getJSONArray("operators");    // Should normally not be neeeded
                                  
         // TODO: werk de template af met Converter.fillTemplateLS(resTmpls, jInput)
         return resTmpl;
    }
    
    private void importIFBlocks(JSONArray jIFBlocks, Template t){        
        for(int i=0; i<jIFBlocks.length(); i++){
            JSONObject jIFBlock = jIFBlocks.getJSONObject(i);
            JSONArray jBindings = jIFBlock.getJSONArray("bindings");
            //LinkedList<Binding> bindings = Converter.fromJSONtoListBindings(jBindings);
            verifyAndImportBindings(jBindings);
        
        // (als IFBlock of type "acticity" is => de classe van het IFBlock zelf ook importeren!!) -> niet nodig: custom events hebben altijd
        // een binding naar hun eigen type
        }
    }

    private void verifyAndImportBindings(JSONArray jBindings){
        for(int i=0; i<jBindings.length(); i++){
            JSONObject jBinding = jBindings.getJSONObject(i);
            JSONObject jEndBinding = jBinding.getJSONObject("endBinding");
            JSONObject jInputObject = jEndBinding.getJSONObject("inputObject");
            JSONArray jFields = jInputObject.getJSONArray("fields");
            
            JSONArray jDeclarations = jBinding.getJSONArray("declarations");
            
            for(int j=0; j<jFields.length(); j++){
                JSONObject currField = jFields.getJSONObject(j);
                String fieldType = currField.getString("fieldType");
                String fieldName = currField.getString("fieldName");                
                if(resolvedTypes.containsKey(fieldType) && (resolvedTypes.get(fieldType).contains(fieldName))){
                    continue;   // FieldType and Fieldname already resolved
                }
                // SQL SQL SQL SQL SQL SQL SQL SQL SQL SQL SQL SQL SQL SQL SQL SQL SQL SQL <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
                   // Levenshtein op field TYPES:
                    //> Levenshtein = 0 (= perfect match) -> check fields
                    //> Levenshtein < treshold (for multiple classes) -> let user decide: choose right class (-> check fields) OR create new class
                    //> Levenshtein > treshold -> create new class
                double levenshtein = 0.5;
                
                if(levenshtein <= 0){
                    // check field names (en create eventuele subclass)
                    FactType levTypeMatch = new FactType(); //<< retrieve from SQL (can be "Event" too, so maybe change to IFactType
                    mergeFieldFields(fieldType, fieldName, jDeclarations, levTypeMatch);
                } else if(levenshtein < levenshteinTreshold){
                    // suggestions (classA, classB,...,newClass) -> check fields OR createNewClass 
                    boolean userDecisionNewClass = false;
                    if(!userDecisionNewClass){
                        createNewClass(fieldType, fieldName); //TODO
                    }
                    else {
                        FactType userChoice = new FactType(); // << retrieve from SQL
                        mergeFieldFields(fieldType, fieldName, jDeclarations, userChoice);
                    }
                }
                else {
                    // createNewClass 
                    createNewClass(fieldType, fieldName);
                }
                    
                // Check Fields (by name AND type)
                    //> All fields matching? -> binding resolved
                    //> Some/None fields matching? -> create subclass (extend), verifyAndImportFields (on type
            }
            // TODO <<< : SQL: levenshtein
            
        }
    }
    
    private void mergeFieldFields(String fieldType, String fieldName, JSONArray declarations, FactType levTypeMatch){
        ArrayList<CMTField> knownFields = levTypeMatch.getFields();
        // Look in declarations for fieldtype
        
        
    }
    
    private void createNewClass(String fieldType, String fieldName){
        
    }
    
    private JSONObject findInDeclarations(String type,JSONArray declarations){
        
        for(int i=0; i< declarations.length(); i++){
         // XXX: momenteel niet mogelijk -> exporter aanpassen om events te mappen hun template...
            
        }       
        
        return new JSONObject();// TODO
    }
}