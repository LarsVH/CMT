package be.ac.vub.wise.cmtgui.views;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.control.TreeCell;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;

import be.ac.vub.wise.cmtgui.util.Styles;

public class ActionCell extends TreeCell<String> {
	 public ActionCell() { 
		 
	 }
   
    @SuppressWarnings({ "unchecked", "rawtypes" })
	 @Override 
    protected void updateItem(final String item, boolean empty) {

        super.updateItem(item, empty);
        setText(item);
        setStyle(Styles.colourAction);
        this.selectedProperty().addListener(new ChangeListener(){

				@Override
				public void changed(ObservableValue arg0, Object arg1,
						Object arg2) {
					System.out.println(arg1);
					if((boolean) arg1){
						
		                	setStyle(Styles.selectedAction);
		                }
					if((boolean) arg2){
						
						setStyle(Styles.colourAction);
					}	
					}
				});
        
            setOnDragDetected(
                    new EventHandler<MouseEvent>() {
                        @Override
                        public void handle(MouseEvent me) {
                            Dragboard db = startDragAndDrop(TransferMode.ANY);
                            ClipboardContent content = new ClipboardContent();
                            String info = "action;"+item;
                            content.putString(info);
                            db.setContent(content);
                        }
                    }
            );
        
    }

}
