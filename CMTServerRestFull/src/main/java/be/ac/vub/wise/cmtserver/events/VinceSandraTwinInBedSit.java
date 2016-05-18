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
public class VinceSandraTwinInBedSit extends be.ac.vub.wise.cmtserver.blocks.Activity {

    public VinceSandraTwinInBedSit() {
        super.setCustom(true);
    }
    public be.ac.vub.wise.cmtserver.facts.Person person1pm1;

    public void setPerson1pm1(be.ac.vub.wise.cmtserver.facts.Person person1pm1) {
        this.person1pm1 = person1pm1;
    }

    public be.ac.vub.wise.cmtserver.facts.Person getPerson1pm1() {
        return this.person1pm1;
    }
    public be.ac.vub.wise.cmtserver.facts.Person person2pm2;

    public void setPerson2pm2(be.ac.vub.wise.cmtserver.facts.Person person2pm2) {
        this.person2pm2 = person2pm2;
    }

    public be.ac.vub.wise.cmtserver.facts.Person getPerson2pm2() {
        return this.person2pm2;
    }
}
