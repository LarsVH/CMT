/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ac.vub.wise.cmtclient.util;

import be.ac.vub.wise.cmtclient.blocks.ActionClient;
import be.ac.vub.wise.cmtclient.blocks.ActionField;
import be.ac.vub.wise.cmtclient.blocks.Binding;
import be.ac.vub.wise.cmtclient.blocks.BindingIF;
import be.ac.vub.wise.cmtclient.blocks.BindingInputFact;
import be.ac.vub.wise.cmtclient.blocks.BindingInputField;
import be.ac.vub.wise.cmtclient.blocks.BindingOutput;
import be.ac.vub.wise.cmtclient.blocks.BindingParameter;
import be.ac.vub.wise.cmtclient.blocks.CMTField;
import be.ac.vub.wise.cmtclient.blocks.Event;
import be.ac.vub.wise.cmtclient.blocks.Fact;
import be.ac.vub.wise.cmtclient.blocks.FactType;
import be.ac.vub.wise.cmtclient.blocks.Function;
import be.ac.vub.wise.cmtclient.blocks.IFBlock;
import be.ac.vub.wise.cmtclient.blocks.IFactType;
import be.ac.vub.wise.cmtclient.blocks.OutputHA;
import be.ac.vub.wise.cmtclient.blocks.Rule;
import be.ac.vub.wise.cmtclient.blocks.Template;
import be.ac.vub.wise.cmtclient.blocks.TemplateActions;
import be.ac.vub.wise.cmtclient.blocks.TemplateHA;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

/**
 *
 * @author Sandra
 */
public class Compilers {
    
