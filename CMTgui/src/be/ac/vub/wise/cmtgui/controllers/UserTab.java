package be.ac.vub.wise.cmtgui.controllers;

import be.ac.vub.wise.cmtclient.blocks.ActionClient;
import be.ac.vub.wise.cmtclient.blocks.ActionField;
import be.ac.vub.wise.cmtclient.blocks.Binding;
import be.ac.vub.wise.cmtclient.blocks.BindingIF;
import be.ac.vub.wise.cmtclient.blocks.BindingInputFact;
import be.ac.vub.wise.cmtclient.blocks.BindingInputField;
import be.ac.vub.wise.cmtclient.blocks.BindingParameter;
import be.ac.vub.wise.cmtclient.blocks.CMTField;
import be.ac.vub.wise.cmtclient.blocks.EventInput;
import be.ac.vub.wise.cmtclient.blocks.Fact;
import be.ac.vub.wise.cmtclient.blocks.FactType;
import be.ac.vub.wise.cmtclient.blocks.FieldValueLimitation;
import be.ac.vub.wise.cmtclient.blocks.Function;
import be.ac.vub.wise.cmtclient.blocks.IFBlock;
import be.ac.vub.wise.cmtclient.blocks.IFactType;
import be.ac.vub.wise.cmtclient.blocks.Operator;
import be.ac.vub.wise.cmtclient.blocks.OutputHA;
import be.ac.vub.wise.cmtclient.blocks.Rule;
import be.ac.vub.wise.cmtclient.blocks.Template;
import be.ac.vub.wise.cmtclient.blocks.TemplateActions;
import be.ac.vub.wise.cmtclient.blocks.TemplateHA;
import be.ac.vub.wise.cmtclient.core.CMTClient;
import be.ac.vub.wise.cmtclient.core.CMTListener;
import be.ac.vub.wise.cmtclient.util.Constants;
import be.ac.vub.wise.cmtgui.dialogs.BrowersDialog;
import java.lang.reflect.Field;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.JPanel;


import be.ac.vub.wise.cmtgui.dialogs.FactDialog;
import be.ac.vub.wise.cmtgui.dialogs.FillInActionFields;
import be.ac.vub.wise.cmtgui.dialogs.FillInFields;
import be.ac.vub.wise.cmtgui.dialogs.ResultFields;
import be.ac.vub.wise.cmtgui.views.CmtLabel;
import be.ac.vub.wise.cmtgui.util.CmtHelper;
import be.ac.vub.wise.cmtgui.util.ConstantsGUI;
import be.ac.vub.wise.cmtgui.util.Property;
import be.ac.vub.wise.cmtgui.views.CmtToggleEU;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.util.Callback;

public class UserTab extends VBox implements CMTListener{
	
	@FXML private AnchorPane ap_workbench;
    @FXML public TreeView<Template> tv_templates;
    @FXML public ListView<FactType> lv_input_activities;
    @FXML public ListView<FactType> lv_input_time;
    @FXML public ListView<Fact> lv_input_persons;
    @FXML public ListView<Fact> lv_input_locations;
    @FXML public TreeView<IFactType> tv_input_objects;
    @FXML public ListView<FactType> lv_userActivities;
    @FXML public ListView<ActionClient> lv_actions;
    @FXML public ListView<Rule> lv_myRules;
  
   // ObservableList<Template> items_lv_templates;
    ObservableList<FactType> items_input_activities;
    ObservableList<FactType> items_input_time;
    ObservableList<Fact> items_input_persons;
    ObservableList<Fact> items_input_locations;
    ObservableList<FactType> items_lv_userActivities;
    ObservableList<ActionClient> items_lv_actions;
    ObservableList<Rule> items_lv_myRules;
    
    private TreeItem<IFactType> ti_root_objects;
    private TreeItem<Template> ti_root_temp;
 
    @FXML private Button Person;
    @FXML private Button Location;
    @FXML private Button object;
    @FXML public HBox hbHeader;
    @FXML public Button bt_save;
    @FXML public Button bt_cancel;

    final static DataFormat timeDataFormat = new DataFormat("time");
    final static DataFormat activityDataFormat = new DataFormat("activity");
    final static DataFormat factDataFormat = new DataFormat("fact");
    final static DataFormat templateDataFormat = new DataFormat("template");
    final static DataFormat actionDataFormat = new DataFormat("action");
    final static DataFormat tempViewDataFormat = new DataFormat("tempView");   
    
    boolean tempOnScreen = false;
    boolean isRuleTemp=false;
    VBox vb_holdingTemp;
    
    Pane paneFillR;
    Pane paneFillL;
    double heightVbL;
    double heightVbR;
    VBox vbRight;
    int amountActions =1;
    int amountInput =0;
    int amountPanesVbLeft = 0;
    int amountPanesVbRight = 0;
    VBox vbLeftPanes;
    VBox vbRightPanes;
    
    TextField tf_ruleName;
    
    LinkedList<CmtLabel> inputLabels;
    LinkedList<CmtToggleEU> inputToggles;
    TextField tf_output;
    Template currentTemp;
    
    HashMap<Integer,IFactType> mapFacts;
    //HashMap<Integer,IFactType> mapFields;
    
    
    @SuppressWarnings("unchecked")
	public UserTab(){
    	
            CMTClient.addListener(this);
    	items_input_activities= FXCollections.observableArrayList();
    	items_input_time= FXCollections.observableArrayList();
    	items_input_persons= FXCollections.observableArrayList();
    	items_input_locations= FXCollections.observableArrayList();
    	items_lv_userActivities= FXCollections.observableArrayList();
    	items_lv_actions= FXCollections.observableArrayList();
    	items_lv_myRules= FXCollections.observableArrayList();
    
    	inputLabels = new LinkedList<CmtLabel>();
        inputToggles = new LinkedList<CmtToggleEU>();
        mapFacts = new HashMap<>();
        //mapFields = new HashMap<>();
    }
	
	public void setDnDZones(){
		setDnD(ap_workbench);
		
	}
	
	
	
	private void setDnD(Pane pane){
        pane.setOnDragOver((me)-> {
        	Dragboard db = me.getDragboard();
            if (db.getContentTypes().contains(templateDataFormat) || db.getContentTypes().contains(tempViewDataFormat)){
            	me.acceptTransferModes(TransferMode.ANY);
            }
        });
        
        pane.setOnDragDropped((me)-> {
        	 if (me.getDragboard().getContentTypes().contains(templateDataFormat)){
        		Template temp = (Template) me.getDragboard().getContent(templateDataFormat);
        		 System.out.println(temp.getName());
        		 if(!tempOnScreen){
        		 currentTemp = temp;
        		 tempOnScreen = true;
        		 Point2D pt = ap_workbench.sceneToLocal(me.getSceneX(), me.getSceneY());
        		 
        		 System.out.println(TemplateHA.class.isAssignableFrom(temp.getClass()));
        		 if(!temp.getName().equals("AND Template")){
        			 if(TemplateHA.class.isAssignableFrom(temp.getClass())){
        				 isRuleTemp = false;
        				 lv_actions.setDisable(true);
        			 }else{
        				 isRuleTemp = true;
        			 }
        			 drawTemplate(temp, pt);
        		 }else{
        			 isRuleTemp = true;
        			 drawDefaultTemplate(temp, pt);
        			 lv_input_persons.setDisable(true);
        			 lv_input_locations.setDisable(true);
        			 tv_input_objects.setDisable(true);
        		 }
        		 changeHeader();
        		
        	 }
                         System.out.println("-- no clear screen");
        	}else{
        		if (me.getDragboard().getContentTypes().contains(tempViewDataFormat)){
            		
            		 Point2D pt = ap_workbench.sceneToLocal(me.getSceneX()-100, me.getSceneY());
            		vb_holdingTemp.setLayoutX(pt.getX());
            		vb_holdingTemp.setLayoutY(pt.getY());
        		}
        	}
        });
    }
	
