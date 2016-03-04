/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ac.vub.wise.cmtserver.util;

/**
 *
 * @author Sandra
 */
public class Constants {
    public static final String URLWS = "http://localhost:8080/CMTServerRestFull/pubsub"; // CHANGE PORT TO YOUR GLASSFISH INSTALLATION
    private static final String PATHCMTPROJECT = "/Users/Tim/Documents/Git_Projects/CMT/"; // CHANGE TO YOUR PROJECT DIRECTORY!!!
    
    public static final String CLASSPATH = PATHCMTPROJECT + "CMTServerRestFull/target/classes"; // path to target/classes folder
    public static final String ROOTFOLDERFORJAR = PATHCMTPROJECT + "CMTServerRestFull/target"; // path to target/classes folder
    public static final String CLASSPATHDicFolder = PATHCMTPROJECT + "CMTServerRestFull/target/CMTServerRestFull-1.0-SNAPSHOT"; // path to target/classes folder
    public static final String JAVAFILEPATH = PATHCMTPROJECT + "CMTServerRestFull/src/main/java/"; // path to temp folder
    public static final String KIEJARPATH = PATHCMTPROJECT + "CMTServerRestFull/src/main/resources/lib/kie-api-6.0.1.Final.jar"; // path to kie folder
    public static final String COMMONSPATH = PATHCMTPROJECT + "CMTServerRestFull/src/main/resources/lib/commons-lang3-3.4.jar"; // path to commons-lang3 folder
    
    public static final String PACKAGEBLOCKS = "be.ac.vub.wise.cmtserver.blocks."; // package name of rmi classes
    public static final String PACKAGEFACTS = "be.ac.vub.wise.cmtserver.facts"; // package name of runtime compiled facts
    public static final String PACKAGEEVENTS = "be.ac.vub.wise.cmtserver.events"; // package name of runtime compiled events
    public static final String PACKAGEFUNCTIONS = "be.ac.vub.wise.cmtserver.functions"; // package name of runtime compiled functionclasses
    public static final String PACKAGEACTIONS = "be.ac.vub.wise.cmtserver.actions"; // package name of runtime compiled actionclasses

    public static final String PACKAGEBLOCKSSLASH = "be/ac/vub/wise/cmtserver/blocks"; // package name of rmi classes
    public static final String PACKAGEFACTSSLASH = "be/ac/vub/wise/cmtserver/facts"; // package name of runtime compiled facts
    public static final String PACKAGEEVENTSSLASH = "be/ac/vub/wise/cmtserver/events"; // package name of runtime compiled events
    public static final String PACKAGEFUNCTIONSSLASH = "be/ac/vub/wise/cmtserver/functions"; // package name of runtime compiled functionclasses
    public static final String PACKAGEACTIONSSLASH = "be/ac/vub/wise/cmtserver/actions"; // package name of runtime compiled actionclasses
    
    public static final String FACT = "fact"; 
    public static final String EVENT = "event"; 
    public static final String ACTION = "action";
    public static final String FUNCTION = "function";
    public static final String EXTENDSACTIVITY = "be.ac.vub.wise.cmtserver.blocks.Activity";
    public static final String EXTENDSTIME = "be.ac.vub.wise.cmtserver.blocks.Time";
    public static final String TIME = "time";
    public static final String ACTIVITY = "activity";
    
    public static final String PATHDRL = PATHCMTPROJECT + "CMTServerRestFull/src/main/resources/rules/rules.drl";
}