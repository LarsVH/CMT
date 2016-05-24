/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ac.vub.wise.cmtserver.core;

import be.ac.vub.wise.cmtserver.blocks.ActionClient;
import be.ac.vub.wise.cmtserver.blocks.ActionField;
import be.ac.vub.wise.cmtserver.blocks.Binding;
import be.ac.vub.wise.cmtserver.blocks.BindingIF;
import be.ac.vub.wise.cmtserver.blocks.BindingInputFact;
import be.ac.vub.wise.cmtserver.blocks.BindingInputField;
import be.ac.vub.wise.cmtserver.blocks.BindingOutput;
import be.ac.vub.wise.cmtserver.blocks.BindingParameter;
import be.ac.vub.wise.cmtserver.blocks.CMTField;
import be.ac.vub.wise.cmtserver.blocks.CMTParameter;
import be.ac.vub.wise.cmtserver.blocks.EventInput;
import be.ac.vub.wise.cmtserver.blocks.Fact;
import be.ac.vub.wise.cmtserver.blocks.FactType;
import be.ac.vub.wise.cmtserver.blocks.FieldValueLimitation;
import be.ac.vub.wise.cmtserver.blocks.IFBlock;
import be.ac.vub.wise.cmtserver.blocks.OutputHA;
import be.ac.vub.wise.cmtserver.blocks.Rule;
import be.ac.vub.wise.cmtserver.blocks.Template;
import be.ac.vub.wise.cmtserver.blocks.TemplateActions;
import be.ac.vub.wise.cmtserver.blocks.TemplateHA;
import be.ac.vub.wise.cmtserver.util.Constants;
import be.ac.vub.wise.cmtserver.util.Converter;
import be.ac.vub.wise.cmtserver.util.HelperClass;
import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author Sandra
 */
public class Compilers {
    
