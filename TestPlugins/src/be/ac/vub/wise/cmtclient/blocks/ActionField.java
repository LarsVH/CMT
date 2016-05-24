/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ac.vub.wise.cmtclient.blocks;

import java.util.ArrayList;

/**
 *
 * @author Sandra
 */
public class ActionField {
    
    public String name;
    public ArrayList<String> varList = null;
    public String format = "";
    public String value;
    
    public ActionField(String name, ArrayList<String> varList, String format){
        this.name = name;
        this.varList = varList;
        this.format = format;
    }

    public String getName() {
        return name;
    }

    public ArrayList<String> getVarList() {
        return varList;
    }

    public String getFormat() {
        return format;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
    
    
}
