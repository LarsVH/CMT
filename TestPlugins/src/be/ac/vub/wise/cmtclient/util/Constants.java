/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ac.vub.wise.cmtclient.util;

/**
 *
 * @author Sandra
 */
public class Constants {
    
    public static final String FACT = "fact";
    public static final String TIME = "time";
    public static final String ACTIVITY = "activity";
    public static final String FUNCTION = "function";

    public static final String EXTENDSACTIVITY = "be.ac.vub.wise.cmtserver.blocks.Activity";
    public static final String EXTENDSTIME = "be.ac.vub.wise.cmtserver.blocks.Time";

    public static final String URLCMT = "http://localhost:18401/CMTServerRestFull/cmt";
    public static final String WSCMT = "ws://localhost:18401/CMTServerRestFull/pubsub";
    
    public static final String PACKAGEFACTS = "be.ac.vub.wise.cmtserver.facts"; // package name of runtime compiled facts
    public static final String PACKAGEEVENTS = "be.ac.vub.wise.cmtserver.events"; // package name of runtime compiled events
    public static final String PACKAGEFUNCTIONS = "be.ac.vub.wise.cmtserver.functions"; // package name of runtime compiled functionclasses
    public static final String PACKAGEACTIONS = "be.ac.vub.wise.cmtserver.actions"; // package name of runtime compiled actionclasses
    
}
