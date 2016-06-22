package be.ac.vub.wise.cmtserver.events;import be.ac.vub.wise.cmtserver.blocks.UriFactType; import org.kie.api.definition.type.Role; import org.kie.api.definition.type.Role.Type; import java.io.Serializable; import be.ac.vub.wise.cmtserver.blocks.IFactType; import org.apache.commons.lang3.builder.EqualsBuilder; import org.apache.commons.lang3.builder.HashCodeBuilder; import be.ac.vub.wise.cmtserver.blocks.EventVariables;  import be.ac.vub.wise.cmtserver.blocks.Time; import be.ac.vub.wise.cmtserver.blocks.Activity;@Role(Type.EVENT) @UriFactType(id = "id") @EventVariables(list = "list", format="") public class Lamp extends be.ac.vub.wise.cmtserver.blocks.Activity {  public java.util.LinkedList<String> list = null;  public Lamp(){ this.list = new java.util.LinkedList<String>();  super.setCustom(true);  this.list.add("on");  this.list.add("off"); } public java.lang.String id ; public void setId(java.lang.String id){ this.id = id;}  public java.lang.String getId(){ return this.id;}  public java.lang.String status ; public void setStatus(java.lang.String status){ this.status = status;}  public java.lang.String getStatus(){ return this.status;} }