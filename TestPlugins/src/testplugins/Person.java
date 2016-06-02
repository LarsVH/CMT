/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package testplugins;

/**
 *
 * @author Sandra
 */
public class Person {
    public String name;
    public Loc2 loc;
    
    public Person(String name, Loc2 loc){
        this.name = name;
        this.loc = loc;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Loc2 getLoc() {
        return loc;
    }

    public void setLoc(Loc2 loc) {
        this.loc = loc;
    }
    
    
}
