/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package testplugins;

import be.ac.vub.wise.cmtclient.blocks.Parameters;

/**
 *
 * @author Sandra
 */
public class Func {
    
    @Parameters(parameters = "person1 location")
    public static boolean personsInLocation(Person person1, Location location){
		return true;
	}
    @Parameters(parameters = "person1 person2")
    public static boolean samePerson(Person person1, Person person2){
        return true;
    }
}
