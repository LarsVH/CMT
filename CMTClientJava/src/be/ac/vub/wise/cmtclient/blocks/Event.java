/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ac.vub.wise.cmtclient.blocks;

import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * @author Sandra
 */
public class Event implements IFactType, Serializable{
   
    public String className;
    public String extend; // type of facttype
    public boolean isCustom = false;
    public String uriField; // field used for input data
    public ArrayList<String> varList;
    public String varFormat;
    public String valueUriField;
    public ArrayList<CMTField> fields;

    public Event(){}
    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getExtend() {
        return extend;
    }

    public void setExtend(String extend) {
        this.extend = extend;
    }

    public boolean isCustom() {
        return isCustom;
    }

    public void setIsCustom(boolean isCustom) {
        this.isCustom = isCustom;
    }

    public String getUriField() {
        return uriField;
    }

    public void setUriField(String uriField) {
        this.uriField = uriField;
    }

    public ArrayList<String> getVarList() {
        return varList;
    }

    public void setVarList(ArrayList<String> varList) {
        this.varList = varList;
    }

    public String getVarFormat() {
        return varFormat;
    }

    public void setVarFormat(String varFormat) {
        this.varFormat = varFormat;
    }

    public ArrayList<CMTField> getFields() {
        return fields;
    }

    public void setFields(ArrayList<CMTField> fields) {
        this.fields = fields;
    }

    public String getValueUriField() {
        return valueUriField;
    }

    public void setValueUriField(String valueUriField) {
        this.valueUriField = valueUriField;
    }

    
    
    
}
