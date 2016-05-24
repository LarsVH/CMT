package be.ac.vub.wise.cmtserver.functions;

import be.ac.vub.wise.cmtserver.blocks.IFunctionClass;
import be.ac.vub.wise.cmtserver.blocks.Parameters;
import java.io.Serializable;

public class Func2 implements IFunctionClass, Serializable {

    @Parameters(parameters = "room ")
    public static boolean InBed(be.ac.vub.wise.cmtserver.facts.Location room) {
        return true;
    }
}