	private void fill(){
		System.out.println(amountActions + "-------" + amountInput);
		if(amountInput<amountActions){
			// fill left side
			vbLeftPanes.getChildren().clear();
			vbRightPanes.getChildren().clear();
			int dif = amountActions-amountInput;
			for(int i = 0; i<dif;i++){
				HBox hbAfterInput = new HBox();
				 hbAfterInput.setMaxWidth(200);
				 hbAfterInput.setMinWidth(200);
				 hbAfterInput.setPrefWidth(200);
				 Pane paneLI = new Pane();
				 paneLI.getStyleClass().add("hbPaneLarge");
				 
				 
				 Pane paneRI = new Pane();
				
				  paneRI.getStyleClass().add("hbPaneColoredLarge");
				 //Pane paneAfterInput = new Pane();
				 hbAfterInput.getChildren().add(paneLI);
				 hbAfterInput.getChildren().add(paneRI);
				vbLeftPanes.getChildren().add(hbAfterInput);
			}
		}else{
			if(amountInput>amountActions){
				System.out.println("in if ------");
				vbLeftPanes.getChildren().clear();
				vbRightPanes.getChildren().clear();
				int dif = amountInput-amountActions;
				for(int i = 0; i<dif;i++){
					System.out.println("in for ---------- ");
					Pane pane = new Pane();
					pane.getStyleClass().add("paneFill");
					pane.setStyle("-fx-background-color:#3F51B5;");
					vbRightPanes.getChildren().add(pane);
					
				}
			}else{
				if(amountActions == amountInput){
					vbLeftPanes.getChildren().clear();
					vbRightPanes.getChildren().clear();
				}
			}
		}
		
	}
	
	private void setDefaultHeader(){
		hbHeader.getChildren().clear();
		hbHeader.setStyle("-fx-background-color: #B0BEC5");
	}
	
	private void changeHeader(){
		if(!isRuleTemp){
			// clear header add Pane 650 + but save + pane 10 + but can + pane 20
			hbHeader.getChildren().clear();
			hbHeader.getStyleClass().clear();
        	hbHeader.getStyleClass().add("lab");
        	hbHeader.setStyle("-fx-background-color: #0097A7 ;");
        	
        	Pane pane = new Pane();
        	pane.setMinWidth(850);
        	pane.setPrefWidth(850);
        	hbHeader.getChildren().add(pane);
        	
        	bt_save = new Button();
        	bt_save.setText("Save");
        	bt_save.setOnAction((event)->{onSave();});
        	bt_save.getStyleClass().add("buttonAct");
        	hbHeader.getChildren().add(bt_save);
        	
        	Pane pane1 = new Pane();
        	pane1.setMinWidth(10);
        	pane1.setPrefWidth(10);
        	hbHeader.getChildren().add(pane1);
        	
        	bt_cancel = new Button();
        	bt_cancel.setText("Cancel");
        	bt_cancel.setOnAction((event)->{onCancel();});
        	bt_cancel.getStyleClass().add("buttonAct");
        	hbHeader.getChildren().add(bt_cancel);
        	
        	
        	TreeItem<Template> temp = tv_templates.getRoot().getChildren().get(1);
        	temp.setExpanded(false);
		}else{
			
			// clear header add Pane 50 tf name 100 +  pane 500 + but save + pane 10 + but can + pane 20
			
			
			hbHeader.getChildren().clear();
			hbHeader.getStyleClass().clear();
        	hbHeader.getStyleClass().add("labRule");
        	hbHeader.setStyle("-fx-background-color: #546E7A ;");
        	
        	Pane pane = new Pane();
        	pane.setMinWidth(50);
        	pane.setPrefWidth(50);
        	hbHeader.getChildren().add(pane);
        	
        	tf_ruleName = new TextField();
        	tf_ruleName.setMinWidth(100);
        	tf_ruleName.setPrefWidth(100);
        	tf_ruleName.setPromptText("Rule name");
        	hbHeader.getChildren().add(tf_ruleName);
        	
        	Pane pane2 = new Pane();
        	pane2.setMinWidth(700);
        	pane2.setPrefWidth(700);
        	hbHeader.getChildren().add(pane2);
        	
        	bt_save = new Button();
        	bt_save.setText("Save");
        	bt_save.setOnAction((event)->{onSave();});
        	bt_save.getStyleClass().add("buttonRule");
        	hbHeader.getChildren().add(bt_save);
        	
        	Pane pane1 = new Pane();
        	pane1.setMinWidth(10);
        	pane1.setPrefWidth(10);
        	hbHeader.getChildren().add(pane1);
        	
        	bt_cancel = new Button();
        	bt_cancel.setText("Cancel");
        	bt_cancel.setOnAction((event)->{onCancel();});
        	bt_cancel.getStyleClass().add("buttonRule");
        	hbHeader.getChildren().add(bt_cancel);
        	TreeItem<Template> temp = tv_templates.getRoot().getChildren().get(0);
        	temp.setExpanded(false);
		}
	}
	
