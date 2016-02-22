package be.ac.vub.wise.cmtgui.util;

import javafx.util.StringConverter;

public class CmtComboboxConverter extends StringConverter<CmtComboxItem>{

	CmtComboxItem item;
	
	@Override
	public CmtComboxItem fromString(String string) {
		// TODO Auto-generated method stub
		return item;
	}

	@Override
	public String toString(CmtComboxItem object) {
			item = object;
		return object.getObj().getClass().getSimpleName();
	}
	

}
