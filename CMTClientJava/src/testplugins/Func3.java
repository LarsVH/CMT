/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package testplugins;

import be.ac.vub.wise.cmtclient.blocks.Parameters;



/**
 *
 * @author lars
 */
public class Func3 {
    @Parameters(parameters = "room person")     // Reflection probleem in Java: naam van PMs zijn weg na compilatie
    public static boolean PersonInLocation(Location room, Person person){
        return true;
    }
    
    @Parameters(parameters = "person1 person2")
    public static boolean SamePerson(Person person1, Person person2){
        return (person1.name).equals(person2.name);
    }
}
