package be.ac.vub.wise.cmtserver.blocks;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Objects;

public class Function implements IFunctionClass, Serializable{

    public String name;
    public ArrayList<CMTParameter> parameters;
    public String encapClass;
    public int sql_id = 0;
    
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

    public ArrayList<CMTParameter> getParameters() {
        return parameters;
    }

    public void setParameters(ArrayList<CMTParameter> parameters) {
        this.parameters = parameters;
    }

    public void addParameter(CMTParameter par){
        this.parameters.add(par);
    }

    public int getSql_id() {
        return sql_id;
    }

    public void setSql_id(int sql_id) {
        this.sql_id = sql_id;
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
