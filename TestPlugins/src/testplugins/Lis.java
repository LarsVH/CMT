/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package testplugins;

import be.ac.vub.wise.cmtclient.blocks.ActionClient;
import be.ac.vub.wise.cmtclient.blocks.Event;
import be.ac.vub.wise.cmtclient.blocks.Fact;
import be.ac.vub.wise.cmtclient.blocks.FactType;
import be.ac.vub.wise.cmtclient.blocks.Function;
import be.ac.vub.wise.cmtclient.blocks.Rule;
import be.ac.vub.wise.cmtclient.blocks.TemplateActions;
import be.ac.vub.wise.cmtclient.blocks.TemplateHA;
import be.ac.vub.wise.cmtclient.core.CMTListener;

/**
 *
 * @author Sandra
 */
public class Lis implements CMTListener{

    @Override
    public void newFactTypeAdded(FactType newFactType) {
        System.out.println("-- fact type registered " + newFactType.getClassName());
    }

    @Override
    public void newEventTypeAdded(FactType newEventType) {
        System.out.println("-- event type registered " + newEventType.getClassName());
    }

    @Override
    public void newFunctionAdded(Function newFunction) {
    System.out.println("-- function added " + newFunction.getName());    
    }

    @Override
    public void newTemplateHAAdded(TemplateHA temp) {
        System.out.println("-- temp HA added " + temp.getName());
    }

    @Override
    public void newTemplateActionsAdded(TemplateActions temp) {
        System.out.println("-- temp action added: " + temp.getName());
    }

    @Override
    public void newFactAdded(Fact newFact) {
        System.out.println("-- fact added " + newFact.getClassName());
    }

    @Override
    public void newEventAdded(Event newEvent) {
        System.out.println("-- event added " + newEvent.getClassName());
    }

    @Override
    public void newRuleAdded(Rule newRule) {
        System.out.println("-- rule added " + newRule.getName());
    }

    @Override
    public void actionInvoked(ActionClient action) {
        System.out.println("-- action invoked " + action.getName());
    }

    @Override
    public void actionAdded(ActionClient action) {
        System.out.println("-- action added: " + action.getName());
    }

    @Override
    public void currentContext(Event currentEvent) {
        System.out.println("-- current context:  " + currentEvent.getClassName());
    }
    
}
