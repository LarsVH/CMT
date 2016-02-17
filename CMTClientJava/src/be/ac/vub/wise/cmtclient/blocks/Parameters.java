package be.ac.vub.wise.cmtclient.blocks;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Parameters {
	String parameters();
}