	private void drawDefaultTemplate(Template temp, Point2D ptInScene){
		vb_holdingTemp = new VBox();
		vb_holdingTemp.setLayoutX(ptInScene.getX()-100);
		vb_holdingTemp.setLayoutY(50);
		vb_holdingTemp.setOnDragDetected((event)->{
			Dragboard db = vb_holdingTemp.startDragAndDrop(TransferMode.ANY);
            
             ClipboardContent content = new ClipboardContent();
             content.put(tempViewDataFormat, "");
             db.setContent(content);
		});
		
		HBox hbLogic = new HBox();
		hbLogic.setMinWidth(400);
		hbLogic.setMaxWidth(400);
		
		VBox vbLeft = new VBox();
		vbLeft.setMinWidth(200);
		vbLeft.setMaxWidth(200);
		
		vbRight = new VBox();
		vbRight.setMinWidth(200);
		vbRight.setMaxWidth(200);
		
		
		
		
		 HBox hb = new HBox();
		 Label paneL = new Label();
		 paneL.setText("IF");
		 paneL.getStyleClass().add("lbHeaderSides");
		 paneL.setStyle("-fx-alignment: center-left;");
		 Label paneR = new Label();
		 paneR.setText("THEN");
		 paneR.getStyleClass().add("lbHeaderSides");
		 paneR.setStyle("-fx-alignment: center-right;");
		 Label lb_header = new Label();
		 lb_header.getStyleClass().add("hbLabel");
		 lb_header.setText(temp.getName());
		 hb.getChildren().add(paneL);
		 hb.getChildren().add(lb_header);
		 hb.getChildren().add(paneR);
		 vb_holdingTemp.getChildren().add(hb);
		
		 // add droplabel time/act to vbleft
		 
		 HBox hbinputdrop = new HBox();
  		Label inputDrop = new Label();
  		inputDrop.setText("Drag an activity \n or time instance");
  		inputDrop.getStyleClass().add("labFactNoId");
  		amountInput += 1;
  		// stukske 50 // 
				Pane paneAfterLabdrop2 = new Pane();
				paneAfterLabdrop2.getStyleClass().add("paneAfterInputLab");
				
			//	hbinputdrop.getChildren().add(paneAfterLabdrop2);
				hbinputdrop.getChildren().add(inputDrop);
				hbinputdrop.getChildren().add(paneAfterLabdrop2);
				hbinputdrop.setOnDragOver((event)->{
					if(event.getDragboard().hasContent(activityDataFormat)|| event.getDragboard().hasContent(timeDataFormat) ){
						event.acceptTransferModes(TransferMode.ANY);
					}
				});
				
				hbinputdrop.setOnDragDropped((event)->{
					
					FactType eventActivity = null;
					
					if(event.getDragboard().hasContent(activityDataFormat)){
						eventActivity = (FactType) event.getDragboard().getContent(activityDataFormat);
					}else{
						if(event.getDragboard().hasContent(timeDataFormat)){
							eventActivity = (FactType) event.getDragboard().getContent(timeDataFormat);
						}	
					}
					 if(eventActivity!=null){
						 
						 
                                                 
                                                 // from facttype to eventinput
                                                 EventInput input = new EventInput();
                                                 input.setClassName(eventActivity.getClassName());
                                                 input.setFields(eventActivity.getFields());
                                              
                                                 for(CMTField f : eventActivity.getFields()){
                                                     FieldValueLimitation lim = new FieldValueLimitation();
                                                     lim.setFieldName(f.getName());
                                                     lim.setValue("");
                                                     lim.setOperator("");
                                                     input.addLimitation(lim);
                                                 }
                                                 
                                                 VBox vbInput = getCMTLabelEventInput(input);
                                                 IFBlock ifblock = new IFBlock();
                                                 ifblock.setEvent(eventActivity);
                                                 ifblock.setType(Constants.ACTIVITY);
                                                 BindingIF ifbind = new BindingIF();
                                                 ifbind.setIfParameter("");
                                                 ifbind.setIndexObj(amountInput);
                                                
                                                 BindingInputFact endbind  = new BindingInputFact();
                                                 endbind.setFactId("");
                                                 endbind.setIndexObj(amountInput);
                                                 endbind.setInputObject(input);
                                                 Binding bi = new Binding();
                                                 bi.setStartBinding(ifbind);
                                                 bi.setEndBinding(endbind);
                                                 ifblock.addBinding(bi);
                                                 Operator oper = new Operator();
                                                 oper.setOperator("AND");
                                                 currentTemp.addIfBlock(ifblock, oper);
//						 CmtLabel labInput = new CmtLabel();
//						 
//						 labInput.setObj(eventActivity);
//						 System.out.println("in time act if");
//						 String idfield = eventActivity.getUriField();
//						 if(idfield.equals("")){
//							 labInput.getStyleClass().add("labFactWithId");
//							 labInput.setText(eventActivity.getClassName());
//							 labInput.isFilled = true;
//							 //listIndexs.add(factbind.getIndexObj());
//							 vbInput.getChildren().add(labInput);
//						 }else{
//						
//									 vbInput.getStyleClass().add("vbParValue");
//									 labInput.setText(eventActivity.getClassName());
//									 labInput.setStyle("-fx-text-fill: white;");
//									 labInput.isFilled = false;
//									 vbInput.getChildren().add(labInput);
//									 //VBox hbField = new VBox();
//									 Label parName = new Label();
//									 parName.setText(idfield + " is ");
//									 parName.setStyle("-fx-text-fill: white;");
//									 vbInput.getChildren().add(parName);
//										String idField = eventActivity.getUriField();
//										String formatVar = eventActivity.getVarFormat();
//										ArrayList<String> listVar = eventActivity.getVarList();
//											if(!(formatVar.equals(""))){
//												TextField tf_inputVar = new TextField();
//												tf_inputVar.setMinWidth(100);
//												tf_inputVar.setMaxWidth(100);
//												labInput.setTf(tf_inputVar);
//												
//													if(formatVar.equals("String")){
//														tf_inputVar.setText("");
//														labInput.setFormat("");
//													}else{
//														tf_inputVar.setText(formatVar);
//														labInput.setFormat(formatVar);
//													}
//													
//													vbInput.getChildren().add(tf_inputVar);
//												
//											}else{
//												if(!(listVar == null)&&!(listVar.isEmpty())){
//													ObservableList<String> items = FXCollections.observableArrayList();
//													items.addAll(listVar);
//													items.add("Any");
//													ComboBox<String> box = new ComboBox<String>();
//													box.setItems(items);
//													box.getSelectionModel().select("Any");
//													vbInput.getChildren().add(box);
//													labInput.setBox(box);
//                                                                                                    }
//												}	
//													
//											}
//										
						 
						 
						 HBox hbAction = new HBox();
			        		
			        		// stukske 50 // 
							Pane paneAfterLab = new Pane();
							paneAfterLab.getStyleClass().add("paneAfterInputLab");
							
							
							hbAction.getChildren().add(vbInput);
							hbAction.getChildren().add(paneAfterLab);
							HBox hbAfterInput = new HBox();
							 hbAfterInput.setMaxWidth(200);
							 hbAfterInput.setMinWidth(200);
							 hbAfterInput.setPrefWidth(200);
							 Pane paneLI = new Pane();
							 paneLI.getStyleClass().add("hbPane");
							 
							 
							 Label paneRI4 = new Label();
							paneRI4.setText("AND");
					
							  paneRI4.getStyleClass().add("hbPaneColoredDefault");
							 //Pane paneAfterInput = new Pane();
							 hbAfterInput.getChildren().add(paneLI);
							 hbAfterInput.getChildren().add(paneRI4);
							 
							vbLeft.getChildren().add(vbLeft.getChildren().size()-3, hbAction);
							vbLeft.getChildren().add(vbLeft.getChildren().size()-3,hbAfterInput);
							amountInput +=1;
							//inputLabels.add(labInput);
							fill();
					 }
					 
					 
					
				});
				
			 vbLeft.getChildren().add(hbinputdrop);
			 
			 HBox hbAfterInput = new HBox();
			 hbAfterInput.setMaxWidth(200);
			 hbAfterInput.setMinWidth(200);
			 hbAfterInput.setPrefWidth(200);
			 Pane paneLI = new Pane();
			 paneLI.getStyleClass().add("hbPane");
			 
			 
			 Pane paneRI4 = new Pane();
			
			  paneRI4.getStyleClass().add("hbPaneColored");
			 //Pane paneAfterInput = new Pane();
			 hbAfterInput.getChildren().add(paneLI);
			 hbAfterInput.getChildren().add(paneRI4);
			 
			 vbLeft.getChildren().add(hbAfterInput);
		 
		 
		 //add actiondrop to vbRight
		 
		 HBox hbActiondrop = new HBox();
  		Label actionDrop = new Label();
  		actionDrop.setText("Drag an action");
  		actionDrop.getStyleClass().add("lbActionDrop");
  		
  		// stukske 50 // 
				Pane paneAfterLabdrop = new Pane();
				paneAfterLabdrop.getStyleClass().add("paneAfterInputLab");
				
				hbActiondrop.getChildren().add(paneAfterLabdrop);
				hbActiondrop.getChildren().add(actionDrop);
				
				hbActiondrop.setOnDragOver((event)->{
					if(event.getDragboard().hasContent(actionDataFormat)){
						event.acceptTransferModes(TransferMode.ANY);
					}
				});
				
				hbActiondrop.setOnDragDropped((event)->{
					ActionClient actionDropped = (ActionClient) event.getDragboard().getContent(actionDataFormat);
					VBox actionBox = getActionBox(actionDropped);
					amountActions +=1;
					HBox hbAction = new HBox();
	        		
	        		// stukske 50 // 
					Pane paneAfterLab = new Pane();
					paneAfterLab.getStyleClass().add("paneAfterInputLab");
					
					hbAction.getChildren().add(paneAfterLab);
					hbAction.getChildren().add(actionBox);
					 Pane paneRI = new Pane();
						
					  paneRI.getStyleClass().add("hbPaneColored");
					  // insert at vb - 2
					  System.out.println("size " + vbRight.getChildren().size());
					 
					vbRight.getChildren().add(vbRight.getChildren().size()-3, hbAction);
					vbRight.getChildren().add(vbRight.getChildren().size()-3,paneRI);
					fill();
                                        TemplateActions tempAct = (TemplateActions) currentTemp;
                                        tempAct.addAction(actionDropped);
				});
				
			 vbRight.getChildren().add(hbActiondrop);
			 Pane paneRI2 = new Pane();
				
			  paneRI2.getStyleClass().add("hbPaneColored");
			  vbRight.getChildren().add(paneRI2);
			  
			  vbRightPanes = new VBox();
				 vbRight.getChildren().add(vbRightPanes);	  
		 
		 paneFillR = new Pane();
		 
		 paneFillR.getStyleClass().add("paneFillRight");
		 
		 //vbRight.getChildren().add(paneFillR);
		 // naar einde
		 
		 HBox hbFillL = new HBox();
		 Pane paneFillLL = new Pane();
		 paneFillLL.setMinWidth(75);
		 paneFillLL.setMaxWidth(75);
		 
		 paneFillL = new Pane();
		 paneFillL.getStyleClass().add("paneFillRight");
		 hbFillL.getChildren().add(paneFillLL);
		 hbFillL.getChildren().add(paneFillL);
		 
		 vbLeftPanes = new VBox();
		 vbLeft.getChildren().add(vbLeftPanes);
		 
		 //vbLeft.getChildren().add(hbFillL);
		 hbLogic.getChildren().add(vbLeft);
		 System.out.println(vbLeft.getHeight());
		 
		 //vbRight.setPrefHeight(vbLeft.getHeight());
		// vbRight.setStyle("-fx-background-color: #3F51B5");
		 hbLogic.getChildren().add(vbRight);
		 vb_holdingTemp.getChildren().add(hbLogic);
		 
		
		 
		 ap_workbench.getChildren().add(vb_holdingTemp);
		 
		
	}

    private CmtLabel getCMTLabelFact(Fact fact){
        CmtLabel labInput = new CmtLabel();
        labInput.setText(fact.getClassName() + " is \n" + fact.getUriValue());
        labInput.getStyleClass().add("labFactWithId");
        labInput.isFact = true;
	labInput.isFilled = true;
        labInput.setObj(fact);
        return labInput;
    }
    
