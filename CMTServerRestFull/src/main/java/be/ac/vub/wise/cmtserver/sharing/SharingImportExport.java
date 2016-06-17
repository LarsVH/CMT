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
import be.ac.vub.wise.cmtserver.blocks.FieldValueLimitation;
import be.ac.vub.wise.cmtserver.blocks.Function;
import be.ac.vub.wise.cmtserver.blocks.IFBlock;
import be.ac.vub.wise.cmtserver.blocks.IFactType;
import be.ac.vub.wise.cmtserver.blocks.IFunctionClass;
import be.ac.vub.wise.cmtserver.blocks.OutputHA;
import be.ac.vub.wise.cmtserver.blocks.Template;
import be.ac.vub.wise.cmtserver.blocks.TemplateActions;
import be.ac.vub.wise.cmtserver.blocks.TemplateHA;
import be.ac.vub.wise.cmtserver.core.CMTDelegator;
import be.ac.vub.wise.cmtserver.restfull.CMTCore;
import be.ac.vub.wise.cmtserver.util.Converter;
import static be.ac.vub.wise.cmtserver.util.Converter.fromJSONtoListBindings;
import be.ac.vub.wise.cmtserver.util.IndexableArraySet;
import com.sun.istack.logging.Logger;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javafx.util.Pair;
import org.json.JSONArray;
import org.json.JSONObject;
import org.kie.api.definition.type.Role;
import org.kie.api.definition.type.Role.Type;

/**
 *
 * @author lars
 */
public class SharingImportExport implements Sharing {

    private static SharingImportExport importExport = null;

    @Deprecated
    HashMap<String, TemplateHA> eventToTemplate = new HashMap<>();

    // <FieldType, <FieldNames>>
    private static HashSet<String> resolvedTypesInThisImport = new HashSet<>();

    // Make default constructor private: SharingImportExport must always be accessed via get singleton
    private SharingImportExport() {
    }

    //Singleton (necessary for importer)
    // Override not possible due to staticness
    public static SharingImportExport get() {
        if (importExport == null) {
            importExport = new SharingImportExport();
        }
        return importExport;
    }

