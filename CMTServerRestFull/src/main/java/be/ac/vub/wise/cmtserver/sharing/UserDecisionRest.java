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
    
    private ArrayList<TemplateSuggestions> suggestionsPool = new ArrayList<>();
    
    public void addTemplateSuggestions(TemplateSuggestions tmplsuggs) {
        suggestionsPool.add(tmplsuggs);
    }
    
    public void addIFactTypeSuggestions(TemplateHA tmpl, Integer index, IFactTypeSuggestions suggs){
        for(TemplateSuggestions tmplsuggs: suggestionsPool){
            if(tmplsuggs.getTemplate().equals(tmpl)){
                tmplsuggs.addIFactTypeSuggestions(index, suggs);
                return;
            }
        }
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
    
}
