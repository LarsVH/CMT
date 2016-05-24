/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package testplugins;

import be.ac.vub.wise.cmtclient.blocks.AInput;

/**
 *
 * @author Sandra
 */
public class ItIsAfter {
    
     @AInput(input=AInput.Input.Variable, format="00:00")
    public String hour;
    
    

    public String getHour() {
        return hour;
    }

    public void setHour(String hour) {
        this.hour = hour;
    }

    
    
    
}
