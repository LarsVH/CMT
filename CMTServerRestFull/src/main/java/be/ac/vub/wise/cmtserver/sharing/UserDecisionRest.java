/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ac.vub.wise.cmtserver.sharing;

import be.ac.vub.wise.cmtserver.blocks.EventInput;
import be.ac.vub.wise.cmtserver.blocks.Fact;
import be.ac.vub.wise.cmtserver.blocks.FactType;
import be.ac.vub.wise.cmtserver.blocks.IFactType;
import be.ac.vub.wise.cmtserver.blocks.TemplateHA;
import be.ac.vub.wise.cmtserver.restfull.CMTCore;
import be.ac.vub.wise.cmtserver.util.Converter;
import be.ac.vub.wise.cmtserver.util.MiscUtil;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author lars
 */
public class UserDecisionRest {
    private SharingImportExport iX;
    private ArrayList<TemplateSuggestions> suggestionsPool;

    public UserDecisionRest(SharingImportExport iX) {
        this.iX = iX;
    } 
    public UserDecisionRest(SharingImportExport iX, ArrayList<TemplateSuggestions> suggestionsPool) {
        this.iX = iX;
        this.suggestionsPool = suggestionsPool;
    }    
    
    public ArrayList<TemplateSuggestions> getSuggestionsPool() {
        return suggestionsPool;
    }

    public void setSuggestionsPool(ArrayList<TemplateSuggestions> suggestionsPool) {
        this.suggestionsPool = suggestionsPool;
    }

    
    
    public JSONArray sendToClient(){
        JSONArray jSuggPool = Converter.fromTemplateSuggestionsListToJSON(suggestionsPool);
        JSONObject out = new JSONObject();
        out.put("suggestions", jSuggPool);
        System.out.println("Output -- Suggestions: \n" + out);    // huidig testing
        
        // TODO: ask Sandra hoe we dit praktisch naar de client moeten sturen<<<<<<<<<<<<<<<<<<<<<<<<<<<
        
        return jSuggPool;
    }
    
    

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
                              
                CMTCore.get().addTemplateHA(tmpl);
                    
        }
        
    }
    
}
