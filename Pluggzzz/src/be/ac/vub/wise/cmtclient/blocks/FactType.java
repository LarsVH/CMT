/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ac.vub.wise.cmtclient.blocks;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Objects;

/**
 *
 * @author Sandra
 */
public class FactType implements IFactType, Serializable{
    //{"className":<>, "type":< fact || time || activity> , "activityCustom": boolean (only if activity type), "varList": [{var:<>}, ..], "varFormat": <>, 
    // "uriField":<>, "fields":[{"fieldName":<>, "fieldType":<>}]}
    String className;
    String type;
    boolean isCustom;
    ArrayList<String> varList;
    String varFormat;
    String uriField;
    ArrayList<CMTField> fields;
    String category;
    
    public FactType(String className, String type, String uriField, ArrayList<CMTField> fields){
        this.className = className;
        this.type = type;
        this.uriField = uriField;
        this.fields = fields;
        this.varList = new ArrayList<String>();
        this.varFormat = "";
    
    }

    public boolean isIsCustom() {
        return isCustom;
    }

    public void setIsCustom(boolean isCustom) {
        this.isCustom = isCustom;
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

    public String getClassName() {
        return className;
    }

    public String getType() {
        return type;
    }

    public String getUriField() {
        return uriField;
    }

    public ArrayList<CMTField> getFields() {
        return fields;
    }
    
    public void addCMTField (CMTField field){
        fields.add(field);
    }
    
    public void setUrifield(String f){
        this.uriField = f;
    }

   

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
    
    

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + Objects.hashCode(this.className);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final FactType other = (FactType) obj;
        if (!Objects.equals(this.className, other.className)) {
            return false;
        }
        return true;
    }
    
    
    
}
