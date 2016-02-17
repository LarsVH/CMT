package be.ac.vub.wise.cmtclient.blocks;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Objects;

public class Function implements IFunctionClass, Serializable{

    public String name;
    public LinkedHashMap<String,String> parameters; // parName parType
    public String encapClass;
    
    public Function(){}

    public String getEncapClass() {
        return encapClass;
    }

    public void setEncapClass(String encapClass) {
        this.encapClass = encapClass;
    }
    
    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    public LinkedHashMap<String, String> getParameters() {
        return parameters;
    }

    public void setParameters(LinkedHashMap<String, String> parameters) {
        this.parameters = parameters;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 83 * hash + Objects.hashCode(this.name);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Function other = (Function) obj;
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return  name ;
    }


}
