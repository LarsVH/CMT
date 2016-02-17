package be.ac.vub.wise.cmtgui.controllers;

import be.ac.vub.wise.cmtclient.blocks.ActionClient;
import be.ac.vub.wise.cmtclient.blocks.Fact;
import be.ac.vub.wise.cmtclient.blocks.FactType;
import be.ac.vub.wise.cmtclient.blocks.Function;
import be.ac.vub.wise.cmtclient.blocks.IFactType;
import be.ac.vub.wise.cmtclient.blocks.Rule;
import be.ac.vub.wise.cmtclient.blocks.Template;
import be.ac.vub.wise.cmtclient.blocks.TemplateActions;
import be.ac.vub.wise.cmtclient.blocks.TemplateHA;
import be.ac.vub.wise.cmtclient.core.CMTClient;
import be.ac.vub.wise.cmtclient.core.CMTListener;
import be.ac.vub.wise.cmtgui.util.ConstantsGUI;
import java.util.HashSet;
import java.util.Iterator;




import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.util.Callback;

public class ExpertTab extends VBox implements CMTListener{
	
    @FXML AnchorPane ap_workbench;
    @FXML ListView<Function> lv_functions;
    @FXML TreeView<IFactType> tv_inputs;
    @FXML ListView<ActionClient> lv_actions;
    @FXML ListView<Template> lv_templates;
    Parent root;
    TemplateExpertController temp = null;
    ObservableList<Function> listFunctions;
    ObservableList<Template> listTemplates;
    ObservableList<ActionClient> listActions;
    TreeItem<IFactType> ti_root_objects;
	
    public ExpertTab(){
        CMTClient.addListener(this);
    }

