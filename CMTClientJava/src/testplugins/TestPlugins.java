package testplugins;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import be.ac.vub.wise.cmtclient.blocks.ActionClient;
import be.ac.vub.wise.cmtclient.blocks.ActionField;
import be.ac.vub.wise.cmtclient.blocks.CMTField;
import be.ac.vub.wise.cmtclient.blocks.FactType;
import be.ac.vub.wise.cmtclient.core.CMTClient;
import be.ac.vub.wise.cmtclient.util.Constants;
import be.ac.vub.wise.cmtclient.util.ConverterCoreBlocks;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
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
        ArrayList<CMTField> fieldsString = new ArrayList<>();
        CMTField f = new CMTField("value", "java.lang.String");
        fieldsString.add(f);
        FactType string = new FactType("java.lang.String", "fact", "value", fieldsString);
        string.setCategory("Code");
        CMTClient.registerFacttypeInCMT(string);
// use case Sleeping
// Locations

            Location loc2 = new Location("My Bedroom");
            CMTClient.shortcutRegisterFacttypeInCMT(loc2.getClass(), "room", "Home");
            
            //CMTClient.shortcutRegisterFacttypeInCMT(loc2.getClass(), "room");
            //CMTClient.shortcutAddFactInCMT(loc2);
          /*  Location loc = new Location("Living Room");
            CMTClient.shortcutAddFactInCMT(loc);
            Location loc3 = new Location("Bathroom");
            /*CMTClient.shortcutAddFactInCMT(loc3);
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
            CMTClient.shortcutAddFunctionInCMT(Func3.class);

            Phone ph1 = new Phone("SandraPhone", per1, loc2);
            CMTClient.shortcutRegisterFacttypeInCMT(ph1.getClass(), "id");
            CMTClient.shortcutAddFactInCMT(ph1);
            
            //System.exit(0);*/
    }

}
