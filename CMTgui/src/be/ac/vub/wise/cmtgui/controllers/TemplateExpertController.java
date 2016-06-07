package be.ac.vub.wise.cmtgui.controllers;

import be.ac.vub.wise.cmtclient.blocks.ActionClient;
import be.ac.vub.wise.cmtclient.blocks.ActionField;
import be.ac.vub.wise.cmtclient.blocks.Binding;
import be.ac.vub.wise.cmtclient.blocks.BindingIF;
import be.ac.vub.wise.cmtclient.blocks.BindingInfo;
import be.ac.vub.wise.cmtclient.blocks.BindingInputFact;
import be.ac.vub.wise.cmtclient.blocks.BindingInputField;
import be.ac.vub.wise.cmtclient.blocks.BindingOutput;
import be.ac.vub.wise.cmtclient.blocks.CMTField;
import be.ac.vub.wise.cmtclient.blocks.CMTParameter;
import be.ac.vub.wise.cmtclient.blocks.Event;
import be.ac.vub.wise.cmtclient.blocks.EventInput;
import be.ac.vub.wise.cmtclient.blocks.Fact;
import be.ac.vub.wise.cmtclient.blocks.FactType;
import be.ac.vub.wise.cmtclient.blocks.FieldValueLimitation;
import be.ac.vub.wise.cmtclient.blocks.Function;
import be.ac.vub.wise.cmtclient.blocks.IFBlock;
import be.ac.vub.wise.cmtclient.blocks.IFactType;
import be.ac.vub.wise.cmtclient.blocks.Operator;
import be.ac.vub.wise.cmtclient.blocks.OutputHA;
import be.ac.vub.wise.cmtclient.blocks.Template;
import be.ac.vub.wise.cmtclient.blocks.TemplateActions;
import be.ac.vub.wise.cmtclient.blocks.TemplateHA;
import be.ac.vub.wise.cmtclient.core.CMTClient;
import be.ac.vub.wise.cmtclient.util.Constants;
import be.ac.vub.wise.cmtclient.util.ConverterCoreBlocks;
import be.ac.vub.wise.cmtgui.util.ConstantsGUI;
import be.ac.vub.wise.cmtgui.util.LineYs;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.rmi.RemoteException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.DragEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;

import be.ac.vub.wise.cmtgui.views.CmtCircle;
import be.ac.vub.wise.cmtgui.views.CmtLine;
import be.ac.vub.wise.cmtgui.views.VBoxAction;
import be.ac.vub.wise.cmtgui.views.VBoxIFBlock;
import be.ac.vub.wise.cmtgui.util.PositionCmtCircle;
import be.ac.vub.wise.cmtgui.util.PositionInMakeTemplate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.EventHandler;
import javafx.scene.control.TreeItem;
import javafx.scene.input.MouseEvent;

public class TemplateExpertController  {

    @FXML private VBox mainVb;
    @FXML private VBox vb1_functions;
    @FXML private VBox vb2_input;
    @FXML private VBox vb3_output;

    @FXML private Label lb1_addFunction;
    @FXML private Label lb2_addInput;
    private Label lb3_addOutput;
    @FXML private RadioButton rb_activity;
    @FXML private RadioButton rb_rule;	
    @FXML private ToggleGroup tempGroup;
    @FXML private TextField tf_nameTemplate;	 
    private Template template;
    BooleanProperty dragging = new SimpleBooleanProperty();
    Parent root;
    Pane pane ;
    public OutputHA outputHA;
    private LinkedList<CmtLine> lines;
    private LinkedList<CmtCircle> circles;
    public CmtLine line = new CmtLine();
    private boolean isRuleTemplate = false;
    public CmtCircle crS;
    public CmtCircle crT;
    public VBox vb_outputHa;
    HashMap<Integer,ArrayList<Integer>> okIndexsIFBlock;
    HashMap<Integer,ArrayList<Integer>> okIndexsInputLeft;
    HashMap<Integer,ArrayList<Integer>> okIndexsInputRight;
    
    
    public TemplateExpertController(){
	FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(ConstantsGUI.PATHFXML+"templateExpert.fxml"));
    	fxmlLoader.setController(this);
        try {
            root = fxmlLoader.load();
            root.getStylesheets().add(ConstantsGUI.PATHCSS+"expertTemplateStyle.css");
	} catch (IOException e) {
            e.printStackTrace();
	}
        