    private CmtToggleEU getCMTToggleFactType(FactType facttype){
    // list to chose from!
        CmtToggleEU labInput = new CmtToggleEU();
        labInput.setText("Choose a \n" + facttype.getClassName());
        labInput.getStyleClass().add("labToggleEU");
	//labInput.isFact = false;
	labInput.isFilled = false;
        labInput.setObj(facttype);
        
        
        labInput.setOnMouseClicked((event)->{
            if(event.getClickCount() == 1){
                // show list
                String classnm = "all";
                if(labInput.getObj() instanceof FactType){
                    classnm = ((FactType)labInput.getObj()).getClassName();
                }else{
                    if(labInput.getObj() instanceof Fact){
                        classnm = ((Fact)labInput.getObj()).getClassName();
                    }
                }
                BrowersDialog dia = new BrowersDialog(classnm);
                Optional<Fact> resFact=  dia.showAndWait();
                if(resFact !=null && resFact.isPresent()){
                    labInput.isFilled = true;
		    labInput.getStyleClass().add("labFactWithId");
                    Fact fact = resFact.get();
                    labInput.setText(fact.getClassName() + " is \n" + fact.getUriValue());
                    labInput.setObj(fact);
                    updateMaps(labInput.getBinding(), fact);
                }
            }
        });
        
        return labInput;
    
    }
    
//    private CmtLabel getCMTLabelFactType(FactType facttype){
//        
//        // list to chose from!
//        CmtLabel labInput = new CmtLabel();
//        labInput.setText("Choose a \n" + facttype.getClassName());
//        labInput.getStyleClass().add("labFactNoId");
//	labInput.isFact = false;
//	labInput.isFilled = false;
//        labInput.setObj(facttype);
//        labInput.setOnDragOver((event)->{
//            Dragboard db = event.getDragboard();
//            System.out.println("- db " + db.getContentTypes().iterator().next().toString());
//            if (db.getContentTypes().contains(factDataFormat)){
//	       	Fact fact = (Fact) db.getContent(factDataFormat);
//                System.out.println("- db " + labInput.getObj().getClass());
//                IFactType factLab = labInput.getObj();
//                System.out.println("--- factLab " + factLab.getClass() + "  " + ((FactType)factLab).getClassName() + "  " + fact.getClassName() );
//		if(fact.getClassName().equals(((FactType)factLab).getClassName())){
//		    event.acceptTransferModes(TransferMode.ANY);
//		}
//            }
//        });
//        labInput.setOnDragDropped((event)->{
//            Dragboard db = event.getDragboard();						 					        
//            if (db.getContentTypes().contains(factDataFormat)){
//		Fact fact = (Fact) db.getContent(factDataFormat);
//		IFactType factLab = labInput.getObj();
//		if(fact.getClassName().equals(((FactType)factLab).getClassName())){
//		    labInput.isFilled = true;
//		    labInput.getStyleClass().add("labFactWithId");
//                    ArrayList<CMTField> factFields = fact.getFields();
//                    labInput.setText(fact.getClassName() + " is \n" + fact.getUriValue());
//                    labInput.isFact = true;
//                    labInput.setObj(fact);
//                    updateMaps(labInput.getBinding(), fact);
//                }
//            }
//	});
//        labInput.setOnMouseClicked((event)->{
//            if(event.getClickCount() == 2){
//                labInput.getStyleClass().clear();
//                labInput.getStyleClass().add("labFactNoId");
//                labInput.setObj(facttype);
//                labInput.setText("Drag a \n" + ((FactType)labInput.getObj()).getClassName());
//                labInput.isFact = false;
//                labInput.isFilled = false;
//                updateMaps(labInput.binding, facttype);
//            }if(event.getClickCount() == 1){
//                // show list
//                BrowersDialog dia = new BrowersDialog(((FactType)labInput.getObj()).getClassName());
//                Optional<Fact> resFact=  dia.showAndWait();
//                if(resFact !=null){
//                    labInput.isFilled = true;
//		    labInput.getStyleClass().add("labFactWithId");
//                    Fact fact = resFact.get();
//                    labInput.setText(fact.getClassName() + " is \n" + fact.getUriValue());
//                    labInput.isFact = true;
//                    labInput.setObj(fact);
//                    updateMaps(labInput.getBinding(), fact);
//                }
//            }
//        });
//        
//        return labInput;
//    }
    
    private void setDragOverCmtLabelLeft(CmtLabel labInput){
        labInput.setOnDragOver((event)->{
            Dragboard db = event.getDragboard();
            if (db.getContentTypes().contains(factDataFormat)){
	       	Fact fact = (Fact) db.getContent(factDataFormat);
                IFactType factLab = labInput.getObj();
		if(fact.getClassName().equals(((FactType)factLab).getClassName())){
		    event.acceptTransferModes(TransferMode.ANY);
		}
            }
        });
        
    }
    
    private void updateMaps(Binding binding, IFactType fact ){
        BindingParameter endbind = binding.getEndBinding();
        
        mapFacts.put(endbind.getIndexObj(), fact);
        
        /*if(endbind instanceof BindingInputFact){
            for(Binding bindFact : mapFacts.get(endbind.getIndexObj())){
               ((BindingInputFact)bindFact.getEndBinding()).setInputObject(fact);
               if(fact instanceof Fact){
                    ((BindingInputFact)bindFact.getEndBinding()).setFactId(((Fact)fact).getUriValue());
               }
            }
        }else{
            if(endbind instanceof BindingInputField){
                for(Binding bindField : mapFields.get(endbind.getIndexObj())){
                    ((BindingInputField)bindField.getEndBinding()).setInputObject(fact);
                    if(fact instanceof Fact){
                        ((BindingInputField)bindField.getEndBinding()).setFactId(((Fact)fact).getUriValue());
                    }
                }
            }
        } */
    }
    
