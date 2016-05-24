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
public class Func2 {
     @Parameters(parameters = "room")
    public static boolean InBed(Location room){
		return true;
	}
    
    @Parameters(parameters = "room")
    public static boolean noMovement(Location room){
		return true;
	}
    

    
}
