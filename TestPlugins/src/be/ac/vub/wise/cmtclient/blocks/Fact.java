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
public class Fact implements IFactType, Serializable{
    public String className;
    public String uriField;
    public ArrayList<CMTField> fields;
    public int id = 0;
    
    public Fact(String className, String uriField){
        this.className = className;
        this.uriField = uriField;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getUriField() {
        return uriField;
    }

    public void setUriField(String uriField) {
        this.uriField = uriField;
    }

    public ArrayList<CMTField> getFields() {
        return fields;
    }

    public void setFields(ArrayList<CMTField> fields) {
        this.fields = fields;
    }
    
    public String getUriValue(){
        for(CMTField f:fields){
            if(f.getName().equals(uriField)){
                return f.getValue().toString();
            }
        }
        return "";
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    
    
    
    
}