    private VBox getCMTLabelEventInput(EventInput input){
        VBox vbox = new VBox();
        CmtLabel label = new CmtLabel();
        vbox.getStyleClass().add("vbParValue");
        label.setText(input.getClassName());
        label.setStyle("-fx-text-fill: white;");
        label.setObj(input);
	vbox.getChildren().add(label);
        System.out.println(" is filled in " + input.isFilledIn());
        if(input.isFilledIn()){
            label.isFilled = true;
        }else{
            label.isFilled = false;
            // button to fill in
            Button but = new Button();
            but.getStyleClass().add("butFillIn");
            but.setText("Enter values");
            but.setOnAction((event)->{
                // popup fields to fill in
              FillInFields dia = new FillInFields(input);
              dia.showAndWait().ifPresent(result->{
                  HashSet<FieldValueLimitation> results = result.getResults();
                  ArrayList<FieldValueLimitation> list = new ArrayList<>();
                  if(results != null){
                    for(FieldValueLimitation prop : results){
                        list.add(prop);
                    }
                    dia.getEventInput().setLimitations(list);
                   but.setText("Completed");
                  }
              });
            });
            vbox.getChildren().add(but);
        }
        inputLabels.add(label);
        return vbox;
    }
    
    
    private void drawTemplate(Template temp, Point2D ptInScene){
		
        vb_holdingTemp = new VBox();
	vb_holdingTemp.setLayoutX(ptInScene.getX()-100);
	vb_holdingTemp.setLayoutY(50);
	vb_holdingTemp.setOnDragDetected((event)->{
            Dragboard db = vb_holdingTemp.startDragAndDrop(TransferMode.ANY);
            ClipboardContent content = new ClipboardContent();
            content.put(tempViewDataFormat, "");
            db.setContent(content);
	});
		
	HBox hbLogic = new HBox();
	hbLogic.setMinWidth(400);
	hbLogic.setMaxWidth(400);
		
	VBox vbLeft = new VBox();
	vbLeft.setMinWidth(200);
	vbLeft.setMaxWidth(200);
		
	vbRight = new VBox();
	vbRight.setMinWidth(200);
	vbRight.setMaxWidth(200);
		
	HBox hb = new HBox();
	Label paneL = new Label();
	paneL.setText("INPUT");
	paneL.getStyleClass().add("lbHeaderSides");
	paneL.setStyle("-fx-alignment: center-left;");
	Label paneR = new Label();
	paneR.setText("THEN");
	paneR.getStyleClass().add("lbHeaderSides");
	paneR.setStyle("-fx-alignment: center-right;");
	Label lb_header = new Label();
	lb_header.getStyleClass().add("hbLabelTitle");
	lb_header.setText("Complete Template: " + temp.getName());
	hb.getChildren().add(paneL);
        Label paneM = new Label();
        paneM.getStyleClass().add("hbLabel");
	hb.getChildren().add(paneM);
	hb.getChildren().add(paneR);
        vb_holdingTemp.getChildren().add(lb_header);
	vb_holdingTemp.getChildren().add(hb);
	       System.out.println("------------ hier" + vb_holdingTemp.getChildren().size());
        
	LinkedList<Integer> listIndexs = new LinkedList<Integer>();
        System.out.println(" size ifblocks user tab " +temp.getIfBlocks().size());
        for(int i = 0; i<temp.getIfBlocks().size(); i++){
            IFBlock ifbl = temp.getIfBlocks().get(i);
            System.out.println("-- bindings " + ifbl.getBindings().size());
            for(int a = 0; a<ifbl.getBindings().size();a++){
                Binding bind = ifbl.getBindings().get(a);
                BindingParameter bindPar = bind.getEndBinding();
                
                HBox hbInput = new HBox();
		CmtLabel labInput = null;
		IFactType inputObj = null;
                int index = 0;
                
		if(BindingInputFact.class.isAssignableFrom(bindPar.getClass())){
                    BindingInputFact factbind = (BindingInputFact) bindPar;
                    inputObj = factbind.getInputObject();
                    index = factbind.getIndexObj();
                   
                }else{
                    BindingInputField fieldbind = (BindingInputField) bindPar;
                    inputObj = fieldbind.getInputObject();
                    System.out.println(" ---- " + inputObj);
                    index = fieldbind.getIndexObj(); 
                }
                if(!listIndexs.contains(index)){
                updateMaps(bind, inputObj);
                    // case 1: set fact -- fact
                    // case 2: fact to fill in -- facttype
                    // case 3: eventinput 
                    // case 3.1: all fields filled in
                    // case 3.2: not filled in
                   System.out.println("--- " + inputObj.getClass());
                    if(inputObj instanceof EventInput){
                        VBox vbox = getCMTLabelEventInput((EventInput)inputObj);
                        hbInput.getChildren().add(vbox);
                        System.out.println("--- " + vbox.getChildren().size());
                    }else{
                        if(inputObj instanceof Fact){
                            labInput = getCMTLabelFact((Fact)inputObj);
                            inputLabels.add(labInput);
                            hbInput.getChildren().add(labInput);
                        }else{
                            if(inputObj instanceof FactType){
                                CmtToggleEU tog = getCMTToggleFactType((FactType) inputObj);
                                tog.setBinding(bind);
                                hbInput.getChildren().add(tog);
                                inputToggles.add(tog);
                                //labInput = getCMTLabelFactType((FactType) inputObj);
                                //labInput.setBinding(bind);
                                //inputLabels.add(labInput);
                            }
                        }
                        //hbInput.getChildren().add(labInput);
                    }
                    
                    listIndexs.add(index);
                    
                 
				 Pane paneAfterLab = new Pane();
				 paneAfterLab.getStyleClass().add("paneAfterInputLab"); //50
				 
				
				 hbInput.getChildren().add(paneAfterLab);
				 vbLeft.getChildren().add(hbInput);
				 
				 
				 HBox hbAfterInput = new HBox();
				 hbAfterInput.setMaxWidth(200);
				 hbAfterInput.setMinWidth(200);
				 hbAfterInput.setPrefWidth(200);
				 Pane paneLI = new Pane();
				 paneLI.getStyleClass().add("hbPane");
				 
				 
				 Pane paneRI = new Pane();
				
				  paneRI.getStyleClass().add("hbPaneColored");
				 //Pane paneAfterInput = new Pane();
				 hbAfterInput.getChildren().add(paneLI);
				 hbAfterInput.getChildren().add(paneRI);
				 
				 vbLeft.getChildren().add(hbAfterInput);
				 
				 
			 
            }
		 }
		
		
	//	}
                 }
		 amountInput = listIndexs.size();
		 
		 vbLeftPanes = new VBox();
		 vbLeft.getChildren().add(vbLeftPanes);
		 
		 // populate right vb
		 
		 if(temp instanceof TemplateHA){
				TemplateHA tempHA = (TemplateHA) temp;
		 	 
				// stukske 50
				Pane paneAfterLab = new Pane();
				paneAfterLab.getStyleClass().add("paneAfterInputLab");
				
				
			 
			 HBox hbOutput = new HBox();
			 hbOutput.getStyleClass().add("labOutputTemp");
			 String outputName = tempHA.getOutput().getName();
			 if(outputName.equals("Enter activity name")){
				 // add textField
				 tf_output = new TextField();
				 tf_output.setPromptText("Enter Situation Name");
				 tf_output.setMaxWidth(130);
				 hbOutput.getChildren().add(tf_output);
			 }else{
				 // blue with name
				 Label namelb = new Label();
				 namelb.setText(outputName);
				 namelb.getStyleClass().add("labOutputTemp");
				 hbOutput.getChildren().add(namelb);
			 }
			 
			 HBox hbRight = new HBox();
			 hbRight.getChildren().add(paneAfterLab);
			 hbRight.getChildren().add(hbOutput);
			 Pane paneRI = new Pane();
				
			  paneRI.getStyleClass().add("hbPaneColored");
			 vbRight.getChildren().add(hbRight);
			 vbRight.getChildren().add(paneRI);
			 
			amountActions =1;
		 }else{
			 
			 TemplateActions tempAction = (TemplateActions) temp;
			 
			 // add drag action box 
			 HBox hbActiondrop = new HBox();
     		Label actionDrop = new Label();
     		actionDrop.setText("Drag an action");
     		actionDrop.getStyleClass().add("lbActionDrop");
     		
     		// stukske 50 // 
				Pane paneAfterLabdrop = new Pane();
				paneAfterLabdrop.getStyleClass().add("paneAfterInputLab");
				
				hbActiondrop.getChildren().add(paneAfterLabdrop);
				hbActiondrop.getChildren().add(actionDrop);
				
				hbActiondrop.setOnDragOver((event)->{
					if(event.getDragboard().hasContent(actionDataFormat)){
						event.acceptTransferModes(TransferMode.ANY);
					}
				});
				
				hbActiondrop.setOnDragDropped((event)->{
					ActionClient actionDropped = (ActionClient) event.getDragboard().getContent(actionDataFormat);
					VBox actionBox = getActionBox(actionDropped);
					amountActions +=1;
					HBox hbAction = new HBox();
	        		
	        		// stukske 50 // 
					Pane paneAfterLab = new Pane();
					paneAfterLab.getStyleClass().add("paneAfterInputLab");
					
					hbAction.getChildren().add(paneAfterLab);
					hbAction.getChildren().add(actionBox);
					 Pane paneRI = new Pane();
						
					  paneRI.getStyleClass().add("hbPaneColored");
					  // insert at vb - 2
					  System.out.println("size " + vbRight.getChildren().size());
					 
					vbRight.getChildren().add(vbRight.getChildren().size()-3, hbAction);
					vbRight.getChildren().add(vbRight.getChildren().size()-3,paneRI);
					fill();
				});
				
			 vbRight.getChildren().add(hbActiondrop);
			 Pane paneRI2 = new Pane();
				
			  paneRI2.getStyleClass().add("hbPaneColored");
			  vbRight.getChildren().add(paneRI2);
			 for(int ii = 0; ii< tempAction.getActions().size();ii++){
				 ActionClient action = tempAction.getActions().get(ii);
				 VBox vbAction = getActionBox(action);
	        		amountActions +=1;
	        		
	        		HBox hbAction = new HBox();
	        		
	        		// stukske 50 // 
					Pane paneAfterLab = new Pane();
					paneAfterLab.getStyleClass().add("paneAfterInputLab");
					
					hbAction.getChildren().add(paneAfterLab);
					hbAction.getChildren().add(vbAction);
					 Pane paneRI = new Pane();
						
					  paneRI.getStyleClass().add("hbPaneColored");
					  // insert at vb - 2
					  System.out.println(vbRight.getChildren().size());
					 if(ii==0){
					vbRight.getChildren().add(vbRight.getChildren().size()-2, hbAction);
					vbRight.getChildren().add(vbRight.getChildren().size()-2,paneRI);
					 }else{
						 vbRight.getChildren().add(vbRight.getChildren().size()-2, hbAction);
							vbRight.getChildren().add(vbRight.getChildren().size()-2,paneRI);
					 }
			 }
		 }
		 
		 vbRightPanes = new VBox();
		 vbRight.getChildren().add(vbRightPanes);
		 
		 
		 //paneFillR = new Pane();
		 
		 //paneFillR.getStyleClass().add("paneFillRight");
		 
		 //vbRight.getChildren().add(paneFillR);
		 // naar einde
		 
		 HBox hbFillL = new HBox();
		 Pane paneFillLL = new Pane();
		 paneFillLL.setMinWidth(75);
		 paneFillLL.setMaxWidth(75);
		 
		 paneFillL = new Pane();
		 paneFillL.getStyleClass().add("paneFillRight");
		 hbFillL.getChildren().add(paneFillLL);
		 hbFillL.getChildren().add(paneFillL);
		 
		 //vbLeft.getChildren().add(hbFillL);
		 hbLogic.getChildren().add(vbLeft);
		 System.out.println(vbLeft.getHeight());
		 
		 //vbRight.setPrefHeight(vbLeft.getHeight());
		// vbRight.setStyle("-fx-background-color: #3F51B5");
		 hbLogic.getChildren().add(vbRight);
		 vb_holdingTemp.getChildren().add(hbLogic);
		 
		fill();
		 
		 ap_workbench.getChildren().add(vb_holdingTemp);
	}
	
	
	private VBox getActionBox(ActionClient action){
		CmtLabel lab = new CmtLabel();
		 lab.setAction(action);
		 VBox vbAction = new VBox();
		 vbAction.getStyleClass().add("vbParValueAction");
		 lab.setText(action.getName());
		 lab.setStyle("-fx-text-fill: white;");
		 vbAction.getChildren().add(lab);
                 
                 Button but = new Button();
                  but.getStyleClass().add("butFillInAction");
                but.setText("Enter values");
                but.setOnAction((event)->{
                // popup fields to fill in
                FillInActionFields dia = new FillInActionFields(action);
                dia.showAndWait().ifPresent(result->{
                  HashMap<String,String> results = result.getResults();
                  
                  if(results != null){
                    
                    ArrayList<ActionField> fields = dia.getActionClient().getFields();
                    for(ActionField f : fields){
                        f.setValue(results.get(f.getName()));
                        System.out.println("getValue " + f.getValue());
                    }
                   but.setText("Completed");
                  }
              });
            });
            vbAction.getChildren().add(but);
//		 ArrayList<ActionField> actFields = action.getFields();
//		 //Field[] fields = action.getClass().getDeclaredFields();
//                System.out.println(" fields size " + actFields.size());
//   		for(ActionField field : actFields){
//   		
//           			Label fieldName = new Label(field.getName() + " is ");
//           			fieldName.setStyle("-fx-text-fill: white;");
//           			vbAction.getChildren().add(fieldName);
//           			if(field.getValue().equals("Any") || field.getValue().equals("")){
//                                    String formatVar = field.getFormat();
//                                    ArrayList<String> listVar = field.getVarList();
//           				if(!(formatVar.equals(""))){
//           				
//           						TextField tf_inputVar = new TextField();
//           						tf_inputVar.setPrefWidth(130);
//           						if(formatVar.equals("String")){
//                                                            tf_inputVar.setText("");
//           						}else{
//                                                            tf_inputVar.setText(formatVar);
//           						}
//           						
//           						vbAction.getChildren().add(tf_inputVar);
//           					
//           				}else{
//           					if(!(listVar == null) && !(listVar.isEmpty())){
//         					        ObservableList<String> items = FXCollections.observableArrayList();
//							items.addAll(listVar);
//							ComboBox<String> box = new ComboBox<String>();
//							box.setItems(items);
//							box.getStyleClass().add("comboAction");
//							vbAction.getChildren().add(box);
//           					}
//           				}	
//           						
//           			}else{
//           				Label fieldValue = new Label(field.getValue());
//           				fieldValue.setStyle("-fx-text-fill: white;");
//                                    vbAction.getChildren().add(fieldValue);
//           			}
//           		
//           		
//   			
//   			}
   		
   		return vbAction;
	}

