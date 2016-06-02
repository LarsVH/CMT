/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ac.vub.wise.cmtclient.blocks;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;

/**
 *
 * @author Sandra
 */

@Retention(RetentionPolicy.RUNTIME)
public @interface AInput {
    public enum Input{
        Variable , Fix
    }
    
    Input input() default Input.Fix;
    String format() default "";
    String[] options() default {};
}




