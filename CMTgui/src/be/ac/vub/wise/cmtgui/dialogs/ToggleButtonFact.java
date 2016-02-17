/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ac.vub.wise.cmtgui.dialogs;

import be.ac.vub.wise.cmtclient.blocks.Fact;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleButton;

/**
 *
 * @author Sandra
 */
public class ToggleButtonFact extends RadioButton{
    
    public Fact fact;

    public Fact getFact() {
        return fact;
    }

    public void setFact(Fact fact) {
        this.fact = fact;
    }
    
    
    
}