	@FXML
	public void newFactDialog(ActionEvent event){
		if(event.getSource()!=object){
			if(event.getSource() == Person){
				new FactDialog("Person");
			} else{if(event.getSource() == Location){
				new FactDialog(Location.getId());
					}
				}	
		}else{
			new FactDialog(null);
		}
	}
	
	public void populateLists(){
		
		populateInputBlocks();
		populateRightSide();
		setDndLists();
		
	}
	
	private void populateInputBlocks(){
            HashSet<FactType> factTypes = CMTClient.getAvailableFactTypes();
            HashSet<FactType> eventTypes = CMTClient.getAvailableEventTypes();
            HashSet<Fact> facts= CMTClient.getAvailableFacts();
            
            ti_root_objects = new TreeItem<IFactType>();
            ti_root_objects.setExpanded(false);
	    tv_input_objects.setRoot(ti_root_objects);
	    tv_input_objects.setShowRoot(false);		
	    tv_input_objects.setCellFactory(getCallBackTvObjects());
	    lv_input_persons.setCellFactory(getCallbackFact());
	    lv_input_locations.setCellFactory(getCallbackFact());
	        
	    lv_input_time.setCellFactory(getCallbackEvent());
	    lv_input_activities.setCellFactory(getCallbackEvent());
	    lv_userActivities.setCellFactory(getCallbackEvent());
	    
            for(FactType factType:factTypes){
                if(!(factType.getClassName().equals("Person")) && !(factType.getClassName().equals("Location"))){
                    TreeItem<IFactType> tp = new TreeItem<IFactType>(factType);
                    tp.setExpanded(true);
                    ti_root_objects.getChildren().add(tp);
                }
            }
            
            for(FactType eventType:eventTypes){
                if(eventType.getType().equals("activity")){
                    if(eventType.isIsCustom()){
                        items_lv_userActivities.add(eventType);
                    }else{
                        items_input_activities.add(eventType);
                    }
                }else{
                    if(eventType.getType().equals("time")){
                        items_input_time.add(eventType);
                    }
                }
                
            }
	    
	        
	        // add fact in objects, persons, location
	        for(Fact fact : facts){
	        	Iterator<TreeItem<IFactType>> it = ti_root_objects.getChildren().iterator();
    			
                        if(!(fact.getClassName().equals("Person")) && !(fact.getClassName().equals("Location"))){
                            while(it.hasNext()){
                                    TreeItem<IFactType> node = it.next();
                                    IFactType typeInTree = (IFactType) node.getValue();
                                    if(typeInTree instanceof FactType){
                                        FactType factType = (FactType) typeInTree;
                                        if(fact.getClassName().equals(factType.getClassName())){
                                            node.getChildren().add(new TreeItem<IFactType>(fact));
                                        }
                                    }
                            }
                        }else{
                            if(fact.getClassName().equals("Person")){
                                    items_input_persons.add(fact);
                            }else{
                                    if(fact.getClassName().equals("Location")){
                                            items_input_locations.add(fact);
                                    }
                            }
                        }
    			
	        }
	       
	        lv_input_persons.setItems(items_input_persons);
	        lv_input_locations.setItems(items_input_locations);
	        lv_input_time.setItems(items_input_time);
	        lv_input_activities.setItems(items_input_activities);
	        lv_userActivities.setItems(items_lv_userActivities);

	}
	
	private void populateRightSide(){
        	populateListTemplates();
			HashSet<ActionClient> actions = CMTClient.getAvailableActions();
			HashSet<Rule> rules = CMTClient.getAvailableRules();
			// get rules
			
			
			for(ActionClient act : actions){
				items_lv_actions.add(act);
			}
			
			for(Rule rule: rules){
				items_lv_myRules.add(rule);
			}
			
		
		lv_actions.setCellFactory(getCallbackAction());
		lv_myRules.setCellFactory(getCallbackRule());
		
		lv_actions.setItems(items_lv_actions);
		lv_myRules.setItems(items_lv_myRules);
		
		
	}
        
