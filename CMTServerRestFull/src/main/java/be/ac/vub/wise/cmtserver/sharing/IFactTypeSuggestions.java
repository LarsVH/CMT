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
    IFactType iFactType;
    ArrayList<FactType> suggestions;   // !! kan null zijn
    IFactType chosenSuggestion;         // kan null zijn
   
    // CASE 3: No match
    public IFactTypeSuggestions(Integer index, IFactType iFactType) {
        this.index = index;
        this.iFactType = iFactType;
    }
    
    // CASE 2: Partial Match
    public IFactTypeSuggestions(Integer index, IFactType iFactType, ArrayList<FactType> suggestions) {
        this.index = index;
        this.iFactType = iFactType;
        this.suggestions = suggestions;
    }
    // CASE 1: PerfectMatch
    public IFactTypeSuggestions(Integer index, IFactType factType, ArrayList<FactType> suggestions, IFactType chosenSuggestion) {
        this.index = index;
        this.iFactType = factType;
        this.suggestions = suggestions;
        this.chosenSuggestion = chosenSuggestion;
    }

    public Integer getIndex() {
        return index;
    }

    public IFactType getIFactType() {
        return iFactType;
    }

    public ArrayList<FactType> getSuggestions() {
        return suggestions;
    }

    public IFactType getChosenSuggestion() {
        return chosenSuggestion;
    }

    public void setChosenSuggestion(IFactType chosenSuggestion) {
        this.chosenSuggestion = chosenSuggestion;
    }    
}
