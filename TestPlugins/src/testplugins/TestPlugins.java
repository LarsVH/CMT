/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package testplugins;

import be.ac.vub.wise.cmtclient.blocks.ActionClient;
import be.ac.vub.wise.cmtclient.blocks.ActionField;
import be.ac.vub.wise.cmtclient.blocks.CMTField;
import be.ac.vub.wise.cmtclient.blocks.Fact;
import be.ac.vub.wise.cmtclient.blocks.FactType;
import be.ac.vub.wise.cmtclient.core.CMTClient;
import java.util.ArrayList;
import javax.swing.JFrame;

/**
 *
 * @author Sandra
 */
public class TestPlugins {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        JFrame frame = new JFrame();
        frame.setSize(200, 200);
        frame.setVisible(true);
////       String json = "{\"type\": \"Person\", \"object\":";
        Lis lis = new Lis();
        CMTClient.addListener(lis);
       CMTClient.startWS();
       
   //    CMTClient.shortcutAddFunctionInCMT(Func.class);
      // CMTClient.shortcutAddFunctionInCMT(Func.class);
      
      //------------------------------------------------------------------------
      
       ArrayList<CMTField> fieldsString = new ArrayList<>();
       CMTField f = new CMTField("value", "java.lang.String");
       fieldsString.add(f);
       FactType string = new FactType("java.lang.String", "fact", "value", fieldsString);
       string.setCategory("Code");
       CMTClient.registerFacttypeInCMT(string);
          
       //-----------------------------------------------------------------------
   
       //Loc2 loc2 = new Loc2("living");
       //CMTClient.shortcutRegisterFacttypeInCMT(loc2.getClass(), "room", "Code");
       
       //-----------------------------------------------------------------------
     
       /*  Fact fact = new Fact("Loc2", "room");
        FactType type = CMTClient.getFactTypeFactWithName("Loc2");
        ArrayList<CMTField> fields = new ArrayList<>();
        for(CMTField fi : type.getFields()){
            System.out.println("--------- id " + fi.getSql_id());
            if(fi.getName().equals("room")){
                fi.setValue("Living"); // getFact!
                fields.add(fi);
            }
        }
        fact.setFields(fields);
       CMTClient.addFactInCMT(fact);
       */
       
       //-----------------------------------------------------------------------
       
     //  CMTClient.shortcutRegisterFacttypeInCMT(Person.class, "name", "Code");
       
       //-----------------------------------------------------------------------
       
      /*  Fact fact2 = new Fact("Person", "name");
        FactType type2 = CMTClient.getFactTypeFactWithName("Person");
        ArrayList<CMTField> fields2 = new ArrayList<>();
        for(CMTField fi : type2.getFields()){
            System.out.println("--------- id " + fi.getSql_id());
            if(fi.getName().equals("name")){
                fi.setValue("Sandra"); // getFact!
                fields2.add(fi);
            }
            if(fi.getName().equals("loc")){
                fi.setValue(CMTClient.getFact("Loc2", "room", "Living")); // getFact!
                fields2.add(fi);
            }
        }
        fact2.setFields(fields2);
       CMTClient.addFactInCMT(fact2); */
       //-----------------------------------------------------------------------
     
