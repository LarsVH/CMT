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
public class Bed {
    
    public String label;
    public Location room;
    public PressureSensor sensor;
    
    public Bed(String label){
        this.label = label ;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Location getRoom() {
        return room;
    }

    public void setRoom(Location room) {
        this.room = room;
    }

    public PressureSensor getSensor() {
        return sensor;
    }

    public void setSensor(PressureSensor sensor) {
        this.sensor = sensor;
    }
    
    
    
    
    
    
}
