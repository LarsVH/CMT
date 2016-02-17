/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ac.vub.wise.cmtgui.dialogs;

import java.util.HashMap;

/**
 *
 * @author Sandra
 */
public class ResultFieldsActions {
    
    public HashMap<String,String> res;
    
    public ResultFieldsActions(HashMap<String,String> res){
        this.res = res;
    }
    
    public HashMap<String,String> getResults(){
        return res;
    }
}
