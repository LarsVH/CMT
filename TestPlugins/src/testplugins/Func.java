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
    
    @Parameters(parameters = "room person")
    public static boolean PersonInLocation(Location room, Person person){
		return true;
	}
}
