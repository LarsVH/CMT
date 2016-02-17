/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ac.vub.wise.cmtgui.dialogs;

import be.ac.vub.wise.cmtclient.blocks.Fact;
import be.ac.vub.wise.cmtclient.blocks.FactType;
import be.ac.vub.wise.cmtclient.core.CMTClient;
import java.util.HashSet;
import java.util.Iterator;
import javafx.geometry.Insets;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.GridPane;

/**
 *
 * @author Sandra
 */
public class BrowersDialog extends Dialog<Fact>{
    private ButtonType ok = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
    private ButtonType cancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
    
    final ToggleGroup group = new ToggleGroup();
    
    public BrowersDialog(String factClassName){
        setTitle("Choose a " + factClassName);
        getDialogPane().getButtonTypes().addAll(ok, cancel);
        getDialogPane().setMaxHeight(250);
        GridPane grid = new GridPane();
grid.setHgap(10);
grid.setVgap(10);
grid.setPadding(new Insets(20, 150, 10, 10));
    ScrollPane sc = new ScrollPane(grid);
    sc.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        HashSet<Fact> facts = null;
        if(factClassName.equals("all")){
            facts = CMTClient.getAvailableFacts();
        }else{
            FactType type = CMTClient.getFactType(factClassName);
            facts = CMTClient.getFactsOfType(type);
        }
        Iterator<Fact> it = facts.iterator();
        for(int i=0;i<facts.size();i++){
            Fact fact = it.next();
            ToggleButtonFact butFact = new ToggleButtonFact();
//            butFact.setStyle("-fx-focus-color: white;");
//            butFact.setStyle("-fx-text-fill: white;");
            butFact.setText(fact.getUriValue());
            butFact.setFact(fact);
            butFact.setToggleGroup(group);
            if(i == 0){
                butFact.setSelected(true);
            }
            grid.add(butFact, 0, i+1);
        }
        
        getDialogPane().setContent(sc);
        getDialogPane().setStyle("-fx-background-color: #0097A7;");
        setResultConverter(button->{
            if(button.getButtonData() == ButtonBar.ButtonData.OK_DONE){
            System.out.println("-- toggle "+ group.getSelectedToggle());
            if(group.getSelectedToggle()!=null){
           Fact factRet= ((ToggleButtonFact)group.getSelectedToggle()).getFact();
            
           return factRet;
            }} return null;
            
        });
    }
    
}
