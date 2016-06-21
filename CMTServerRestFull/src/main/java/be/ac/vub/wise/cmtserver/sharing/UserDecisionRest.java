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
import be.ac.vub.wise.cmtserver.blocks.Template;
import be.ac.vub.wise.cmtserver.blocks.TemplateHA;
import be.ac.vub.wise.cmtserver.restfull.CMTCore;
import be.ac.vub.wise.cmtserver.util.Converter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import org.json.JSONArray;

/**
 *
 * @author lars
 */
public class UserDecisionRest {
    private SharingImportExport iX;
    private ArrayList<TemplateSuggestions> suggestionsPool;

    
    public UserDecisionRest(SharingImportExport iX, ArrayList<TemplateSuggestions> suggestionsPool) {
        this.iX = iX;
        this.suggestionsPool = suggestionsPool;
    }    
    
    public ArrayList<TemplateSuggestions> getSuggestionsPool() {
        return suggestionsPool;
    }    
    
    public JSONArray sendToClient(){
        JSONArray jSuggPool = Converter.fromTemplateSuggestionsListToJSON(suggestionsPool);
        
        // TODO: ask Sandra hoe we dit praktisch naar de client moeten sturen<<<<<<<<<<<<<<<<<<<<<<<<<<<
        
        return jSuggPool;
    }
    
    
    
    // TODO
    public void onSuggestionsReceived(JSONArray jSuggsList){
        ArrayList<TemplateSuggestions> tmplSuggsList = Converter.fromJSONToTemplateSuggestionsList(jSuggsList);
        
        for(TemplateSuggestions tmplSuggs: tmplSuggsList){
            TemplateHA tmpl = tmplSuggs.getTemplate();
            HashMap<Integer, IFactTypeSuggestions> indexToSuggs = tmplSuggs.getIndexToSuggestions();
            HashMap<Integer, IFactType> indexToToFillInBlocks = iX.getInputsTemplate(tmpl);
            
            for(Map.Entry<Integer, IFactTypeSuggestions> entry: indexToSuggs.entrySet()){
                Integer index = entry.getKey();
                IFactTypeSuggestions iFTSuggs = entry.getValue();
                IFactType fTypeToResolve = iFTSuggs.getImportIFactType();
                if(fTypeToResolve instanceof FactType){
                    iX.doSolveFactType(iFTSuggs, indexToToFillInBlocks);
                    
                } else if(fTypeToResolve instanceof Fact){
                    iX.doSolveFact(iFTSuggs, indexToToFillInBlocks);
                    
                } else if(fTypeToResolve instanceof EventInput){
                    iX.doSolveEventInput(iFTSuggs, tmplSuggs, indexToToFillInBlocks);                    
                }
            }
                iX.setInputsTemplate(tmpl, indexToToFillInBlocks);
                // TODO registreer template
                CMTCore.get().addTemplateHA(tmpl);
                    
        }
        
    }
    
}
