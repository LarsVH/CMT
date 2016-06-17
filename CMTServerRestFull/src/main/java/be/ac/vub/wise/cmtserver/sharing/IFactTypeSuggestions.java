/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ac.vub.wise.cmtserver.sharing;

import be.ac.vub.wise.cmtserver.blocks.IFactType;
import java.util.ArrayList;

/**
 *
 * @author lars
 */
public class IFactTypeSuggestions {
    
    Integer id;
    Integer index;
    IFactType iFactType;
    ArrayList<IFactType> suggestions;
    IFactType selectedSuggestion;

    public IFactTypeSuggestions(Integer id, Integer index, IFactType factType, ArrayList<IFactType> suggestions) {
        this.id = id;
        this.index = index;
        this.iFactType = factType;
        this.suggestions = suggestions;
    }

    public Integer getId() {
        return id;
    }

    public Integer getIndex() {
        return index;
    }

    public IFactType getIFactType() {
        return iFactType;
    }

    public ArrayList<IFactType> getSuggestions() {
        return suggestions;
    }

    public IFactType getSelectedSuggestion() {
        return selectedSuggestion;
    }

    public void setSelectedSuggestion(IFactType selectedSuggestion) {
        this.selectedSuggestion = selectedSuggestion;
    }    
}