        private void populateListTemplates(){
            HashSet<TemplateHA> templates = CMTClient.getAvailableTemplateHA();
            HashSet<TemplateActions> tempsAct = CMTClient.getAvailableTemplateActions();
            ti_root_temp = new TreeItem<Template>();
			ti_root_temp.setExpanded(true);
	       
			
	        tv_templates.setCellFactory(getCallbackTemplate());
			
	        Template tempHa = new Template();
	        tempHa.setName("For Activities");
	        
	        Template tempRule = new Template();
	        tempRule.setName("For Rules");
	        
	        // add default temp
	        
	        TemplateActions tempDefault = new TemplateActions();
	        tempDefault.setName("AND Template");
	        
	        TreeItem<Template> def = new TreeItem<Template>(tempDefault);
	        TreeItem<Template> it = new TreeItem<Template>(tempHa);
	        TreeItem<Template> it2 = new TreeItem<Template>(tempRule);
			
	       
	        it2.getChildren().add(def);
	        
	        for(TemplateHA template : templates){
                    it.getChildren().add(new TreeItem<Template>(template));
                    for(IFBlock bl : template.getIfBlocks()){
                        System.out.println(" -- type bl " + bl.getType());
                    }
	        }
	        for(TemplateActions temp: tempsAct){
                    it2.getChildren().add(new TreeItem<Template>(temp));
                }
	        ti_root_temp.getChildren().add(it);
	        ti_root_temp.getChildren().add(it2);
	        
	        tv_templates.setRoot(ti_root_temp);
	        tv_templates.setShowRoot(false);
//                
//                Platform.runLater(new Runnable() {
//                    @Override
//                    public void run() {
//                       // Thread.sleep( 250 );
//                        it.setExpanded(true);
//			it2.setExpanded(true);
//                }});
//               
	        
        }
	
	
	
	
	private void setDndLists(){
		
		tv_input_objects.setOnDragDetected(new EventHandler<Event>() {
	         public void handle(Event event) {

	        	 	 TreeItem<IFactType> factTreeItem = tv_input_objects.getSelectionModel().getSelectedItem();
	        	 	int counter = 0;
	        	 	 for(TreeItem<IFactType> tr : tv_input_objects.getRoot().getChildren()){
	        	 		if(tr == factTreeItem ){
	        	 			counter += 1;
	        	 		}
	        	 	}
	        	 	 if(counter == 0){
	               
	                IFactType fact = factTreeItem.getValue();	
	               	Dragboard db = tv_input_objects.startDragAndDrop(TransferMode.ANY);
	                ClipboardContent content = new ClipboardContent();
	                content.put(factDataFormat, fact);
	                db.setContent(content);
	        	 	 }
	         }
	    });
		
		lv_actions.setOnDragDetected((event) ->{
			Dragboard db = lv_actions.startDragAndDrop(TransferMode.ANY);
            ActionClient act = lv_actions.getSelectionModel().getSelectedItem();
             ClipboardContent content = new ClipboardContent();
             content.put(actionDataFormat, act);
             db.setContent(content);
		});
		
		tv_templates.setOnDragDetected((event) ->{
			Dragboard db = tv_templates.startDragAndDrop(TransferMode.ANY);
            Template  temp  = tv_templates.getSelectionModel().getSelectedItem().getValue();
             ClipboardContent content = new ClipboardContent();
             content.put(templateDataFormat, temp);
             db.setContent(content);
		});
		
		lv_input_time.setOnDragDetected((event) ->{
			Dragboard db = lv_input_time.startDragAndDrop(TransferMode.ANY);
            FactType time = lv_input_time.getSelectionModel().getSelectedItem();
             ClipboardContent content = new ClipboardContent();
             content.put(timeDataFormat, time);
             db.setContent(content);
		});
		
		lv_input_activities.setOnDragDetected((event) ->{
			Dragboard db = lv_input_activities.startDragAndDrop(TransferMode.ANY);
            FactType act = lv_input_activities.getSelectionModel().getSelectedItem();
             ClipboardContent content = new ClipboardContent();
             content.put(activityDataFormat, act);
             db.setContent(content);
		});
		
		lv_userActivities.setOnDragDetected((event) ->{
			Dragboard db = lv_userActivities.startDragAndDrop(TransferMode.ANY);
            FactType act = lv_userActivities.getSelectionModel().getSelectedItem();
             ClipboardContent content = new ClipboardContent();
             content.put(activityDataFormat, act);
             db.setContent(content);
		});
		
		lv_input_persons.setOnDragDetected((event) ->{
			Dragboard db = lv_input_persons.startDragAndDrop(TransferMode.ANY);
            Fact act = lv_input_persons.getSelectionModel().getSelectedItem();
             ClipboardContent content = new ClipboardContent();
             content.put(factDataFormat, act);
             db.setContent(content);
		});
		
		lv_input_locations.setOnDragDetected((event) ->{
			Dragboard db = lv_input_locations.startDragAndDrop(TransferMode.ANY);
            Fact act = lv_input_locations.getSelectionModel().getSelectedItem();
            ClipboardContent content = new ClipboardContent();
             content.put(factDataFormat, act);
             db.setContent(content);
		});
		
		
		
	}
	
	private Callback<ListView<ActionClient>, ListCell<ActionClient>> getCallbackAction(){
		return new Callback<ListView<ActionClient>, ListCell<ActionClient>>(){
       	 
            @Override
            public ListCell<ActionClient> call(ListView<ActionClient> p) {
                 
                ListCell<ActionClient> cell = new ListCell<ActionClient>(){
 
                    @Override
                    protected void updateItem(ActionClient t, boolean bln) {
                        super.updateItem(t, bln);
                        if (t != null) {
                        	 String text = t.getName();
                        	 setText(text);
				           
                        }
                    }
 
                };
                 
                return cell;
            }
		};
	}
	
	private Callback<TreeView<Template>, TreeCell<Template>> getCallbackTemplate(){
		return new Callback<TreeView<Template>, TreeCell<Template>>(){
	       	 
            @Override
            public TreeCell<Template> call(TreeView<Template> p) {
                 
                TreeCell<Template> cell = new TreeCell<Template>(){
 
                    @Override
                    protected void updateItem(Template t, boolean bln) {
                    	//if (t != null) {
                    	super.updateItem(t, bln);
                        if(bln){
                            setText(null);
                             
                        }else{
			    setText(t.getName());
                        }
                      //  }
                        setGraphic(null);
                    }
                };
                return cell;
            }
         };
		
	}
	
	private Callback<ListView<Rule>, ListCell<Rule>> getCallbackRule(){
		return new Callback<ListView<Rule>, ListCell<Rule>>(){
	       	 
            @Override
            public ListCell<Rule> call(ListView<Rule> p) {
                 
                ListCell<Rule> cell = new ListCell<Rule>(){
 
                    @Override
                    protected void updateItem(Rule t, boolean bln) {
                        super.updateItem(t, bln);
                        if (t != null) {
                        	 
				           setText(t.getName());
                        }
                    }
                };
                return cell;
            }
         };
		
	}
	private Callback<TreeView<IFactType>, TreeCell<IFactType>> getCallBackTvObjects(){
		return new Callback<TreeView<IFactType>, TreeCell<IFactType>>(){
       	 
            @Override
            public TreeCell<IFactType> call(TreeView<IFactType> p) {
                 
                TreeCell<IFactType> cell = new TreeCell<IFactType>(){
 
                    @Override
                    protected void updateItem(IFactType t, boolean bln) {
                    	
                        super.updateItem(t, bln);
                        if (t != null) {
                        	if(t instanceof FactType){
                                    
                        		FactType type = (FactType) t;
                        		setText(type.getClassName());

                        	}else{
                                    if(t instanceof Fact){
                                        setText(((Fact)t).getUriValue());
                                    }
                                }
                    
                        }
                    }
 
                };
                 
                return cell;
            }
		};
        
	}
	
	
	private Callback<ListView<Fact>, ListCell<Fact>> getCallbackFact(){
		return new Callback<ListView<Fact>, ListCell<Fact>>(){
       	 
            @Override
            public ListCell<Fact> call(ListView<Fact> p) {
                 
                ListCell<Fact> cell = new ListCell<Fact>(){
 
                    @Override
                    protected void updateItem(Fact t, boolean bln) {
                        super.updateItem(t, bln);
                        if (t != null) {
                        	 setText(t.getUriValue());
			}
			            	  
                        
                    }
 
                };
                 
                return cell;
            }
        };
	}
	
	
	
	private Callback<ListView<FactType>, ListCell<FactType>> getCallbackEvent(){
		return new Callback<ListView<FactType>, ListCell<FactType>>(){
       	 
            @Override
            public ListCell<FactType> call(ListView<FactType> p) {
                 
                ListCell<FactType> cell = new ListCell<FactType>(){
                	 final Tooltip tooltip = new Tooltip();
                    @Override
                    protected void updateItem(FactType t, boolean bln) {
                        super.updateItem(t, bln);
                        if (t != null) {
                           
                        	 setText(t.getClassName());
                        	 ArrayList<String> varList = t.getVarList();
                        	 String varFormat = t.getVarFormat();
                        	 Field fi;

                        	 String text ="";
						
								if(!varList.isEmpty()){
								
                                                                    for(String st: varList){
                                                                            text += ", " + st;
                                                                    }
								}else{
									if(!varFormat.equals("")){
										text = varFormat;
									}
								}
                        	 
                        	 tooltip.setText(text);
                        	 setTooltip(tooltip);
							
                        }
                    }
 
                };
                 
                return cell;
            }
        };
	}

	

	
	private boolean checkInputsComplete(){ // + check fields activities! TODO
		
                if(tf_output != null && tf_output.getText().equals("")){
                    System.out.println(" check Input compl in 1ste if" );
                    return true;
                }
            
		for(CmtLabel lab : inputLabels){
                    System.out.println(" in labels " + lab.getObj().getClass().getSimpleName());
                    IFactType fact = lab.getObj();
                    
                    
                    if(!lab.isFilled){
                        if(fact instanceof EventInput){
                            EventInput input = (EventInput) fact;
                            for(FieldValueLimitation lim : input.getLimitations()){
                                System.out.println(" in check field " + lim.getValue());
                                if(lim.getValue() != null && lim.getValue().equals("")){
                                    return true;
                                }
                            }
                        }
//				// check tf & box
//				if(lab.getTf()!=null){
//					String text_tf = lab.getTf().getText();
//					if(text_tf.equals(lab.getFormat())){
//						isNotComplete = true;
//					}else{lab.isFilled = true;}
//				}else{
//					if(lab.getBox()!=null){
//						String selectedItem = (String)lab.getBox().getSelectionModel().getSelectedItem();
//						if(selectedItem.equals("Any")){
//							isNotComplete = true;
//						}else{lab.isFilled = true;}
//					}else{
//						isNotComplete = true;
//					}
//				}
				
			}
		}
		return false;
	}
	
