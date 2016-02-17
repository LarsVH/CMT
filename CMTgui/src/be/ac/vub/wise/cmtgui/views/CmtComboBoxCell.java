package be.ac.vub.wise.cmtgui.views;

import be.ac.vub.wise.cmtgui.util.CmtComboxItem;
import be.ac.vub.wise.cmtgui.util.Styles;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.control.ListCell;
import javafx.scene.control.TreeCell;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;

public class CmtComboBoxCell  extends ListCell<CmtComboxItem> {

    
     @SuppressWarnings({ "unchecked", "rawtypes" })
	 @Override 
     protected void updateItem( CmtComboxItem item, boolean empty) {

         super.updateItem(item, empty);
         System.out.println(empty);
         setText(item.getObj().getClass().getSimpleName());

         this.selectedProperty().addListener(new ChangeListener(){

				@Override
				public void changed(ObservableValue arg0, Object arg1,
						Object arg2) {
				//	System.out.println(arg0);
					if((boolean) arg1){
						
		                	setStyle("-fx-text-fill: black");
		                }
					if((boolean) arg2){
						
						setStyle("-fx-text-fill: black");
					}	
					}
				});
         
     }
}
