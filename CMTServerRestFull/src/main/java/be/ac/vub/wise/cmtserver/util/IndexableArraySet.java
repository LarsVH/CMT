/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ac.vub.wise.cmtserver.util;

import java.util.ArrayList;
import java.util.HashSet;

/**
 *
 * @author lars
 * @param <E>
 */
public class IndexableArraySet<E> extends ArrayList<E> {
    HashSet<E> duplCheck = new HashSet<>();
    HashSet<E> procCheck = new HashSet<>();
    
    public void markProcessed(E element) throws Exception{
        if(!duplCheck.contains(element))
            throw new Exception("Element not yet in IndexableArraySet");
        else 
            procCheck.add(element);        
    }    
    public void unmarkProcessed(E element) throws Exception {
        if(!(duplCheck.contains(element)||(procCheck.contains(element))))
            throw new Exception("Element not marked as processed or not in IndexableArraySet");
    }
    public boolean isProcessed(E element) throws Exception {
        if(procCheck.contains(element))
            return true;
        else if(duplCheck.contains(element))
            return false;
        else
            throw new Exception("Element not in IndexableArraySet");
    }
    public ArrayList<E> getUnprocessed(){
        ArrayList<E> unProcessed = new ArrayList<>();
        super.forEach((el) -> {
            if(!procCheck.contains(el))
                unProcessed.add(el);
        });        
        return unProcessed;
    }
    
    
    @Override
    public boolean add(E element) {
        boolean ret = false;
        if (!duplCheck.contains(element)) {
            ret = super.add(element);
            duplCheck.add(element);
        }
        return ret;
    }

    @Override
    public void add(int index, E element) {
        if (!duplCheck.contains(element)) {
            super.add(index, element);
            duplCheck.add(element);
        }
    }

    @Override
    public E remove(int index) {
        E removed = super.remove(index);
        duplCheck.remove(removed);
        return removed;
    }

    @Override
    public boolean remove(Object o) {
        duplCheck.remove(o);
        return super.remove(o);
    }

    @Override
    public void clear() {
        duplCheck.clear();
        super.clear();
    }
}

    
    
    
    
    /*public IndexableArraySet(int initialCapacity) {
        list = new ArrayList<>(initialCapacity);
    }
    public IndexableArraySet(){
        list = new ArrayList<>();
    }
    
    public int size(){
        return list.size();
    }
    public boolean isEmpty(){
        return list.isEmpty();
    }
    public booolean contains*/
    


