package be.ac.vub.wise.cmtserver.functions; import be.ac.vub.wise.cmtserver.blocks.IFunctionClass; import be.ac.vub.wise.cmtserver.blocks.Parameters; import java.io.Serializable;  public class Func implements IFunctionClass, Serializable{ @Parameters(parameters = "room person ") public static boolean PersonInLocation ( be.ac.vub.wise.cmtserver.facts.Location room, be.ac.vub.wise.cmtserver.facts.Person person ){
		return true;
	} @Parameters(parameters = "person1 person2 ") public static boolean SamePerson ( be.ac.vub.wise.cmtserver.facts.Person person1, be.ac.vub.wise.cmtserver.facts.Person person2 ){
        return true;
    } }