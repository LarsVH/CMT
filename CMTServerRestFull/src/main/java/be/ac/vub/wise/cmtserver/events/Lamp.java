package be.ac.vub.wise.cmtserver.events;

import be.ac.vub.wise.cmtserver.blocks.UriFactType;
import org.kie.api.definition.type.Role;
import org.kie.api.definition.type.Role.Type;
import java.io.Serializable;
import be.ac.vub.wise.cmtserver.blocks.IFactType;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import be.ac.vub.wise.cmtserver.blocks.EventVariables;
import be.ac.vub.wise.cmtserver.blocks.Time;
import be.ac.vub.wise.cmtserver.blocks.Activity;

@Role(Type.EVENT)
@UriFactType(id = "")
@EventVariables(list = "", format = "")
public class Lamp extends be.ac.vub.wise.cmtserver.blocks.Activity {

    public Lamp() {
        super.setCustom(true);
    }
    public java.lang.String id;

    public void setId(java.lang.String id) {
        this.id = id;
    }

    public java.lang.String getId() {
        return this.id;
    }
    public java.lang.String status;

    public void setStatus(java.lang.String status) {
        this.status = status;
    }

    public java.lang.String getStatus() {
        return this.status;
    }
    public java.lang.String veld1;

    public void setVeld1(java.lang.String veld1) {
        this.veld1 = veld1;
    }

    public java.lang.String getVeld1() {
        return this.veld1;
    }
    public java.lang.String veld2;

    public void setVeld2(java.lang.String veld2) {
        this.veld2 = veld2;
    }

    public java.lang.String getVeld2() {
        return this.veld2;
    }
}