        template = new Template();
        lines = new LinkedList<CmtLine>();
        circles = new LinkedList<CmtCircle>();
        okIndexsIFBlock = new HashMap<Integer,ArrayList<Integer>>();
        tf_nameTemplate.setPromptText("Enter a name");
    }
	
    public Parent getRoot(){
	return root;
    }
	
    public void setRootScene(Parent parent){
	if(parent instanceof Pane)
            this.pane = (Pane)parent;
    }
	
    public void addDnd(){
	initActTemplate();
	tempGroup.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
            @Override
	    public void changed(ObservableValue<? extends Toggle> ov, Toggle t, Toggle t1) {
	        RadioButton checkToggle = (RadioButton)t1.getToggleGroup().getSelectedToggle(); // Cast object to radio button
	        if(checkToggle.getText().equals(rb_activity.getText())){
                    isRuleTemplate = false;
	            vb3_output.getChildren().clear();
	            initActTemplate();
	        }else{
                    isRuleTemplate = true;
	            vb3_output.getChildren().clear();
	            initRuleTemplate();
                }
	    }
	});	
	lb1_addFunction.setOnDragOver((event) -> {
            if(event.getDragboard().hasContent(ConstantsGUI.functionDataFormat) || event.getDragboard().hasContent(ConstantsGUI.eventDataFormat)){
            	event.acceptTransferModes(TransferMode.ANY);
            }
	});	
	lb1_addFunction.setOnDragDropped((event) -> {
            event.acceptTransferModes(TransferMode.ANY);
            if(event.getDragboard().hasContent(ConstantsGUI.functionDataFormat)){
		Function function = CMTClient.getFunctionWithName((String)event.getDragboard().getContent(ConstantsGUI.functionDataFormat));
                VBox vb_function = createLogicFunctionBox(function);
                int size = vb1_functions.getChildren().size();			
		if(vb1_functions.getChildren().size()>=1){
                    Pane paneVB2 = new Pane();
                    paneVB2.setMinHeight(5);
                    paneVB2.setPrefHeight(5);
                    vb1_functions.getChildren().add(size-1,paneVB2);
                    vb1_functions.getChildren().add(size-1, vb_function);
                }else{
                    vb1_functions.getChildren().add(size-1, vb_function);
		}
            }else{
		if(event.getDragboard().hasContent(ConstantsGUI.eventDataFormat)){ // draw input block event
                    IFactType factType= (IFactType)event.getDragboard().getContent(ConstantsGUI.eventDataFormat);
                    FactType factTypeEvent = (FactType) factType;
                    VBox vb_event = createEventLogicBox(factType);
                    int size = vb1_functions.getChildren().size();
                    if(vb1_functions.getChildren().size()>=1){
			Pane paneVB2 = new Pane();
			paneVB2.setMinHeight(5);
			paneVB2.setPrefHeight(5);
                        vb1_functions.getChildren().add(size-1,paneVB2);
        		vb1_functions.getChildren().add(size-1, vb_event);
                    }else{
			vb1_functions.getChildren().add(size-1, vb_event);
                    }
                    VBox vb_input = createEventInputBox(factType);
                    int size2 = vb2_input.getChildren().size();
                    if(vb2_input.getChildren().size()>=1){
			Pane paneVB2 = new Pane();
                        paneVB2.setMinHeight(5);
			paneVB2.setPrefHeight(5);
			vb2_input.getChildren().add(size2-1,paneVB2);
			vb2_input.getChildren().add(size2-1, vb_input);
                    }else{				
			vb2_input.getChildren().add(size2-1, vb_input);
                    }
                    // draw lines
                    // 
                    BindingInfo info = new BindingInfo();
        	info.setBinding1(factTypeEvent);
        	info.setParameter1("");
                    info.setParameter2("");
                    info.setParameterType2(factTypeEvent.getClassName());
                    info.setParameterType1(factTypeEvent.getClassName());
        	
        	info.setBinding2(factTypeEvent);
                // draw line
                System.out.println(" in start draw line ---------------------------- ");
                line = new CmtLine();
       		line.setInfo(info);        
       		line.setIndexInFunction(vb1_functions.getChildren().size()-3);
       		line.setIndexCrS(0);
        	line.setIndexInInput(vb2_input.getChildren().size()-3);
        	line.setIndexCrT(0);

                   
                        
                        drawLineLaterEventDrop();

                      
                    
		}
            }
        });

	lb2_addInput.setOnDragOver((event) -> {
            if(event.getDragboard().hasContent(ConstantsGUI.inputDataFormat) || event.getDragboard().hasContent(ConstantsGUI.eventDataFormat) ){
            		event.acceptTransferModes(TransferMode.ANY);
            }
	});
		
	lb2_addInput.setOnDragDropped((event) -> {
            if(event.getDragboard().hasContent(ConstantsGUI.inputDataFormat)){
		IFactType factType= (IFactType)event.getDragboard().getContent(ConstantsGUI.inputDataFormat);	
                
                System.out.println("--------------------- in drop facttype "  + ((FactType)factType).getFields().get(0).getSql_id());
		VBox vb_input = createFactInputBox(factType);
                System.out.println("--------------------- in drop" + vb_input);
		int size = vb2_input.getChildren().size();
		if(vb2_input.getChildren().size()>=1){
                    Pane paneVB2 = new Pane();
                    paneVB2.setMinHeight(5);
                    paneVB2.setPrefHeight(5);
                    vb2_input.getChildren().add(size-1,paneVB2);
                    vb2_input.getChildren().add(size-1, vb_input);
		}else{			
                    vb2_input.getChildren().add(size-1, vb_input);
		}
            }else{
		if(event.getDragboard().hasContent(ConstantsGUI.eventDataFormat)){
                    IFactType eventType= (IFactType)event.getDragboard().getContent(ConstantsGUI.eventDataFormat);
                    FactType factTypeEvent = (FactType) eventType;
                    VBox vb_input = createEventInputBox(eventType);
                    int size = vb2_input.getChildren().size();
                    if(vb2_input.getChildren().size()>=1){
			Pane paneVB2 = new Pane();
                        paneVB2.setMinHeight(5);
			paneVB2.setPrefHeight(5);
			vb2_input.getChildren().add(size-1,paneVB2);
			vb2_input.getChildren().add(size-1, vb_input);
                    }else{				
			vb2_input.getChildren().add(size-1, vb_input);
                    }
                    
                    VBox vb_event = createEventLogicBox(eventType);
                    int size2 = vb1_functions.getChildren().size();
                    if(vb1_functions.getChildren().size()>=1){
			Pane paneVB2 = new Pane();
			paneVB2.setMinHeight(5);
			paneVB2.setPrefHeight(5);
                        vb1_functions.getChildren().add(size2-1,paneVB2);
        		vb1_functions.getChildren().add(size2-1, vb_event);
                    }else{
			vb1_functions.getChildren().add(size2-1, vb_event);
                    }
                    
                    // draw lines
                    // 
                    BindingInfo info = new BindingInfo();
        	info.setBinding1(factTypeEvent);
        	info.setParameter1("");
                    info.setParameter2("");
                    info.setParameterType2(factTypeEvent.getClassName());
                    info.setParameterType1(factTypeEvent.getClassName());
        	
        	info.setBinding2(factTypeEvent);
                // draw line
                System.out.println(" in start draw line ---------------------------- ");
                line = new CmtLine();
       		line.setInfo(info);        
       		line.setIndexInFunction(vb1_functions.getChildren().size()-3);
       		line.setIndexCrS(0);
        	line.setIndexInInput(vb2_input.getChildren().size()-3);
        	line.setIndexCrT(0);

                   
                        
                        drawLineLaterEventDrop();
                    
                    
                    
                }
            }
	});
	
	mainVb.setOnMousePressed((event) -> {	
            LinkedList<CmtCircle> copyCircles = new LinkedList<CmtCircle>();
            for(CmtCircle circle : circles){
		copyCircles.add(circle);
            }
            // check if in circle
            for(CmtCircle cr : copyCircles){
		if(checkPointInCircle(cr, new Point2D(event.getSceneX(),event.getSceneY()))){
                    // make line to drag    
                    Point2D centerCr = cr.localToScene(new Point2D(cr.getCenterX(),cr.getCenterY()));
                    line = new CmtLine();
                    line.setStartX(centerCr.getX());
	            line.setStartY(centerCr.getY());
	            line.setEndX(event.getSceneX());
	            line.setEndY(event.getSceneY());
	            line.setIndexInFunction(cr.getIndexFunction());
	            BindingInfo info = new BindingInfo();
	            info.setBinding1(cr.getObj());
	            info.setParameter1(cr.getParameter());
	            info.setParameterType1(cr.getTypeOfParameter());
	            line.setInfo(info);
	            line.setIndexCrS(cr.getIndexOfCrInVB());
	            pane.getChildren().add(line);      
	            dragging.set(true);
                    // enable/disab other circles
	            if(cr.getPosition() == PositionCmtCircle.Function){        	
	                for(CmtCircle circle : circles){
                            if(circle.getPosition() == PositionCmtCircle.InputLeft && cr.getTypeOfParameter().equals(circle.getTypeOfParameter())){
                                circle.setEnabled(true);		 
	                    }else{
                                circle.setEnabled(false);
	                    }
	                }
                    }else{
                        if(cr.getPosition() == PositionCmtCircle.InputLeft){
                            for(CmtCircle circle : circles){
                                if(circle.getPosition() == PositionCmtCircle.Function && cr.getTypeOfParameter().equals(circle.getTypeOfParameter())){
                                    circle.setEnabled(true);
		                }else{
                                    circle.setEnabled(false);
		                }
		            } 
	                }else{
                            if(cr.getPosition() == PositionCmtCircle.InputRight){
                                for(CmtCircle circle : circles){
                                    if(circle.getPosition() == PositionCmtCircle.Output && cr.getTypeOfParameter().equals(circle.getTypeOfParameter())){
                                        circle.setEnabled(true);
			            }else{
                                        circle.setEnabled(false);
		                    }
			        }
                            }
	                }
	            }
	            cr.setDefaultColor();     
                }
            }				
	});
		
	mainVb.setOnMouseDragged((event) ->{
            if(dragging.get()){
		line.setEndX(event.getSceneX());
                line.setEndY(event.getSceneY());
            }
	});
		
	mainVb.setOnMouseReleased((event) ->{
            if(dragging.get()){			
		LinkedList<CmtCircle> copyCircles = new LinkedList<CmtCircle>();
		for(CmtCircle circle : circles){
                    copyCircles.add(circle);			
		}
		CmtCircle okcircle = null;
		for(CmtCircle cr : copyCircles){			
                    if(checkPointInCircle(cr, new Point2D(event.getSceneX(),event.getSceneY())) && cr.getEnabled()){				
			okcircle = cr;
                    }
                    cr.setDefaultColor();			
                }
		if(okcircle != null){
                    Point2D centerCr = okcircle.localToScene(new Point2D(okcircle.getCenterX(),okcircle.getCenterY()));
                    line.setEndX(centerCr.getX());
                    line.setEndY(centerCr.getY());
                    line.setIndexInInput(okcircle.getIndexInput());
                    line.getInfo().setBinding2(okcircle.getObj());
                    line.getInfo().setParameter2(okcircle.getParameter());
                    line.getInfo().setParameterType2(okcircle.getTypeOfParameter());
                    line.setIndexCrT(okcircle.getIndexOfCrInVB());
                    lines.add(line);
		}else{
                    pane.getChildren().remove(line);
		}
		dragging.set(false);
            }
	});	
    }
    
    private void drawLineLaterEventDrop(){
        Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        HBox headerS = (HBox)((VBoxIFBlock)(vb1_functions.getChildren().get(vb1_functions.getChildren().size()-3))).getChildren().get(0);  
                        Node nodeS = headerS.getChildren().get(headerS.getChildren().size()-1);
                        CmtCircle crSInput = null;
                        CmtCircle crT = null;
                        if(nodeS.getClass().getSimpleName().equals("CmtCircle")){
                            crSInput = (CmtCircle) nodeS;     
                        }

                        HBox headerT = (HBox)((VBox)(vb2_input.getChildren().get(vb2_input.getChildren().size()-3))).getChildren().get(0);
                        Node nodeT = headerT.getChildren().get(1);
                        if(nodeT.getClass().getSimpleName().equals("CmtCircle")){
                            crT = (CmtCircle) nodeT;
                        }
                        if(crSInput != null && crT !=null){    
                        if(crSInput.localToScene(crSInput.getCenterX(), crSInput.getCenterY()).getX() < 350){
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                        
                                    drawLineLaterEventDrop();

                            }});
                        }else{
                            DoubleProperty propEndX = new SimpleDoubleProperty(crT.localToScene(crT.getCenterX(), crT.getCenterY()).getX());
                            DoubleProperty propEndY = new SimpleDoubleProperty(crT.localToScene(crT.getCenterX(), crT.getCenterY()).getY());
                            DoubleProperty propstartX = new SimpleDoubleProperty(crSInput.localToScene(crSInput.getCenterX(), crSInput.getCenterY()).getX());
                            DoubleProperty propstartY = new SimpleDoubleProperty(crSInput.localToScene(crSInput.getCenterX(), crSInput.getCenterY()).getY());
                            line.endXProperty().bind(propEndX);
                            line.endYProperty().bind(propEndY);
                            line.startXProperty().bind(propstartX);
                            line.startYProperty().bind(propstartY);
                            pane.getChildren().add(line);
			lines.add(line);
                           //line(start, endOriginal);
                        }
                        }
                         }});
    }
    
    private void redrawLines(PositionInMakeTemplate posInTemp, int index,int indexIFEvent, HashMap<Integer, HashMap<Integer,LineYs>> lineYsRight, HashMap<Integer, HashMap<Integer,LineYs>> lineYsLeft ){
        switch (posInTemp){
            case Function:
                redrawLinesFunction(index, lineYsRight);
                break;
            case Input:
                redrawLinesInput(indexIFEvent , index, lineYsRight, lineYsLeft);
                break;
            case Output:
                break;
        }
    
    }
    private void redrawLinesInput(int indexEventIFSide, int index,HashMap<Integer, HashMap<Integer,LineYs>> lineYsRight, HashMap<Integer, HashMap<Integer,LineYs>> lineYsLeft ){
       Platform.runLater(new Runnable() {
        @Override
        public void run() {
     //       try{
           int counterok =0; // tellen of elke linksre en rechtse index hertekent is.
           for(CmtLine li : lines){
               if(li.getIndexInInput()>index && li.getIndexInOutput()>-1){
                   //dan rechtse lijn
                   if(!okIndexsInputRight.containsKey(li.getIndexInInput()-2)){
                       System.out.println(" in not in arr list " + counterok);
                       counterok +=1;
                   }else{
                       ArrayList<Integer> vals = okIndexsInputRight.get(li.getIndexInFunction()-2);
                       if(!vals.contains(li.getIndexCrS())){
                           counterok +=1;
                       }
                   }
               }else{
                   if(li.getIndexInInput()>index && li.getIndexInFunction()>-1){
                   //dan linkse lijn
                   if(!okIndexsInputLeft.containsKey(li.getIndexInInput()-2)){
                       System.out.println(" in not in arr list " + counterok);
                       counterok +=1;
                   }else{
                       ArrayList<Integer> vals = okIndexsInputLeft.get(li.getIndexInFunction()-2);
                       if(!vals.contains(li.getIndexCrT())){
                           counterok +=1;
                       }
                   }
               }
           
           }
           }
           if(counterok >0 && lines.size()>0){
              
            for(int i =0; i<lines.size();i++){
                CmtLine cmtLine = lines.get(i);
                System.out.println(" --- lines size " + lines.size());
                
                if(cmtLine.getIndexInInput()>index && cmtLine.getIndexInFunction()>-1){
                    
                    System.out.println("--------------------------------------- Input  indeex in fnc " + cmtLine.getIndexInFunction() + "  " + i);
                    // redraw lines Vbox als field line -is 
                    VBox inbl=null;
                    if(vb1_functions.getChildren().get(cmtLine.getIndexInFunction()) instanceof Label){
                        inbl = ((VBox)(vb1_functions.getChildren().get(cmtLine.getIndexInFunction()-2)));
                    }else{
                        inbl = ((VBox)(vb1_functions.getChildren().get(cmtLine.getIndexInFunction())));
                    }
                    HBox crHbox;
                    System.out.println(" ------------------------ index crS " + cmtLine.getIndexCrS());
                    if(cmtLine.getIndexCrS()>0){
                        VBox vb = (VBox) inbl.getChildren().get(1);
                        crHbox = (HBox) vb.getChildren().get(cmtLine.getIndexCrS()-1);
                    }else{
                        crHbox = (HBox)inbl.getChildren().get(0);  
                    }       
                    Node nodeS = crHbox.getChildren().get(crHbox.getChildren().size()-1);
                            CmtCircle crSInput = null;
                            CmtCircle crTar = null;
                            if(nodeS.getClass().getSimpleName().equals("CmtCircle")){
                                crSInput = (CmtCircle) nodeS;     
                            }
                            
                            VBox inputVbox = (VBox)(vb2_input.getChildren().get(cmtLine.getIndexInInput()-2));
                            
                            HBox crTHbox;
                            System.out.println(" ------------------------ index crT " + cmtLine.getIndexCrT());
                    if(cmtLine.getIndexCrT()>0){
                        VBox vb2 = (VBox) inputVbox.getChildren().get(1);
                        crTHbox = (HBox) vb2.getChildren().get(cmtLine.getIndexCrT()-1);
                    }else{
                        crTHbox = (HBox)inputVbox.getChildren().get(0);  
                    }       
                            
                            
                            Node nodeT = crTHbox.getChildren().get(1);
                            if(nodeT.getClass().getSimpleName().equals("CmtCircle")){
                                crTar = (CmtCircle) nodeT;
                            }
                            
                            if(crTar.localToScene(crTar.centerXProperty().doubleValue(), crTar.centerYProperty().doubleValue()).getY() >= lineYsLeft.get(cmtLine.getIndexInInput()).get(cmtLine.getIndexCrT()).getEndY()){
                                System.out.println(" --- ok ");
                                Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    System.out.println(" --- ok ----------------------------------------------------------------------------");
                                    redrawLinesInput(indexEventIFSide,index, lineYsRight, lineYsLeft);

                            }});
                            }else{
                                cmtLine.setIndexInInput(cmtLine.getIndexInInput()-2);
                                redrawLinesSetPoints(cmtLine, crSInput, crTar);
                                if(okIndexsInputLeft.containsKey(cmtLine.getIndexInInput())){
                                    okIndexsInputLeft.get(cmtLine.getIndexInInput()).add(cmtLine.getIndexCrT());
                                }else{
                                    ArrayList<Integer> val = new ArrayList<>();
                                    val.add(cmtLine.getIndexCrT());
                                    okIndexsInputLeft.put(cmtLine.getIndexInInput(), val);
                                }
                            
                                
                            }
                }else{
                    if(cmtLine.getIndexInInput()>index && cmtLine.getIndexInOutput()>-1){
                     // System.out.println("--------------------------------------- indeex in fnc " + cmtLine.getIndexInFunction() + "  " + i);
                    // redraw lines Vbox als field line -is 
                    VBox inbl = ((VBox)(vb2_input.getChildren().get(cmtLine.getIndexInInput() -2)));
                    HBox crHbox;
                    System.out.println(" ------------------------ index crS " + cmtLine.getIndexCrS());
                    if(cmtLine.getIndexCrS()>0){
                        VBox vb = (VBox) inbl.getChildren().get(1);
                        crHbox = (HBox) vb.getChildren().get(cmtLine.getIndexCrS()-1);
                    }else{
                        crHbox = (HBox)inbl.getChildren().get(0);  
                    }       
                    Node nodeS = crHbox.getChildren().get(crHbox.getChildren().size()-2);
                            CmtCircle crSInput = null;
                            CmtCircle crTar = null;
                            System.out.println("---------- " + nodeS.getClass());
                            if(nodeS.getClass().getSimpleName().equals("CmtCircle")){
                                crSInput = (CmtCircle) nodeS;     
                            }
                            
//                            VBox inputVbox = (VBox)(vb3_output.getChildren().get(cmtLine.getIndexInOutput()));
//                            
//                            HBox crTHbox;
//                            System.out.println(" ------------------------ index crT " + cmtLine.getIndexCrT());
//                    if(cmtLine.getIndexCrT()>0){
//                        VBox vb2 = (VBox) inputVbox.getChildren().get(1);
//                        crTHbox = (HBox) vb2.getChildren().get(cmtLine.getIndexCrT()-1);
//                    }else{
//                        crTHbox = (HBox)inputVbox.getChildren().get(0);  
//                    }       
//                            
//                            
//                            Node nodeT = crTHbox.getChildren().get(1);
//                            if(nodeT.getClass().getSimpleName().equals("CmtCircle")){
//                                crTar = (CmtCircle) nodeT;
//                            }
//                            
                            System.out.println("----- " +crSInput);
                            if(crSInput.localToScene(crSInput.centerXProperty().doubleValue(), crSInput.centerYProperty().doubleValue()).getY() >= lineYsRight.get(cmtLine.getIndexInInput()).get(cmtLine.getIndexCrS()).getStartY()){
                                System.out.println(" --- ok ");
                                Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    System.out.println(" --- ok ----------------------------------------------------------------------------");
                                    redrawLinesInput(-1,index, lineYsRight, lineYsLeft);

                            }});
                            }else{
                                cmtLine.setIndexInInput(cmtLine.getIndexInInput()-2);
                                redrawLinesSetPoints(cmtLine, crSInput, crTar);
                                if(okIndexsInputRight.containsKey(cmtLine.getIndexInInput())){
                                    okIndexsInputRight.get(cmtLine.getIndexInInput()).add(cmtLine.getIndexCrS());
                                }else{
                                    ArrayList<Integer> val = new ArrayList<>();
                                    val.add(cmtLine.getIndexCrS());
                                    okIndexsInputRight.put(cmtLine.getIndexInInput(), val);
                                }
                            
                                
                            }
                    }
                
                }
                
                
            }
            // 
            int counterok2 =0; // tellen of elke linksre en rechtse index hertekent is.
           for(CmtLine li : lines){
               if(li.getIndexInInput()>index && li.getIndexInOutput()>-1){
                   //dan rechtse lijn
                   if(!okIndexsInputRight.containsKey(li.getIndexInInput()-2)){
                       System.out.println(" in not in arr list " + counterok);
                       counterok2 +=1;
                   }else{
                       ArrayList<Integer> vals = okIndexsInputRight.get(li.getIndexInFunction()-2);
                       if(!vals.contains(li.getIndexCrS())){
                           counterok2 +=1;
                       }
                   }
               }else{
                   if(li.getIndexInInput()>index && li.getIndexInFunction()>-1){
                   //dan linkse lijn
                   if(!okIndexsInputLeft.containsKey(li.getIndexInInput()-2)){
                       System.out.println(" in not in arr list " + counterok);
                       counterok2 +=1;
                   }else{
                       ArrayList<Integer> vals = okIndexsInputLeft.get(li.getIndexInFunction()-2);
                       if(!vals.contains(li.getIndexCrT())){
                           counterok2 +=1;
                       }
                   }
               }
           
           }
           }
           if(counterok2 == 0 && indexEventIFSide!=-1){
               // if event delete event in IF block
               VBox vb = (VBox)vb1_functions.getChildren().get(indexEventIFSide);
               HBox header = (HBox)vb.getChildren().get(0);
               Button but = (Button)header.getChildren().get(header.getChildren().size()-3);
               but.fire();
           }
           
           
           }else{
               if(lines.size() == 0 && indexEventIFSide !=-1){
               VBox vb = (VBox)vb1_functions.getChildren().get(indexEventIFSide);
               HBox header = (HBox)vb.getChildren().get(0);
               Button but = (Button)header.getChildren().get(header.getChildren().size()-3);
               but.fire();
               }
           }
        /*    } catch (NullPointerException e) {
                for(CmtLine line : lines){
			pane.getChildren().remove(line);
		}
		lines.clear();
                okIndexsIFBlock.clear();
                okIndexsInputLeft.clear();
                okIndexsInputRight.clear();
                final JPanel panel = new JPanel();
                JOptionPane.showMessageDialog(panel, "Sorry something went wrong :( \n Please redraw your connector lines. ", "Error", JOptionPane.ERROR_MESSAGE);
                
            }
           */
         }});
    }
    
    private void redrawLinesFunction(int index, HashMap<Integer, HashMap<Integer, LineYs>>  lineYs){ // index die weggedaan is
        Platform.runLater(new Runnable() {
        @Override
        public void run() {
//            try {
//                Thread.sleep(5000);
//            } catch (InterruptedException ex) {
//                Logger.getLogger(TemplateExpertController.class.getName()).log(Level.SEVERE, null, ex);
//            }
           int counterok =0;
           for(CmtLine li : lines){
               if(li.getIndexInFunction()>index){
                   if(!okIndexsIFBlock.containsKey(li.getIndexInFunction()-2)){
                       System.out.println(" in not in arr list " + counterok);
                       counterok +=1;
                   }else{
                       ArrayList<Integer> vals = okIndexsIFBlock.get(li.getIndexInFunction()-2);
                       if(!vals.contains(li.getIndexCrS())){
                           counterok +=1;
                       }
                   }
               }
           
           }
           if(counterok >0){
               
            for(int i =0; i<lines.size();i++){
                CmtLine cmtLine = lines.get(i);
                System.out.println(" --- lines size " + lines.size());
                if(cmtLine.getIndexInFunction()>index){
                    
                    System.out.println("--------------------------------------- indeex in fnc " + cmtLine.getIndexInFunction() + "  " + i);
                    // redraw lines Vbox als field line -is 
                    VBoxIFBlock ifbl = ((VBoxIFBlock)(vb1_functions.getChildren().get(cmtLine.getIndexInFunction()-2)));
                    HBox crHbox;
                    System.out.println(" ------------------------ index crS " + cmtLine.getIndexCrS());
                    if(cmtLine.getIndexCrS()>0){
                        VBox vb = (VBox) ifbl.getChildren().get(1);
                        crHbox = (HBox) vb.getChildren().get(cmtLine.getIndexCrS()-1);
                    }else{
                        crHbox = (HBox)ifbl.getChildren().get(0);  
                    }       
                    Node nodeS = crHbox.getChildren().get(crHbox.getChildren().size()-1);
                            CmtCircle crSInput = null;
                            CmtCircle crTar = null;
                            if(nodeS.getClass().getSimpleName().equals("CmtCircle")){
                                crSInput = (CmtCircle) nodeS;     
                            }
                            VBox inputVbox = null;
                            if(vb2_input.getChildren().get(cmtLine.getIndexInInput()) instanceof Label){
                                inputVbox = (VBox)(vb2_input.getChildren().get(cmtLine.getIndexInInput()-2));
                            }else{
                             inputVbox = (VBox)(vb2_input.getChildren().get(cmtLine.getIndexInInput()));
                            }
                            HBox crTHbox;
                            System.out.println(" ------------------------ index crT " + cmtLine.getIndexCrT());
                    if(cmtLine.getIndexCrT()>0){
                        VBox vb2 = (VBox) inputVbox.getChildren().get(1);
                        crTHbox = (HBox) vb2.getChildren().get(cmtLine.getIndexCrT()-1);
                    }else{
                        crTHbox = (HBox)inputVbox.getChildren().get(0);  
                    }       
                            
                            
                            Node nodeT = crTHbox.getChildren().get(1);
                            if(nodeT.getClass().getSimpleName().equals("CmtCircle")){
                                crTar = (CmtCircle) nodeT;
                            }
                            try{
                            if(crSInput.localToScene(crSInput.centerXProperty().doubleValue(), crSInput.centerYProperty().doubleValue()).getY() >= lineYs.get(cmtLine.getIndexInFunction()).get(cmtLine.getIndexCrS()).getStartY()){
                                System.out.println(" --- ok ");
                                Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    System.out.println(" --- ok ----------------------------------------------------------------------------");
                                    redrawLinesFunction(index, lineYs);

                            }});
                            }else{
                                cmtLine.setIndexInFunction(cmtLine.getIndexInFunction()-2);
                                redrawLinesSetPoints(cmtLine, crSInput, crTar);
                                if(okIndexsIFBlock.containsKey(cmtLine.getIndexInFunction())){
                                    okIndexsIFBlock.get(cmtLine.getIndexInFunction()).add(cmtLine.getIndexCrS());
                                }else{
                                    ArrayList<Integer> val = new ArrayList<>();
                                    val.add(cmtLine.getIndexCrS());
                                    okIndexsIFBlock.put(cmtLine.getIndexInFunction(), val);
                                }
                            
                                
                            }
                            } catch (NullPointerException e) {
                                System.out.println(" kleine null pointer ");
            }
                }
            }
            
           }
         }});
    }
    
    private void redrawLinesSetPoints(CmtLine cmtLine, CmtCircle crS, CmtCircle crT){
        
                       
                        if(crS != null && crT !=null){    
                        if(crS.localToScene(crS.getCenterX(), crS.getCenterY()).getX() < 350){
                            System.out.println(" in here");
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                        
                                    redrawLinesSetPoints(cmtLine, crS, crT);

                            }});
                        }else{
                            System.out.println(" set points Start " + crS.localToScene(crS.centerXProperty().doubleValue(), crS.centerYProperty().doubleValue()));
                            System.out.println(" set points End" + crT.localToScene(crT.getCenterX(), crT.getCenterY()));
                            DoubleProperty propEndX = new SimpleDoubleProperty(crT.localToScene(crT.getCenterX(), crT.getCenterY()).getX());
                            DoubleProperty propEndY = new SimpleDoubleProperty(crT.localToScene(crT.getCenterX(), crT.getCenterY()).getY());
                            DoubleProperty propstartX = new SimpleDoubleProperty(crS.localToScene(crS.centerXProperty().doubleValue(), crS.centerYProperty().doubleValue()).getX());
                            DoubleProperty propstartY = new SimpleDoubleProperty(crS.localToScene(crS.centerXProperty().doubleValue(), crS.centerYProperty().doubleValue()).getY());
                            cmtLine.endXProperty().bind(propEndX);
                            cmtLine.endYProperty().bind(propEndY);
                            cmtLine.startXProperty().bind(propstartX);
                            cmtLine.startYProperty().bind(propstartY);
                            
                           //line(start, endOriginal);//
                        }
                        }else{
                            if(crS!=null && crT == null){
                                DoubleProperty propstartX = new SimpleDoubleProperty(crS.localToScene(crS.centerXProperty().doubleValue(), crS.centerYProperty().doubleValue()).getX());
                                DoubleProperty propstartY = new SimpleDoubleProperty(crS.localToScene(crS.centerXProperty().doubleValue(), crS.centerYProperty().doubleValue()).getY());
                                cmtLine.startXProperty().bind(propstartX);
                                cmtLine.startYProperty().bind(propstartY);
                            }
                        }
    
    }
    
    private void initActTemplate(){
        if(!isRuleTemplate){
            outputHA = new OutputHA();
            vb_outputHa = new VBox();
            vb_outputHa.setMinWidth(295);
            vb_outputHa.setPrefWidth(295);
            vb_outputHa.getStyleClass().add("boxes");
            HBox hb_header = new HBox();
            hb_header.getStyleClass().add("headerBoxOutputHa");
            TextField tf_name = new TextField();
            tf_name.setPromptText("Enter situation name");
            tf_name.getStyleClass().add("tf_name_Activity");
            hb_header.getChildren().add(tf_name);
            vb_outputHa.getChildren().add(hb_header);	
            vb3_output.getChildren().add(vb_outputHa);
	}	
    }
	
    private void initRuleTemplate(){
	for(CmtLine line : lines){
            if(line.getIndexInOutput() != -1){
                lines.remove(line);
		pane.getChildren().remove(line);
            }
	}
	VBox vb_output = new VBox();
	Label lb_drop = new Label("Drag an action");
	lb_drop.setMinHeight(60);
	lb_drop.setPrefHeight(60);
	lb_drop.setMinWidth(295);
	lb_drop.setPrefWidth(295);
	lb_drop.getStyleClass().add("labInBox");
	lb_drop.setOnDragOver((event)->{
            if(event.getDragboard().hasContent(ConstantsGUI.dragActionToOutputDataFormat) ){
        	event.acceptTransferModes(TransferMode.ANY);
            }
	});
	lb_drop.setOnDragDropped((event)->{
            if(event.getDragboard().hasContent(ConstantsGUI.dragActionToOutputDataFormat) ){
        	event.acceptTransferModes(TransferMode.ANY);
        	ActionClient action = (ActionClient) event.getDragboard().getContent(ConstantsGUI.dragActionToOutputDataFormat);
        	VBoxAction vb_outputRule = new VBoxAction();
        	vb_outputRule.setAction(action);
		vb_outputRule.getStyleClass().add("boxes");
       		HBox header = new HBox();
       		Label type = new Label(action.getName());
       		Button delete = new Button();
       		delete.getStyleClass().add("buttonClose");
        	delete.setOnAction((eventClose)->{
                    vb3_output.getChildren().remove(vb_outputRule);
        	});
        	header.getStyleClass().add("headerBoxOutputHa");
        	type.getStyleClass().add("labInHeaderAction");
        	header.getChildren().add(type);
        	header.getChildren().add(delete);
        	vb_outputRule.getChildren().add(header);
       		// add parameters
        	ArrayList<ActionField> fields = action.getFields();
        	for(ActionField field : fields){
                    HBox hb_par = new HBox();
                    hb_par.setStyle("-fx-alignment:center-left;");
                    Label fieldName = new Label(field.getName());
                    fieldName.getStyleClass().add("labInParTypeHbInputEvent");
                    hb_par.getChildren().add(fieldName);
                    String formatVar = field.getFormat();
                    System.out.println(" field format " + formatVar);
                    if(!(formatVar.equals(""))){					
                        TextField tf_inputVar = new TextField();
                        if(formatVar.equals("String")){
                            tf_inputVar.setPromptText("");
                        }else{
                            tf_inputVar.setPromptText(formatVar);                						}
                            tf_inputVar.setMinWidth(150);
                            tf_inputVar.setPrefWidth(150);
                            hb_par.getChildren().add(tf_inputVar);
                    }else{
                        ArrayList<String> list = field.getVarList();
                	if(!(list.isEmpty())){
                            ComboBox box = getComboBoxInput(list);
                            hb_par.getChildren().add(box);
                	}
                    }	
                    vb_outputRule.getChildren().add(hb_par);
        	}
        	int size = vb3_output.getChildren().size();
		if(size>=1){
                    Pane paneVB2 = new Pane();
                    paneVB2.setMinHeight(5);
                    paneVB2.setPrefHeight(5);
                    vb3_output.getChildren().add(size-1,paneVB2);
                    vb3_output.getChildren().add(size-1, vb_outputRule);
		}else{
                    vb3_output.getChildren().add(size-1, vb_outputRule);
		}
            }
	});	
	vb_output.getChildren().add(lb_drop);
	vb3_output.getChildren().add(vb_output);
    }
	
	
    private Button getToutputButton(boolean isField, String fieldName, String fieldType, IFactType fact, HBox hb_field, int indexVb_input, int indexInVB){
	Button bt_toOutput = new Button();
	bt_toOutput.getStyleClass().add("buttonArrow");
        String factClassName1 ="";
        if(fact != null && fact instanceof FactType){
            factClassName1 = ((FactType)fact).getClassName();
        }else{
            if(fact !=null && fact instanceof Fact){
                factClassName1 = ((Fact)fact).getClassName();
            }
        }

        final String factClassName = factClassName1;
	bt_toOutput.setOnAction((event)->{
            if(!isRuleTemplate){
		HBox hb_parOutput = new HBox();
		hb_parOutput.getStyleClass().add("labInParTypeHbOutput");
		hb_parOutput.setId(""+vb_outputHa.getChildren().size());
		Pane pane3 = new Pane();
		pane3.setMinWidth(5);
		pane3.setPrefWidth(5);
 		CmtCircle crOutput = getCmtCircle(outputHA, PositionCmtCircle.Output);
       		crOutput.setId("#" + outputHA.getClass().getSimpleName() + Integer.toString(vb_outputHa.getChildren().size()-1)); 		
        	crOutput.setIndexOfCrInVB((vb_outputHa.getChildren().size()-1));
        	hb_parOutput.getChildren().add(crOutput);
        	Pane pane4 = new Pane();
		pane4.setMinWidth(7);
		pane4.setPrefWidth(7);
		hb_parOutput.getChildren().add(pane4);
                TextField tf_parName = new TextField();
       		if(isField){
                    tf_parName.setText(fieldName);
        	}else{
                    tf_parName.setText("Enter a label");
        	}
        	tf_parName.getStyleClass().add("tf_name");
       		Label lb_parType = new Label();
       		if(isField){
                    lb_parType.setText(ConverterCoreBlocks.getSimpleNameAll(fieldType));
        	}else{
                    lb_parType.setText(factClassName);
        	}
        	lb_parType.setMinWidth(80);
        	lb_parType.setPrefWidth(80);
        	hb_parOutput.getChildren().add(tf_parName);
        	Pane pane6 = new Pane();
		pane6.setMinWidth(15);
		pane6.setPrefWidth(15);
		hb_parOutput.getChildren().add(pane6);
        	hb_parOutput.getChildren().add(lb_parType);
        	Pane pane7 = new Pane();
		pane7.setMinWidth(46);
		pane7.setPrefWidth(46);
		hb_parOutput.getChildren().add(pane7);
        	Button close = new Button();
        	close.getStyleClass().add("buttonClose");
        	close.setOnAction((eventCl) ->{
                    int index = vb_outputHa.getChildren().indexOf(hb_parOutput);
                    LinkedList<CmtLine> copyLines = new LinkedList<CmtLine>();
                    for(CmtLine lin : lines){
                        copyLines.add(lin);
                    }
                    for(CmtLine lineCmt : copyLines){
                        if(lineCmt.getIndexInOutput() == index){
                            pane.getChildren().remove(lineCmt);
                            lines.remove(lineCmt);
                        }
                        if(lineCmt.getIndexInOutput()> index){
                            lineCmt.setIndexInOutput(lineCmt.getIndexInOutput() - 1) ;
                            lineCmt.setEndY(lineCmt.getEndY()-32);
                        }
                    }
                    vb_outputHa.getChildren().remove(hb_parOutput);
                });
                hb_parOutput.getChildren().add(close);
        	int size = vb_outputHa.getChildren().size();
        	vb_outputHa.getChildren().add(hb_parOutput);
                BindingInfo info = new BindingInfo();
        	info.setBinding1(fact);
        	if(isField){
                    info.setParameter1(fieldName);
                    info.setParameter2(fieldName);
                    info.setParameterType2(fieldType);
                    info.setParameterType1(fieldType);
        	}else{
                    info.setParameter1("");
                    info.setParameter2("");
                    info.setParameterType2(factClassName);
                    info.setParameterType1(factClassName);
        	}
        	info.setBinding2(outputHA);
                // draw line
                System.out.println(" in start draw line ---------------------------- ");
                line = new CmtLine();
       		line.setInfo(info);        
       		line.setIndexInInput(indexVb_input);
       		line.setIndexCrS(indexInVB);
        	line.setIndexInOutput(vb_outputHa.getChildren().size()-1);
        	line.setIndexCrT(vb_outputHa.getChildren().size()-1);
        	Node nodeS;
        	if(isField){
                    nodeS = hb_field.getChildren().get(hb_field.getChildren().size()-2);
        	}else{
                    nodeS = hb_field.getChildren().get(hb_field.getChildren().size()-1);
        	}
        	Point2D start = null;
                System.out.println(" ---------- nodeS " + nodeS.getClass().getSimpleName());
        	if(nodeS.getClass().getSimpleName().equals("CmtCircle")){
                    CmtCircle crSInput = (CmtCircle) nodeS;
                    start = crSInput.localToScene(crSInput.getCenterX(), crSInput.getCenterY());
                    System.out.println(" start " + start);
       		}
       		Node nodeT = pane.getScene().lookup("#" + crOutput.getId());
       		Point2D end = null;
       		if(nodeT.getClass().getSimpleName().equals("CmtCircle")){
                    CmtCircle crT = (CmtCircle) nodeT;
                    String crTId = crT.getId();
                    int sizeVb = vb_outputHa.getChildren().size() ;
                    int multi = 0;
                    if(sizeVb == 2){
        		multi = 60; //49
                    }else{
        		if(sizeVb>2){
                            multi = 60 + ((sizeVb-2) * 32); 
        		}
                    }
                    Point2D endOriginal = crT.localToScene(crT.getCenterX() + 16, crT.getCenterY()+ multi);
                    double endX = (endOriginal.getX() - (endOriginal.getX() -999)) + 16;
                    double endY = (endOriginal.getY()-(endOriginal.getY()-137)) +  multi;
                    end = new Point2D(endX, endY);
       		}
        	line(start, end);
            }
	});		
       
	return bt_toOutput;	
    }
	
	private void line(Point2D start, Point2D end){
            System.out.println("in draw line before if !!! ------------------------------ " + start + "   "+ end);
		if(start != null && end != null){
                    System.out.println("in draw line !!! ------------------------------ ");
			line.setStartX(start.getX());
			line.setStartY(start.getY());
			line.setEndX(end.getX());
			line.setEndY(end.getY());
			pane.getChildren().add(line);
			lines.add(line);
		}
		line = null;
		
	}
	private boolean checkPointInCircle(Circle cr, Point2D pointMouse){
		
		 Point2D centerCr = cr.localToScene(new Point2D(cr.getCenterX(),cr.getCenterY()));
		double x = pointMouse.getX();
		double y = pointMouse.getY();
		double eq = Math.pow((x - centerCr.getX()),2) + Math.pow((y - centerCr.getY()),2) ;
		if(eq < Math.pow(cr.getRadius(), 2)){
			return true;
		}else{
			return false;
		}		
		
		
	}
	
	
	private ComboBox<String> getNewComboBoxOperators(){
		ComboBox<String> combo = new ComboBox<String>();
                /*combo.setOnMousePressed(new EventHandler<MouseEvent>(){
                            @Override
                            public void handle(MouseEvent event) {
                                combo.requestFocus();
                                
                            }
                        });*/
		ObservableList<String> list = FXCollections.observableArrayList();
		list.addAll("AND","OR","NOT");
		combo.setItems(list);
		combo.getSelectionModel().selectFirst();
                combo.getStyleClass().add("comboOps");
		return combo;
	}
	
    private VBox createLogicFunctionBox(Function function){
	VBoxIFBlock vb_function = new VBoxIFBlock();
	vb_function.setMinWidth(200);
	vb_function.setPrefWidth(200);
	vb_function.getStyleClass().add("boxes");
	vb_function.setFunctionOrEvent(function);
	vb_function.setType("function");
	HBox hb_header = getHeaderSubBoxFunction(function,vb_function,PositionInMakeTemplate.Function);
	hb_header.getStyleClass().add("headerBox");
	vb_function.getChildren().add(hb_header);
	// add parameters 
	ArrayList<CMTParameter> pars = function.getParameters();
        VBox vb_par = new VBox();
       // Iterator<String> it = pars.keySet().iterator();
        int counter = 0;
	//while(it.hasNext()){
        for(CMTParameter pa : pars){
            HBox parhb = new HBox();
            parhb.setStyle("-fx-alignment: center-left;");
            String parName = pa.getParName();
            String parType =  ConverterCoreBlocks.getSimpleNameAll(pa.getType());//ConverterCoreBlocks.getSimpleName(pars.get(parName));
            HBox hb = getParameterAndTypeHBox(parName,parType);
            Pane pancr = new Pane();
            pancr.setMinWidth(55);
            pancr.setPrefWidth(55);
            CmtCircle cr = getCmtCircle(function, PositionCmtCircle.Function);
            cr.setParameter(parName);
            cr.setTypeOfParameter(parType);
            cr.setIndexFunction(vb1_functions.getChildren().size()-1);
            counter += 1;
            cr.setIndexOfCrInVB(counter);
            parhb.getChildren().add(hb);
            parhb.getChildren().add(pancr);
            parhb.getChildren().add(cr);
            vb_par.getChildren().add(parhb);
	}
        counter=0;
	vb_function.getChildren().add(vb_par);
	return vb_function;
    }
	
    private VBox createEventLogicBox(IFactType event){
        VBoxIFBlock vb_function = new VBoxIFBlock();
	vb_function.setMinWidth(200);
	vb_function.getStyleClass().add("boxes");
	vb_function.setFunctionOrEvent(event);
	vb_function.setType(Constants.ACTIVITY); // pasop met time
	HBox hb_typeName = getHeaderSubBoxEvent((FactType)event, vb_function, PositionInMakeTemplate.Function);
	hb_typeName.getStyleClass().add("headerBox");
	VBox vb_fields = new VBox();
        FactType eventType = (FactType) event;
        ArrayList<CMTField> fields =eventType.getFields();
	int i = 1;
	for(CMTField field : fields){
            HBox hb_field = new HBox();
            hb_field.setStyle("-fx-alignment:center-left;");
            HBox hb_par;
          
                hb_par = getParameterAndTypeHBox(field.getName(), CMTClient.getSimpleNameAll(field.getType()));
		Pane pane = new Pane();
		pane.setMinWidth(55);
		pane.setPrefWidth(55);
		hb_field.getChildren().add(hb_par);
		hb_field.getChildren().add(pane);
          
           
            vb_fields.getChildren().add(hb_field);
            i += 1;
	}
	vb_function.getChildren().add(hb_typeName);
	vb_function.getChildren().add(vb_fields);
	return vb_function;
    }

    private VBox createFactInputBox(IFactType fact){
	VBox vb_input = new VBox();
        boolean isFact = true;
        HBox hb_header;
        if(fact.getClass().isAssignableFrom(FactType.class)){
            isFact = false;
            System.out.println("--------- header1");
            hb_header = getHeaderSubBoxFact(isFact,(FactType)fact, null,vb_input, PositionInMakeTemplate.Input);
        }else{
            System.out.println("--------- header2");
            hb_header = getHeaderSubBoxFact(isFact,null,(Fact)fact,vb_input, PositionInMakeTemplate.Input);
        }        
        System.out.println("--------- header" + hb_header);
    	hb_header.getStyleClass().add("headerBox");
	vb_input.getChildren().add(hb_header);
	VBox vb_fields = new VBox();
        ArrayList<CMTField> fields = null;
        if(isFact){
            fields = ((Fact)fact).getFields();
        }else{
            fields = ((FactType)fact).getFields();
        }
	for(int i = 0; i< fields.size() ; i++){
            int indexX = vb_fields.getChildren().size();
            vb_fields.getChildren().add(getFactInputBoxField(fields.get(i), fact, indexX, i+1));
	}
	vb_input.getChildren().add(vb_fields);
	vb_input.getStyleClass().add("vb-drops");
	return vb_input;
    }
    
    private HBox getFactInputBoxField(CMTField field, IFactType fact, int index, int counter){
        HBox hb_field = new HBox();
	hb_field.setStyle("-fx-alignment:center-left");
        String parName = field.getName();
        String parType = CMTClient.getSimpleNameAll(field.getType());
        HBox parNameType;
        if(field.isIsVar()){
            parNameType = getParameterAndTypeBoxVarEvent(field);
        }else{
            parNameType = getParameterAndTypeHBox(parName,parType);
        }
        
	CmtCircle crLeft = getCmtCircle(fact, PositionCmtCircle.InputLeft);
	CmtCircle crRight = getCmtCircle(fact, PositionCmtCircle.InputRight);
	crLeft.setParameter(parName);
	crLeft.setObj(fact);
	crLeft.setTypeOfParameter(parType);
	crLeft.setId("#CrL" + vb2_input.getChildren().size() + index);
	crLeft.setIndexInput(vb2_input.getChildren().size()-1);
	crLeft.setIndexOfCrInVB(counter);
	crRight.setParameter(parName);
	crRight.setTypeOfParameter(parType);
	crRight.setId("#CrR" + vb2_input.getChildren().size() + index);
	crRight.setIndexInput(vb2_input.getChildren().size()-1);
	crRight.setIndexOfCrInVB(counter);
	crRight.setObj(fact);
	hb_field.setId(""+(vb2_input.getChildren().size() - 3));
	Button bt_toOutput = getToutputButton(true, parName, field.getType(), fact, hb_field, vb2_input.getChildren().size()-1, counter);
	Pane pane1 = new Pane();
	pane1.setMinWidth(7);
	pane1.setPrefWidth(7);
	hb_field.getChildren().add(pane1);
	hb_field.getChildren().add(crLeft);
	Pane pane2 = new Pane();
	pane2.setMinWidth(4);
	pane2.setPrefWidth(4);
	hb_field.getChildren().add(pane2);
	hb_field.getChildren().add(parNameType);
	if(!isRuleTemplate){
            hb_field.getChildren().add(bt_toOutput);
            Pane pane3 = new Pane();
            pane3.setMinWidth(6);
            pane3.setPrefWidth(6);
            hb_field.getChildren().add(pane3);
            hb_field.getChildren().add(crRight);
            Pane pane4 = new Pane();
            pane4.setMinWidth(5);
            pane4.setPrefWidth(5);
            hb_field.getChildren().add(pane4);
	}
        return hb_field;
    }
    
    private VBox createEventInputBox(IFactType event){
        FactType eventType = (FactType)event;
        VBox vb_input = new VBox();
       // vb_input.setMaxWidth(150);
	//vb_input.getStyleClass().add("boxes");
        HBox hb_header = getHeaderSubBoxEvent((FactType)event, vb_input, PositionInMakeTemplate.Input);
        hb_header.getStyleClass().add("headerBox");
       
	vb_input.getChildren().add(hb_header);
        ArrayList<CMTField> fields = ((FactType)event).getFields();
        String idField = eventType.getUriField();
	
	/*VBox vb_par = new VBox();
        
	for(int i = 0; i< fields.size() ; i++){
            CMTField field = fields.get(i);
            HBox hb_field = new HBox();
            hb_field.setStyle("-fx-alignment: center-left");
            HBox hb_par;
            if(!(field.getName().equals(idField))){
		hb_par = getParameterAndTypeHBox(field.getName(),CMTClient.getSimpleTypeName(field.getType()));
            }else{
		hb_par = getHBoxParameterForEventInput(event);
            }
            Button bt_toOutput = getToutputButton(true, field.getName(), CMTClient.getSimpleTypeName(field.getType()), event, hb_field, vb2_input.getChildren().size()-3, i+1);
            CmtCircle crLeft = getCmtCircle(event, PositionCmtCircle.InputLeft);
            CmtCircle crRight = getCmtCircle(event, PositionCmtCircle.InputRight);
            crLeft.setParameter(idField);
            crLeft.setTypeOfParameter(eventType.getClassName());
            crLeft.setIndexInput(vb2_input.getChildren().size()-1);
            crLeft.setIndexOfCrInVB(i+1);
            crRight.setParameter(idField);
            crRight.setTypeOfParameter(event.getClass().getName());
            crRight.setIndexInput(vb2_input.getChildren().size()-1);
            crRight.setIndexOfCrInVB(i+1);
					
            Pane pane1 = new Pane();
            pane1.setMinWidth(7);
            pane1.setPrefWidth(7);
            hb_field.getChildren().add(pane1);
            hb_field.getChildren().add(crLeft);
            Pane pane2 = new Pane();
            pane2.setMinWidth(4);
            pane2.setPrefWidth(4);
            hb_field.getChildren().add(pane2);
            hb_field.getChildren().add(hb_par);
            Pane pane7 = new Pane();
            pane7.setMinWidth(13);
            pane7.setPrefWidth(13);
            hb_field.getChildren().add(pane7);
            if(!isRuleTemplate){
		hb_field.getChildren().add(bt_toOutput);
		Pane pane3 = new Pane();
		pane3.setMinWidth(6);
		pane3.setPrefWidth(6);
		hb_field.getChildren().add(pane3);
		hb_field.getChildren().add(crRight);
		Pane pane4 = new Pane();
		pane4.setMinWidth(5);
		pane4.setPrefWidth(5);
		hb_field.getChildren().add(pane4);
            }
            vb_par.getChildren().add(hb_field);
	}
	vb_input.getChildren().add(vb_par);
	vb_input.getStyleClass().add("vb-drops");*/
        VBox vb_fields = new VBox();
       
	for(int i = 0; i< fields.size() ; i++){
            int indexX = vb_fields.getChildren().size();
            vb_fields.getChildren().add(getFactInputBoxField(fields.get(i),(FactType)event, indexX, i+1));
	}
	vb_input.getChildren().add(vb_fields);
	vb_input.getStyleClass().add("vb-drops");
	return vb_input;
    }
	
    private HBox getHeaderSubBoxFunction(Function function, VBox vb_holdingHeader, PositionInMakeTemplate pos){
       	HBox hb_header = new HBox();
	CmtCircle crIr = null;
	hb_header.setAlignment(Pos.CENTER_LEFT);
	hb_header.setPadding(new Insets(5, 2, 4, 2));
	Label lb_name = new Label();
	//lb_name.setStyle("-fx-text-fill: white"); .labInHeaderVbInput
	
	Button close = new Button();
	close.getStyleClass().add("buttonClose");
        Pane pane9 = new Pane();
        pane9.setMinWidth(5);
        pane9.setPrefWidth(5);
        hb_header.getChildren().add(pane9);
        lb_name.setText(function.getName());
        lb_name.getStyleClass().add("labInHeaderVbInputEvent");
        hb_header.getChildren().add(lb_name);
//        close.setOnAction((event) ->{		
//            int index = vb1_functions.getChildren().indexOf(vb_holdingHeader);	
//            LinkedList<CmtLine> copyLines = new LinkedList<CmtLine>();
//            for(CmtLine lin : lines){
//                copyLines.add(lin);
//            }
//            for(CmtLine lineCmt : copyLines){				
//                if(lineCmt.getIndexInFunction() == index){
//                    pane.getChildren().remove(lineCmt);
//                    lines.remove(lineCmt);
//                }
//                if(lineCmt.getIndexInFunction()> index){
//                    lineCmt.setIndexInFunction(lineCmt.getIndexInFunction() - 2) ;
//                    VBox fields = (VBox)vb_holdingHeader.getChildren().get(1);
//                    fields.getChildren().size();				
//                    lineCmt.setStartY(lineCmt.getStartY()-((fields.getChildren().size()+1) * 30));
//                }	
//            }
//            vb1_functions.getChildren().remove(vb1_functions.getChildren().indexOf(vb_holdingHeader) +1);
//            vb1_functions.getChildren().remove(vb_holdingHeader);
//            if(vb1_functions.getChildren().size()>1){
//                VBox vb = (VBox)vb1_functions.getChildren().get(0);
//                HBox hbHeader = (HBox) vb.getChildren().get(0);		
//                hbHeader.getChildren().remove(2);
//                Pane pane = new Pane();
//                pane.setMinWidth(90);
//                pane.setPrefWidth(90);
//                hbHeader.getChildren().add(2, pane);
//            }
//        });
        setOnCloseHeader(close, vb_holdingHeader, pos);
        if(pos == PositionInMakeTemplate.Function){
            if(vb1_functions.getChildren().size() > 1){
		Pane pane = new Pane();
		pane.setMinWidth(42);
                pane.setPrefWidth(42);			
		hb_header.getChildren().add(getNewComboBoxOperators());
		hb_header.getChildren().add(pane);		
		lb_name.getStyleClass().add("labInHeaderVbFunction");
		lb_name.setMinWidth(130);
		lb_name.setPrefWidth(130);
		}else{
                     Pane pane = new Pane();
		pane.setMinWidth(72);
                pane.setPrefWidth(72);			
		
		hb_header.getChildren().add(pane);	
            }
	}	
	hb_header.getChildren().add(close);
	Pane pane5 = new Pane();
	pane5.setMinWidth(5);
	pane5.setPrefWidth(5);
	hb_header.getChildren().add(pane5);
	if(crIr != null && !isRuleTemplate){
            hb_header.getChildren().add(crIr);
	}
	return hb_header;
    }
      
    private HBox getHeaderSubBoxEvent(FactType factType, VBox vb_holdingHeader, PositionInMakeTemplate pos){
        HBox hb_header = new HBox();
	CmtCircle crIr = null;
	hb_header.setAlignment(Pos.CENTER_LEFT);
	hb_header.setPadding(new Insets(5, 2, 4, 2));
	Label lb_name = new Label();
	//lb_name.setStyle("-fx-text-fill: white"); .labInHeaderVbInput
	
	
        lb_name.setText(factType.getClassName());
	Button close = new Button();
	close.getStyleClass().add("buttonClose");
        
        if(!(factType.getType().equals("time")) && PositionInMakeTemplate.Input == pos){ 
            
            Pane pane = new Pane();
            pane.setMinWidth(5);
            pane.setPrefWidth(5);
            CmtCircle cr = getCmtCircle(factType, PositionCmtCircle.InputLeft);	
            cr.setParameter("");
            cr.setTypeOfParameter(factType.getClassName());
            cr.setIndexInput(vb2_input.getChildren().size()-1);
            cr.setIndexOfCrInVB(0);
            
            hb_header.getChildren().add(pane);
            hb_header.getChildren().add(cr);
            Pane pane9 = new Pane();
            pane9.setMinWidth(10);
            pane9.setPrefWidth(10);
            hb_header.getChildren().add(pane9);
            hb_header.getChildren().add(lb_name);
	}else{
            Pane pane9 = new Pane();
            pane9.setMinWidth(5);
            pane9.setPrefWidth(5);
            hb_header.getChildren().add(pane9);
            hb_header.getChildren().add(lb_name);
	}
        if(pos == PositionInMakeTemplate.Input){
            lb_name.getStyleClass().add("labInHeaderVbInputEvent");
            if(!isRuleTemplate)  {
                crIr = getCmtCircle(factType, PositionCmtCircle.InputRight);
                    Button bt_toOutput = getToutputButton(false,"","", factType, hb_header, vb2_input.getChildren().size()-1, 0);
                    hb_header.getChildren().add(bt_toOutput);
                
               
		crIr.setParameter("");
		crIr.setTypeOfParameter(factType.getClassName());
  
		crIr.setId("#" + factType.getClassName() + Integer.toString(vb2_input.getChildren().size()-1));
              
		crIr.setIndexInput(vb2_input.getChildren().size()-1);
		crIr.setIndexOfCrInVB(0);
               
            }else{
                Pane pane2 = new Pane();
		pane2.setMinWidth(40);
                pane2.setPrefWidth(40);			
		hb_header.getChildren().add(pane2);		
            }
        }else{
            lb_name.getStyleClass().add("labInHeaderVbIFEvent");
            if(pos == PositionInMakeTemplate.Function){
               
                    crIr = getCmtCircle(factType, PositionCmtCircle.InputRight);
                   
		crIr.setParameter("");
		crIr.setTypeOfParameter(factType.getClassName());
  
		crIr.setId("#" + factType.getClassName() + Integer.toString(vb1_functions.getChildren().size()-1));
              
		crIr.setIndexInput(vb1_functions.getChildren().size()-1);
		crIr.setIndexOfCrInVB(0);
                
            }
        }
   //     lb_name.getStyleClass().add("labInHeaderVbInputEvent");
        setOnCloseHeader(close, vb_holdingHeader, pos);
        if(pos == PositionInMakeTemplate.Function){
            if(vb1_functions.getChildren().size() > 1){
		Pane pane = new Pane();
		pane.setMinWidth(20);
                pane.setPrefWidth(20);			
		hb_header.getChildren().add(getNewComboBoxOperators());
		hb_header.getChildren().add(pane);		
		lb_name.getStyleClass().add("labInHeaderVbFunction");
		lb_name.setMinWidth(155);
		lb_name.setPrefWidth(155);
		}else{
                    Pane pane = new Pane();
		pane.setMinWidth(71);
                pane.setPrefWidth(71);			
		
		hb_header.getChildren().add(pane);		
                }
	}	
	hb_header.getChildren().add(close);
	Pane pane5 = new Pane();
	pane5.setMinWidth(5);
	pane5.setPrefWidth(5);
	hb_header.getChildren().add(pane5);
	if(crIr != null ){
            hb_header.getChildren().add(crIr);
	}
	return hb_header;
    }
    
    private HBox getHeaderSubBoxFact(boolean isFact, FactType factType, Fact fact, VBox vb_holdingHeader, PositionInMakeTemplate pos){

        HBox hb_header = new HBox();
	CmtCircle crIr = null;
	hb_header.setAlignment(Pos.CENTER_LEFT);
	hb_header.setPadding(new Insets(5, 2, 4, 2));
	Label lb_name = new Label();
	//lb_name.setStyle("-fx-text-fill: white"); .labInHeaderVbInput
	lb_name.getStyleClass().add("labInHeaderVbInput");
//	lb_name.setMinWidth(50);
//	lb_name.setPrefWidth(50);
        String className = "";
        if(isFact){
            className = fact.getClassName();
        }else{
            className = factType.getClassName();
        }
        lb_name.setText(className);
	Button close = new Button();
	close.getStyleClass().add("buttonClose");
        
        Pane pane = new Pane();
        pane.setMinWidth(5);
        pane.setPrefWidth(5);
        
        CmtCircle cr = null;
        if(isFact){
            cr = getCmtCircle(fact, PositionCmtCircle.InputLeft);	
        }else{
            cr = getCmtCircle(factType, PositionCmtCircle.InputLeft);	
        }
        cr.setParameter("");
        cr.setTypeOfParameter(className);
        cr.setIndexInput(vb2_input.getChildren().size()-1);
        cr.setIndexOfCrInVB(0);
        hb_header.getChildren().add(pane);
        hb_header.getChildren().add(cr);
        Pane pane9 = new Pane();
        pane9.setMinWidth(10);
        pane9.setPrefWidth(10);
        hb_header.getChildren().add(pane9);
        hb_header.getChildren().add(lb_name);

	if(pos == PositionInMakeTemplate.Input){
            FactType type = factType;
            if(isFact){
                type = CMTClient.getFactType(fact.getClassName());
             
            }
            System.out.println(" get type of " + type);
            HashSet<Fact> facts = CMTClient.getFactsOfType(type);
            
            LinkedList<String> list =  new LinkedList<String>();
            String uriField = type.getUriField();
            for(Fact fa : facts){
                list.add(fa.getUriValue());
            }
            ComboBox<String> box = getComboBoxInput(list);
            if(isFact){
                ArrayList<CMTField> fields = fact.getFields();
                String uriValue = "";
                for(CMTField fi:fields){
                    if(fi.getName().equals(fact.getUriField())){
                        uriValue = fi.getValue().toString();
                    }
                }
                box.getSelectionModel().select(uriValue);
            }else{
                box.getSelectionModel().select("Any");
            }
   
            Pane pane1 = new Pane();
            pane1.setMinWidth(5);
            pane1.setPrefWidth(5);
            hb_header.getChildren().add(pane1);
            hb_header.getChildren().add(box);
            Pane pane2 = new Pane();
            if(!isRuleTemplate)  {

		pane2.setMinWidth(10);
		pane2.setPrefWidth(10);
		hb_header.getChildren().add(pane2);
                if(isFact){
                    crIr = getCmtCircle(fact, PositionCmtCircle.InputRight);
                    Button bt_toOutput = getToutputButton(false,"","",fact , hb_header, vb2_input.getChildren().size()-1, 0);
                   
                    hb_header.getChildren().add(bt_toOutput);
                }else{
                    crIr = getCmtCircle(factType, PositionCmtCircle.InputRight);
                    Button bt_toOutput = getToutputButton(false,"","", factType, hb_header, vb2_input.getChildren().size()-1, 0);
                    hb_header.getChildren().add(bt_toOutput);
                }
               
		crIr.setParameter("");
		crIr.setTypeOfParameter(type.getClassName());
  
		crIr.setId("#" + type.getClassName() + Integer.toString(vb2_input.getChildren().size()-1));
              
		crIr.setIndexInput(vb2_input.getChildren().size()-1);
		crIr.setIndexOfCrInVB(0);
               
            }else{
		pane2.setMinWidth(53);
		pane2.setPrefWidth(53);
		hb_header.getChildren().add(pane2);
            }							
           
            setOnCloseHeader(close, vb_holdingHeader, pos);
        }
        
        
        
        if(pos == PositionInMakeTemplate.Function){
            if(vb1_functions.getChildren().size() > 1){
		Pane pane2 = new Pane();
		pane2.setMinWidth(35);
                pane2.setPrefWidth(35);			
		hb_header.getChildren().add(getNewComboBoxOperators());
		hb_header.getChildren().add(pane2);		
		lb_name.getStyleClass().add("labInHeaderVbFunction");
		lb_name.setMinWidth(130);
		lb_name.setPrefWidth(130);
		}
	}	
	hb_header.getChildren().add(close);
	Pane pane5 = new Pane();
	pane5.setMinWidth(5);
	pane5.setPrefWidth(5);
	hb_header.getChildren().add(pane5);
	if(crIr != null && !isRuleTemplate){
            hb_header.getChildren().add(crIr);
	}
       
	return hb_header;
    }
    
    private void setOnCloseHeader(Button close, VBox vb_holdingHeader, PositionInMakeTemplate pos){
        close.setOnAction((event) ->{
            if(pos == PositionInMakeTemplate.Input){
                int indexEventIF = -1;
		int index = vb2_input.getChildren().indexOf(vb_holdingHeader);							
                LinkedList<CmtLine> copyLines = new LinkedList<CmtLine>();
                HashMap<Integer, HashMap<Integer,LineYs>> oldYValsLeft = new HashMap<>();
                HashMap<Integer, HashMap<Integer,LineYs>> oldYValsRight = new HashMap<>();
		for(CmtLine lin : lines){
                    copyLines.add(lin);
		}
		for(CmtLine lineCmt : copyLines){
                    if(lineCmt.getIndexInInput() == index && lineCmt.getIndexInOutput() != -1){						
			for(CmtLine li : lines){			
                            if(li.getIndexInOutput()>lineCmt.getIndexInOutput()){										
				li.setIndexInOutput(li.getIndexInOutput()-1);
				li.setEndY(li.getEndY()-28); // TODO effe hardcodes
                            }
                        }
                        vb_outputHa.getChildren().remove(lineCmt.getIndexInOutput());
                        pane.getChildren().remove(lineCmt);
                        lines.remove(lineCmt);
                    }else{
                        if(lineCmt.getIndexInInput() == index && lineCmt.getIndexInFunction() != -1){
                            if(lineCmt.getIndexCrS()==0){
                                indexEventIF = lineCmt.getIndexInFunction();
                            }
                            pane.getChildren().remove(lineCmt);	
                            lines.remove(lineCmt);
			}
                    }
                    
                    
                    if(lineCmt.getIndexInInput()> index && lineCmt.getIndexInOutput() != -1){
                        HashMap<Integer,LineYs> mapVal = null;
                        if(oldYValsRight.containsKey(lineCmt.getIndexInInput())){
                            mapVal = oldYValsRight.get(lineCmt.getIndexInInput());
                        }else{
                            mapVal = new HashMap<>();
                        }
                        LineYs ysLine = new LineYs();
                            ysLine.setStartY(lineCmt.startYProperty().doubleValue());
                            ysLine.setEndY(lineCmt.endYProperty().doubleValue());
                            mapVal.put(lineCmt.getIndexCrS(), ysLine);
                            oldYValsRight.put(lineCmt.getIndexInInput(), mapVal);
//			lineCmt.setIndexInInput(lineCmt.getIndexInInput() - 2) ;
//			VBox fields = (VBox)vb_holdingHeader.getChildren().get(1);
//			fields.getChildren().size();
//			lineCmt.setStartY(lineCmt.getStartY()-((fields.getChildren().size()+1) * 30));
                    }else{
			if(lineCmt.getIndexInInput()> index && lineCmt.getIndexInFunction() != -1){
                            HashMap<Integer,LineYs> mapVal = null;
                        if(oldYValsLeft.containsKey(lineCmt.getIndexInInput())){
                            mapVal = oldYValsLeft.get(lineCmt.getIndexInInput());
                        }else{
                            mapVal = new HashMap<>();
                        }
                        LineYs ysLine = new LineYs();
                            ysLine.setStartY(lineCmt.startYProperty().doubleValue());
                            ysLine.setEndY(lineCmt.endYProperty().doubleValue());
                            mapVal.put(lineCmt.getIndexCrT(), ysLine);
                            oldYValsLeft.put(lineCmt.getIndexInInput(), mapVal);
//                            lineCmt.setIndexInInput(lineCmt.getIndexInInput() - 2) ;
//                            VBox fields = (VBox)vb_holdingHeader.getChildren().get(1);
//                            fields.getChildren().size();										
                          //  lineCmt.setEndY(lineCmt.getEndY()-((fields.getChildren().size()+1) * 30));
			}
                    }
                    
		}
                
		vb2_input.getChildren().remove(vb2_input.getChildren().indexOf(vb_holdingHeader) +1);
		vb2_input.getChildren().remove(vb_holdingHeader);
                System.out.println("------------------------------------------------------------ before call");
                    okIndexsInputRight = new HashMap<Integer,ArrayList<Integer>>();
                    okIndexsInputLeft = new HashMap<Integer,ArrayList<Integer>>();
                    redrawLines(pos, index, indexEventIF, oldYValsRight, oldYValsLeft);
            }else{
		if(pos == PositionInMakeTemplate.Function){
                    int index = vb1_functions.getChildren().indexOf(vb_holdingHeader);				
                    LinkedList<CmtLine> copyLines = new LinkedList<CmtLine>();
                    HashMap<Integer, HashMap<Integer,LineYs>> ycoordLinesAfterVbox = new HashMap<>();
                    for(CmtLine lin : lines){
                        copyLines.add(lin);
                    }
                    int indexEventInput = -1;
                    for(CmtLine lineCmt : copyLines){
                        System.out.println(" -- line index " + lineCmt.getIndexInFunction() +"  indexc "+ index);
                        if(lineCmt.getIndexInFunction() == index){
                            if(vb_holdingHeader instanceof VBoxIFBlock){
                                if(((VBoxIFBlock)vb_holdingHeader).getType().equals("event")){
                                    indexEventInput = lineCmt.getIndexInInput();
                                    
                                }
                            }
                            pane.getChildren().remove(lineCmt);
                            lines.remove(lineCmt);
                        }						
                        if(lineCmt.getIndexInFunction()> index){
                            LineYs ys = new LineYs();
                            ys.setStartY(lineCmt.startYProperty().doubleValue());
                            ys.setEndY(lineCmt.endYProperty().doubleValue());
                            if(ycoordLinesAfterVbox.containsKey(lineCmt.getIndexInFunction())){
                                ycoordLinesAfterVbox.get(lineCmt.getIndexInFunction()).put(lineCmt.getIndexCrS(), ys);
                            }else{
                                HashMap<Integer, LineYs> y = new HashMap<>();
                                y.put(lineCmt.getIndexCrS(), ys);
                                ycoordLinesAfterVbox.put(lineCmt.getIndexInFunction(), y);
                            }
//                            lineCmt.setIndexInFunction(lineCmt.getIndexInFunction() - 2) ;
//                            VBox fields = (VBox)vb_holdingHeader.getChildren().get(1);
//                            fields.getChildren().size();
//                            //lineCmt.setStartY(lineCmt.getStartY()-((fields.getChildren().size()+1) * 30));
                        }
                    }
                    if(indexEventInput >-1){
                        if(((VBoxIFBlock)vb_holdingHeader).getType().equals("event")){
                                        VBox vboxIn = (VBox)vb2_input.getChildren().get(indexEventInput);
                                        HBox header = (HBox)vboxIn.getChildren().get(0);
                                        Button but = (Button)header.getChildren().get(header.getChildren().size()-3);
                                        but.fire();
                        }
                    }
                    vb1_functions.getChildren().remove(vb1_functions.getChildren().indexOf(vb_holdingHeader) +1);
                    vb1_functions.getChildren().remove(vb_holdingHeader);
                    
                    if(vb1_functions.getChildren().size()>1){			
                        VBox vb = (VBox)vb1_functions.getChildren().get(0);
                        HBox hbHeader = (HBox) vb.getChildren().get(0);					
                        hbHeader.getChildren().remove(2);
                        
                        if(vb instanceof VBoxIFBlock){
                            Pane pane2 = new Pane();
                            VBoxIFBlock ifblock = (VBoxIFBlock) vb;
                            if(ifblock.getType().equals("event")){
                                pane2.setMinWidth(75);
                                pane2.setPrefWidth(75);
                                // remove event inputblock
                                
                            }else{
                                pane2.setMinWidth(78);
                                pane2.setPrefWidth(78);
                            }
                            hbHeader.getChildren().add(2, pane2);
                        }
                    }
                    
                    System.out.println("------------------------------------------------------------ before call");
                    okIndexsIFBlock = new HashMap<Integer,ArrayList<Integer>>();
                    redrawLines(PositionInMakeTemplate.Function, index,-1, ycoordLinesAfterVbox, null);
                }
                }
            
	});
        
    }
    
    
            
    private HBox getParameterAndTypeBoxVarEvent(CMTField field){
        HBox hb = new HBox();
	Label name = new Label();
	name.setText(field.getName());
	name.getStyleClass().add("labInParTypeHbInputEvent");
	hb.getChildren().add(name);
        if(!field.getFormat().equals("")){
            ArrayList<String> ops = new ArrayList();
            ops.add("=="); ops.add("!="); ops.add(">"); ops.add("<");
            ObservableList<String> items = FXCollections.observableArrayList();
		items.addAll(ops);
		ComboBox<String> box = new ComboBox<String>();
                box.setItems(items);
                box.getSelectionModel().select("==");
               /* box.setOnMousePressed(new EventHandler<MouseEvent>(){
                            @Override
                            public void handle(MouseEvent event) {
                                box.requestFocus();
                                
                            }
                        });*///
		
            box.getStyleClass().add("boxOps");
            hb.getChildren().add(box);
            Pane pane2 = new Pane();
            pane2.setPrefWidth(30);
            pane2.setMaxWidth(30);
            hb.getChildren().add(pane2);
            TextField textField = new TextField();
            textField.setText(field.getFormat());
            textField.getStyleClass().add("textFieldInputBox");
            hb.getChildren().add(textField);
            Pane pane1 = new Pane();
            pane1.setPrefWidth(32);
            pane1.setMaxWidth(32);
            hb.getChildren().add(pane1);
        }else{
            if(!field.getOptions().isEmpty()){
                 ArrayList<String> ops = new ArrayList();
                ops.add("="); ops.add("!=");
                ObservableList<String> items = FXCollections.observableArrayList();
		items.addAll(ops);
                ComboBox<String> boxop = new ComboBox<String>();
               /* boxop.setOnMousePressed(new EventHandler<MouseEvent>(){
                            @Override
                            public void handle(MouseEvent event) {
                                boxop.requestFocus();
                                
                            }
                        });*/
                boxop.setItems(items);
                boxop.getStyleClass().add("boxOps");
                boxop.getSelectionModel().select("==");
                hb.getChildren().add(boxop);
                Pane pane2 = new Pane();
            pane2.setPrefWidth(30);
            pane2.setMaxWidth(30);
            hb.getChildren().add(pane2);
                ComboBox box = getComboBoxInput(field.getOptions());
                hb.getChildren().add(box);
                Pane pane1 = new Pane();
            pane1.setPrefWidth(32);
            pane1.setMaxWidth(32);
            hb.getChildren().add(pane1);
            }else{
                Label type = new Label(ConverterCoreBlocks.getSimpleNameAll(field.getType()));
                type.getStyleClass().add("labInParTypeHbInput");
                hb.getChildren().add(type);
            }
        }
        
	
	
	return hb;
    
    }

	
	
    private HBox getParameterAndTypeHBox(String namePar, String typePar){
	HBox hb = new HBox();
	Label name = new Label();
	name.setText(namePar);
	name.getStyleClass().add("labInParTypeHbInput");
	Label type = new Label();
	type.getStyleClass().add("labInParTypeHbInput");
        type.setText(typePar);
	hb.getChildren().add(name);
	hb.getChildren().add(type);
	return hb;
    }
	
