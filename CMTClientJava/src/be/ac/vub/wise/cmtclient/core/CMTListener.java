/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ac.vub.wise.cmtclient.core;

import be.ac.vub.wise.cmtclient.blocks.ActionClient;
import be.ac.vub.wise.cmtclient.blocks.Event;
import be.ac.vub.wise.cmtclient.blocks.Fact;
import be.ac.vub.wise.cmtclient.blocks.FactType;
import be.ac.vub.wise.cmtclient.blocks.Function;
import be.ac.vub.wise.cmtclient.blocks.Rule;
import be.ac.vub.wise.cmtclient.blocks.TemplateActions;
import be.ac.vub.wise.cmtclient.blocks.TemplateHA;

/**
 *
 * @author Sandra
 */
public interface CMTListener {
    
    public void newFactTypeAdded(FactType newFactType);
    public void newEventTypeAdded(FactType newEventType);
    public void newFunctionAdded(Function newFunction);
    public void newTemplateHAAdded(TemplateHA temp);
    public void newTemplateActionsAdded(TemplateActions temp);
    public void newFactAdded(Fact newFact);
    public void newEventAdded(Event newEvent);
    public void newRuleAdded(Rule newRule);
    public void actionInvoked(ActionClient action);
    public void actionAdded(ActionClient action);
    public void currentContext(Event currentEvent);
   
}
