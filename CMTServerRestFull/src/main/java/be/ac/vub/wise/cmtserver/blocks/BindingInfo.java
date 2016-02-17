package be.ac.vub.wise.cmtserver.blocks;

import java.io.Serializable;

public class BindingInfo implements Serializable{
	
	Object binding1;
	String parameterName1;
	String parameterType1;
	Object binding2;
	String parameterName2;
	String parameterType2;
	
	
	public Object getBinding1() {
		return binding1;
	}
	public void setBinding1(Object binding1) {
		this.binding1 = binding1;
	}
	public String getParameter1() {
		return parameterName1;
	}
	public void setParameter1(String parameter1) {
		this.parameterName1 = parameter1;
	}
	public Object getBinding2() {
		return binding2;
	}
	public void setBinding2(Object binding2) {
		this.binding2 = binding2;
	}
	public String getParameter2() {
		return parameterName2;
	}
	public void setParameter2(String parameter2) {
		this.parameterName2 = parameter2;
	}
	public String getParameterType1() {
		return parameterType1;
	}
	public void setParameterType1(String parameterType1) {
		this.parameterType1 = parameterType1;
	}
	public String getParameterType2() {
		return parameterType2;
	}
	public void setParameterType2(String parameterType2) {
		this.parameterType2 = parameterType2;
	}
	
	
	

}
