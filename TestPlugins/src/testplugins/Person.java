/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package testplugins;

/**
 *
 * @author lars
 */
public class Person {
    
    public String name;
    public Location room;

    public Person(String name, Location room) {
        this.name = name;
        this.room = room;
        
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Location getRoom() {
        return room;
    }

    public void setRoom(Location room) {
        this.room = room;
    }
   

    
    
}
