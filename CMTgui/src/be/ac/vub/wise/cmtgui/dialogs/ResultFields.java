/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ac.vub.wise.cmtgui.dialogs;

import be.ac.vub.wise.cmtclient.blocks.FieldValueLimitation;
import be.ac.vub.wise.cmtgui.util.Property;
import java.util.HashMap;
import java.util.HashSet;

/**
 *
 * @author Sandra
 */
public class ResultFields {
    HashSet<FieldValueLimitation> results;
    
    public ResultFields(HashSet<FieldValueLimitation> res){
        this.results = res;
    }

    public HashSet<FieldValueLimitation> getResults() {
        return results;
    }
    
    
}