    public static Rule compileDrlRuleActivity(Template temp){
        Rule result = new Rule();
        boolean isHA = false;
        if(temp instanceof TemplateHA){
            isHA = true;
        }
        String name ="";
        if(isHA){
            name = ((TemplateHA)temp).getOutput().getName(); // check for cap
        }else{
            name = ((TemplateActions)temp).getName();
        }
        // todo compile rule 
        String drlLHS = "";
        HashSet<String> imports = new HashSet<>();
        LinkedList<IFBlock> ifBlocks = temp.getIfBlocks();
        
        for(int i = 0; i<ifBlocks.size(); i++){
            IFBlock ifBlock = ifBlocks.get(i);
            String type = ifBlock.getType();
            LinkedList<Binding> bindings = ifBlock.getBindings();
            if(bindings.size() == 0 && type.equals(Constants.TIME) || type.equals(Constants.ACTIVITY)){ // 
                // no input blocks
                FactType event = ifBlock.getEvent();
                drlLHS += event.getClassName() +"() ";
            }else{
                ArrayList<String> parsInDrl = new ArrayList<String>();
                for(int a=0; a<bindings.size(); a++){
                    Binding binding = bindings.get(a);
                    BindingIF startbind = (BindingIF) binding.getStartBinding(); // ifblock parameter
                    String startParName = startbind.getIfParameter();
                    
                    if(binding.getEndBinding() instanceof BindingInputFact){
                        BindingInputFact factBind = (BindingInputFact)binding.getEndBinding();
                        IFactType factEvent = factBind.getInputObject();
                        String classNameBinding = "";
                            String factId = "";
                            String uriField = "";
                            String parDrlBind ="";
                            if(factEvent instanceof Event){
                                Event eventBind = (Event) factEvent;
                                classNameBinding = eventBind.getClassName();
                                uriField = eventBind.getUriField();
                                parDrlBind = " $"+name+Integer.toString(i)+Integer.toString(a);
                                drlLHS += parDrlBind +": "+ classNameBinding+"(";
                                if(!eventBind.getValueUriField().isEmpty()){
                                    drlLHS += uriField+" == " +eventBind.getValueUriField()+"";
                                }
                                String importEvent = Constants.PACKAGEEVENTS + "." + classNameBinding;
                                imports.add(importEvent);
                            }else{
                                if(factEvent instanceof Fact){
                                    classNameBinding = ((Fact)factEvent).getClassName();
                                    factId = factBind.getFactId();
                                    uriField = ((Fact)factEvent).getUriField();
                                    parDrlBind = " $"+name+Integer.toString(i)+Integer.toString(a);
                                    drlLHS += parDrlBind +": "+ classNameBinding+"(";
                                    if(!factId.isEmpty()){
                                        drlLHS += uriField+" == \"" +factId+"\"";
                                    }
                                   String importFact = Constants.PACKAGEFACTS + "." + classNameBinding;
                                imports.add(importFact); 
                                }
                            }
                            
                            drlLHS += ") \n";
                            String parDrl ="";
                            if(!type.equals(Constants.FUNCTION)){
                                parDrl = startParName +"=="+ parDrlBind;
                            }else{
                                parDrl = parDrlBind;
                            }
                            parsInDrl.add(parDrl);
                        }else{
                            if(binding.getEndBinding() instanceof BindingInputField){
                                BindingInputField fieldBind = (BindingInputField)binding.getEndBinding();
                                IFactType factEvent = fieldBind.getInputObject();
                                String classNameBinding = "";
                                String factId = "";
                                String uriField = "";
                               // String inputEvent ="";
                                String parDrlBind = " $"+name+Integer.toString(i)+Integer.toString(a);
                               // String inputField ="";
                                if(factEvent instanceof Event){
                                    Event eventBind = (Event) factEvent;
                                    classNameBinding = eventBind.getClassName();
                                    String inputEvent = eventBind.getValueUriField();
                                    String inputField = eventBind.getUriField();
                                    drlLHS += parDrlBind +": "+ classNameBinding+"("+inputField +" == " + inputEvent+" )";
                                    String parDrl ="";
                                    if(!type.equals(Constants.FUNCTION)){
                                        parDrl = startParName + " == " + parDrlBind +"."+inputField;
                                    }else{
                                        parDrl = parDrlBind +"."+inputField;
                                    }
                                    String importEvent = Constants.PACKAGEEVENTS + "." + classNameBinding;
                                imports.add(importEvent); 
                                    parsInDrl.add(parDrl);
                                }else{
                                    if(factEvent instanceof Fact){
                                        classNameBinding = ((Fact)factEvent).getClassName();
                                        factId = fieldBind.getFactId();
                                        uriField = ((Fact)factEvent).getUriField();
                                        drlLHS += classNameBinding+"(";
                                    if(!factId.isEmpty()){
                                        drlLHS += uriField+" == " +factId+", ";
                                    }
                                    drlLHS += parDrlBind +": "+ fieldBind.getField().getName();
                                    drlLHS += ")";
                                    String parDrl ="";
                                    if(!type.equals(Constants.FUNCTION)){
                                        parDrl = startParName +"=="+ parDrlBind;
                                    }else{
                                        parDrl = parDrlBind;
                                    }
                                    parsInDrl.add(parDrl);
                                        String importFact = Constants.PACKAGEFACTS + "." + classNameBinding;
                                imports.add(importFact); 
                                    }
                                }
                                
                                
                                
                            }
                        }
                    }
                    if(!type.equals(Constants.FUNCTION)){
                        FactType event = ifBlock.getEvent();
                        drlLHS += event.getClassName()+"(";
                        String importEvent = Constants.PACKAGEEVENTS + "." + event.getClassName();
                                imports.add(importEvent); 
                    }else{
                        Function func = ifBlock.getFunction(); 
                        drlLHS += "eval("+func.getName()+"("; // add eval!!!
                        String importFunc = Constants.PACKAGEFUNCTIONS + "." +func.getEncapClass()+"."+ func.getName();
                        imports.add(importFunc); 
                    }
                    for(int c = 0; c<parsInDrl.size(); c++){
                        drlLHS += parsInDrl.get(c);
                        if(!(c == parsInDrl.size()-1)){
                            drlLHS += ", ";
                        }
                    }
                    
                    drlLHS += ") ";
                    if(type.equals(Constants.FUNCTION)){
                        drlLHS +=") ";
                    }
                    drlLHS += "\n";
                }
                // add operator TODO only AND now
        }
        String drlRHS = "";
        if(isHA){
           
            drlRHS += "insert( new "+ name+"());" 
                  + " \n end \n ";
            String importEvent = Constants.PACKAGEEVENTS + "." +name;
            imports.add(importEvent); 
        }else{
            // actions 
            LinkedList<ActionClient> actions = ((TemplateActions)temp).getActions();
            for(int i=0; i<actions.size(); i++){
                ActionClient action = (ActionClient) actions.get(i);
                String className = action.getName();
                ArrayList<ActionField> fields = action.getFields();
                String varDrl = "rule"+className+Integer.toString(i);
                drlRHS += className + " "+ varDrl + " = new "+className+"(); \n";
                for(ActionField field : fields){
                    drlRHS+= varDrl + ".set"+field.getName()+"("+field.getValue()+"); \n";
                }
                drlRHS += " insert("+varDrl+"); \n";
                String importAction = Constants.PACKAGEACTIONS + "." + className;
                imports.add(importAction); 
            }
            drlRHS += " end \n";
        }
        
        
        String drl = "";
        for(String im : imports){
            if(im.contains(Constants.PACKAGEFUNCTIONS)){
                drl += "import function " + im + "\n";
            }else{
                drl += "import " + im + "\n";
            }
        }
        drl += "rule \"rule"+name+"\" \n " 
                    + " when \n " + drlLHS +  "\n then \n" + drlRHS;
        result.setName("rule"+name);
        result.setDrlRule(drl);
        System.out.println("----------------------------------------- drl ruel :  " + result);
        System.out.println("----------------------------------------- drl ruel :  " + drl);
        return result;
    }
    
