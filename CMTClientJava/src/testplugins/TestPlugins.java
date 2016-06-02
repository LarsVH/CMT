package testplugins;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import be.ac.vub.wise.cmtclient.blocks.CMTField;
import be.ac.vub.wise.cmtclient.blocks.Fact;
import be.ac.vub.wise.cmtclient.blocks.FactType;
import be.ac.vub.wise.cmtclient.core.CMTClient;
import be.ac.vub.wise.cmtclient.util.Constants;
import be.ac.vub.wise.cmtclient.util.ConverterCoreBlocks;
import java.util.ArrayList;
import java.util.Scanner;
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

        Scanner sc = new Scanner(System.in);
        while (sc.hasNext()) {
            String in = sc.next();
            if (in.equals("s")) {
                registerString();
            } else if (in.equals("l")) {
                Location loc2 = new Location("living");
                CMTClient.shortcutRegisterFacttypeInCMT(loc2.getClass(), "room", "Code");
            }
            else if(in.equals("lf")){
                Fact fact = new Fact("Location", "room");
                FactType type = CMTClient.getFactTypeFactWithName("Location");
                ArrayList<CMTField> fields = new ArrayList<>();
                for (CMTField fi : type.getFields()) {
                    System.out.println("--------- id " + fi.getSql_id());
                    if (fi.getName().equals("room")) {
                        fi.setValue("Living"); // getFact!
                        fields.add(fi);
                    }
                }
                fact.setFields(fields);
                CMTClient.addFactInCMT(fact);
                
            } else if (in.equals("p")) {
                CMTClient.shortcutRegisterFacttypeInCMT(Person.class, "name", "Code");
            }
        };
        // Fact loc2f = ConverterCoreBlocks.fromObjectToFactInstance(loc2);
        // CMTClient.addFactInCMT(loc2f);
        //System.exit(0);
    }

    private static void registerString() {
        ArrayList<CMTField> fieldsString = new ArrayList<>();
        CMTField f = new CMTField("value", "java.lang.String");
        fieldsString.add(f);
        FactType string = new FactType("java.lang.String", "fact", "value", fieldsString);
        string.setCategory("Code");
        CMTClient.registerFacttypeInCMT(string);
    }

}
