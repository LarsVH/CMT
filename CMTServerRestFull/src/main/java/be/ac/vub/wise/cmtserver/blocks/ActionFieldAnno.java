/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ac.vub.wise.cmtserver.blocks;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 *
 * @author Sandra
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface ActionFieldAnno {
    String list();
	String format();
}
