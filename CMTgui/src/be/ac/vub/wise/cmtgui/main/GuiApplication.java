package be.ac.vub.wise.cmtgui.main;

import be.ac.vub.wise.cmtclient.core.CMTClient;
import be.ac.vub.wise.cmtclient.util.Constants;
import java.util.LinkedList;


import be.ac.vub.wise.cmtgui.controllers.MainController;
import be.ac.vub.wise.cmtgui.util.ConstantsGUI;


import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;


public class GuiApplication extends javafx.application.Application {

    // Saved variables to access later on in static context.
    public static Stage stage;
    private static Parent root;
    private static Scene scene;

    // run this class.
    public static void main(String[] args) throws Exception{
        Constants.setURLCMT("http://localhost:8080/CMTServerRestFull/cmt");
        Constants.setWSCMT("ws://localhost:8080/CMTServerRestFull/pubsub");
        launch();
    }

    @Override
    public void start(Stage stage) throws Exception {

    	 MainController main = new MainController();
         
         root = main.getRoot();

         
       // UserApplicationController controller = new UserApplicationController(root, primitives);
       // fxmlLoader.setController(controller);

        Scene scene = new Scene(root, 1600, 960);
        scene.getStylesheets().add(ConstantsGUI.PATHCSS+"styles.css");
        
        stage.setTitle("CMT - Context Modelling Toolkit");
        stage.getIcons().add(new Image(ConstantsGUI.ICON16));
        stage.getIcons().add(new Image(ConstantsGUI.ICON32));
        stage.setScene(scene);
        stage.show();
     
        stage.setOnCloseRequest((event)->{
        
        	Platform.exit();
        	System.exit(0);
        }); 
        CMTClient.startWS();
    }
    @Override
    public void init() throws Exception{

    }



}