	private void clearScreen(){
		tempOnScreen = false;
		 vb_holdingTemp = null;
		 ap_workbench.getChildren().clear();
		 amountActions =1;
		 amountInput =0;
		 setDefaultHeader();
		 currentTemp = null;
		 inputLabels.clear();
		 
		 lv_input_persons.setDisable(false);
		 lv_input_locations.setDisable(false);
		 tv_input_objects.setDisable(false);
		 lv_actions.setDisable(false);
                  mapFacts = new HashMap<>();
                //mapFields = new HashMap<>();
	}
	
	@FXML
	public void onSave(){
		boolean isNotComplete = checkInputsComplete();
		 System.out.println(" is compl " + isNotComplete);
		if(TemplateHA.class.isAssignableFrom(currentTemp.getClass())){
                    
                   
			if(!isNotComplete ){
			
                                 TemplateHA template = (TemplateHA) currentTemp;
				OutputHA output = template.getOutput();
                                 if(tf_output != null){
                                     output.setName(tf_output.getText());
                                 }
				
				
				System.out.println(output.getBindings().size());
                                // set binding values lhs 
                                LinkedList<IFBlock> ifblocks = template.getIfBlocks();
                                for(IFBlock ifblock: ifblocks){
                                    LinkedList<Binding> bindings = ifblock.getBindings();
                                    System.out.println(" type " + ifblock.getType());
                                    for(Binding binding : bindings){
                                        if(binding.getEndBinding() instanceof BindingInputFact){
                                            ((BindingInputFact) binding.getEndBinding()).setInputObject(mapFacts.get(binding.getEndBinding().getIndexObj()));
                                            
                                        }else{
                                             ((BindingInputField) binding.getEndBinding()).setInputObject(mapFacts.get(binding.getEndBinding().getIndexObj()));
                                        }
                                    }
                                }
                                
				CMTClient.createNewActivity(template);
                                clearScreen();
				
			}else{
				
				final JPanel panel = new JPanel();

			    JOptionPane.showMessageDialog(panel, "Still input blocks or activity name missing", "Error", JOptionPane.ERROR_MESSAGE);
			
				}
			
		}else{
			// rule template
                    System.out.println(" --- " + currentTemp.getClass());
                    System.out.println("in else " + TemplateActions.class.isAssignableFrom(currentTemp.getClass()));
			if(TemplateActions.class.isAssignableFrom(currentTemp.getClass())){
                            System.out.println("in save ok 1" + (!isNotComplete) + !tf_ruleName.getText().equals(""));
                            
			if(!isNotComplete && !tf_ruleName.getText().equals("")){
                            System.out.println("in save ok ");
                            ((TemplateActions)currentTemp).setRuleName(tf_ruleName.getText());
                            if(!currentTemp.getName().equals("AND Template")){
                            LinkedList<IFBlock> ifblocks = currentTemp.getIfBlocks();
                                for(IFBlock ifblock: ifblocks){
                                    LinkedList<Binding> bindings = ifblock.getBindings();
                                    System.out.println(" type " + ifblock.getType());
                                    for(Binding binding : bindings){
                                        if(binding.getEndBinding() instanceof BindingInputFact){
                                            ((BindingInputFact) binding.getEndBinding()).setInputObject(mapFacts.get(binding.getEndBinding().getIndexObj()));
                                            
                                        }else{
                                             ((BindingInputField) binding.getEndBinding()).setInputObject(mapFacts.get(binding.getEndBinding().getIndexObj()));
                                        }
                                    }
                                }
                            }
                            
				CMTClient.createNewRule((TemplateActions)currentTemp);
                                clearScreen();
								 
			}else{
				final JPanel panel = new JPanel();

			    JOptionPane.showMessageDialog(panel, "Still input blocks or rule name missing", "Error", JOptionPane.ERROR_MESSAGE);
			}	
                        }
		}
		
	}
	
	@FXML
	public void onCancel(){
		
		clearScreen();

		
	}

    @Override
    public void newFactTypeAdded(FactType newFactType) {
        if(!(newFactType.getClassName().equals("Person")|| newFactType.getClassName().equals("Location"))){
        
        Platform.runLater(new Runnable() {
	    @Override
	    public void run() {
        ti_root_objects.getChildren().add(new TreeItem<IFactType>(newFactType));
        tv_input_objects.setRoot(ti_root_objects);
                }});
        }
    }

    @Override
    public void newEventTypeAdded(FactType newEventType) {
        Platform.runLater(new Runnable() {
	    @Override
	    public void run() {
        if(newEventType.getType().equals("activity")){
            if(newEventType.isIsCustom()){
                items_lv_userActivities.add(newEventType);
		lv_userActivities.setItems(items_lv_userActivities);
            }else{
                items_input_activities.add(newEventType);
		lv_input_activities.setItems(items_input_activities);
            }
        }else{
            items_input_time.add(newEventType);
            lv_input_time.setItems(items_input_time);
        }
                }});
    }

    @Override
    public void newFunctionAdded(Function newFunction) {
        // not relevant
    }

    @Override
    public void newTemplateHAAdded(TemplateHA temp) {
        System.out.println("-------- new templ userttyab added!!!  " + temp.getName());
        Platform.runLater(new Runnable() {
	    @Override
	    public void run() {
                populateListTemplates();
//        Iterator<TreeItem<Template>> it = ti_root_temp.getChildren().iterator();
//                    while(it.hasNext()){
//                        TreeItem<Template> node = it.next();
//                        Template typeInTree = (Template) node.getValue();
//                        if(typeInTree.getName().equals("For Activities")){
//                            node.getChildren().add(new TreeItem<Template>(temp));		
//                        }
//                    }
                }});
    }

    @Override
    public void newTemplateActionsAdded(TemplateActions temp) {
        Platform.runLater(new Runnable() {
	    @Override
	    public void run() {
                populateListTemplates();
                }});
    }

    @Override
    public void newFactAdded(Fact newFact) {
        Platform.runLater(new Runnable() {
	        @Override
	        public void run() {
                    if(newFact.getClassName().equals("Person")){
			items_input_persons.add(newFact);
			lv_input_persons.setItems(items_input_persons);
                    }else{
			if(newFact.getClassName().equals("Location")){
				items_input_locations.add(newFact);
				lv_input_locations.setItems(items_input_locations);
			}else{
                    
                    Iterator<TreeItem<IFactType>> it = ti_root_objects.getChildren().iterator();
                    while(it.hasNext()){
                        TreeItem<IFactType> node = it.next();
                        IFactType typeInTree = (IFactType) node.getValue();
                        if(typeInTree instanceof FactType){
                            FactType t = (FactType) typeInTree;
                            if(newFact.getClassName().equals(t.getClassName())){
                            node.getChildren().add(new TreeItem<IFactType>(newFact));		
                            }
                        }
                    }
                }
		
	   }
                }
		});
    }

    @Override
    public void newEventAdded(be.ac.vub.wise.cmtclient.blocks.Event newEvent) {
        // not relevant
    }

    @Override
    public void newRuleAdded(Rule newRule) {
        Platform.runLater(new Runnable() {
	    @Override
	    public void run() {
        items_lv_myRules.add(newRule);
	lv_myRules.setItems(items_lv_myRules);
                }});
    }

    @Override
    public void actionInvoked(ActionClient action) {
        System.out.println(" --- print action " + action.getName());
    }

    @Override
    public void actionAdded(ActionClient action) {
        Platform.runLater(new Runnable() {
	    @Override
	    public void run() {
        items_lv_actions.add(action);
	lv_actions.setItems(items_lv_actions);
                }});
    }

    @Override
    public void currentContext(be.ac.vub.wise.cmtclient.blocks.Event currentEvent) {
        // not relevant
    }
	
	
	
}