    public void initController(Parent root){		
		
        this.root = root;
	HashSet<Function> functionClasses = CMTClient.getAvailableFunctions();
	listFunctions = FXCollections.observableArrayList(); // add all Function obj
	lv_functions.setCellFactory(getCallbackFunction());
	for(Function classfct : functionClasses){ // Function
            listFunctions.add(classfct);
	}
	lv_functions.setItems(listFunctions);
	lv_functions.setOnDragDetected(new EventHandler<Event>() {
            public void handle(Event event) {
		Dragboard db = lv_functions.startDragAndDrop(TransferMode.ANY);
		Function fct = lv_functions.getSelectionModel().getSelectedItem();
		ClipboardContent content = new ClipboardContent();
		content.put(ConstantsGUI.functionDataFormat, fct.getName());
                db.setContent(content);
            }
	});
	
	listTemplates = FXCollections.observableArrayList();
	HashSet<TemplateActions> templatesAct = CMTClient.getAvailableTemplateActions();
        HashSet<TemplateHA> templatesHA = CMTClient.getAvailableTemplateHA();
	for(TemplateActions temp : templatesAct){
            listTemplates.add(temp);
        }
        for(TemplateHA temp1 : templatesHA){
            listTemplates.add(temp1);
        }
        lv_templates.setCellFactory(getCallbackTemplate());
	lv_templates.setItems(listTemplates);
			
	// populate input
	ti_root_objects = new TreeItem<IFactType>();
	ti_root_objects.setExpanded(false);
	tv_inputs.setRoot(ti_root_objects);
	tv_inputs.setShowRoot(false);        
	tv_inputs.setCellFactory(new Callback<TreeView<IFactType>, TreeCell<IFactType>>(){        	 
	    @Override
	    public TreeCell<IFactType> call(TreeView<IFactType> p) {            
	        TreeCell<IFactType> cell = new TreeCell<IFactType>(){
	            @Override
	            protected void updateItem(IFactType t, boolean bln) {                   	
	                super.updateItem(t, bln);
	                if (t != null) {
	                    if(t.getClass().isAssignableFrom(FactType.class)){
                                FactType type = (FactType) t;
                                setText(type.getClassName());
                            }else{
                                if(t.getClass().isAssignableFrom(Fact.class)){
                                    Fact fact = (Fact)t;
                                    System.out.println("--- " + fact.getUriValue());
                                    setText(fact.getUriValue());
                                }else{
                                    setText("to debug");
                                }
                            }
                            System.out.println("in --------------------------------" + t.getClass().getSimpleName() + t.getClass().isAssignableFrom(FactType.class));
                        }  
	            };
                };
	        return cell;
	    }
        });
	HashSet<FactType> factTypes = CMTClient.getAvailableFactTypes();
        HashSet<FactType> factTypesEvents = CMTClient.getAvailableEventTypes();
	HashSet<Fact> facts = CMTClient.getAvailableFacts();		
	int indexTime=0;
	int indexAct=0;
        TreeItem<IFactType> tp = new TreeItem<IFactType>(new FactType("Time","time","Time", null));
        tp.setExpanded(true);
        ti_root_objects.getChildren().add(tp);
        indexTime = ti_root_objects.getChildren().indexOf(tp);
        TreeItem<IFactType> tpa = new TreeItem<IFactType>(new FactType("Activity","activity","Activity", null));
        tpa.setExpanded(true);
        ti_root_objects.getChildren().add(tpa);
        indexAct = ti_root_objects.getChildren().indexOf(tpa);
        for(FactType type : factTypes){
	    TreeItem<IFactType> tpf = new TreeItem<IFactType>(type);
            tpf.setExpanded(true);
            ti_root_objects.getChildren().add(tpf);
	}
	for(FactType type : factTypesEvents){
            if(type.getType().equals("time")){ 
                TreeItem<IFactType> tpt = new TreeItem<IFactType>(type);
	        ti_root_objects.getChildren().get(indexTime).getChildren().add(tpt);
	    }else{
		if(type.getType().equals("activity")){ 
                    TreeItem<IFactType> tpac = new TreeItem<IFactType>(type);
                    ti_root_objects.getChildren().get(indexAct).getChildren().add(tpac);
          	}
            }    	
	} 
	for(Fact fact : facts){
            System.out.println(" ------ size facts expert tab " + facts.size());
            System.out.println(" ------ size facts expert tab " + fact.getClassName());
            Iterator<TreeItem<IFactType>> it = ti_root_objects.getChildren().iterator();
            while(it.hasNext()){
                TreeItem<IFactType> node = it.next();
    		FactType typeInTree = (FactType) node.getValue();
    		if(fact.getClassName().equals(typeInTree.getClassName())){
            System.out.println(" ------ size facts expert tab in if ok ");

                    node.getChildren().add(new TreeItem<IFactType>(fact));
                    
                }
            }
	}
	      
	tv_inputs.setOnDragDetected(new EventHandler<Event>() {
            public void handle(Event event) {
		TreeItem<IFactType> factTreeItem = tv_inputs.getSelectionModel().getSelectedItem();
		
                IFactType fact = factTreeItem.getValue();              
		Dragboard db = tv_inputs.startDragAndDrop(TransferMode.ANY);
		ClipboardContent content = new ClipboardContent();
		   
                if(fact instanceof FactType){
                    FactType type = (FactType) fact;
                    if(type.getType().equals("time") || type.getType().equals("activity")){
                        content.put(ConstantsGUI.eventDataFormat, fact);
                    }else{
                        content.put(ConstantsGUI.inputDataFormat, fact);
                    }
                }else{
                    content.put(ConstantsGUI.inputDataFormat, fact);
                }
                db.setContent(content);
		
		         
            }
        });

	// populate actions	
	lv_actions.setCellFactory(new Callback<ListView<ActionClient>, ListCell<ActionClient>>(){
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
        });
		
	HashSet<ActionClient> actionSet = CMTClient.getAvailableActions();
	listActions = FXCollections.observableArrayList();
	for(ActionClient act : actionSet){
            listActions.add(act);
	}
	lv_actions.setItems(listActions);
		
	lv_actions.setOnDragDetected((event) ->{
            Dragboard db = lv_actions.startDragAndDrop(TransferMode.ANY);
            ActionClient act = lv_actions.getSelectionModel().getSelectedItem();
            ClipboardContent content = new ClipboardContent();
            content.put(ConstantsGUI.dragActionToOutputDataFormat, act);
            db.setContent(content);
	});
		
        temp  = new TemplateExpertController();
	Parent par = temp.getRoot();
	ap_workbench.getChildren().add(par);
	temp.setRootScene(root);
	temp.addDnd();
    }
	
	
    @FXML 
    public void newTemplate(){
	temp  = new TemplateExpertController();
	Parent par = temp.getRoot();
	ap_workbench.getChildren().add(par);
	temp.setRootScene(ap_workbench.getScene().getRoot());
	temp.addDnd();
    }

    private Callback<ListView<Function>, ListCell<Function>> getCallbackFunction(){
	return new Callback<ListView<Function>, ListCell<Function>>(){       	 
            @Override
            public ListCell<Function> call(ListView<Function> p) {
                ListCell<Function> cell = new ListCell<Function>(){
                    @Override
                    protected void updateItem(Function t, boolean bln) {
                        super.updateItem(t, bln);
                        if (t != null) {
                        	 String text = t.getName(); // function getname
                        	 setText(text);
                        }
                    }
                };
                return cell;
            }
        };
    }
	
