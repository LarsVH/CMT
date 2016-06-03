/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ac.vub.wise.cmtgui.dialogs;

import be.ac.vub.wise.cmtclient.blocks.CMTField;
import be.ac.vub.wise.cmtclient.blocks.EventInput;
import be.ac.vub.wise.cmtclient.blocks.FieldValueLimitation;
import be.ac.vub.wise.cmtgui.util.Property;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.stage.StageStyle;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.math.NumberUtils;

/**
 *
 * @author Sandra
 */
public class FillInFields extends Dialog<ResultFields>{
    
    private ButtonType ok = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
    private ButtonType cancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
    EventInput input;
    GridPane grid;
    
    
    public FillInFields(EventInput input){
        this.input = input;
        drawDialog();
        setResultConverter(button->{
            if(button.getButtonData() == ButtonBar.ButtonData.OK_DONE){
                HashSet<FieldValueLimitation> res = new HashSet<>();
                HashMap<Integer,FieldValueLimitation> vars = new HashMap<>();
                for(Node node :grid.getChildren()){
                    int rowIndex = GridPane.getRowIndex(node);
                    int colIndex = GridPane.getColumnIndex(node);
                    
                    if(rowIndex > 0 && colIndex==0){
                        String fieldName = "";
                        if(node instanceof Label){
                            fieldName = ((Label)node).getText();
                        }
                        if(vars.containsKey(rowIndex)){
                            vars.get(rowIndex).setFieldName(fieldName);
                        }else{
                            FieldValueLimitation prop = new FieldValueLimitation();
                            prop.setFieldName(fieldName);
                            vars.put(rowIndex, prop);
                        }
                    }else{
                        if(rowIndex >0 && colIndex ==2){
                            String value = "";
                            if(node instanceof TextField){
                                value = StringEscapeUtils.escapeJava(((TextField)node).getText());
                                
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
                                    FieldValueLimitation prop = new FieldValueLimitation();
                                    prop.setValue(value);
                                    vars.put(rowIndex, prop);
                                }
                            }
                        }else{
                            if(rowIndex >0 && colIndex == 1){
                                String op = "";
                                if(node instanceof ComboBox){
                                    op = (String)((ComboBox)node).getSelectionModel().getSelectedItem();
                                }
                                if(vars.containsKey(rowIndex)){
                                vars.get(rowIndex).setOperator(op);
                            }else{
                                FieldValueLimitation prop = new FieldValueLimitation();
                                prop.setOperator(op);
                                vars.put(rowIndex, prop);
                            }
                            }
                        }
                    }
                }
                res.addAll(vars.values());
                return new ResultFields(res);
            }
            return new ResultFields(null);
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
        ArrayList<CMTField> fields = input.getFields();
        for(int i=0;i<fields.size();i++){
            CMTField field = fields.get(i);
            if(field.isIsVar()){
                // make label + textfield or combobox
                Label lb_fieldName = new Label();
                lb_fieldName.setText(field.getName());
                grid.add(lb_fieldName, 0, i+1);
                // operators
                ComboBox<String> boxop = new ComboBox<>();
                boxop.setOnMousePressed(new EventHandler<MouseEvent>(){
                            @Override
                            public void handle(MouseEvent event) {
                                boxop.requestFocus();
                                
                            }
                        });
                ObservableList<String> listop = FXCollections.observableArrayList();
                
                        System.out.println("--------- field format " + field.getFormat());
                
                if(!field.getFormat().equals("")){
                    System.out.println("---------- in op format");
                    listop.add("==");
                    listop.add("!=");
                    if(NumberUtils.isNumber(field.getFormat())){
                        listop.add(">");
                        listop.add("<");
                    }
                    boxop.setItems(listop);
                    String text = "";
                    TextField tf = new TextField();
                    System.out.println("-- " + input.getLimitations().size());
                    // new way add field value if data is set!
                    if(input.getFieldValueLimitation(field.getName()) != null){
                        
                        text = input.getFieldValueLimitation(field.getName()).getValue();
                        System.out.println("-- ok in lim format value = " + text);
                        tf.setText(text);
                        boxop.getSelectionModel().select(input.getFieldValueLimitation(field.getName()).getOperator());
                        
                    }else{
                        text = field.getFormat();
                        tf.setPromptText(text);
                         boxop.getSelectionModel().selectFirst();
                    }
//                    if(input.getFieldValueLimitation(field.getName()).getValue().equals("")){
//                        text = field.getFormat();
//                        tf.setPromptText(text);
//                    }else{
//                        text = input.getFieldValueLimitation(field.getName()).getValue();
//                        tf.setText(text);
//                    }
//                    
                    
                    tf.getStyleClass().add("tf_name");
                    grid.add(tf, 2, i+1);
                    
//                    if(input.getFieldValueLimitation(field.getName()).getOperator().equals("")){
//                        boxop.getSelectionModel().selectFirst();
//                    }else{
//                        boxop.getSelectionModel().select(input.getFieldValueLimitation(field.getName()).getOperator());
//                    }
//                    
                    
                }else{
                    if(!field.getOptions().isEmpty()){
                        ComboBox<String> box = new ComboBox<>();
                        box.setOnMousePressed(new EventHandler<MouseEvent>(){
                            @Override
                            public void handle(MouseEvent event) {
                                box.requestFocus();
                                
                            }
                        });
                        ObservableList<String> list = FXCollections.observableArrayList();
                        list.addAll(field.getOptions());
                        box.setItems(list);
                         listop.add("==");
                        listop.add("!=");
                        boxop.setItems(listop);
                        if(input.getFieldValueLimitation(field.getName())!=null){
                            box.getSelectionModel().select(input.getFieldValueLimitation(field.getName()).getValue());
                            boxop.getSelectionModel().select(input.getFieldValueLimitation(field.getName()).getOperator());
                        }else{
                            box.getSelectionModel().selectFirst();
                            boxop.getSelectionModel().selectFirst();
                        }
                        
//                        if(input.getFieldValueLimitation(field.getName()).getValue().equals("")){
//                             box.getSelectionModel().selectFirst();
//                        }else{
//                            box.getSelectionModel().select(input.getFieldValueLimitation(field.getName()).getValue());
//                        }
                        grid.add(box, 2, i+1);
//                        listop.add("==");
//                        listop.add("!=");
//                        boxop.setItems(listop);
//                        if(input.getFieldValueLimitation(field.getName()).getOperator().equals("")){
//                        boxop.getSelectionModel().selectFirst();
//                    }else{
//                        boxop.getSelectionModel().select(input.getFieldValueLimitation(field.getName()).getOperator());
//                    }
                    }
                }
                
                grid.add(boxop, 1, i+1);
            }else{
                Label lb_fieldName = new Label();
                lb_fieldName.setText(field.getName());
                grid.add(lb_fieldName, 0, i+1);
                Label mes = new Label();
                mes.setText("Cannot be changed");
                grid.add(mes, 1, i+1);
            }
        }
        getDialogPane().getButtonTypes().addAll(ok, cancel);
        getDialogPane().setContent(grid);
    }
    
    public EventInput getEventInput(){
        return input;
    }
    
   
}
