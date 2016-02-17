/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ac.vub.wise.cmtgui.views;

import be.ac.vub.wise.cmtclient.blocks.Binding;
import be.ac.vub.wise.cmtclient.blocks.IFactType;
import javafx.scene.control.ToggleButton;

/**
 *
 * @author Sandra
 */
public class CmtToggleEU extends ToggleButton{
    
    public boolean isFilled;
    public IFactType obj;
    public Binding binding = null;
    
    
    public boolean isIsFilled() {
        return isFilled;
    }

    public void setIsFilled(boolean isFilled) {
        this.isFilled = isFilled;
    }

    public IFactType getObj() {
        return obj;
    }

    public void setObj(IFactType obj) {
        this.obj = obj;
    }

    public Binding getBinding() {
        return binding;
    }

    public void setBinding(Binding binding) {
        this.binding = binding;
    }
    
    
    
}
