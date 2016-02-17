/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ac.vub.wise.cmtserver.blocks;

import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * @author Sandra
 */
public class ActionClient implements Action, Serializable{
    
    public String name;
    public ArrayList<ActionField> fields;
    
    public ActionClient(String name, ArrayList<ActionField> fields ){
       this.name = name;
       this.fields = fields;
    }

    public String getName() {
        return name;
    }

    public ArrayList<ActionField> getFields() {
        return fields;
    }
    
    
}
