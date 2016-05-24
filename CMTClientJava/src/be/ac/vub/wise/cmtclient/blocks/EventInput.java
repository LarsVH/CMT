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
public class EventInput implements IFactType{
    
    String className;
    ArrayList<FieldValueLimitation> limitations;
    ArrayList<CMTField> fields;
    
    public EventInput(){
        limitations = new ArrayList<>();
        fields = new ArrayList<>();
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public ArrayList<FieldValueLimitation> getLimitations() {
        return limitations;
    }

    public void setLimitations(ArrayList<FieldValueLimitation> limitations) {
        this.limitations = limitations;
    }
    
    public void addLimitation(FieldValueLimitation fieldLimitation){
        this.limitations.add(fieldLimitation);
    }

    public ArrayList<CMTField> getFields() {
        return fields;
    }

    public void setFields(ArrayList<CMTField> fields) {
        this.fields = fields;
    }
    
    public void addField(CMTField field){
        this.fields.add(field);
    }
    
    public FieldValueLimitation getFieldValueLimitation(String fieldName){
        for(FieldValueLimitation lim : limitations){
            if(lim.getFieldName().equals(fieldName)){
                return lim;
            }
        }
        return null;
    }
    
    public boolean isFilledIn(){
        int counter = 0;
        for(CMTField f : fields){
            if(f.isIsVar()){counter += 1;}
        }
        
        
        
        
//        int counter = limitations.size();
//     
//        for(FieldValueLimitation f : limitations){
//            boolean isVar = false;
//            for(CMTField cmtF: fields){
//                if(cmtF.getName().equals(f.getFieldName())){
//                    isVar = cmtF.isIsVar();
//                    break;
//                }
//            }
//            if(!isVar || (isVar && !f.getValue().equals(""))){
//                
//                
//                counter -=1;
//            }
//        }
        return limitations.size() == counter;
    }
    
}
