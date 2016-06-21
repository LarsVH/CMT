/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ac.vub.wise.cmtserver.sharing;

import be.ac.vub.wise.cmtserver.blocks.FactType;
import be.ac.vub.wise.cmtserver.blocks.IFactType;
import java.util.ArrayList;

/**
 *
 * @author lars
 */
public class IFactTypeSuggestions {
    
    Integer index;
    IFactType importIFactType;
    ArrayList<FactType> suggestions;   // !! kan null zijn
    IFactType chosenSuggestion;         // kan null zijn
    FactType eventType;                // in case of a custom event -> zijn eventType bijhouden (kan null zijn)
   
    // Converter
    public IFactTypeSuggestions(){};
    
    // CASE 3: No match
    public IFactTypeSuggestions(Integer index, IFactType iFactType) {
        this.index = index;
        this.importIFactType = iFactType;
    }
    
    // CASE 2: Partial Match
    public IFactTypeSuggestions(Integer index, IFactType iFactType, ArrayList<FactType> suggestions) {
        this.index = index;
        this.importIFactType = iFactType;
        this.suggestions = suggestions;
    }
    // CASE 1: PerfectMatch
    public IFactTypeSuggestions(Integer index, IFactType factType, ArrayList<FactType> suggestions, IFactType chosenSuggestion) {
        this.index = index;
        this.importIFactType = factType;
        this.suggestions = suggestions;
        this.chosenSuggestion = chosenSuggestion;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public IFactType getImportIFactType() {
        return importIFactType;
    }

    public void setImportIFactType(IFactType importIFactType) {
        this.importIFactType = importIFactType;
    }

    public ArrayList<FactType> getSuggestions() {
        return suggestions;
    }

    public void setSuggestions(ArrayList<FactType> suggestions) {
        this.suggestions = suggestions;
    }

    public IFactType getChosenSuggestion() {
        return chosenSuggestion;
    }

    public void setChosenSuggestion(IFactType chosenSuggestion) {
        this.chosenSuggestion = chosenSuggestion;
    }

    public FactType getEventType() {
        return eventType;
    }

    public void setEventType(FactType eventType) {
        this.eventType = eventType;
    }

 
}
