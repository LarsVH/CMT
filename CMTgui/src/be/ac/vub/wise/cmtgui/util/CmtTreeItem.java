package be.ac.vub.wise.cmtgui.util;

import javafx.scene.control.TreeItem;

public class CmtTreeItem extends TreeItem<String>{
	
	private Object obj;
	
	public CmtTreeItem(Object obj){
		super(obj.toString());
		this.obj = obj;
	}

	public Object getObj() {
		return obj;
	}

	public void setObj(Object obj) {
		this.obj = obj;
	}
	
	

}
