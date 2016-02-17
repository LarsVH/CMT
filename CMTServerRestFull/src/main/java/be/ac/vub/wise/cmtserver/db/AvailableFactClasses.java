/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ac.vub.wise.cmtserver.db;

import com.db4o.activation.ActivationPurpose;
import com.db4o.collections.ArrayList4;

/**
 *
 * @author Sandra
 */
public class AvailableFactClasses {
    
    public ArrayList4<String> list = null;

    public AvailableFactClasses(){
        list = new ArrayList4<>();
    }
    
    public ArrayList4<String> getList() { 
        list.activate(ActivationPurpose.WRITE);
        return list;
    }

    public void setList(ArrayList4<String> list) {
        this.list = list;
    }
    
    
    public void addClass(String className){
        list.activate(ActivationPurpose.WRITE);
        list.add(className);
    }
}
