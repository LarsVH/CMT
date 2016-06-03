package be.ac.vub.wise.cmtserver.facts; import be.ac.vub.wise.cmtserver.blocks.UriFactType; import org.kie.api.definition.type.Role; import org.kie.api.definition.type.Role.Type; import java.io.Serializable; import be.ac.vub.wise.cmtserver.blocks.IFactType; import org.apache.commons.lang3.builder.EqualsBuilder; import org.apache.commons.lang3.builder.HashCodeBuilder; @Role(Type.FACT) @UriFactType(id = "label") public class PressureSensor implements IFactType, Serializable {  public java.lang.String label ; public void setLabel(java.lang.String label){ this.label = label;}  public java.lang.String getLabel(){ return this.label;} @Override
 public int hashCode(){  return new HashCodeBuilder().append(this.label).toHashCode();}@Override
 public boolean equals(Object obj){ if (obj instanceof PressureSensor == false){ return false;} if (this == obj){ return true;} final PressureSensor otherObject = (PressureSensor) obj; return new EqualsBuilder().append(this.label, otherObject.label).isEquals();} @Override
 public String toString(){ return this.label ;} }