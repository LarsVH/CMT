/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ac.vub.wise.cmtserver.sharing;

import be.ac.vub.wise.cmtserver.blocks.Fact;
import be.ac.vub.wise.cmtserver.blocks.FactType;
import java.util.ArrayList;

/**
 *
 * @author lars
 */
public interface UserDecision {
    
    public FactType requestUserFactTypeDecision(ArrayList<FactType> factTypes);
    public Fact requestUserFactDecision(ArrayList<Fact> dbFacts, Fact importFact);   
}
