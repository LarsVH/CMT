/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ac.vub.wise.cmtgui.dialogs;

import be.ac.vub.wise.cmtclient.blocks.ActionClient;
import be.ac.vub.wise.cmtclient.blocks.ActionField;
import be.ac.vub.wise.cmtclient.blocks.CMTField;
import be.ac.vub.wise.cmtclient.blocks.EventInput;
import be.ac.vub.wise.cmtclient.blocks.FieldValueLimitation;
import be.ac.vub.wise.cmtgui.util.Property;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.StageStyle;
import org.apache.commons.lang3.math.NumberUtils;

/**
 *
 * @author Sandra
 */
public class FillInActionFields extends Dialog<ResultFieldsActions>{
    
    private ButtonType ok = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
    private ButtonType cancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
    ActionClient input;
    GridPane grid;
    
    
    public FillInActionFields(ActionClient input){
        this.input = input;
        drawDialog();
        setResultConverter(button->{
            if(button.getButtonData() == ButtonBar.ButtonData.OK_DONE){
                HashMap<String,String> res = new HashMap<>();
                HashMap<Integer,Property> vars = new HashMap<>();
                for(Node node :grid.getChildren()){
                    int rowIndex = GridPane.getRowIndex(node);
                    int colIndex = GridPane.getColumnIndex(node);
                    
                    if(rowIndex > 0 && colIndex==0){
                        String fieldName = "";
                        if(node instanceof Label){
                            fieldName = ((Label)node).getText();
                        }
                        if(vars.containsKey(rowIndex)){
                            vars.get(rowIndex).setKey(fieldName);
                        }else{
                            Property prop = new Property();
                            prop.setKey(fieldName);
                            vars.put(rowIndex, prop);
                        }
                    }else{
                        if(rowIndex >0 && colIndex ==1){
                            String value = "";
                            if(node instanceof TextField){
                                value = ((TextField)node).getText();
                            }else{
                                if(node instanceof ComboBox){
                                    value = (String)((ComboBox)node).getSelectionModel().getSelectedItem();
                                }
                            }
                            if(value.equals("")){
                                return null;
                            }else{
                                if(vars.containsKey(rowIndex)){
                                    vars.get(rowIndex).setValue(value);
                                }else{
                                    Property prop = new Property();
                                    prop.setValue(value);
                                    vars.put(rowIndex, prop);
                                }
                            }
                        }
                    }
                }
                for(Property p : vars.values()){
                    res.put(p.getKey(), p.getValue());
                }
                
                return new ResultFieldsActions(res);
            }
            return new ResultFieldsActions(null);
        });
    }
    
    public void drawDialog(){
        grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(0, 10, 0, 10));
        setHeaderText("Please fill in the values:");
        initStyle(StageStyle.DECORATED);
 // add operators!
        ArrayList<ActionField> fields = input.getFields();
        for(int i=0;i<fields.size();i++){
            ActionField field = fields.get(i);
            
                // make label + textfield or combobox
                Label lb_fieldName = new Label();
                lb_fieldName.setText(field.getName());
                grid.add(lb_fieldName, 0, i+1);
                // operators
                
                
                        
                
                if(!field.getFormat().equals("")){
                    String text = "";
                    TextField tf = new TextField();
                    if(field.getValue()== null || field.getValue().equals("")){
                        text = field.getFormat();
                        tf.setPromptText(text);
                    }else{
                        tf.setText(field.getValue());
                    }
                    tf.getStyleClass().add("tf_name");
                    grid.add(tf, 1, i+1);
                    
                }else{
                    if(!field.getVarList().isEmpty()){
                        ComboBox<String> box = new ComboBox<>();
                        ObservableList<String> list = FXCollections.observableArrayList();
                        list.addAll(field.getVarList());
                        box.setItems(list);
                         if(!field.getValue().equals("")){
                             box.getSelectionModel().selectFirst();
                         }else{
                             box.getSelectionModel().select(field.getValue());
                         }
                        grid.add(box, 1, i+1);
                        
                    
                    }
                }
                
               
            
        }
        getDialogPane().getButtonTypes().addAll(ok, cancel);
        getDialogPane().setContent(grid);
    }
    
    public ActionClient getActionClient(){
        return input;
    }
    
}