     /*
       Location loc2 = new Location("living");
       CMTClient.registerFactOnServer(loc2.getClass(), "room");
       Location loc = new Location("living");
 CMTClient.addFactOnServer(loc);      
// CMTClient.addFactOnServer(loc);
       
      // Location loc2 = new Location("vi");
       //CMTClient.addFactOnServer(loc2);
      
        Person per = new Person("ikke", "ok");
        per.setLoc(loc);
        CMTClient.registerFactOnServer(per.getClass(), "name");
        CMTClient.addFactOnServer(per);
       Person per2 = new Person("vince", "ok");
        per2.setLoc(loc);
        CMTClient.addFactOnServer(per2);
//        CisaMeeting me = new CisaMeeting();
//        me.setDay("today");
//    CMTClient.addEventOnServer(me);
         ArrayList<String> days = new ArrayList<>();
        days.add("today");
        days.add("tomorrow");
        CMTClient.registerEventOnServer(me, true, true, "day", days, "");
      
       // CMTClient.addEventOnServer(me);
  /*      Meeting me2 = new Meeting();
        me2.setDay("today");
        CMTClient.addEventOnServer(me);
*/
       //       ArrayList<String> days = new ArrayList<>();
//        days.add("today");
//        days.add("tomorrow");
//        CMTClient.registerEventOnServer(me, true, false, "day", days, "");
//      
//      // CMTClient.addEventOnServer(me);
//       System.out.println(CMTClient.getAvailableEventTypes().size());
      
        
//        System.out.println(CMTClient.getAvailableFunctions().size());
   /*
    String rule = " import be.ac.vub.wise.cmtserver.facts.Person   rule testRule5" 
            +" when "
            +" Person(name == \"ikke\")"
            + " then "
            +" System.out.println(\"------------- rule triggerd\");"
            + " end ";
    CMTClient.addRuleOnServer("testRule5", rule);
   */
    /*
   CMTClient.shortcutRegisterFacttypeInCMT(Location.class, "room");
 CMTClient.shortcutRegisterFacttypeInCMT(Person.class, "name");
//
    //CMTClient.shortcutRegisterEventInCMT(new TestActivity(), true, true, "", new ArrayList<String>(), "");
    Location liv = new Location("living");
    CMTClient.shortcutAddFactInCMT(liv);
    Person per1 = new Person("Sandra", "ok");
    per1.setLoc(liv);
    CMTClient.shortcutAddFactInCMT(per1);
    
  /*  Person per2 = new Person("ik2", "ok");
    per2.setLoc(liv);
    CMTClient.shortcutAddFactInCMT(per2);
    */
  // CMTClient.shortcutAddFunctionInCMT(Func2.class);

   /*
    CisaMeeting me = new CisaMeeting();
 //     me.setDay("today");
//  //  CMTClient.addEventOnServer(me);
         ArrayList<String> days = new ArrayList<>();
        days.add("today");
        days.add("tomorrow");
CMTClient.shortcutRegisterEventInCMT(me, true, true, "day", days, "", "Code");
       */
//        
//       // ItIsAfter i = new ItIsAfter();
//        CMTClient.shortcutRegisterEventInCMT(i, true, false, "hour", days, "00:00", "Code");
//         /*  
//    
//    ArrayList<String> days = new ArrayList<String>();
//    days.add("Monday");
//		days.add("Tuesday");
//		days.add("Wednesday");
//		days.add("Thursday");
//		days.add("Friday");
//		days.add("Saturday");
//		days.add("Sunday");
//                
//    Day day = new Day();
//           CMTClient.shortcutRegisterEventInCMT(day, false, false, "day", days, "", "Code");
//    
    
  /*  ArrayList<ActionField> fields = new ArrayList<ActionField>();
    ArrayList<String> status = new ArrayList<>();
    status.add("ON");
    status.add("OFF");
    ActionField f = new ActionField("status", status, "" );
    fields.add(f);
    ActionClient act = new ActionClient("NightLight", fields);  
 //   CMTClient.addActionInCMT(act);
   
    // use case Sleeping
       
    // Locations
        Location loc2 = new Location("My Bedroom");
        CMTClient.shortcutRegisterFacttypeInCMT(loc2.getClass(), "room");
        CMTClient.shortcutAddFactInCMT(loc2);
        Location loc = new Location("Living Room");
        CMTClient.shortcutAddFactInCMT(loc);
        Location loc3 = new Location("Bathroom");
        CMTClient.shortcutAddFactInCMT(loc3);
        Location loc4 = new Location("Kitchen");
        CMTClient.shortcutAddFactInCMT(loc4);
        Location loc5 = new Location("Vince Bedroom");
        CMTClient.shortcutAddFactInCMT(loc5);
    
    Person per1 = new Person("Sandra", loc4);
    CMTClient.shortcutRegisterFacttypeInCMT(per1.getClass(), "name");
    CMTClient.shortcutAddFactInCMT(per1);
    Person per2 = new Person("Vince", loc3);
    CMTClient.shortcutAddFactInCMT(per2);
    
    CMTClient.shortcutAddFunctionInCMT(Func2.class);
    */
    
  System.exit(0);
  
    }
    
    
    
}