    // todo get rid of ouputha use event in gui code -- no time now
//    public static Event createNewActivity(TemplateHA temp){ // return facttype
//        
//        Event result = new Event();
//        OutputHA output = temp.getOutput();
//        result.setClassName(output.getName());
//        result.setExtend(Constants.ACTIVITY);
//        result.setIsCustom(true);
//        result.setUriField(""); /// fix is niet empty 
//        result.setVarFormat("");
//        ArrayList<String> varList = new ArrayList<String>();
//        result.setVarList(varList);
//        ArrayList<CMTField> fields = new ArrayList<CMTField>();
//        for(Binding bind : output.getBindings()){
//            BindingOutput parbind = (BindingOutput) bind.getEndBinding();
//            CMTField field = new CMTField(parbind.getParameter(), parbind.getParType());
//            BindingParameter bin = bind.getStartBinding();
//            if(bin instanceof BindingInputFact){
//                BindingInputFact inBind = (BindingInputFact) bin;
//                field.setValue(inBind.getFactId());
//            }else{
//                if(bin instanceof BindingInputField){
//                    BindingInputField inBind = (BindingInputField) bin;
//                    field.setValue(inBind.getFactId());
//                }
//            }
//            fields.add(field);
//        }
//        result.setFields(fields);
//        return result;
//    }
    
  public static FactType createNewActivity(TemplateHA temp){ // return facttype
        //String className, String type, String uriField, ArrayList<CMTField> fields
        
        OutputHA output = temp.getOutput();
        
        
        ArrayList<CMTField> fields = new ArrayList<CMTField>();
        for(Binding bind : output.getBindings()){
            BindingOutput parbind = (BindingOutput) bind.getEndBinding();
            CMTField field = new CMTField(parbind.getParameter(), parbind.getParType());
            BindingParameter bin = bind.getStartBinding();
            if(bin instanceof BindingInputFact){
                BindingInputFact inBind = (BindingInputFact) bin;
                field.setValue(inBind.getFactId());
            }else{
                if(bin instanceof BindingInputField){
                    BindingInputField inBind = (BindingInputField) bin;
                    field.setValue(inBind.getFactId());
                }
            }
            fields.add(field);
        }
        FactType result = new FactType(output.getName(), "activity", "", fields);
        result.setVarFormat("");
        result.setVarList( new ArrayList<String>());
        result.setIsCustom(true);
        return result;
    }
    
    
}
