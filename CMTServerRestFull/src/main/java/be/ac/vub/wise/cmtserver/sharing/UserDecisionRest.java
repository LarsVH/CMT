/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ac.vub.wise.cmtserver.sharing;

import be.ac.vub.wise.cmtserver.blocks.Binding;
import be.ac.vub.wise.cmtserver.blocks.BindingOutput;
import be.ac.vub.wise.cmtserver.blocks.BindingParameter;
import be.ac.vub.wise.cmtserver.blocks.CMTField;
import be.ac.vub.wise.cmtserver.blocks.EventInput;
import be.ac.vub.wise.cmtserver.blocks.Fact;
import be.ac.vub.wise.cmtserver.blocks.FactType;
import be.ac.vub.wise.cmtserver.blocks.IFactType;
import be.ac.vub.wise.cmtserver.blocks.OutputHA;
import be.ac.vub.wise.cmtserver.blocks.TemplateHA;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 *
 * @author lars
 */
public class UserDecisionRest {
    static int id = 0;
    private HashMap<TemplateHA, TemplateSuggestions> tmplToTmplSuggs = new HashMap<>(); // Mapt een template op zijn suggestions object
    private HashMap<Integer, SolveState> idToSolveState = new HashMap<>(); // Hou id bij en stuur die mee met request naar de client (later nodig om state terug op te halen)
    
    // Overload FactType
    public void requestUserDecision(FactType factType, Integer index,
            ArrayList<FactType> suggestions, TemplateHA tmpl, Integer recursionlevel, SolveState state) {
        ArrayList<IFactType> iSuggestions = new ArrayList<>();
        for (FactType sugg : suggestions) {
            iSuggestions.add(sugg);
        }
        requestUserDecision(factType, index, iSuggestions, tmpl, recursionlevel, state);
    }

    // Overload Fact
    public void requestUserDecision(Fact fact, Integer index,
            ArrayList<Fact> suggestions, TemplateHA tmpl, Integer recursionlevel, SolveState state) {        
        ArrayList<IFactType> iSuggestions = new ArrayList<>();
        for (Fact sugg : suggestions) {
            iSuggestions.add(sugg);
        }
        requestUserDecision(fact, index, iSuggestions, tmpl, recursionlevel, state);

    }
    
    private void requestUserDecision(IFactType fact_type, Integer index,
            ArrayList<IFactType> iSuggestions, TemplateHA tmpl, Integer recursionlevel, SolveState state) {
        
        Integer stateId = addSolveState(state); // Bookkeeping
        TemplateSuggestions tmplSuggs;
        if(tmplToTmplSuggs.containsKey(tmpl)){
            tmplSuggs = tmplToTmplSuggs.get(tmpl);
        } else {
            FactType tmplEventType = getEventTypeTemplateProducing(tmpl);
            if (tmplEventType != null) {// Template produces an event => include it
                tmplSuggs = new TemplateSuggestions(recursionlevel, tmpl, tmplEventType);
            } else {
                tmplSuggs = new TemplateSuggestions(recursionlevel, tmpl);
            }
            addTemplateSuggestion(tmpl, tmplSuggs); // Bookkeeping
        }

        IFactTypeSuggestions iFactSuggs = new IFactTypeSuggestions(stateId, index, fact_type, iSuggestions);
        HashMap<Integer, IFactTypeSuggestions> indexToSuggestions = tmplSuggs.getIndexToSuggestions();
        indexToSuggestions.put(index, iFactSuggs);         
    }
    
    private Integer addSolveState(SolveState state){
        idToSolveState.put(id, state);
        id = id + 1;
        return id;
    }
    
    private void addTemplateSuggestion(TemplateHA tmpl, TemplateSuggestions tmplSuggs){
        tmplToTmplSuggs.put(tmpl, tmplSuggs);     
    }
    
    public void sendToClient(){
        // TODO : loop over tmplToTmplSuggestions
        // Convert to JSON, sent to client
    }
    public void onSuggestionsReceived(ArrayList<TemplateSuggestions> tmplSuggsList){
        // TODO
        for(TemplateSuggestions tmplSuggs: tmplSuggsList){
            TemplateHA tmpl = tmplSuggs.getTemplate();
            HashMap<Integer, IFactTypeSuggestions> indexToSuggs = tmplSuggs.getIndexToSuggestions();
            for(Map.Entry<Integer, IFactTypeSuggestions> entry: indexToSuggs.entrySet()){
                Integer index = entry.getKey();
                IFactTypeSuggestions iFTSuggs = entry.getValue();
                IFactType fTypeToResolve = iFTSuggs.getIFactType();
                SolveState state = idToSolveState.get(iFTSuggs.getId());
                if(fTypeToResolve instanceof FactType){
                    // TODO: resolve FactType
                    
                } else if(fTypeToResolve instanceof Fact){
                    // TODO: resolve FactType
                    
                } else if(fTypeToResolve instanceof EventInput){
                    // TODO: resolve FactType
                    
                }

            }
        }
        
    }
    
    public FactType getEventTypeTemplateProducing(TemplateHA tmpl) {
        OutputHA output = tmpl.getOutput();
        LinkedList<Binding> bindings = output.getBindings();
        if (bindings != null) {
            ArrayList<CMTField> fields = new ArrayList<>();
            for (Binding b : bindings) {
                BindingParameter endBinding = b.getEndBinding();
                if (endBinding instanceof BindingOutput) {
                    BindingOutput bindingOut = (BindingOutput) endBinding;
                    CMTField f = new CMTField(bindingOut.getParameter(), bindingOut.getParType());
                    fields.add(f);
                }
            }
            FactType eventType = new FactType(output.getName(), "activity", "", fields);
            return eventType;
        } else {
            return null;
        }
    }
}
