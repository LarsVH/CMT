/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package testplugins;

import be.ac.vub.wise.cmtclient.blocks.AInput;
import be.ac.vub.wise.cmtclient.blocks.AInput.Input;

/**
 *
 * @author Sandra
 */
public class CisaMeeting {
    
    @AInput(input=Input.Variable, format="00:00")
    public String day;
    
    @AInput(input=Input.Variable, options={"today", "yesterday", "tomorrow"})
    public String day1;
    
    @AInput(input=Input.Fix)
    public String day2;

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getDay1() {
        return day1;
    }

    public void setDay1(String day1) {
        this.day1 = day1;
    }

    public String getDay2() {
        return day2;
    }

    public void setDay2(String day2) {
        this.day2 = day2;
    }
    
    
    
}
