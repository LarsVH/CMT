/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ac.vub.wise.cmtserver.sharing;

import be.ac.vub.wise.cmtserver.blocks.EventInput;
import be.ac.vub.wise.cmtserver.blocks.IFactType;
import java.util.HashMap;

/**
 *
 * @author lars
 */
public class SolveState {
            
    private HashMap<Integer, IFactType> indexToToFillInBlocks;
    private HashMap<IFactType, Integer> toFillInBlocksToIndex;
    private EventInput eventInput; // In case of an Event

    
    public SolveState(HashMap<Integer, IFactType> indexToToFillInBlocks, 
            HashMap<IFactType, Integer> toFillInBlocksToIndex, EventInput eventInput){
        
        // Make copies of both maps
        this.indexToToFillInBlocks = (HashMap<Integer, IFactType>) indexToToFillInBlocks.clone();
        this.toFillInBlocksToIndex = (HashMap<IFactType, Integer>) toFillInBlocksToIndex.clone(); 
        
        this.eventInput = eventInput;        
    }
    
    public SolveState(HashMap<Integer, IFactType> indexToToFillInBlocks, 
            HashMap<IFactType, Integer> toFillInBlocksToIndex){
        
        // Make copies of both maps
        this.indexToToFillInBlocks = (HashMap<Integer, IFactType>) indexToToFillInBlocks.clone();
        this.toFillInBlocksToIndex = (HashMap<IFactType, Integer>) toFillInBlocksToIndex.clone();        
    }

    public HashMap<Integer, IFactType> getIndexToToFillInBlocks() {
        return indexToToFillInBlocks;
    }

    public void setIndexToToFillInBlocks(HashMap<Integer, IFactType> indexToToFillInBlocks) {
        this.indexToToFillInBlocks = indexToToFillInBlocks;
    }

    public HashMap<IFactType, Integer> getToFillInBlocksToIndex() {
        return toFillInBlocksToIndex;
    }

    public void setToFillInBlocksToIndex(HashMap<IFactType, Integer> toFillInBlocksToIndex) {
        this.toFillInBlocksToIndex = toFillInBlocksToIndex;
    }
    
    
}
