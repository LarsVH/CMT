/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package testplugins;

import be.ac.vub.wise.cmtclient.blocks.AInput;
import be.ac.vub.wise.cmtclient.blocks.AInput.Input;

/**
 *
 * @author Sandra
 */
public class Lamp {
    
    @AInput(input=Input.Fix)
    public String id;
    
    @AInput(input=Input.Variable, options={"on", "off"})
    public String status;
    
    public Lamp(){}
   
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    
    
}
