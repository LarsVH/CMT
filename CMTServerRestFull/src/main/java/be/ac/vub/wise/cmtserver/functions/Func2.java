package be.ac.vub.wise.cmtserver.functions; import be.ac.vub.wise.cmtserver.blocks.IFunctionClass; import be.ac.vub.wise.cmtserver.blocks.Parameters; import java.io.Serializable;  public class Func2 implements IFunctionClass, Serializable{ @Parameters(parameters = "person1 person2 ") public static boolean personsInLocation ( be.ac.vub.wise.cmtserver.facts.Person person1, be.ac.vub.wise.cmtserver.facts.Person person2 ){
		return person1.getName().equals(person2.getName());
	} @Parameters(parameters = "name1 name2 ") public static boolean samePerson ( java.lang.String name1, java.lang.String name2 ){
		return name1.equals(name2);
	} }