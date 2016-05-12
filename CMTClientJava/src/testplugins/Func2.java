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
public class Func2 {
    @Parameters(parameters = "room")            // Reflection probleem in Java: naam van PMs zijn weg na compilatie
    public static boolean InBed(Location room){
        return true;
    }
}

