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
public class Phone {
    public String id;
    public Person owner;
    public Location location;
    
    public Phone(String id, Person owner, Location location){
        this.id = id;
        this.owner = owner;
        this.location = location;
    }

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    public Person getOwner() {
        return owner;
    }
    public void setOwner(Person owner) {
        this.owner = owner;
    }

    public Location getLocation() {
        return location;
    }
    public void setLocation(Location location) {
        this.location = location;
    }  
    
}
