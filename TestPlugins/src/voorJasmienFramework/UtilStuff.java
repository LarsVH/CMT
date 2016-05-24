/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package voorJasmienFramework;

import be.ac.vub.wise.cmtclient.blocks.CMTField;
import be.ac.vub.wise.cmtclient.blocks.Event;
import be.ac.vub.wise.cmtclient.blocks.Fact;
import be.ac.vub.wise.cmtclient.blocks.FactType;
import be.ac.vub.wise.cmtclient.core.CMTClient;
import java.util.ArrayList;
import java.util.HashSet;

/**
 *
 * @author Sandra
 */
public class UtilStuff {
    
    public HashSet<Fact> getCompatibleUIs(ArrayList<FactType> selectedEvents){ // verander facttype voor event!!
        HashSet<Fact> resultUis = new HashSet<>();
        HashSet<String> propsToBeChecked = new HashSet<String>();
        for(FactType eventType : selectedEvents){
            ArrayList<CMTField> fieldsEvent = eventType.getFields();
            if(fieldsEvent.contains(new CMTField("properties", null))){
                CMTField props = fieldsEvent.get(fieldsEvent.indexOf(new CMTField("properties", null)));
                if(props.getValue() instanceof ArrayList){
                    ArrayList<String> propList = (ArrayList) props.getValue();
                    for(String prop : propList){
                        propsToBeChecked.add(prop);
                    }
                }
            }
        }
        // getUIs that matches all props in propsTobeChecked
        HashSet<String> uisMatched = findUIMatches(propsToBeChecked, new HashSet<String>());
        for(String st : uisMatched){
            Fact uiFact = CMTClient.getFact("AUI", "name", st);
            if(uiFact !=null){
                resultUis.add(uiFact);
            }
        }
        return resultUis;
    }
    
    private HashSet<String> findUIMatches(HashSet<String> props, HashSet<String> uis){
        String prop = props.iterator().next();
        props.remove(prop);
        HashSet<String> uisMatches = new HashSet();
        if(!uis.isEmpty()){
            for(String ui : uis){
                boolean res = Communication.getAUIHasProperty(ui, "configProp", prop);
                if(res){
                    uisMatches.add(ui);
                }
            }
        }else{
            uisMatches.addAll(Communication.getAUIsWithPropertyWithNameAndValue("configProp", prop));
        }
        if(!props.isEmpty()){
            findUIMatches(props, uisMatches);
        }else{
            return uis;
        }
        return null;
    }
    
}