    @Override
    public JSONObject exportActivity(Template templ) {
        System.out.println("1>: exportActivityIn, sharing template: " + templ.getName());
        JSONObject resultjson = null;
        //eventToTemplate = this.prepareOutputBlockIndex();

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
    public JSONObject prepareTemplateSkeletonJSON(Template templ) throws Exception {
        JSONObject res = Converter.fromTemplateToJSON(templ);
        res.remove("ifblocks");

        JSONArray nestedIFBlocks = new JSONArray();
        processIFBlocks(templ, nestedIFBlocks);

        res.put("ifblocks", nestedIFBlocks);

        return res;
    }

    // Check whether or not an event is custom and therefore needs further processing
    public JSONArray processEvent(IFactType event) throws Exception {
        //Event is already written to JSON, we only need to write declarations if IFBlocks contain custom events

        // If event is (a FactType and custom) or the event is of type "EventInput" (= event is custom anyway)
        // => we have to check the IFBlocks on other custom events
        if ((event instanceof FactType && ((FactType) event).isIsCustom())
                || event instanceof EventInput) {
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
        // >> SQL: Search template responsible for "event"
        CMTDelegator delegator = CMTDelegator.get();
        System.out.println("3>>> Retrieving template for situation: " + eventClassName);
        Template parentTemplate = delegator.getTemplateOfSituation(eventClassName);

        System.out.println("4>>> Retrieved template: '" + parentTemplate.getName() + "-- with SQLID = " + parentTemplate.getSql_id());

        // >> SQL: check for custom event IFBlocks (! recursively!!!!)
        ArrayList<FactType> customEvents = delegator.getCustomEventsUsedInTemplate(parentTemplate.getSql_id());
        System.out.println("5>>> Retrieved " + customEvents.size() + " customEvents for Template " + parentTemplate.getName());

        // There exist custom events
        if (!customEvents.isEmpty()) {
            jResultArray.put(prepareTemplateSkeletonJSON(parentTemplate));
        } else {
            // parentTemplate has no custom Events?
            // Still have to convert the parentTemplate itself...
            jResultArray.put(Converter.fromTemplateToJSON(parentTemplate));
        }

        return jResultArray;
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

        for (int i = 0; i < ifBlocks.size(); i++) {

            IFBlock currBlock = ifBlocks.get(i);
            JSONObject jIFBlock = new JSONObject();
            JSONObject jFuncEvent;
            JSONArray jRecArray;

            System.out.print("2>> CurrIFBLock type: " + currBlock.getType());

            jIFBlock.put("index", i);

            if (currBlock.getEvent() != null) {
                jFuncEvent = Converter.fromFactTypeToJSON(currBlock.getEvent());
                //jRecArray = processEvent(currBlock.getEvent()); // FIXME: MAJOR BUG <<< JRecArray wordt op 145 overschreven

                System.out.print("; name " + currBlock.getEvent().getClassName());
                jIFBlock.put("event", jFuncEvent);
                jIFBlock.put("typeBlock", "activity");

            } else if (currBlock.getFunction() != null) {
                Function func = currBlock.getFunction();
                jFuncEvent = Converter.fromFunctionToJSON(func);

                System.out.println(" name " + currBlock.getFunction().getName());
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
    // 4. In case of a specific Fact, -> not needed
    // Returns a JSONarray (incl. required declarations) for key "bindings:"
    public JSONArray processBindings(LinkedList<Binding> bindings) throws ClassNotFoundException, Exception {
        JSONArray jResBindings = new JSONArray();
        for (int i = 0; i < bindings.size(); i++) {
            Binding currBinding = bindings.get(i);
            JSONObject objBind;

            objBind = Converter.fromBindingToJSON(currBinding, i);

            JSONArray declarations = new JSONArray();

            BindingParameter endBinding = currBinding.getEndBinding();
            BindingInputBlock bindingInputBlock = (BindingInputBlock) endBinding;
            IFactType inputObject = bindingInputBlock.getInputObject();

            // DEBUG
            System.out.println("3>>> processBindings, currBinding = " + bindingParameterType(endBinding));

            // InputObject is ALWAYS an event (a function can never be in the endbindings)
            if (inputObject instanceof FactType) {
                FactType inputObjectEvent = (FactType) inputObject;

                System.out.println("3>>> Handling InputObjectFactType: " + inputObjectEvent.getClassName()
                        + " -- isCustom: " + inputObjectEvent.isIsCustom());
                boolean isCustom = inputObjectEvent.isIsCustom();
                if (isCustom) {
                    declarations = processEvent(inputObjectEvent);
                } else {
                    // inputObject is not custom -> no declarations needed
                    // => declarations: []
                }
            } else if (inputObject instanceof Fact) {
                Fact inputObjectFact = (Fact) inputObject;
                System.out.println("3>>> Handling InputObjectFactType: " + inputObjectFact.getClassName());

                continue;      //**<<<<<<<< TO CHECK

            } // ASK SANDRA: wat is het verschil tussen een "custom FactType" en "EventInput"
            // In case the inputObject is of type "EventInput"
            // "EventInput" = corresponding 
            else if (inputObject instanceof EventInput) {
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
            } else if (bindFact.getInputObject() instanceof FactType) {
                return FactType.class.getName();
            } else if (bindFact.getInputObject() instanceof EventInput) {
                return EventInput.class.getName();
            }
        } else if (par instanceof BindingIF) {
            return BindingIF.class.getName();
        } else if (par instanceof BindingOutput) {
            return BindingOutput.class.getName();
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
        for (CMTParameter pm : parameters) {
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

//==============================================================================
    private final double scoreTreshold = 0.5;
    private JSONObject jInput;

    @Override
    public HashMap<String, HashMap<Integer, String>> importTemplateRule(JSONObject jInput) {

        // Werk verder uit (bottom-up)
        //**********************************************************************
        // Make sure this is clear before looping (unique for every import 
        //(pe SandraInKitchen kan meermaals voorkomen in template, doch slechts 1 keer importeren)
        this.jInput = jInput;   // necessary for callback
        resolvedTypesInThisImport.clear();

        // Get suggestions choices from user first
        // <Type, <id, suggestion>>
        HashMap<String, HashMap<Integer, String>> suggestionsThisImport = generateSuggestions(jInput);

        //**********************************************************************
        // DEPRECATED
        /*String tmplType = jInput.getString("tempType");
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
         
        //--------------------Fix de dependencies eerst-------------------------
        checkIFBlocks(jIFBlocks);
        //----------------------------------------------------------------------
         
        // Vul de template in (standaard converter), gebruikmakende van de
        // gekregen JSON (zonder "declarations")
        Converter.fillTemplateLS(resTmpl, jInput);*/
        return suggestionsThisImport;

    }

    /**
     * Generate a HashMap with each of the eventTypes matched (Jaro-Winker)
     * against those in the Db. Loop over the inputTemplate and get the output
     * (containing the name of the type to be matched). Then run Jaro-Winkler
     * with all the eventTypes in Db. Sort them by score and add the whole to
     * the returned hashmap
     *
     *
     * @param jInputTemplate
     * @return : hashmap: (matchedtype,(id, dbtype)) -> db types are sorted by
     * highest score first
     */
    public HashMap<String, HashMap<Integer, String>> generateSuggestions(JSONObject jInputTemplate) {
        HashMap<String, HashMap<Integer, String>> suggestions = new HashMap<>();
        generateSuggestionsRec(jInputTemplate, suggestions);
        return suggestions;
    }

    public void generateSuggestionsRec(JSONObject jInputTemplate, HashMap<String, HashMap<Integer, String>> suggestions) {

        HashSet<FactType> dbEventTypes = CMTDelegator.get().getAvailableEventTypes();

        JSONObject jOutput = jInputTemplate.getJSONObject("output");
        String thisClassName = jOutput.getString("name");

        ArrayList<Pair<Double, FactType>> collectedScores = generateJaroWinklerScores(thisClassName, dbEventTypes, scoreTreshold);
        collectedScores.sort(new Comparator<Pair<Double, FactType>>() {
            @Override
            public int compare(Pair<Double, FactType> o1, Pair<Double, FactType> o2) {
                if (o2.getKey() > o1.getKey()) {
                    return 1;
                } else {
                    return -1;
                }
            }
        });
        // Looping over the sorted (by score) FactTypes and adding them with
        // sorted (by using ID) to the HashMap of this type)
        HashMap<Integer, String> suggestionsThisType = new HashMap<>();
        for (int i = 0; i < collectedScores.size(); i++) {
            suggestionsThisType.put(i, collectedScores.get(i).getValue().getClassName());
        }
        // Put in the final suggestions HashMap
        suggestions.put(thisClassName, suggestionsThisType);

        // Go into declarations of all bindings of all IFBlocks and recursive call for each declaration
        JSONArray jIFBlocks = jOutput.getJSONArray("ifblocks");
        for (int i = 0; i < jIFBlocks.length(); i++) {
            JSONObject jCurrBlock = jIFBlocks.getJSONObject(i);
            JSONArray jBindings = jCurrBlock.getJSONArray("bindings");
            for (int j = 0; j < jBindings.length(); j++) {
                JSONObject jCurrBinding = jBindings.getJSONObject(j);
                if (jCurrBinding.has("declarations")) {
                    JSONArray jDeclarations = jCurrBinding.getJSONArray("declarations");
                    for (int k = 0; k < jDeclarations.length(); k++) {
                        JSONObject jcurrDeclaration = jDeclarations.getJSONObject(k);
                        generateSuggestionsRec(jcurrDeclaration, suggestions); // Recursive call
                    }
                }
            }

        }

    }

    private ArrayList<Pair<Double, FactType>> generateJaroWinklerScores(String thisClassName, HashSet<FactType> dbTypes, Double scoreTreshold) {
        ArrayList<Pair<Double, FactType>> collectedScores = new ArrayList<>();
        for (FactType matchEventType : dbTypes) {
            Double score = JaroWinklerDistance
                    .apply(thisClassName, matchEventType.getClassName());
            if (score >= scoreTreshold) {
                collectedScores.add(new Pair<>(score, matchEventType));
            }
        }
        return collectedScores;
    }

    public void onSuggestionsReceived(HashMap<String, HashMap<Integer, String>> solvedSuggestions) {
        // de effectieve import
        HashSet<FactType> dbEventTypes = CMTDelegator.get().getAvailableEventTypes();
        HashMap<String, FactType> dbClassNameToEventType = new HashMap<>();
        for (FactType eventType : dbEventTypes) {
            dbClassNameToEventType.put(eventType.getClassName(), eventType);
        }
        loopDownDeclarations(jInput, solvedSuggestions, dbClassNameToEventType);
    }

    // Bottom-up approach
    /**
     * 1. Loop over de IFBlocks 2. In elke IFBlock: loop over de bindings 3. In
     * elke Binding: als er declarations zijn, loop erover, else: 5. 4. Voor
     * elke declaration: recursive call naar 1. 5. Na recursieve call: start met
     * solven: ...
     *
     * @param jInputTemplate
     * @param dbEventTypes: alle CUSTOM EventTypes in de database
     */
    private void loopDownDeclarations(JSONObject jInputTemplate,
            HashMap<String, HashMap<Integer, String>> solvedSuggestions,
            HashMap<String, FactType> dbClassNameToEventType) {
        JSONArray jIFBlocks = jInputTemplate.getJSONArray("ifblocks");
        for (int i = 0; i < jIFBlocks.length(); i++) {
            JSONObject jIFBlock = jIFBlocks.getJSONObject(i);
            JSONArray jBindings = jIFBlock.getJSONArray("bindings");
            for (int j = 0; j < jBindings.length(); j++) {
                JSONObject jBinding = jBindings.getJSONObject(j);

                // If "declarations" exists and is not empty, loop over declarations
                if (jBinding.has("declarations") && (jBinding.getJSONArray("declarations").length() != 0)) {
                    JSONArray jDeclarations = jBinding.getJSONArray("declarations");
                    for (int k = 0; k < jDeclarations.length(); k++) {
                        JSONObject jDeclaration = jDeclarations.getJSONObject(k);
                        // Recursion on current Declaration
                        loopDownDeclarations(jDeclaration, solvedSuggestions, dbClassNameToEventType);
                    }

                }
            }
            // Alle bindings afgewerkt, en declarations resolved
        }
        // Alle IFBlocks afgewerkt, en bindings resolved
        // Resolve nu het huidige eventType

        JSONObject jOutput = jInputTemplate.getJSONObject("output");
        OutputHA output = Converter.fromJSONtoOutputHA(jOutput);
        String eventTypeName = output.getName();
        if (!isJavaType(eventTypeName) && !isResolved(eventTypeName)) { //?! Output van een event is toch altijd een custom event (of een actie), right?
            /* ArrayList<Pair<Double, FactType>> scoreSortedFactTypes = new ArrayList<>();
                    
                    for (FactType matchEventType : dbEventTypes) {
                        Double score = JaroWinklerDistance                      // DEPRECATED: wordt al in de preprocessing gedaan
                                .apply(eventTypeName, matchEventType.getClassName());
                    
                    if(score >= scoreTreshold){
                        scoreSortedFactTypes.add(new Pair<>(score, matchEventType));
                    }
                    else {/*Score lower than treshold => ignore*//*}*/


            //--------- hieronder: TO REMOVE (zie processScores())
            /*
                    if(score == 1.0){
                        // CASE1: Perfect match                  
                        mergeFields(jEndBinding.getJSONObject("inputObject"), matchEventType);
                    } else if (score >= scoreTreshold) {
                        // CASE2: Partial Match: let user decide
                        // suggestions (classA, classB,...,newClass) -> check fields OR createNewClass
                        
                        boolean userDecisionNewClass = false;   // -> True = user wil nieuwe classe; False = user wil mergen met een bepaalde classe
                        if (userDecisionNewClass) {
                            createNewClass(className); //TODO
                        } else {
                            FactType userChoice = new FactType(); // << retrieve from SQL
                            // MergeFields
                        }
                    } else { // CASE3: No Match: score < treshold
                        //createNewClassDepr(fieldType, fieldName);
                    }*/
            //------------------ Hierboven
            /*}
                    scoreSortedFactTypes.sort(new Comparator<Pair<Double,FactType>>() {
                        @Override
                        public int compare(Pair<Double, FactType> o1, Pair<Double, FactType> o2) {
                            if(o2.getKey()> o1.getKey())
                                return 1;
                            else return -1;
                        }
                    });*/
            processSuggestions(solvedSuggestions.get(eventTypeName), dbClassNameToEventType, output); // TODO/FIXME: zorg voor een directe mapping op het FactType van elke suggestie
            // Bookkeeping
            addResolvedTypeField(eventTypeName);    // Types is resolved => add to bookkeeping     
        }

        // TODO: alle IFBlocks afgewerkt, importeer nu de TEMPLATE ZELF!!!!!!!!!!!!!!!!!!!!!
    }

    /**
     * @param scoreSortedFactTypes
     */
    // !!! Doubles altijd comparen met .equals()
    private void processSuggestions(HashMap<Integer, String> solvedSuggestions,
            HashMap<String, FactType> dbClassNameToEventType,
            /*ArrayList<Pair<Double, FactType>> scoreSortedFactTypes,*/ OutputHA templateOutput) {

        // CASE 1: user chose suggestion
        if (solvedSuggestions.containsKey(-1)) {  // suggestion with key -1 = suggestion user has chosen
            String chosenSuggestion = solvedSuggestions.get(-1);

            System.out.println(">>>>ShIX -- processSuggestions: user chose: " + chosenSuggestion);
            if (!dbClassNameToEventType.containsKey(chosenSuggestion)) {
                System.out.println("ERROR>>>>ShIX -- processSuggestions -- "
                        + "User chose non-existing suggestion: "
                        + chosenSuggestion);
                return;
            }
            mergeFields(templateOutput, dbClassNameToEventType.get(chosenSuggestion));
        } // CASE 2: user did not choose suggestion => create new event type
        else {
            createNewEventType(templateOutput);
        }

        // DEPRECATED
        /* ArrayList<FactType> perfectMatches;
        perfectMatches = scoreSortedFactTypes.stream()
                .filter(scorePair -> scorePair.getKey().equals(1.0))
                .map(perfectMatch -> perfectMatch.getValue())
                .collect(Collectors.toCollection(ArrayList::new));
        
        if(!perfectMatches.isEmpty()){
            if(perfectMatches.size() == 1){ // DONE
                // CASE 1:  1 perfect match => mergeFields (no user interaction)
                mergeFields(templateOutput, perfectMatches.get(0));
            }
            else{ // TODO
                // CASE 2: Multiple perfect matches -> ask client for suggestions between these matches
            }
        }
        else if (!scoreSortedFactTypes.isEmpty()){ // TODO
            // CASE 3: No perfect matches: take the ?5 first elements of the index and ask the user to choose the closest one
            // OR let him decide to create a new class
        }
        else { // DONE
            // CASE 4 : No matches within treshold -> create a new class
            createNewEventClass(templateOutput);
        }*/
    }

    // Merg fields van 2 custom eventTypes
    // Add fields from "jInputObject" to FactType "dbClass" if not existing
    private void mergeFields(OutputHA templateOutput, FactType dbClass) {    // DONE
        ArrayList<CMTField> dbFields = dbClass.getFields();
        LinkedList<Binding> bindings = templateOutput.getBindings();

        // In each binding, the "endBinding" JSONObject represents the field
        // Just create a new "CMTField" based on the fieldtype and name
        HashSet<CMTField> importFields = new HashSet<>();
        for (Binding binding : bindings) {
            BindingOutput endBinding = (BindingOutput) binding.getEndBinding();
            CMTField f = new CMTField(endBinding.getParameter(), endBinding.getParType());
            importFields.add(f);
        }

        mergeFieldsIntoFactType(importFields, dbClass);

    }
    @Deprecated // zie: mergeFactTypeFacts of mergeEventTypes
    private void mergeFieldsIntoFactType(HashSet<CMTField> toMergeFields, FactType dbType) {
        ArrayList<CMTField> dbFields = dbType.getFields();
        ArrayList<CMTField> fieldsToCreate = new ArrayList<>();
        // Subtracting dbFields from import fields (= fields we need to add)
        // ! DO NOT USE Set subtraction (subtraction must only be based on nameXtype)
        for (CMTField dbField : dbFields) {
            for (CMTField importField : toMergeFields) {
                if (importField.getType().equals(dbField.getType())
                        && importField.getName().equals(dbField.getName())) {
                        // Field zit er al in => niets doen
                } else {
                    // Field zit er nog niet in => toevoegen aan de toMerge list
                    fieldsToCreate.add(importField);
                }
            }
        }

        // Check same name/different type fields
        // This is necessary, as it can be possible that two fields have the same
        // name but different type (in a Java scope, all names must be unique)
        ArrayList<CMTField> checkedFieldsToCreate =  new ArrayList<>();
        checkedFieldsToCreate = checkSameNameDifferentType(dbFields, fieldsToCreate);

        // Adding the new fields
        CMTCore core = CMTCore.get();
        core.addFieldsToEventType(dbType, checkedFieldsToCreate);
    }
    /**
     * Checks if fields we want to merge have the same name, but a different
     * type. If so (for a field 'fld'), the name of 'fld' is changed to
     * "typeoffld"+"nameoffld"
     *
     * @param fieldsToCompare: the fields to compare with (from db)
     * @param fieldsToCorrect: fields to change name of (if necessary)
     * @return List with the corrected version of "fields"
     */
    private ArrayList<CMTField> checkSameNameDifferentType(ArrayList<CMTField> fieldsToCompare, ArrayList<CMTField> fieldsToCorrect) {
        ArrayList<CMTField> result = new ArrayList<>();

        // Put fields to compare in hashmap to <name,field> to increase performance
        HashMap<String, CMTField> hFieldsToCompare = new HashMap<>();
        fieldsToCompare.stream().forEach(field -> {
            hFieldsToCompare.put(field.getName(), field);
        });

        // Do the comparison and change name if necessary
        fieldsToCorrect.stream().forEach(field -> {
            if (hFieldsToCompare.containsKey(field.getName())) {
                field.setName(field.getType() + field.getName());     // Solve the name issue by appending its type to the front of its name
            }
            result.add(field);
        });

        return result;
    }

    private void createNewEventType(OutputHA templateOutput) {   // DONE
        // gebruik standaard registratie mechanisme voor event van CMT

        LinkedList<Binding> bindings = templateOutput.getBindings();

        ArrayList<CMTField> fields = new ArrayList<>();
        for (Binding b : bindings) {
            BindingOutput endB = (BindingOutput) b.getEndBinding();
            CMTField f = new CMTField(endB.getParameter(), endB.getParType());
            fields.add(f);
        }

        FactType eventType = new FactType(templateOutput.getName(), "activity", "", fields);
        eventType.setIsCustom(true);
        createNewEventType(eventType);
    }
    // Refactor voor "nieuwe importer"
    private void createNewEventType(FactType eventType) {
        if (eventType.getCategory() == null) {
            eventType.setCategory("Code");
        }
        // Registreren met core
        CMTCore.get().registerEventClass(eventType);
    }

    private void createNewFactType(FactType factType) {
        if (factType.getCategory() == null) {
            factType.setCategory("Code");
        }
        CMTCore.get().registerFactClass(factType);
    }

    // Check if a particular type is already resolved in this import
    private boolean isResolved(String type) {
        return resolvedTypesInThisImport.contains(type);
    }

    // Add a resolved fieldType && fieldName to the bookkeeping
    private boolean addResolvedTypeField(String fieldType) {
        return resolvedTypesInThisImport.add(fieldType);
    }

//==========================================================================================================================================================
//----------------TERUG TOP DOWN-----SANDRA-------------------------------------
//==========================================================================================================================================================
    // ALGORITME:
    // > Krijg een template "t" in JSON formaat binnen
    // > Convert to "TemplateHA" met standaard converter (ZONDER MUTATIE VAN DB/DROOLS)
    // > Loop over de IFBlocks van "t" + hou bij welke FactTypes je al geresolved hebt
    // >> CurrIfblock: check type (.getType())
    // >> In geval van function: importeer zijn string versie (zie Sandra)
    // >> In geval van activity:
    // >>> Gebruik map functie Sandra: mapt PURE IFBlock op de indexen van de "To Fill In Blocks" waaraan hij gebonden is
    // >>> Loop over die "To Fill In Blocks" indexen die je van Sandrafunctie gekregen hebt
    // >>> Voor elke index, retrieve de "To Fill In Block" "tFiB" 
    //      -> vraag SANDRA hoe (wrs endBinding opvragen, casten naar Interfacte "BindingInputBlock" en dan "getInputObject"
    // >>>> Check wat voor type "tFiB" is: FactTypeFact (FactType.type = fact) , Fact of FactTypeEvent (FactType.type = activity)
    // >>>>> FactTypeFact: solveFactTypeFact()
    // >>>>> Fact: solveFact()
    // >>>>> FactTypeEvent: solveFactTypeEvent()
    
    // FactType solveFactTypeFact(FactType f)
    //--------------------------------------------
    // > JaroWinkler: bereken scores van f over alle FactTypes in de database
    // > Filter diegene die niet boven de treshold komen
    // > CASES:
    // >> Perfect Match: importeer automatisch => mergeFields
    // >> Partial Match: user beslist
    // >> No Match: Nieuwe classe aanmaken
    // > Return: Nieuw FactType na merge/freshly created
    
    // Fact solveFact(Fact f)
    //--------------------------------------------
    // > Obtain FactType van f
    // > solve dat factType met solveFactTypeFact
    // > Voeg f toe aan FactType dat je terugkreeg van solveFactTypeFact
    
    // solveFactTypeEvent()
    //--------------------------------------------
    // Check whether or not the event is custom
    // > Fix (= niet custom): Solve het FactType met solveFactTypeFact(), MAAR:
    // >> check op limitations: ==20u <-> >=22u -> laat user beslissen
    // > Custom: (hier bestaan geen limitations), wordt gedeft door een template
    // >> Solve met aangepaste solveFactTypeFact(): bij perfecte match -> vergeet de import gewoon, MAAR:
    //      Zorg er dan wel voor dat de geimporteerde template die dit event genereert point naar.... (? vraag Sandra)
    private Integer importRecursionLevel;
    private UserDecisionRest suggestionsPool;
    public void importTemplate(JSONObject jTemplate) {
        importRecursionLevel = 0;
        // suggestionPool
        // + houdt ook alle Template HashMaps bij (we moeten die op de één of de andere manier achteraf nog zien aan te passen
        // wanneer de suggesties binnenkomen
        suggestionsPool = new UserDecisionRest();
        
        
        importTemplateRec(jTemplate);
    }
     
    public void importTemplateRec(JSONObject jTemplate){
        ArrayList<Integer> resolvedIndexes = new ArrayList<>();

        TemplateHA tmpl = Converter.fromJSONtoTemplateHA(jTemplate);

        
        // Voor event processing (EventInputs kunnen mappen op hun EventType)(! enkel eventTypes)
        HashMap<String, FactType> classNameToEventType = generateClassNameToEventType(tmpl);
        
        // Bookkeeping: de originele houden we mutable, de andere gebruiken we om
        // over te itereren
        HashMap<Integer, IFactType> indexToToFillInBlocks = getInputsTemplate(tmpl); // Sandra Functie, MUTABLE
        HashMap<IFactType, Integer> toFillInBlocksToIndex = new HashMap<>(); // UNMUTABLE, iterable        
        for(Map.Entry<Integer, IFactType> entry: indexToToFillInBlocks.entrySet()){
            toFillInBlocksToIndex.put(entry.getValue(), entry.getKey());
        }
        HashSet<FactType> dbFactTypes = CMTDelegator.get().getAvailableFactTypes();

        // toFillInBlocksToIndex -> ALTIJD BY REFERENCE DOORGEVEN!!
        processToFillInBlocks(indexToToFillInBlocks, toFillInBlocksToIndex,
                classNameToEventType, dbFactTypes, resolvedIndexes, jTemplate, tmpl);
        
        // Using the modified indexToToFillInBlocks to change the Template
        setInputsTemplate(tmpl, indexToToFillInBlocks);
        
        // TODO: register template in CMT (use standard registration mechanism) .. na suggestions
        //..

    }
    
/**
 * 
 * @param toFillInBlocks: 
 * @param indexToToFillInBlocks: bookkeeping: used to change indexes of the imported template to
 * a local Fact (by solveFact)
 * @param dbFactTypes 
 */
    private void processToFillInBlocks(HashMap<Integer, IFactType> indexToToFillInBlocks,
            HashMap<IFactType, Integer> toFillInBlocksToIndex, HashMap<String,FactType> classNameToEventType,
            HashSet<FactType> dbFactTypes, ArrayList<Integer> resolvedIndexes , JSONObject jTemplate,
            TemplateHA tmpl) {
        // Make toFillInBlocksToIndex readable/mutable
        HashMap<IFactType, Integer> toFillInBlocksToIndexIter = (HashMap<IFactType, Integer>) toFillInBlocksToIndex.clone(); // TODO maak hier hard-copy van

        for (Map.Entry<IFactType, Integer> entry : toFillInBlocksToIndexIter.entrySet()) {
            IFactType toFillInBlock = entry.getKey();
            
            if (toFillInBlock instanceof FactType) {
                // ??LET OP: onderscheid maken tussen FactTypeFact en FactTypeEvent!! (volgens Sandra: enkel FactTypeFact)
                
                FactType fType = (FactType) toFillInBlock;                
                solveFactTypeFact(fType, dbFactTypes, indexToToFillInBlocks,
                        toFillInBlocksToIndex, resolvedIndexes, tmpl);
                
            } else if (toFillInBlock instanceof Fact) {
                // Fact
                // ==> solveFact
                solveFact((Fact) toFillInBlock, indexToToFillInBlocks,
                        toFillInBlocksToIndex, dbFactTypes, resolvedIndexes, tmpl);

            } else if (toFillInBlock instanceof EventInput) {
                 solveFactTypeEvent((EventInput) toFillInBlock, classNameToEventType,
                         dbFactTypes, indexToToFillInBlocks, toFillInBlocksToIndex,
                         resolvedIndexes, jTemplate, tmpl);
            }
        }
    }
    
    public HashMap<Integer,IFactType> getInputsTemplate(Template temp){
        HashMap<Integer,IFactType> result = new HashMap<Integer,IFactType>();
        for(IFBlock ifblock : temp.getIfBlocks()){
            for(Binding binding : ifblock.getBindings()){
                BindingParameter bind = binding.getEndBinding();
                if(!result.containsKey(bind.getIndexObj())){
                    IFactType inputobject = null;
                    if(bind instanceof BindingInputFact ){
                        inputobject = ((BindingInputFact) bind).getInputObject();
                    }else{
                        if(bind instanceof BindingInputField){
                        inputobject = ((BindingInputField) bind).getInputObject();
                        }
                    }
                    result.put(bind.getIndexObj(), inputobject);
                }
            }
        }
        return result;
    }
    public void setInputsTemplate(Template tmpl, HashMap<Integer, IFactType> inputs){
        for(IFBlock ifBlock: tmpl.getIfBlocks()){
            for(Binding binding: ifBlock.getBindings()){
                BindingParameter bind = binding.getEndBinding();
                if(inputs.containsKey(bind.getIndexObj())){
                    IFactType newInputObject = inputs.get(bind.getIndexObj());
                    if(bind instanceof BindingInputFact){
                        ((BindingInputFact) bind).setInputObject(newInputObject);
                    }
                    if(bind instanceof BindingInputField){
                        ((BindingInputField) bind).setInputObject(newInputObject);
                    }
                    if(newInputObject instanceof EventInput){
                        // PAS de startbinding aan
                        BindingParameter startBind = binding.getStartBinding();
                        // TODO -------------->>> VRAAG SANDRA WAT ER PRECIES AAN DE STARTBIDING MOET WORDEN AANGEPAST
                    }
                }
            }
        }
        
    }
    
    private EventInput getEventInputOfEventType(FactType eventType) {
        Template tmpl = CMTDelegator.get().getTemplateOfSituation(eventType.getClassName());
        if (tmpl != null) {
            HashSet<EventInput> eventInputsOfTmpl = getEventInputsOfTemplate(tmpl);
            for(EventInput eventInput: eventInputsOfTmpl){
                if(eventInput.getClassName().equals(eventType.getClassName())){
                    return eventInput;
                }
            }
            System.out.println("ERROR -- ShIX -- getEventInputOfEventType: "
                    + "couldn't find eventType '" + eventType.getClassName() + "' in template");
        } else {
            System.out.println("ERROR -- ShIX -- getEventInputOfEventType: "
                    + "couldn't find template of eventType : " + eventType.getClassName());
        }
        return null;
    }
   
    // Avoid to use this method (db intensive
    private HashSet<EventInput> getEventInputsOfTemplate(Template tmpl) {
        HashSet<EventInput> eventInputs = new HashSet<>();
            for (IFBlock ifBlock : tmpl.getIfBlocks()) {
                for (Binding binding : ifBlock.getBindings()) {
                    BindingParameter bind = binding.getEndBinding();
                    IFactType inputObject = null;
                    if (bind instanceof BindingInputFact) {
                        inputObject = ((BindingInputFact) bind).getInputObject();
                    } else if (bind instanceof BindingInputField) {
                        inputObject = ((BindingInputField) bind).getInputObject();
                    }
                    if (inputObject instanceof EventInput) {
                        eventInputs.add((EventInput) inputObject);
                    }

                }
            }        
        return eventInputs;
    }
    
    private HashMap<String, FactType> generateClassNameToEventType(Template tmpl){
        HashMap<String,FactType> result = new HashMap<>();
        for(IFBlock ifBlock: tmpl.getIfBlocks()){
            if(ifBlock.getType().equals("activity")){   // this IFBlock represents an eventType
                FactType eventType = ifBlock.getEvent();
                result.put(eventType.getClassName(), eventType);
            }
        }        
        return result;
    }
    
    private void mergeFactTypeFacts(FactType toMerge, FactType dbType){
        ArrayList<CMTField> toMergeFields = toMerge.getFields();
        ArrayList<CMTField> dbFields = dbType.getFields();
        ArrayList<CMTField> fieldsToCreate = new ArrayList<>();
        // Subtracting dbFields from import fields (= fields we need to add)
        // ! DO NOT USE Set subtraction (subtraction must only be based on nameXtype)
        for (CMTField dbField : dbFields) {
            for (CMTField importField : toMergeFields) {
                // Importfield is not yet in dbtype => add it to the "toCreate" list
                if (!(importField.getType().equals(dbField.getType())
                        && importField.getName().equals(dbField.getName()))) {
                        fieldsToCreate.add(importField);
                }
            }
        }

        // Check same name/different type fields
        // This is necessary, as it can be possible that two fields have the same
        // name but different type (in a Java scope, all names must be unique)
        ArrayList<CMTField> checkedFieldsToCreate =
                checkSameNameDifferentType(dbFields, fieldsToCreate);

        // Adding the new fields
        CMTCore core = CMTCore.get();
        core.addFieldsToFactTypeFact(dbType, checkedFieldsToCreate);
        
    }
    
    private void mergeEventTypes(FactType toMerge, EventInput toMergeInput,
            FactType dbType, EventInput dbInput) {
        if(!(toMerge.getFields().equals(toMergeInput.getFields()) && dbType.getFields().equals(dbInput.getFields()))){
            System.out.println("ShIX -- mergeEventTypes -- ASSERTION FactType <-> EventInput failed!!");
        }
        
        ArrayList<CMTField> dbFields = dbInput.getFields();
        ArrayList<CMTField> importFields = toMergeInput.getFields();
        ArrayList<CMTField> fieldsToCreate = new ArrayList<>();
        // Field per field de limitaties checken
        
        for(CMTField dbField: dbFields){
            for(CMTField importField: importFields){
                if(importField.getType().equals(dbField.getType())  // beide velden hebben zelfde naam en type
                        && importField.getName().equals(dbField.getName())){
                    // Deze twee fields moeten dus gemerged worden
                    // => check limitations in geval van non-custom event
                    if(!toMerge.isIsCustom()){
                    FieldValueLimitation toMergeLim = 
                            toMergeInput.getFieldValueLimitation(importField.getName());
                    FieldValueLimitation dbLim = dbInput.getFieldValueLimitation(dbField.getName());
                    // TODO: SUGGESTIONS: limitations <<<<<<<<<<<<<<<<<<<<<<<<<< SUGGESTIONS
                    // requestUserDecision(Lim 1 , Lim 2), return decision
                    FieldValueLimitation decidedLimitation = null; // DUMMY
                    dbInput.setFieldValueLimitation(dbField.getName(), decidedLimitation); 
                    }
                } else {
                    // Nieuw veld aan te maken in db (want bestaat nog niet in db)
                    fieldsToCreate.add(importField);
                }
            }
            // TODO
            ArrayList<CMTField> checkedFieldsToCreate = checkSameNameDifferentType(dbFields, fieldsToCreate);
            CMTCore.get().addFieldsToEventType(dbType, checkedFieldsToCreate);
        }
    }

    /**
     * @param factType
     * @param dbTypes
     * @param toFillInBlocksToIndex
     * @param resolvedIndexes
     * @return : returns the FactType from DB with which has been merged. In case
     * a new FactType has been created, this FactType will be returned
     */
    // DONE except suggestions
    public FactType solveFactTypeFact(FactType factType, HashSet<FactType> dbTypes, 
            HashMap<Integer, IFactType> indexToToFillInBlocks,
            HashMap<IFactType, Integer> toFillInBlocksToIndex, 
            ArrayList<Integer> resolvedIndexes, TemplateHA tmpl) {
        
        // Type is already resolved
        if(resolvedIndexes.contains(toFillInBlocksToIndex.get(factType))){
            return factType;
        }
        resolvedIndexes.add(toFillInBlocksToIndex.get(factType)); // BOOKKEEPING (this FactType will be resolved) at the end of this function
        
        ArrayList<Pair<Double, FactType>> scores
                = generateJaroWinklerScores(factType.getClassName(), dbTypes, scoreTreshold);
        if (!scores.isEmpty()) {
            for (Pair<Double, FactType> score : scores) {
                if (score.getKey().equals(1.0)) {
                    // PerfectMatch
                    // automerge
                    mergeFactTypeFacts(factType, score.getValue());
                    return score.getValue();        // CASE1: perfectMatch
                }
            }
            // Suggestions
            // => ask suggestions to user            
            // First sort the scores
            scores.sort(new Comparator<Pair<Double,FactType>>() {
                        @Override
                        public int compare(Pair<Double, FactType> o1, Pair<Double, FactType> o2) {
                            if(o2.getKey()> o1.getKey())
                                return 1;
                            else return -1;
                        }
                    });
            
            // Then retrieve the FactTypes in the same order
            ArrayList<FactType> suggestions = scores.stream()
                    .map(scorepair -> scorepair.getValue())
                    .collect(Collectors.toCollection(ArrayList::new));
            
            // sent suggestions to user (arraylist "suggestions")
            // requestUserFactTypeDecision(suggestions) <<<<<<<<<<<<<<<<<<<<<<<<TODO Suggestions
            SolveState currState = new SolveState(indexToToFillInBlocks, toFillInBlocksToIndex);
            suggestionsPool.requestUserDecision(factType, 
                    toFillInBlocksToIndex.get(factType), suggestions, tmpl,
                    importRecursionLevel, currState);
            
            FactType chosenDbFactType = null; // DUMMY !let op: user kan ook gekozen hebben om een nieuwe class aan te maken!!
            mergeFactTypeFacts(factType, chosenDbFactType);
            return chosenDbFactType; // CASE 2: user suggestion
        } else {
            // Scores is empty -> no match
            // => create new class
            createNewFactType(factType);
            return factType; // CASE 3: No matches, completely new import
        }
    }
    

    // DONE except suggestions
    public void solveFact(Fact fact, HashMap<Integer, IFactType> indexToToFillInBlocks, 
            HashMap<IFactType, Integer> toFillInBlocksToIndex, HashSet<FactType> dbTypes,
            ArrayList<Integer> resolvedIndexes, TemplateHA tmpl) {
        
        if(resolvedIndexes.contains(toFillInBlocksToIndex.get(fact))){
            return; // fact already resolved
        }
        resolvedIndexes.add(toFillInBlocksToIndex.get(fact)); // BOOKKEEPING (this Fact will be resolved) at the end of this function
        
        // First: solve the FactType
        FactType factTypeFact = fromFactToFactType(fact);
        FactType resolvedFactType = solveFactTypeFact(factTypeFact, dbTypes, 
                indexToToFillInBlocks, toFillInBlocksToIndex, resolvedIndexes, tmpl);
        // Fetch the Facts of the resolved FactType
        HashSet<Fact> dbFacts = CMTDelegator.get()
                .getFactsInFactVersionWithType(resolvedFactType.getClassName());
        
        // TODO: SUGGESTIONS to user: (user moet ALTIJD kiezen hier! Geen matches in DB -> kies het geimporteerde Fact)
        // TODO: requestUserFactDecision(ArrayList<Fact> dbFacts, Fact importFact)
        //----------------------------
        // USER CHOSE DBFact => boekhouding aanpassen (hashmap)
        SolveState currState = new SolveState(indexToToFillInBlocks, toFillInBlocksToIndex);
        suggestionsPool.requestUserDecision(fact, importRecursionLevel, new ArrayList<>(dbFacts), tmpl, importRecursionLevel, currState);
        
        Fact chosenDbFact = null; // TODO
        if (chosenDbFact != null) {
            Integer index = toFillInBlocksToIndex.get(fact);
            indexToToFillInBlocks.replace(index, chosenDbFact); // change the importtemplate to the db fact the user has chosen
            toFillInBlocksToIndex.remove(fact);
            toFillInBlocksToIndex.put(chosenDbFact, index);
        } else {
            // USER CHOSE TO IMPORT FACT
            // Voeg fact toe (geen veranderingen in boekhouding nodig)

            CMTCore.get().addFactInFactFormat(fact);            
        }

    }
    
    public FactType fromFactToFactType(Fact fact){
        FactType factTypeFact = new FactType(fact.getClassName(), "fact", fact.getUriField(), fact.getFields());
        factTypeFact.setIsCustom(false);
        factTypeFact.setCategory("Code");        
        return factTypeFact;
    }
    /*
     *   Both HashMaps are mutable :)
     */
    public void solveFactTypeEvent(EventInput eventInput,
            HashMap<String, FactType> classNameToEventType, HashSet<FactType> dbTypes,
            HashMap<Integer, IFactType> indexToToFillInBlocks,
            HashMap<IFactType, Integer> toFillInBlocksToIndex, 
            ArrayList<Integer> resolvedIndexes, JSONObject jTemplate,
            TemplateHA tmpl) {
        
        if(resolvedIndexes.contains(toFillInBlocksToIndex.get(eventInput))){
            return; // eventInput already resolved
        }
        resolvedIndexes.add(toFillInBlocksToIndex.get(eventInput)); // BOOKKEEPING (this event will be resolved) at the end of this function

        FactType eventType = classNameToEventType.get(eventInput.getClassName());
        ArrayList<Pair<Double, FactType>> scores
                = generateJaroWinklerScores(eventType.getClassName(), dbTypes, scoreTreshold);
        if (!scores.isEmpty()) {
            for (Pair<Double, FactType> score : scores) {
                if (score.getKey().equals(1.0)) {
                    // PerfectMatch
                    //--------------------------
                    FactType dbType = score.getValue();
                    EventInput dbInput = getEventInputOfEventType(dbType);
                    if (!eventType.isIsCustom()) { // Non-Custom eventType                          
                        mergeEventTypes(eventType, eventInput, dbType, dbInput);
                        return;
                    } else { // Custom eventType (hier bestaan geen limitations)
                        // -> neem user zijn eigen Event dat al in de db zit,
                        // negeer de import, maar pas wel aan in de TemplateHashMap
                        // Hou beide hashMaps up to date
                        Integer idx = toFillInBlocksToIndex.get(eventInput);
                        indexToToFillInBlocks.replace(idx, dbInput);
                        toFillInBlocksToIndex.remove(eventInput);
                        toFillInBlocksToIndex.put(dbInput, idx);
                        return;
                    }
                }
            } 
            // Partial match
            scores.sort(new Comparator<Pair<Double, FactType>>() {
                @Override
                public int compare(Pair<Double, FactType> o1, Pair<Double, FactType> o2) {
                    if (o2.getKey() > o1.getKey()) {
                        return 1;
                    } else {
                        return -1;
                    }
                }
            });
            // PartialMatch
            //--------------------------
            ArrayList<FactType> suggestions = scores.stream()
                    .map(scorepair -> scorepair.getValue())
                    .collect(Collectors.toCollection(ArrayList::new));
            // sent suggestions to user (arraylist "suggestions")
            // requestUserFactTypeDecision(suggestions) <<<<<<<<<<<<<<<<<<<<<<<<TODO Suggestions: Voor events gaan we ook EventInput ergens moeten bijhouden...
            SolveState currState = new SolveState(indexToToFillInBlocks, toFillInBlocksToIndex, eventInput);
            suggestionsPool.requestUserDecision(eventType, importRecursionLevel, new ArrayList<>(suggestions), tmpl, importRecursionLevel, currState);
            
            FactType chosenDbEventType = null;
            if (chosenDbEventType == null) {
                // User wil nieuw type (zelfde voor fix als custom event)
                createNewEventType(eventType);
            } else {
                EventInput dbInput = getEventInputOfEventType(chosenDbEventType);
                // User heeft een suggestie gekozen
                if (!eventType.isIsCustom()) { // non-custom event                            
                    mergeEventTypes(eventType, eventInput, chosenDbEventType, dbInput);
                } else { // Custom event
                    // ignore import, pas aan in de hashmap van de importTemplate
                    Integer idx = toFillInBlocksToIndex.get(eventInput);
                    indexToToFillInBlocks.replace(idx, dbInput);
                    toFillInBlocksToIndex.remove(eventInput);
                    toFillInBlocksToIndex.put(dbInput, idx);
                }
            }
        }    
        else { 
            // No Matches
            //------------------------
            createNewEventType(eventType); // same for custom/non-custom
    }
        if(eventType.isIsCustom()){
            JSONObject jDeclaringTemplate = getDeclaringJTemplateOfCustomEvent(jTemplate, eventType);
            // RECURSIE
            importRecursionLevel = importRecursionLevel + 1; // increase recursionlevel
            importTemplateRec(jTemplate);
        }

}
    // Gegeven de huidige template "jTemplate" waarin "eventType" zich bevindt, geeft de JSONRepresentatie
    // van de template terug die "eventType" representeert
    public JSONObject getDeclaringJTemplateOfCustomEvent(JSONObject jTemplate, FactType eventType) {
        JSONArray jIFBlocks = jTemplate.getJSONArray("ifblocks");
        for (int i = 0; i < jIFBlocks.length(); i++) {
            JSONObject jIFBlock = jIFBlocks.getJSONObject(i);
            JSONArray jBindings = jIFBlock.getJSONArray("bindings");
            for (int j = 0; j < jBindings.length(); j++) {
                JSONObject jBinding = jBindings.getJSONObject(j);
                JSONArray jDeclarations = jBinding.getJSONArray("declarations");
                for (int k = 0; k < jDeclarations.length(); k++) {
                    JSONObject jTemplateDecl = jDeclarations.getJSONObject(k);
                    JSONObject jOutput = jTemplateDecl.getJSONObject("output");
                    if (jOutput.getString("name").equals(eventType.getClassName())) {
                        return jTemplateDecl;
                    }
                }
            }
        }
        return null;
    }


    // IFBlockLoop
    @Deprecated
    private static void checkIFBlocks(JSONArray jIFBlocks) {
        for (int i = 0; i < jIFBlocks.length(); i++) {
            JSONObject jIFBlock = jIFBlocks.getJSONObject(i);
            JSONArray jBindings = jIFBlock.getJSONArray("bindings");
            /* verifyAndImportBindings(jBindings);*/

            // (als IFBlock of type "acticity" is => de classe van het IFBlock zelf ook importeren!!) -> niet nodig: custom events hebben altijd
            // een binding naar hun eigen type (wordt dus al door de TemplateConverter gedaan)
        }
    }

    // BindingsLoop
    /*@Deprecated
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
                if((resolvedTypesInThisImport.containsKey(fieldType) && (resolvedTypesInThisImport.get(fieldType).contains(fieldName)))
                        || isJavaType(fieldType)){
                    continue;   // FieldType and Fieldname already resolved (or it's a Java type)
                }
                // SQL SQL SQL SQL SQL SQL SQL SQL SQL SQL SQL SQL SQL SQL SQL SQL SQL SQL
                HashSet<FactType> eventTypes = CMTDelegator.get().getAvailableEventTypes();
                for(FactType matchEventType : eventTypes){
                    Double score = JaroWinklerDistance
                            .apply(fieldType, matchEventType.getClassName());
                    if(score == 1.0){
                        // CASE1: Perfect match                  
                        mergeFieldFields(fieldType, fieldName, jDeclarations, matchEventType);
                    } else if (score >= scoreTreshold) {
                        // CASE2: Partial Match: let user decide
                        // suggestions (classA, classB,...,newClass) -> check fields OR createNewClass 
                        boolean userDecisionNewClass = false;   //TODO
                        if (!userDecisionNewClass) {
                            createNewClassDepr(fieldType, fieldName); //TODO
                        } else {
                            FactType userChoice = new FactType(); // << retrieve from SQL
                            mergeFieldFields(fieldType, fieldName, jDeclarations, userChoice);
                        }
                    } else { // CASE3: No Match: score < treshold
                        createNewClassDepr(fieldType, fieldName);
                    }
                }                   
                // Check Fields (by name AND type)
                    //> All fields matching? -> binding resolved
                    //> Some/None fields matching? -> create subclass (extend), verifyAndImportFields (on type
            }
                   
        }
    }/**/

    public boolean isJavaType(String type) {
        return type.toLowerCase().contains("java");
    }
}