    public static Rule compileDrlRuleActivity(Template temp){
        HashMap<Integer, String> drlDeclarationsInputBlocks = new HashMap<>();
        HashMap<Integer, ArrayList<String>> declaredParametersInDrl = new HashMap<>();
        HashMap<Integer, HashMap<String,String>> parametersIfSide = new HashMap<>();
        Rule result = new Rule();
        boolean isHA = false;
        OutputHA output = null;
        if(temp instanceof TemplateHA){
            isHA = true;
            output = ((TemplateHA)temp).getOutput();
        }
        String name ="";
        if(isHA){
            System.out.println("------------------------------------------------- output name " + name);
            
            name = ((TemplateHA)temp).getOutput().getName(); // check for cap
            System.out.println("------------------------------------------------- output name " + name);
        }else{
            name = ((TemplateActions)temp).getRuleName();
        }
        // todo compile rule 
        
        HashSet<String> imports = new HashSet<>();
        LinkedList<IFBlock> ifBlocks = temp.getIfBlocks();
        System.out.println(" size ifblocks " + ifBlocks.size());
        for(int i = 0; i<ifBlocks.size(); i++){
            IFBlock ifBlock = ifBlocks.get(i);
            String type = ifBlock.getType();
            LinkedList<Binding> bindingsIf = ifBlock.getBindings();
            if(type.equals(Constants.FUNCTION)){
                for(int a=0; a<bindingsIf.size(); a++){
                    Binding binding = bindingsIf.get(a);
                    BindingIF startbind = (BindingIF) binding.getStartBinding(); // ifblock parameter
                    BindingParameter endbind = binding.getEndBinding();
                    // check if is already in map ifSide
                    if(!parametersIfSide.containsKey(startbind.getIndexObj())){
                        // add func index to map
                        parametersIfSide.put(startbind.getIndexObj(), new HashMap<>());
                    }
                    // check if par is already defined
                    if(!parametersIfSide.get(startbind.getIndexObj()).containsKey(startbind.getIfParameter())){
                       // add current par in map
                        HashMap<String,String> parsfunc = parametersIfSide.get(startbind.getIndexObj());
                        parsfunc.put(startbind.getIfParameter(), "");
                    }
                    // check if value par is not already defined
                    if(parametersIfSide.get(startbind.getIndexObj()).get(startbind.getIfParameter()).equals("")){
                        // add inputblocks of binding to maps
                        System.out.println("- - " + drlDeclarationsInputBlocks.size());
                        addDeclarations(endbind, declaredParametersInDrl, drlDeclarationsInputBlocks, imports);
                        System.out.println("- - " + drlDeclarationsInputBlocks.size());
                        HashMap<String,String> funcpars = parametersIfSide.get(startbind.getIndexObj());
                        if(endbind instanceof BindingInputFact){
                            funcpars.put(startbind.getIfParameter(), "$"+endbind.getIndexObj());
                        }else{
                            BindingInputField inputField = (BindingInputField)endbind;
                            funcpars.put(startbind.getIfParameter(), "$"+getUniqueIDField(inputField, declaredParametersInDrl));
                        }
                        parametersIfSide.put(startbind.getIndexObj(), funcpars);
                    }      
                }
            }else{
                
                if(type.equals(Constants.TIME) || type.equals(Constants.ACTIVITY)){
                    for(int a=0; a<bindingsIf.size(); a++){
                        Binding binding = bindingsIf.get(a);
                        BindingParameter endbind = binding.getEndBinding();
                        if(!drlDeclarationsInputBlocks.containsKey(endbind.getIndexObj())){
                            addEventToMapsAndDrl(endbind, declaredParametersInDrl, drlDeclarationsInputBlocks, imports);
                        }
                    }
                }
            }
        }
        
        // before writing drlLHS add drl declarations of output
        
        String drlRHS = "";
        if(isHA){
            LinkedList<Binding> bindingsOutput = output.getBindings();
            if(bindingsOutput.size()==0){
                drlRHS += "insert( new "+ HelperClass.toUppercaseFirstLetter(name) +"());" 
                      + " \n end \n ";
            }else{
                String classNameInsert = HelperClass.toUppercaseFirstLetter(name);
                String varLabel = name +classNameInsert ;
                drlRHS += classNameInsert + " " + varLabel+"= new "+classNameInsert+"(); \n " ;
                for(Binding binding:bindingsOutput){
                    BindingOutput bindOut = (BindingOutput) binding.getEndBinding();
                    drlRHS+= varLabel + ".set"+
                           HelperClass.toUppercaseFirstLetter(bindOut.getParameter()) +"( $"  +
                    binding.getStartBinding().getIndexObj();
                    // check if input is in maps
                    if(!drlDeclarationsInputBlocks.containsKey(binding.getStartBinding().getIndexObj())){
                        addDeclarations(binding.getStartBinding(), declaredParametersInDrl, drlDeclarationsInputBlocks, imports);
                    }
                   //check fact or field
                   if(binding.getStartBinding() instanceof BindingInputField){
                       drlRHS += "."+((BindingInputField) binding.getStartBinding()).getField().getName()+"); \n";
                   }else{
                       if(binding.getStartBinding() instanceof BindingInputFact){
                           drlRHS += "); \n";
                       }
                   }
                   
                }
                 drlRHS += "insert("+varLabel+"); \n end \n";           
            }
            String importEvent = Constants.PACKAGEEVENTS + "." +HelperClass.toUppercaseFirstLetter(name);
            imports.add(importEvent); 
        }else{
            TemplateActions actTemp = (TemplateActions) temp;
            
            for(int i=0; i<actTemp.getActions().size();i++){
                ActionClient act = actTemp.getActions().get(i);
                String classNameInsert = HelperClass.toUppercaseFirstLetter(act.getName());
                String varLabel = name +classNameInsert+i ;
                drlRHS += classNameInsert + " " + varLabel+"= new "+classNameInsert+"(); \n " ;
                for(ActionField field : act.getFields()){
                    drlRHS+= varLabel + ".set"+
                           HelperClass.toUppercaseFirstLetter(field.getName()) +"(\""+ field.getValue()+"\"); \n";
                }
                drlRHS += "insert("+varLabel+"); \n ";     
                String importEvent = Constants.PACKAGEACTIONS + "." +HelperClass.toUppercaseFirstLetter(act.getName());
                imports.add(importEvent);
            }
            drlRHS += " end \n";
       }
        
        // write LHS 
        String drlLHS = "";
        System.out.println("------------------------------------------------- size vals" + drlDeclarationsInputBlocks.values().size());
        for(String str: drlDeclarationsInputBlocks.values()){
            if(!str.endsWith(")")){
                // remove last comma
                int lastComma = str.lastIndexOf(",");
                if(lastComma == -1 ){
                    // close statement 
                    drlLHS += str + ") \n";
                }else{
                    if(lastComma >= drlDeclarationsInputBlocks.values().size()-2){
                        // remove last comma and close
                        String newDecl = str.substring(0, lastComma);
                        newDecl += ") \n ";
                        drlLHS += newDecl;
                    }
                }
            }else{
                drlLHS += str + " \n";
            }
        }
        // write functions
        
        for(IFBlock ifb : temp.getIfBlocks()){
            System.out.println("------------------ fun cons " + ifb.getType() + " --------- " + Constants.FUNCTION);
            if(ifb.getType().equals(Constants.FUNCTION)){
                String func = "eval("+ ifb.getFunction().getName()+"(";
                for(CMTParameter par: ifb.getFunction().getParameters()){
                    String parName = par.getParName();
                    String parValue = parametersIfSide.get(ifb.getBindings().getFirst().getStartBinding().getIndexObj()).get(parName);
                    func += parValue + ", ";
                }
                String removeComma = func.substring(0, func.lastIndexOf(","));
                drlLHS += removeComma + ")) \n";
                // add import
                String importFunc = Constants.PACKAGEFUNCTIONS + "." +ifb.getFunction().getEncapClass()+"."+ ifb.getFunction().getName();
                imports.add(importFunc);
            }
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

    private static void addDeclarations(BindingParameter endbind, HashMap<Integer, ArrayList<String>> declaredParametersInDrl,
            HashMap<Integer, String> drlDeclarationsInputBlocks, HashSet<String> imports){
      int cases =0;
                        if(endbind instanceof BindingInputFact){
                            if(((BindingInputFact)endbind).getInputObject() instanceof Fact){
                                cases = 1;
                            }else {
                                cases = 2;
                            }
                        }else{
                            if(endbind instanceof BindingInputField){
                                if(((BindingInputField)endbind).getInputObject() instanceof Fact){
                                    cases =3;
                                }else{
                                    cases=4;
                                }
                            }
                        }
                        System.out.println(" cases " + cases);
                        if(cases != 0){
                            switch(cases){
                                case 1: // is fact type fact
                                    // check if fact is already defined
                                    if(!drlDeclarationsInputBlocks.containsKey(endbind.getIndexObj())){
                                        drlDeclarationsInputBlocks.put(endbind.getIndexObj(), getFactDeclarationDrl(endbind, imports));
                                        declaredParametersInDrl.put(endbind.getIndexObj(), new ArrayList<>());
                                    }
                                    
                                    
                                    break;
                                case 2: // is fact type event
                                    // check if event is already defined
                                    System.out.println(" case 2");
                                    if(!drlDeclarationsInputBlocks.containsKey(endbind.getIndexObj())){
                                        addEventToMapsAndDrl(endbind, declaredParametersInDrl, drlDeclarationsInputBlocks, imports);
                                    }
                                    
                                    
                                    break;
                                case 3: // is field type fact
                                    if(!drlDeclarationsInputBlocks.containsKey(endbind.getIndexObj())){
                                        drlDeclarationsInputBlocks.put(endbind.getIndexObj(), getFactDeclarationDrl(endbind, imports));
                                        declaredParametersInDrl.put(endbind.getIndexObj(), new ArrayList<>());
                                    }
                                    BindingInputField inputField = (BindingInputField)endbind;
                                    // add field
                                    String fieldName = inputField.getField().getName();
                                    if(!declaredParametersInDrl.get(endbind.getIndexObj()).contains(fieldName)){
                                        declaredParametersInDrl.get(endbind.getIndexObj()).add(fieldName);
                                        String drl = drlDeclarationsInputBlocks.get(endbind.getIndexObj());
                                        drl += "$" + getUniqueIDField(inputField, declaredParametersInDrl) + ":" + fieldName + ", ";
                                        drlDeclarationsInputBlocks.put(endbind.getIndexObj(), drl);
                                    }
                                    
                                    
                                    break;
                                case 4: // is field type event
                                    // check if event is already defined
                                    if(!drlDeclarationsInputBlocks.containsKey(endbind.getIndexObj())){
                                        addEventToMapsAndDrl(endbind, declaredParametersInDrl, drlDeclarationsInputBlocks, imports);
                                    }
                                    break;
                            }
                        }
    }
    
    
    private static String getUniqueIDField(BindingInputField endbind, HashMap<Integer, ArrayList<String>> declaredParametersInDrl){
        String fieldName = endbind.getField().getName();
        String uniquiIDField = Integer.toString(endbind.getIndexObj()) + Integer.toString(declaredParametersInDrl.get(endbind.getIndexObj()).indexOf(fieldName));
        return uniquiIDField;
    }
    
    private static void addEventToMapsAndDrl(BindingParameter endbind, HashMap<Integer, ArrayList<String>> declaredParametersInDrl , 
            HashMap<Integer, String> drlDeclarationsInputBlocks, HashSet<String> imports){
        
        drlDeclarationsInputBlocks.put(endbind.getIndexObj(), getEventDeclarationDrl(endbind));
        declaredParametersInDrl.put(endbind.getIndexObj(), new ArrayList<>());
        // check if there are variable fields
        // add variable fields in drl string
        EventInput event = getEventInputFromBinding(endbind);
        System.out.println("--- event " + event);
        String importEvent = Constants.PACKAGEEVENTS + "." + HelperClass.toUppercaseFirstLetter(event.getClassName());
        imports.add(importEvent); 
        // add all fields to includesparsfact for index keeping
        String drlDeclaration = drlDeclarationsInputBlocks.get(endbind.getIndexObj());
        ArrayList<String> declFields = declaredParametersInDrl.get(endbind.getIndexObj());
        for(FieldValueLimitation f : event.getLimitations()){
            // add to drl string + add to declared pars
            if(!declFields.contains(f.getFieldName())){
                declFields.add(f.getFieldName());
                String uniqueID = Integer.toString(endbind.getIndexObj()) + Integer.toString(declFields.indexOf(f.getFieldName()));
                if(f.getValue() != null && !f.getValue().equals("")){
                    if(NumberUtils.isNumber(f.getValue())){
                        drlDeclaration += " $" + uniqueID +":" + f.getFieldName() + f.getOperator() + f.getValue() + ",";
                    }else{
                        if(!f.getOperator().equals(">") && !f.getOperator().equals("<")){
                            drlDeclaration += " $" + uniqueID +":" + f.getFieldName() + f.getOperator() + "\""+f.getValue()+ "\""+ ",";
                        }
                    }
                }else{
                    drlDeclaration += " $" + uniqueID +":" + f.getFieldName() + ",";
                }
            }
        }
        // close drlStatement nothing can be added anymore
        int comma = drlDeclaration.lastIndexOf(",");
        String newDecl = "";
        if(comma != -1){
            newDecl = drlDeclaration.substring(0, comma);
        }else{
            newDecl = drlDeclaration;
        }
        newDecl += ")";
        drlDeclarationsInputBlocks.put(endbind.getIndexObj(), newDecl);
    }
    
    private static EventInput getEventInputFromBinding(BindingParameter binding){
        EventInput res = null;
        System.out.println(" binding " + binding.getClass().getName());
         if(binding instanceof BindingInputFact){
            BindingInputFact bindingfact = (BindingInputFact) binding;
            
            res =(EventInput)bindingfact.getInputObject();
            System.out.println(" binding " + res);
        }else{
            if(binding instanceof BindingInputField){
                BindingInputField bindingfield = (BindingInputField) binding;
                res =(EventInput)bindingfield.getInputObject();
            }
        }
         return res;
    }
    
    private static String getEventDeclarationDrl(BindingParameter binding){
        EventInput fact = getEventInputFromBinding(binding);
        if(fact != null){
        String result = "$" + binding.getIndexObj()+":" + HelperClass.toUppercaseFirstLetter(fact.getClassName())+"(";
            return result;
        }
        return "";
    }
    
    private static String getFactDeclarationDrl(BindingParameter binding, HashSet<String> imports){
        Fact fact = null;
        if(binding instanceof BindingInputFact){
            BindingInputFact bindingfact = (BindingInputFact) binding;
            fact =(Fact)bindingfact.getInputObject();
        }else{
            if(binding instanceof BindingInputField){
                BindingInputField bindingfield = (BindingInputField) binding;
                fact =(Fact)bindingfield.getInputObject();
            }
        }
        if(fact != null){
            String importEvent = Constants.PACKAGEFACTS + "." + HelperClass.toUppercaseFirstLetter(fact.getClassName());
            imports.add(importEvent); 
        String result = "$" + binding.getIndexObj()+":" + HelperClass.toUppercaseFirstLetter(fact.getClassName())+"("+
                    fact.getUriField()+"== \""+fact.getUriValue() + "\" , ";
            return result;
        }
        return "";
    }
    
    
    
    
  public static FactType createNewActivity(TemplateHA temp){ // return facttype
        //String className, String type, String uriField, ArrayList<CMTField> fields
        System.out.println("- in new situ");
        OutputHA output = temp.getOutput();
        
        
        ArrayList<CMTField> fields = new ArrayList<CMTField>();
        for(Binding bind : output.getBindings()){
            BindingOutput parbind = (BindingOutput) bind.getEndBinding();
            CMTField field = new CMTField(parbind.getParameter(), parbind.getParType());
            System.out.println("field output " + field.getName());
     
            fields.add(field);
        }
        FactType result = new FactType(HelperClass.toUppercaseFirstLetter(output.getName()), "activity", "", fields);
        result.setVarFormat("");
        result.setVarList( new ArrayList<String>());
        result.setIsCustom(true);
        result.setCategory("My Situations");
        
        // create java class
        
        String className = HelperClass.toUppercaseFirstLetter(HelperClass.getSimpleNameAll(result.getClassName()));
        String typeEvent = result.getType();
        String extendsClass = "";
        boolean custom = false;
        boolean isActivity = false;
        switch(typeEvent){
            case "activity":
                 extendsClass = "be.ac.vub.wise.cmtserver.blocks.Activity";
                 isActivity = true;
                 custom = result.isIsCustom();
                 break;
            case "time":
                extendsClass = "import be.ac.vub.wise.cmtserver.blocks.Time";
                break;
        }
        
        String uriField = result.getUriField();
        
        String varFormat = result.getVarFormat();
        
        
            String source = "package "+ Constants.PACKAGEEVENTS+ ";"
                                    + "import " + Constants.PACKAGEBLOCKS +"UriFactType; import org.kie.api.definition.type.Role; import org.kie.api.definition.type.Role.Type; "
                                    + "import java.io.Serializable; import " + Constants.PACKAGEBLOCKS +"IFactType; "
                                    + "import org.apache.commons.lang3.builder.EqualsBuilder; import org.apache.commons.lang3.builder.HashCodeBuilder; "
                                    + "import be.ac.vub.wise.cmtserver.blocks.EventVariables;  import be.ac.vub.wise.cmtserver.blocks.Time; import be.ac.vub.wise.cmtserver.blocks.Activity;"
                                    + "@Role(Type.EVENT) @UriFactType(id = \""+ uriField+"\") ";
            
            if(result.getVarList().isEmpty() ){ // then format -- todo add exception if both are empty
                if(varFormat.isEmpty()){
                    source += "@EventVariables(list = \"\", format=\"\")";
                }else{
                    source += "@EventVariables(list = \"\", format=\"format\")";
                }
            }else{
                source += "@EventVariables(list = \"list\", format=\"\")";
            }
            
            source += " public class " +className + " extends "+extendsClass+" { ";
            
            if(result.getVarList().isEmpty()){ 
                if(varFormat.isEmpty()){
                    source += " public "+ className+"(){"; // constructor
                            
                }else{
                    source += " java.lang.String format = \"\"; "
                            + " public "+ className+"(){" // constructor
                            + " this.format = \""+varFormat+"\"; ";
                }
                if(isActivity){
                    source += " super.setCustom("+custom+"); } ";
                } else{
                    source +="}";
                }        
            }else{
                source += " public java.util.LinkedList<String> list = null; "
                        + " public "+ className+"(){" // constructor
                        + " this.list = new java.util.LinkedList<String>(); ";
                if(isActivity){
                    source += " super.setCustom("+custom+"); ";
                }
                for(int i = 0; i<result.getVarList().size(); i++){
                    String var = result.getVarList().get(i);
                    source += " this.list.add(\""+var+"\"); "; //populate list
                }
                
                source +="}";
            }
            
            for(int i = 0; i< result.getFields().size(); i++){
                CMTField field = result.getFields().get(i);
                String type = field.getType();
                String fieldName = field.getName();
                if(!type.contains("java")){
                    String[] splitLastPoint = type.split("\\.");
                    int z = splitLastPoint.length;
                    String simpleClassName = type;
                    if(z>0){
                        simpleClassName= splitLastPoint[z-1];
                    }
                    if(checkTypeClassPath(simpleClassName)){
                        //String okType = StringUtils.capitalize(type);
                        source = addSetterGetters(source, fieldName, Constants.PACKAGEFACTS+"."+simpleClassName);
                    }
                }else{
                    source = addSetterGetters(source, fieldName, type);
                }
            }
            
            source += "}";
            HelperClass.compile(source, className, Constants.PACKAGEEVENTSSLASH);
            
            CMTDelegator.get().registerEventType(result);
                    
        
        
        
        return result;
    }
    
  private static boolean checkTypeClassPath(String type){
        
        String classUri = Constants.CLASSPATH +"\\\\"+Constants.PACKAGEFACTSSLASH+"\\\\"+type + ".class";
        
        String classUriEv = Constants.CLASSPATH +"\\\\"+Constants.PACKAGEEVENTSSLASH+"\\\\"+type + ".class";
        if(Files.exists(new File(classUri).toPath()) || Files.exists(new File(classUriEv).toPath())  ){
            return true;
        } return false;
    }
  
  private static String addSetterGetters(String source, String fieldName, String type){
        String capField = StringUtils.capitalize(fieldName);
        String result = source;
                    result += " public " + type + " " + fieldName + " ;" 
                            + " public void set"+capField+"("+type+" " + fieldName +"){"
                            + " this."+fieldName+" = " +fieldName+ ";} "
                            + " public " + type + " get"+capField+"(){"
                            + " return this."+fieldName+";} ";
        return result;
    }
    
    
}
