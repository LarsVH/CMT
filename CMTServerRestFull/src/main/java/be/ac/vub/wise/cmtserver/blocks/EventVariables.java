package be.ac.vub.wise.cmtserver.blocks;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface EventVariables {
	
	String list();
	String format();
	
}