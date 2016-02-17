package be.ac.vub.wise.cmtgui.dialogs;

import be.ac.vub.wise.cmtclient.blocks.ActionClient;
import be.ac.vub.wise.cmtclient.blocks.CMTField;
import be.ac.vub.wise.cmtclient.blocks.Event;
import be.ac.vub.wise.cmtclient.blocks.Fact;
import be.ac.vub.wise.cmtclient.blocks.FactType;
import be.ac.vub.wise.cmtclient.blocks.Function;
import be.ac.vub.wise.cmtclient.blocks.IFactType;
import be.ac.vub.wise.cmtclient.blocks.Rule;
import be.ac.vub.wise.cmtclient.blocks.TemplateActions;
import be.ac.vub.wise.cmtclient.blocks.TemplateHA;
import be.ac.vub.wise.cmtclient.core.CMTClient;
import be.ac.vub.wise.cmtclient.core.CMTListener;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.StringConverter;



import java.io.IOException;
import java.lang.reflect.Field;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashSet;


public class FactDialog  {

	 @FXML
	 private ListView<FactType> list;
	 @FXML
	 private VBox attributeBox;
	 @FXML private Button bt_create;
	 @FXML private Button bt_cancel;
	 
	 private   ObservableList<FactType> items;
	
    public FactDialog(String focus){
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/org/contextbox/gui/fxml/newFactDialog.fxml"));
            fxmlLoader.setController(this);	
            Parent root = fxmlLoader.load();
            Stage stage = new Stage();
            stage.setTitle("New Object");
            Scene scene = new Scene(root, 450, 450);
            scene.getStylesheets().add("/org/contextbox/gui/fxml/styles.css");
            stage.setScene(scene);
            stage.show();
	    HashSet<FactType> types = CMTClient.getAvailableFactTypes();
            items = FXCollections.observableArrayList();
	    for(FactType type : types){
		items.add(type);
            }
            list.setItems(items);
            list.setCellFactory(new Callback<ListView<FactType>, ListCell<FactType>>(){
		        	 
		            @Override
		            public ListCell<FactType> call(ListView<FactType> p) {
		                 
		                ListCell<FactType> cell = new ListCell<FactType>(){
		 
		                    @Override
		                    protected void updateItem(FactType t, boolean bln) {
		                        super.updateItem(t, bln);
		                        if (t != null) {
		                            setText(t.getClassName());
		                        }
		                    }
		 
		                };
		                 
		                return cell;
		            }
            });
		        
	    list.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<FactType>() {
		public void changed(ObservableValue<? extends FactType> observable, FactType oldValue, FactType newValue) {
		    FactType selected = (FactType) list.getSelectionModel().getSelectedItem();
		    // add them to VBox
                    attributeBox.getChildren().clear();
                    ArrayList<CMTField> fields = selected.getFields();
                    for(CMTField field : fields){
			Pane vspaceBetweenFields = new Pane();
			vspaceBetweenFields.setMinHeight(7);
			HBox hbox = new HBox();     
                        Label label = new Label(field.getName() + ":");
                        label.getStyleClass().add("labNewFact");	        
                        label.setTextAlignment(TextAlignment.CENTER);
			Pane space1 = new Pane();
			space1.setMinWidth(10);
                        
			if(field.getType().equals("String") || field.getType().equals("Integer")){
                            TextField textField = new TextField();
			    hbox.getChildren().add(label);
			    hbox.getChildren().add(space1);
			    hbox.getChildren().add(textField);
                        }else{     	
                            ObservableList<Fact> list = FXCollections.observableArrayList();
                            FactType fieldType = CMTClient.getFactType(field.getType());
                            HashSet<Fact> facts = CMTClient.getFactsOfType(fieldType);
                            for(Fact fa:facts){
                                list.add(fa);
                            }
                            ComboBox<Fact> combo = new ComboBox<Fact>(list);
			    combo.setConverter(new StringConverter<Fact>() {
					            @Override
					            public String toString(Fact object) {
					              if (object == null){
					                return "empty";
					              } else {
                                                          String uriField = object.getUriField();
                                                          ArrayList<CMTField> fieldsFact = object.getFields();
                                                          for(CMTField f: fieldsFact){
                                                              if(f.getName().equals(uriField)){
                                                                  return f.getValue().toString();
                                                              }
                                                          }
					              }
					              return null;
					            }

					            @Override
					            public Fact fromString(String s) {
					          	
                                                            return  CMTClient.getFact(field.getType(), fieldType.getUriField(), s);
					            }
                            });
			    hbox.getChildren().add(label);
			    hbox.getChildren().add(space1);
                            hbox.getChildren().add(combo);
                            Pane space2 = new Pane();
			    space2.setMinWidth(10);
			    Button plus = new Button();
			    plus.getStyleClass().add("buttonPlus");
			    plus.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
                                    new FactDialog(null);
				}
                            });
                            hbox.getChildren().add(space2);
			    hbox.getChildren().add(plus);
                        }	       
			attributeBox.getChildren().add(vspaceBetweenFields);
			attributeBox.getChildren().add(hbox);
                    }			        
		}
            });
            
            if(focus != null){
	       	for(FactType ty : items){
                    if(ty.getClassName().equals(focus)){
		        list.getSelectionModel().select(ty);
                    }
		}
            }else{
		list.getSelectionModel().selectFirst();
            }
            
            bt_create.setOnAction(new EventHandler<ActionEvent>() {
                @Override
		public void handle(ActionEvent event) {
                    FactType selectedType = list.getSelectionModel().getSelectedItem();
                    Fact newFact = new Fact(selectedType.getClassName(), selectedType.getUriField());
                    ArrayList<CMTField> factFields = new ArrayList<CMTField>();
					
						
						ObservableList<Node> nodes = attributeBox.getChildren();
						System.out.println(nodes);
						for(Node node : nodes){
							if(node.getTypeSelector().equals("HBox")){
								HBox hboxToSave = (HBox) node;
								ObservableList<Node> elements = hboxToSave.getChildren();
								Label lab = null;
								TextField tfield = null;
                                                                Fact comboFact = null;
								for(Node el : elements){
									if(el.getTypeSelector().equals("Label")){
										lab = (Label) el;
										System.out.println(lab.getText());
										
									}else{
										if(el.getTypeSelector().equals("TextField")){
											tfield = (TextField) el;
											
											System.out.println(tfield.getText());
											
										}else{
                                                                                    if(el.getTypeSelector().equals("ComboBox")){
                                                                                        comboFact = (Fact)((ComboBox)el).getSelectionModel().getSelectedItem();
                                                                                    }
                                                                                }
									}	
								}
                                                                String cmtFieldType = "";
                                                                ArrayList<CMTField> fieldsType = selectedType.getFields();
                                                                for(CMTField f: fieldsType){
                                                                    if(f.getName().equals(lab.getText())){
                                                                        cmtFieldType = CMTClient.getSimpleTypeName(f.getType());
                                                                    }
                                                                }
                                                                
                                                                CMTField field = new CMTField(lab.getText(),cmtFieldType);
                                                                if(tfield != null){
                                                                    field.setValue(tfield.getText());
                                                                }else{
                                                                    if(comboFact != null){
                                                                        field.setValue(comboFact);
                                                                    }
                                                                }
								factFields.add(field);
							}
							
						}
						newFact.setFields(factFields);
                                                CMTClient.addFactInCMT(newFact);
						
						stage.close();
						
					}
				});
		        
		        bt_cancel.setOnAction(new EventHandler<ActionEvent>() {

					@Override
					public void handle(ActionEvent event) {
						stage.close();
						
					}
				});

		        } catch (IOException e) {
		            e.printStackTrace();
		        }
	}

	

   
}
