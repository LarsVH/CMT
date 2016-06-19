/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ac.vub.wise.cmtserver.sharing;

import be.ac.vub.wise.cmtserver.blocks.FactType;
import be.ac.vub.wise.cmtserver.blocks.TemplateHA;
import java.util.HashMap;

/**
 *
 * @author lars
 */
public class TemplateSuggestions {
    
    Integer recursionLevel;
    TemplateHA template;
    HashMap<Integer, IFactTypeSuggestions> indexToSuggestions = new HashMap<>();  // Mapt de index van een template (ToFillInBlocks) op het suggestiesobject voor die index
    FactType eventType; // Als template een event genereert, dat event ook bijhouden (voor GUI)

    public TemplateSuggestions(Integer recursionLevel, TemplateHA template) {
        this.recursionLevel = recursionLevel;
        this.template = template;
    }

    public TemplateSuggestions(Integer recursionLevel, TemplateHA template, FactType eventType) {
        this.recursionLevel = recursionLevel;
        this.template = template;
        this.eventType = eventType;
    }

    public Integer getRecursionLevel() {
        return recursionLevel;
    }

    public TemplateHA getTemplate() {
        return template;
    }

    public HashMap<Integer, IFactTypeSuggestions> getIndexToSuggestions() {
        return indexToSuggestions;
    }

    public FactType getEventType() {
        return eventType;
    }
    
    public void addIFactTypeSuggestions(Integer index, IFactTypeSuggestions suggs){
        indexToSuggestions.put(index, suggs);
    }   
}