    private Callback<ListView<Template>, ListCell<Template>> getCallbackTemplate(){
        return new Callback<ListView<Template>, ListCell<Template>>(){ 
            @Override
            public ListCell<Template> call(ListView<Template> p) {
                ListCell<Template> cell = new ListCell<Template>(){
                    @Override
                    protected void updateItem(Template t, boolean bln) {
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

    public void resetExpertTab(){
	if(temp != null){
            temp.onActionCancel();
	}
    }
	
    public void setView(){
	temp  = new TemplateExpertController();
	Parent par = temp.getRoot();
	ap_workbench.getChildren().add(par);
	temp.setRootScene(root);
	temp.addDnd();
    }

    // listeners
    
    @Override
    public void newFactTypeAdded(FactType newFactType){
 
        
        Platform.runLater(new Runnable() {
	    @Override
	    public void run() {
                ti_root_objects.getChildren().add(new TreeItem<IFactType>(newFactType));
                tv_inputs.setRoot(ti_root_objects);
            }});
        
    }

    @Override
    public void newFunctionAdded(Function newFunction) {
        Platform.runLater(new Runnable() {
	    @Override
	    public void run() {
                listFunctions.add(newFunction);	
                lv_functions.setItems(listFunctions);
                }});
    }

    @Override
    public void newEventTypeAdded(FactType newEventType) {
        Platform.runLater(new Runnable() {
	    @Override
	    public void run() {
                ObservableList<TreeItem<IFactType>> list = ti_root_objects.getChildren();
                for(TreeItem<IFactType> ti : list){
                    IFactType factType = ti.getValue();
                    if(factType instanceof FactType){
                        FactType nodeType = (FactType)factType;
                        if(newEventType.getType().equals("time") && nodeType.getClassName().equals("Time")){
                            ti.getChildren().add(new TreeItem<IFactType>(newEventType));
                        }else{
                            if(newEventType.getType().equals("activity") && nodeType.getClassName().equals("Activity")){
                               ti.getChildren().add(new TreeItem<IFactType>(newEventType));
                            }              
                        }
                    }
                }
                tv_inputs.setRoot(ti_root_objects);
                }});
    }

    @Override
    public void newTemplateHAAdded(TemplateHA temp) {
        System.out.println("-------------------------------------------------------------------------- new templ added!!!  " + temp.getName());
        templateAdded(temp);
    }

    @Override
    public void newTemplateActionsAdded(TemplateActions temp) {
        templateAdded(temp);
    }

    @Override
    public void newFactAdded(Fact newFact) {
        Platform.runLater(new Runnable() {
	    @Override
	    public void run() {
               
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
               
                }});
    }

    @Override
    public void newEventAdded(be.ac.vub.wise.cmtclient.blocks.Event newEvent) {
        // not relevant here in gui
    }

    @Override
    public void newRuleAdded(Rule newRule) {
        // not relevant here in gui
    }

    @Override
    public void actionInvoked(ActionClient action) {
       // not relevant here in gui
    }

    @Override
    public void actionAdded(ActionClient action) {
        Platform.runLater(new Runnable() {
	    @Override
	    public void run() {
               
        listActions.add(action);
	lv_actions.setItems(listActions);
                }});
    }

    @Override
    public void currentContext(be.ac.vub.wise.cmtclient.blocks.Event currentEvent) {
        // not relevant here in gui
    }
	
    private void templateAdded(Template temp){
        System.out.println("-------- new templ added!!!  " + temp.getName());
        Platform.runLater(new Runnable() {
	    @Override
	    public void run() {
	      	listTemplates.add(temp);
                //lv_templates.getItems().add(temp);
		lv_templates.setItems(listTemplates);	
            }
	});	
    }
}

// Not yet in listener REST
//	@Override
//	public void actionRemoved(Action action) {
//		for(Action act : listActions){
//			if(act.getClass().isAssignableFrom(action.getClass())){
//				listActions.remove(act);
//			}
//		}
//		lv_actions.setItems(listActions);
//	}
//    	@Override
//	public void contextFormRemoved(Template form) {
//		for(Template temp : listTemplates){
//			if(temp.getName().equals(form.getName())){
//				listTemplates.remove(temp);
//				lv_templates.setItems(listTemplates);
//			}
//		}
//		
//	}