//    private HBox getHBoxParameterForEventInput(IFactType event){
//        FactType eventType = (FactType)event;
//	HBox hb_par = new HBox();
//	hb_par.setStyle("-fx-alignment:center-left;");
//	String idField = eventType.getUriField();
//	Label fieldName = new Label(idField);
//	fieldName.getStyleClass().add("labInParTypeHbInputEvent");
//	hb_par.getChildren().add(fieldName);
//	String formatVar = eventType.getVarFormat();
//	ArrayList<String> listVar = eventType.getVarList();
//        if(!(formatVar.equals(""))){
//            ArrayList<String> ops = new ArrayList();
//            ops.add("="); ops.add("!="); ops.add(">"); ops.add("<");
//            ComboBox box = getComboBoxInput(ops);
//           // box.getStyleClass().add("boxOps");
//            hb_par.getChildren().add(box);
//            TextField tf_inputVar = new TextField();
//            tf_inputVar.setText(formatVar);	
//            tf_inputVar.setMinWidth(90);
//            tf_inputVar.setPrefWidth(90);
//            hb_par.getChildren().add(tf_inputVar);
//        }else{
//            if(!(listVar.isEmpty())){
//                
//                ArrayList<String> ops = new ArrayList();
//                ops.add("="); ops.add("!=");
//                ComboBox boxop = getComboBoxInput(ops);
//              //  boxop.getStyleClass().add("boxOps");
//                hb_par.getChildren().add(boxop);
//                ComboBox box = getComboBoxInput(listVar);
//		hb_par.getChildren().add(box);
//            }
//	}	
//	return hb_par;	
//    }
	
	private CmtCircle getCmtCircle(Object obj, PositionCmtCircle which){
		CmtCircle cr = new CmtCircle();
		cr.setRadius(10);
		cr.setObj(obj);
		cr.setPosition(which);
		cr.setDefaultColor();
		circles.add(cr);
		return cr;
	}
	
	private ComboBox<String> getComboBoxInput(List<String> list){
		ObservableList<String> items = FXCollections.observableArrayList();
		items.addAll(list);
		items.add("Any");
		ComboBox<String> box = new ComboBox<String>();
               /* box.setOnMousePressed(new EventHandler<MouseEvent>(){
                            @Override
                            public void handle(MouseEvent event) {
                                box.requestFocus();
                                
                            }
                        });*/
                box.setItems(items);
		box.getSelectionModel().select("Any");
		
		return box;
	}
	
	private BindingInputFact getBindingInputFact(CmtLine line, IFactType fact){
		
		BindingInputFact bindFact = new BindingInputFact();
		System.out.println(line.getIndexInInput() );
		
                bindFact.setInputObject(fact);
		bindFact.setIndexObj(line.getIndexInInput());
                
                if(line.getIndexInInput() != -1){
		
                
                if(fact instanceof Fact){
                    bindFact.setFactId(((Fact)fact).getUriValue());
                    bindFact.setIndexObj(line.getIndexInInput());
                    bindFact.setInputObject(fact);
                    
                }else{ 
                    if(fact instanceof FactType){
                        FactType facttype = (FactType) fact;
                        IFactType bindObject = getInputObjectBinding(line, facttype);
                        bindFact.setInputObject(bindObject);
                        bindFact.setIndexObj(line.getIndexInInput());
                        bindFact.setFactId("");
                    }
                }
                }
                
                 // case1 : facttype of fact
                        // case1.1: facttype combo is set -- getFact in CMT to add to binding
                        // case 1.2: facttype combo is still ANY -- keep facttype
                        // case 2: facttype of event -- create eventinput
                        // case 2.1: value is set
                        // case 2.2: value is not set
                
		/*// getBox if Any than 
		if(line.getIndexInInput() != -1){
			Node nodefact = vb2_input.getChildren().get(line.getIndexInInput());
			VBox vbFact = (VBox) nodefact;
			HBox hbHeader = (HBox) vbFact.getChildren().get(0);
			Node nodeCom = hbHeader.getChildren().get(5);
			//System.out.println( hbHeader.getChildren().get(5).getClass().getSimpleName());
			if(nodeCom.getClass().getSimpleName().equals("ComboBox")){
				ComboBox combo = (ComboBox) nodeCom;
				String comboSelect = combo.getSelectionModel().getSelectedItem().toString();
				String factId = "";
				if(!comboSelect.equals("Any")){
					factId = comboSelect;
					
				}
				bindFact.setFactId(factId); // uriField fact eg Person sandra
			}
			return bindFact;
		} */
		return bindFact;
		
	}
	
	private BindingInputField getBindingInputField(CmtLine line, IFactType fact, String parameter){
		
                BindingInputField bindField = new BindingInputField();
		if(line.getIndexInInput() != -1){
		
                
                if(fact instanceof Fact){
                    bindField.setFactId(((Fact)fact).getUriValue());
                    bindField.setField(getBindField(((Fact) fact).getFields(), parameter));
                    bindField.setIndexObj(line.getIndexInInput());
                    bindField.setInputObject(fact);
                    
                }else{ 
                    if(fact instanceof FactType){
                        FactType facttype = (FactType) fact;
                        IFactType bindObject = getInputObjectBinding(line, facttype);
                        bindField.setInputObject(bindObject);
                        bindField.setField(getBindField(facttype.getFields(), parameter));
                        bindField.setIndexObj(line.getIndexInInput());
                        bindField.setFactId("");
                    }
                }
                
                /*
		if(line.getIndexInInput() != -1 && !isTime){
			Node nodefact = vb2_input.getChildren().get(line.getIndexInInput());
			VBox vbFact = (VBox) nodefact;
			HBox hbHeader = (HBox) vbFact.getChildren().get(0);
			Node nodeCom = hbHeader.getChildren().get(5);
			if(nodeCom.getClass().getSimpleName().equals("ComboBox")){
				ComboBox combo = (ComboBox) nodeCom;
				String comboSelect = combo.getSelectionModel().getSelectedItem().toString();
				String factId = "";
				if(!comboSelect.equals("Any")){
					factId = comboSelect;
					
				}
				bindField.setFactId(factId);
			}
			CMTField fi= null;
                        ArrayList<CMTField> fields; 
                        if(fact instanceof FactType){
                            fields = ((FactType)fact).getFields();    
                        }else{
                            fields = ((Fact)fact).getFields();
                        }
                        for(CMTField f: fields){
                                if(f.getName().equals(parameter))
                                    fi = f;
                        }
			bindField.setField(fi);
			bindField.setIndexObj(line.getIndexInInput());
		}else{
			if(line.getIndexInInput() != -1 && isTime){
				// set value textfield or box 
                                FactType eventType = (FactType)fact;
				Node nodefact = vb2_input.getChildren().get(line.getIndexInInput());
				VBox vbFact = (VBox) nodefact;
				VBox vbPars = (VBox) vbFact.getChildren().get(1);
				for(int z = 0 ; z<vbPars.getChildren().size(); z++){
					HBox hbPar = (HBox)vbPars.getChildren().get(z);
					HBox hbParLab = (HBox) hbPar.getChildren().get(3);
					Node nodeParVal = hbParLab.getChildren().get(1);
					String parName =((Label) hbParLab.getChildren().get(0)).getText();
					String parValue = "";
					if(nodeParVal.getClass().isAssignableFrom(ComboBox.class)){
						String comboSelect = ((ComboBox) nodeParVal).getSelectionModel().getSelectedItem().toString();
						if(!comboSelect.equals("Any")){
							parValue = comboSelect;
						}
					}else{
						if(nodeParVal.getClass().isAssignableFrom(TextField.class)){
							parValue = ((TextField) nodeParVal).getText();
							// if value diff than format
							String format = eventType.getVarFormat();
								if(format.equals(parValue)){
									parValue="";
								}							
						}
					}
                                        
                                        ArrayList<CMTField> fields = eventType.getFields();
					for(CMTField f: fields){
                                                if(f.getName().equals(parameter))
                                                   // f.setValue(parValue);
                                                    bindField.setField(f);
                                        }
					bindField.setFactId(parValue);
					bindField.setIndexObj(line.getIndexInInput());
					
				}
			}
			
		} */
                }
		return bindField;
		
		
	}
        
        private IFactType getInputObjectBinding(CmtLine line, FactType ifacttype){
            // case1 : facttype of fact
            if(ifacttype.getType().equals("fact")){
                Node nodefact = vb2_input.getChildren().get(line.getIndexInInput());
                VBox vbFact = (VBox) nodefact;
                HBox hbHeader = (HBox) vbFact.getChildren().get(0);
                Node nodeCom = hbHeader.getChildren().get(5);
                if(nodeCom.getClass().getSimpleName().equals("ComboBox")){
                    ComboBox combo = (ComboBox) nodeCom;
                    String comboSelect = combo.getSelectionModel().getSelectedItem().toString();
                    if(!comboSelect.equals("Any")){
                        Fact fact = CMTClient.getFact(CMTClient.getSimpleNameAll(ifacttype.getClassName()), ifacttype.getUriField(), comboSelect);
                        if(fact != null){
                            return fact;
                        }
                    }else{
                        return ifacttype;
                    }
                }
                    
            }else{
                // case 2: facttype of event -- create eventinput
                        // case 2.1: value is set
                        // case 2.2: value is not set
                EventInput input = new EventInput();
                input.setClassName(ifacttype.getClassName());
                for(CMTField cmtField : ifacttype.getFields()){
                   // if(cmtField.isIsVar()){
                        FieldValueLimitation valLim = new FieldValueLimitation();
                        valLim.setFieldName(cmtField.getName());
                        input.addLimitation(valLim);
                        input.addField(cmtField);
                  //  }
                }
                Node nodefact = vb2_input.getChildren().get(line.getIndexInInput());
                VBox vbFact = (VBox) nodefact;
		VBox vbPars = (VBox) vbFact.getChildren().get(1);
		for(int z = 0 ; z<vbPars.getChildren().size(); z++){
                    HBox hbPar = (HBox)vbPars.getChildren().get(z);
                    HBox hbParLab = (HBox) hbPar.getChildren().get(3);
                    String parName =((Label) hbParLab.getChildren().get(0)).getText();
                    // check if field is variable
                    for(CMTField cmtField : ifacttype.getFields()){
                        if(cmtField.getName().equals(parName) && cmtField.isIsVar()){
                            Node nodeParVal = hbParLab.getChildren().get(3);
                            String parValue = "";
                            if(nodeParVal.getClass().isAssignableFrom(ComboBox.class)){
                                String comboSelect = ((ComboBox) nodeParVal).getSelectionModel().getSelectedItem().toString();
                                FieldValueLimitation f2 = input.getFieldValueLimitation(parName);    
                                if(!comboSelect.equals("Any")){

                                        f2.setValue(comboSelect);

                                        // set operator 
                                    }else{
                                    f2.setValue("");
                                }
                                    f2.setOperator(getOperatorCombobox(hbParLab));
                            }else{
                                if(nodeParVal.getClass().isAssignableFrom(TextField.class)){
                                    parValue = ((TextField) nodeParVal).getText();
                                    // if value diff than format
                                    String format = "";
                                    for(CMTField f : ifacttype.getFields()){
                                        if(f.getName().equals(parName)){
                                            format = f.getFormat();
                                        }
                                    }


                                    FieldValueLimitation f = input.getFieldValueLimitation(parName);

                                    if(!format.equals(parValue)){

                                        f.setValue(parValue);

                                    }else{
                                        f.setValue("");
                                    }	
                                    f.setOperator(getOperatorCombobox(hbParLab));
                                }
                            }
                            break;
                        }
                    }
                    
               
                }
                return input;
            }
            return null;
                        // case1.1: facttype combo is set -- getFact in CMT to add to binding
                        // case 1.2: facttype combo is still ANY -- keep facttype
                        // case 2: facttype of event -- create eventinput
                        // case 2.1: value is set
                        // case 2.2: value is not set
        }
        
        private String getOperatorCombobox(HBox hbox){
            Node node = hbox.getChildren().get(1);
            if(node.getClass().isAssignableFrom(ComboBox.class)){
                String comboSelect = ((ComboBox) node).getSelectionModel().getSelectedItem().toString();
                    return comboSelect;
            }
            return "";
        }
        
        private CMTField getBindField(ArrayList<CMTField> fields, String name){
            for(CMTField f: fields){
                if(f.getName().equals(name)){
                    return f;
                }
            }
            return null;
        }
	
	private boolean populateLeftHandSideTemplate(Template temp){
		
		// check alle circles ingevuld vb1
		
		int amountLinesInIf = 0;
		for(CmtLine lineA : lines){
			if(lineA.getIndexInFunction() != -1){
				amountLinesInIf += 1;
			}
		}
		int amountOfCircles = 0;
		for(int i =0 ; i <vb1_functions.getChildren().size() ; i++){
			Node node = vb1_functions.getChildren().get(i);
			if(node.getClass().getSimpleName().equals("VBoxIFBlock")){
                            
				VBoxIFBlock vbFunction = (VBoxIFBlock) node;
				if(vbFunction.getType().equals("function")){
                                int amountPar = ((VBox) vbFunction.getChildren().get(1)).getChildren().size();
				amountOfCircles += amountPar;
                                }else{
                                    amountOfCircles +=1;
                                }
			}
		}

		if(amountLinesInIf == amountOfCircles){
		
		
		for(int i =0 ; i <vb1_functions.getChildren().size() ; i++){
			Node node = vb1_functions.getChildren().get(i);
			if(node.getClass().getSimpleName().equals("VBoxIFBlock")){
                            System.out.println("ifblock i " + i);
				VBoxIFBlock vbFunction = (VBoxIFBlock) node;
				IFBlock ifblock = new IFBlock();
				if(vbFunction.getFunctionOrEvent() instanceof Function){
					Function fct = (Function) vbFunction.getFunctionOrEvent();
					ifblock.setFunction(fct);
				}else{ // todo change!
                                    FactType eventType = (FactType) vbFunction.getFunctionOrEvent();

                                    ifblock.setEvent(eventType);
				}
                                System.out.println(" ---- type is " + vbFunction.getType());
				ifblock.setType(vbFunction.getType());
                              
				// add bindings
				
				for(CmtLine line : lines){
					Binding binding = new Binding();
					if(vb1_functions.getChildren().indexOf(vbFunction) == line.getIndexInFunction()){
						BindingInfo info = line.getInfo();
						BindingIF ifbind = new BindingIF();
						ifbind.setIfParameter(info.getParameter1());
						binding.setStartBinding(ifbind);
						if(line.getIndexCrT() == 0){ // kan enkel fact zijn geen time!
                                                    
							binding.setEndBinding(getBindingInputFact(line, (IFactType)info.getBinding2()));
						}else{
							if(line.getIndexCrT() > 0){
							   
								binding.setEndBinding(getBindingInputField(line, (IFactType)info.getBinding2(), info.getParameter2()));
							}
							
						}
						
						ifblock.addBinding(binding);
						
					}
				}
				
				if(i>0){
					// get operator
					HBox hbHeader = (HBox) vbFunction.getChildren().get(0);
					Operator oper = new Operator();
					for(Node nodeHb : hbHeader.getChildren()){
						if(nodeHb.getClass().isAssignableFrom(ComboBox.class)){
							String op = ((ComboBox) nodeHb).getSelectionModel().getSelectedItem().toString();
							oper.setOperator(op);
							
						}
					}
					temp.addIfBlock(ifblock, oper);
				}else{
				Operator oper = new Operator();
				oper.setOperator("");
				temp.addIfBlock(ifblock, oper);
                                }
			}
                        
			}
			return true; // fix check fields
		}else{
			return false;
		}
		
	}
	
	private String getModifiedString(String tf_string, String defaultName){
		
		String tempNameFinal = ""; 
		if(!tf_string.equals(defaultName) && !tf_string.isEmpty()){
			String[] str = tf_string.split("\\s");
			for(String st : str){
				 char[] stringArray = st.trim().toCharArray();
				 System.out.println(stringArray[0]);
				char ch = Character.toUpperCase(stringArray[0]);
				stringArray[0] = ch;
				String fst = new String(stringArray);
			
				 System.out.println(fst);
			
				tempNameFinal += fst;
			}
			
		}else{
			tempNameFinal = defaultName;
		}
		return tempNameFinal;
	}
		
	@FXML
	public void onActionSave(){
		if(!isRuleTemplate){
			TemplateHA temp = new TemplateHA();
			
			                
			if(populateLeftHandSideTemplate(temp) && !(tf_nameTemplate.getPromptText().equals(tf_nameTemplate.getText()))){
			String name = getModifiedString(tf_nameTemplate.getText(), tf_nameTemplate.getPromptText());
			outputHA = new OutputHA();
			temp.setName(name);
			
			
			
			VBox vb =(VBox) vb3_output.getChildren().get(0);
			ObservableList<Node> itemsvb = vb.getChildren();
			HBox hbvb = (HBox) itemsvb.get(0);
                        TextField actName = (TextField)hbvb.getChildren().get(0);
			
			

			outputHA.setName(getModifiedString(actName.getText(),actName.getPromptText()));
			
			
			for(CmtLine line : lines){
				if(line.getIndexInOutput() != -1){
          
					Binding binding = new Binding();
					BindingInfo info = line.getInfo();
					if(line.getIndexCrS() == 0){
						binding.setStartBinding(getBindingInputFact(line, (IFactType)info.getBinding1() ));
					}else{
						if(line.getIndexCrS() >0){
							binding.setStartBinding(getBindingInputField(line, (IFactType)info.getBinding1() , info.getParameter1()));
						}
					}
					
					BindingOutput outBin = new BindingOutput();
					outBin.setOutputObj(outputHA);
					outBin.setParType(info.getParameterType2());
					VBox vbPar =(VBox) vb3_output.getChildren().get(0);
					ObservableList<Node> itemsPar = vbPar.getChildren();
					HBox hb = (HBox) itemsPar.get(line.getIndexCrT());
					String par = ((TextField)hb.getChildren().get(2)).getText();
					outBin.setParameter(par + line.getIndexInOutput());
					binding.setEndBinding(outBin);
					outputHA.addBinding(binding);
					// par tf
										
					
					
					
				}
			}
			temp.setOutput(outputHA);
			CMTClient.addTemplateHAInCMT(temp);
			onActionCancel();
			}else{
				
				final JPanel panel = new JPanel();

			    JOptionPane.showMessageDialog(panel, "Please connect all circles in IF part and add a template name!", "Error", JOptionPane.ERROR_MESSAGE);

				// notify niet ingevult of geen name template 
				
				
				
			}
			
			
		}else{
			if(isRuleTemplate){
				TemplateActions temp = new TemplateActions();
				String name = getModifiedString(tf_nameTemplate.getText(), "Give me a name");
				
				if(populateLeftHandSideTemplate(temp) && !name.equals("Give me a name")){
				temp.setName(name);
                                temp.setRuleName("");
				                                System.out.println(" size ifblocks exp " + temp.getIfBlocks().size());
				ObservableList<Node> itemsVb3 = vb3_output.getChildren();
				
				for(Node node: itemsVb3){
					if(node.getClass().getSimpleName().equals("VBoxAction")){
						ActionClient action = ((VBoxAction) node).getAction();
                                                
						if(action != null){
                                                    ArrayList<ActionField> actFields = action.getFields();
							ObservableList<Node> itemsVbAction = ((VBoxAction) node).getChildren();
							for(int i = 1 ; i< itemsVbAction.size(); i++){
								HBox hb = (HBox) itemsVbAction.get(i);
								String parName = ((Label)hb.getChildren().get(0)).getText();
								String parValue = "";
								Node nodeValue = hb.getChildren().get(1);
								if(nodeValue.getClass().getSimpleName().equals("TextField")){
									// check format maybe
									parValue = ((TextField) nodeValue).getText();
								}else{
									if(nodeValue.getClass().getSimpleName().equals("ComboBox")){
										parValue = ((String)((ComboBox) nodeValue).getSelectionModel().getSelectedItem());
									}
								}
								
                                                                for(ActionField f:actFields){
                                                                    if(f.getName().equals(parName)){
                                                                        f.setValue(parValue);
                                                                    }
                                                                }
								
								
							}
						}
						temp.addAction(action);
			
					}
				}
				
				CMTClient.addTemplateActionsInCMT(temp);
				onActionCancel();
				}else{
					
					// notify niet ingevult 

					final JPanel panel = new JPanel();

				    JOptionPane.showMessageDialog(panel, "Please connect all circles in IF part and add a template name!", "Error", JOptionPane.ERROR_MESSAGE);

					// notify niet ingevult of geen name template 
					
					
					
				}
			}
			
		}
		
		
	}
	
	@FXML
	public void onActionCancel(){
		
		Node node = pane.getScene().lookup("#ap_workbench");
		if(node.getClass().getSimpleName().equals("AnchorPane")){
			AnchorPane ap = (AnchorPane) node;
			ap.getChildren().remove(mainVb);
		}
			
		for(CmtLine line : lines){
			pane.getChildren().remove(line);
		}
		lines.clear();
		circles.clear();
                okIndexsIFBlock.clear();
		// + remove line from scene root
	}

}

