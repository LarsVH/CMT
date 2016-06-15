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
public class UserDecisionRest implements UserDecision {

    @Override
    public FactType requestUserFactTypeDecision(ArrayList<FactType> factTypes) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Fact requestUserFactDecision(ArrayList<Fact> dbFacts, Fact importFact) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
