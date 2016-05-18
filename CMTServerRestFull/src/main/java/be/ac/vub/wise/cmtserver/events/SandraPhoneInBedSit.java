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
public class SandraPhoneInBedSit extends be.ac.vub.wise.cmtserver.blocks.Activity {

    public SandraPhoneInBedSit() {
        super.setCustom(true);
    }
    public be.ac.vub.wise.cmtserver.facts.Person phoneOwn1;

    public void setPhoneOwn1(be.ac.vub.wise.cmtserver.facts.Person phoneOwn1) {
        this.phoneOwn1 = phoneOwn1;
    }

    public be.ac.vub.wise.cmtserver.facts.Person getPhoneOwn1() {
        return this.phoneOwn1;
    }
}
