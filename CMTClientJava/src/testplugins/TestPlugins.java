package testplugins;



/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import be.ac.vub.wise.cmtclient.blocks.ActionClient;
import be.ac.vub.wise.cmtclient.blocks.ActionField;
import be.ac.vub.wise.cmtclient.core.CMTClient;
import be.ac.vub.wise.cmtclient.util.Constants;
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
        
         Constants.setURLCMT("http://localhost:8080/CMTServerRestFull/cmt");
         Constants.setWSCMT("ws://localhost:8080/CMTServerRestFull/pubsub");
////       String json = "{\"type\": \"Person\", \"object\":";
       // Lis lis = new Lis();
       // CMTClient.addListener(lis);
      // CMTClient.startWS();
     /*
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

   
    //CisaMeeting me = new CisaMeeting();
   //     me.setDay("today");
  //  CMTClient.addEventOnServer(me);
      /*   ArrayList<String> days = new ArrayList<>();
        days.add("today");
        days.add("tomorrow");
        CMTClient.shortcutRegisterEventInCMT(me, true, true, "day", days, "");
         /*  
    
    ArrayList<String> days = new ArrayList<String>();
    days.add("Monday");
		days.add("Tuesday");
		days.add("Wednesday");
		days.add("Thursday");
		days.add("Friday");
		days.add("Saturday");
		days.add("Sunday");
                
    Day day = new Day();
           CMTClient.shortcutRegisterEventInCMT(day, false, false, "day", days, "");
    
    *
    ArrayList<ActionField> fields = new ArrayList<ActionField>();
    ActionField f = new ActionField("folder", new ArrayList<String>(), "path" );
    fields.add(f);
    ActionClient act = new ActionClient("OpenFolder", fields);  
    CMTClient.addActionInCMT(act);
   */ 
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
    
    
    }
    
    
    
}
