/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ac.vub.wise.cmtserver.blocks;

/**
 *
 * @author lars
 */
public interface BindingInputBlock extends BindingParameter {
    
    public IFactType getInputObject();
    public void setInputObject(IFactType inputObject);
    
    public String getFactId();
    public void setFactId(String factId);
    
    
}