/*
	private HBox getHeaderSubBox(boolean isFact, Object obj, VBox vb_holdingHeader, PositionInMakeTemplate pos){
		HBox hb_header = new HBox();
		CmtCircle crIr = null;
		hb_header.setAlignment(Pos.CENTER_LEFT);
		hb_header.setPadding(new Insets(5, 2, 4, 2));
		Label lb_name = new Label();
		//lb_name.setStyle("-fx-text-fill: white"); .labInHeaderVbInput
		lb_name.getStyleClass().add("labInHeaderVbInput");
		lb_name.setMinWidth(50);
		lb_name.setPrefWidth(50);
		Button close = new Button();
		close.getStyleClass().add("buttonClose");
		if(obj instanceof Function){
                    Pane pane9 = new Pane();
                    pane9.setMinWidth(5);
                    pane9.setPrefWidth(5);
                    hb_header.getChildren().add(pane9);
                    lb_name.setText(((Function) obj).getName());
                    lb_name.getStyleClass().add("labInHeaderVbInputEvent");
                    hb_header.getChildren().add(lb_name);
                    close.setOnAction((event) ->{		
			int index = vb1_functions.getChildren().indexOf(vb_holdingHeader);	
			LinkedList<CmtLine> copyLines = new LinkedList<CmtLine>();
			for(CmtLine lin : lines){
                            copyLines.add(lin);
			}
			for(CmtLine lineCmt : copyLines){				
                            if(lineCmt.getIndexInFunction() == index){
                                pane.getChildren().remove(lineCmt);
				lines.remove(lineCmt);
                            }
                            if(lineCmt.getIndexInFunction()> index){
                                lineCmt.setIndexInFunction(lineCmt.getIndexInFunction() - 2) ;
				VBox fields = (VBox)vb_holdingHeader.getChildren().get(1);
				fields.getChildren().size();				
				lineCmt.setStartY(lineCmt.getStartY()-((fields.getChildren().size()+1) * 30));
                            }	
			}
                        vb1_functions.getChildren().remove(vb1_functions.getChildren().indexOf(vb_holdingHeader) +1);
			vb1_functions.getChildren().remove(vb_holdingHeader);
			if(vb1_functions.getChildren().size()>1){
                            VBox vb = (VBox)vb1_functions.getChildren().get(0);
                            HBox hbHeader = (HBox) vb.getChildren().get(0);		
                            hbHeader.getChildren().remove(2);
                            Pane pane = new Pane();
                            pane.setMinWidth(90);
                            pane.setPrefWidth(90);
                            hbHeader.getChildren().add(2, pane);
			}
                    });
		}else{
                    if(obj instanceof IFactType){
			IFactType fact = (IFactType) obj;
			String name = "";
                        if(isFact){
                            name = ((Fact)fact).getClassName();
                        }else{
                            name = ((FactType)fact).getClassName();
                        }
                        lb_name.setText(name);
                                        
					if(!(Time.class.isAssignableFrom(fact.getClass()))){ 
						Pane pane = new Pane();
						pane.setMinWidth(5);
						pane.setPrefWidth(5);
						CmtCircle cr = getCmtCircle(fact, PositionCmtCircle.InputLeft);	
						cr.setParameter("");
						cr.setTypeOfParameter(fact.getClass().getName());
						cr.setIndexInput(vb2_input.getChildren().size()-1);
						cr.setIndexOfCrInVB(0);
						hb_header.getChildren().add(pane);
						hb_header.getChildren().add(cr);
						Pane pane9 = new Pane();
						pane9.setMinWidth(10);
						pane9.setPrefWidth(10);
						hb_header.getChildren().add(pane9);
						hb_header.getChildren().add(lb_name);
					}else{
						Pane pane9 = new Pane();
						pane9.setMinWidth(5);
						pane9.setPrefWidth(5);
						hb_header.getChildren().add(pane9);
						hb_header.getChildren().add(lb_name);
					}
                                            // is fact or facttype type == fact
						if(!(Time.class.isAssignableFrom(fact.getClass())) && !(Activity.class.isAssignableFrom(fact.getClass())) && pos == PositionInMakeTemplate.Input){
							String idField = fact.getClass().getAnnotation(UriFactType.class).id();
							Field field = fact.getClass().getDeclaredField(idField);
							Object objId = field.get(fact);
							HashSet<IFactType> factsOfType = RmiInterface.getRMI().getStub().getFacts();
							LinkedList<String> list =  new LinkedList<String>();
							for(IFactType factType : factsOfType){
								if(factType.getClass().isAssignableFrom(fact.getClass())){
									list.add(factType.getClass().getDeclaredField(idField).get(factType).toString());
								}
							}
							ComboBox<String> box = getComboBoxInput(list);
							if(objId instanceof String){
								String idString = (String) objId;
								if(idString == null || (idString.equals(""))){
									box.getSelectionModel().select("Any");
								}else{
									box.getSelectionModel().select(idString);
								}
							}else{
								if(objId == null){
									box.getSelectionModel().select("Any");
								}else{
									box.getSelectionModel().select(objId.toString());
								}
							}
							Pane pane1 = new Pane();
							pane1.setMinWidth(5);
							pane1.setPrefWidth(5);
							hb_header.getChildren().add(pane1);
							hb_header.getChildren().add(box);
							Pane pane2 = new Pane();
							if(!isRuleTemplate)  {
							pane2.setMinWidth(10);
							pane2.setPrefWidth(10);
							hb_header.getChildren().add(pane2);
							crIr = getCmtCircle(fact, PositionCmtCircle.InputRight);
							crIr.setParameter("");
							crIr.setTypeOfParameter(fact.getClass().getName());
							crIr.setId("#" + fact.getClass().getSimpleName() + Integer.toString(vb2_input.getChildren().size()-1));
							crIr.setIndexInput(vb2_input.getChildren().size()-1);
							crIr.setIndexOfCrInVB(0);
								Button bt_toOutput = getToutputButton(fact, hb_header, null, vb2_input.getChildren().size()-1, 0);
								hb_header.getChildren().add(bt_toOutput);
							}else{
								pane2.setMinWidth(53);
								pane2.setPrefWidth(53);
								hb_header.getChildren().add(pane2);
							}
						}else{
							//if(pos==PositionInMakeTemplate.Function){
								lb_name.getStyleClass().add("labInHeaderVbInputEvent");
							//}
						}	
						
						close.setOnAction((event) ->{
							if(pos == PositionInMakeTemplate.Input){
								int index = vb2_input.getChildren().indexOf(vb_holdingHeader);
								
								LinkedList<CmtLine> copyLines = new LinkedList<CmtLine>();
								for(CmtLine lin : lines){
									copyLines.add(lin);
								}
								for(CmtLine lineCmt : copyLines){
											
											if(lineCmt.getIndexInInput() == index && lineCmt.getIndexInOutput() != -1){
												
												for(CmtLine li : lines){
									
														if(li.getIndexInOutput()>lineCmt.getIndexInOutput()){
														
															li.setIndexInOutput(li.getIndexInOutput()-1);
															li.setEndY(li.getEndY()-28);
														}
														
										
												}
													
												vb_outputHa.getChildren().remove(lineCmt.getIndexInOutput());
												pane.getChildren().remove(lineCmt);
												
												
												lines.remove(lineCmt);
												System.out.println("lines size " + lines.size());
											
											}else{
												if(lineCmt.getIndexInInput() == index && lineCmt.getIndexInFunction() != -1){
													pane.getChildren().remove(lineCmt);	
													lines.remove(lineCmt);
												}
											}
											
											
											if(lineCmt.getIndexInInput()> index && lineCmt.getIndexInOutput() != -1){
												
												lineCmt.setIndexInInput(lineCmt.getIndexInInput() - 2) ;
												VBox fields = (VBox)vb_holdingHeader.getChildren().get(1);
												fields.getChildren().size();
												
												lineCmt.setStartY(lineCmt.getStartY()-((fields.getChildren().size()+1) * 30));
											}else{
												if(lineCmt.getIndexInInput()> index && lineCmt.getIndexInFunction() != -1){
													lineCmt.setIndexInInput(lineCmt.getIndexInInput() - 2) ;
													VBox fields = (VBox)vb_holdingHeader.getChildren().get(1);
													fields.getChildren().size();
													
													lineCmt.setEndY(lineCmt.getEndY()-((fields.getChildren().size()+1) * 30));
													
													
													
												}
											}
											
									
								}
								
								
								vb2_input.getChildren().remove(vb2_input.getChildren().indexOf(vb_holdingHeader) +1);
								vb2_input.getChildren().remove(vb_holdingHeader);
								// remove all lines from pane + lines
								// remove circle from circles
					}else{
						if(pos == PositionInMakeTemplate.Function){
							int index = vb1_functions.getChildren().indexOf(vb_holdingHeader);
							
							LinkedList<CmtLine> copyLines = new LinkedList<CmtLine>();
							for(CmtLine lin : lines){
								copyLines.add(lin);
							}
							for(CmtLine lineCmt : copyLines){
										
										if(lineCmt.getIndexInFunction() == index){
											System.out.println("same inderx " +index);
											System.out.println("line index function " + line.getIndexInFunction() + " line index input " + line.getIndexInInput());
											pane.getChildren().remove(lineCmt);
											lines.remove(lineCmt);
										}
										
										if(lineCmt.getIndexInFunction()> index){
											System.out.println(index);
											System.out.println("line index function " + line.getIndexInFunction() + " line index input " + line.getIndexInInput());
											lineCmt.setIndexInFunction(lineCmt.getIndexInFunction() - 2) ;
											VBox fields = (VBox)vb_holdingHeader.getChildren().get(1);
											fields.getChildren().size();
											
											lineCmt.setStartY(lineCmt.getStartY()-((fields.getChildren().size()+1) * 30));
										}
										
								
							}
							
							
							vb1_functions.getChildren().remove(vb1_functions.getChildren().indexOf(vb_holdingHeader) +1);
							vb1_functions.getChildren().remove(vb_holdingHeader);
							if(vb1_functions.getChildren().size()>1){
								
								VBox vb = (VBox)vb1_functions.getChildren().get(0);
								HBox hbHeader = (HBox) vb.getChildren().get(0);
								
								hbHeader.getChildren().remove(2);
								Pane pane = new Pane();
								pane.setMinWidth(90);
								pane.setPrefWidth(90);
								hbHeader.getChildren().add(2, pane);
							}
							// remove all lines from pane + lines
							// remove circle from circles
						
					}
					}
							});
				}
		
			
		}
		
		if(pos == PositionInMakeTemplate.Function){
			
			if(vb1_functions.getChildren().size() > 1){
				Pane pane = new Pane();
				pane.setMinWidth(35);
				pane.setPrefWidth(35);			
				hb_header.getChildren().add(getNewComboBoxOperators());
				hb_header.getChildren().add(pane);		
				lb_name.getStyleClass().add("labInHeaderVbFunction");
				lb_name.setMinWidth(130);
				lb_name.setPrefWidth(130);
			}
		}
		
		hb_header.getChildren().add(close);
		Pane pane5 = new Pane();
		pane5.setMinWidth(5);
		pane5.setPrefWidth(5);
		hb_header.getChildren().add(pane5);
		if(crIr != null && !isRuleTemplate){
			hb_header.getChildren().add(crIr);
		}
		return hb_header;
	}
*/