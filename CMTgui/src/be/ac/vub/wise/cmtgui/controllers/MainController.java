package be.ac.vub.wise.cmtgui.controllers;

import be.ac.vub.wise.cmtgui.util.ConstantsGUI;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;





import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Tab;
import static javafx.scene.input.DataFormat.URL;

public class MainController {
	
	@FXML
    private UserTab userTabController;
    @FXML
    private ExpertTab expertTabController ;
  
    
    @FXML
    private Tab tb_activities;
    
    Parent root;
	
	public MainController(){
            try {
                
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/be/ac/vub/wise/cmtgui/fxml/main.fxml"));
   	 	fxmlLoader.setController(this);
               // System.out.println(fxmlLoader.getLocation().getPath());
	
			root = fxmlLoader.load();
			System.out.println(root);
		} catch (MalformedURLException ex) {
                Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
            }
		userTabController.setDnDZones();
		userTabController.populateLists();
		expertTabController.initController(root);
		
		tb_activities.setOnSelectionChanged((event) ->{
			if(root.getClass().getSimpleName().equals("Pane")){
				
				
				expertTabController.resetExpertTab();
				expertTabController.setView();
			}
		});
		
		
		
	}

	public Parent getRoot(){
		return root;
	}
}
