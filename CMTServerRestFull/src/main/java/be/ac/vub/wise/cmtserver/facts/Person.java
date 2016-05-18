package be.ac.vub.wise.cmtserver.facts;

import be.ac.vub.wise.cmtserver.blocks.UriFactType;
import org.kie.api.definition.type.Role;
import org.kie.api.definition.type.Role.Type;
import java.io.Serializable;
import be.ac.vub.wise.cmtserver.blocks.IFactType;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

@Role(Type.FACT)
@UriFactType(id = "name")
public class Person implements IFactType, Serializable {

    public java.lang.String name;

    public void setName(java.lang.String name) {
        this.name = name;
    }

    public java.lang.String getName() {
        return this.name;
    }
    public be.ac.vub.wise.cmtserver.facts.Location room;

    public void setRoom(be.ac.vub.wise.cmtserver.facts.Location room) {
        this.room = room;
    }

    public be.ac.vub.wise.cmtserver.facts.Location getRoom() {
        return this.room;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(this.name).toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Person == false) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        final Person otherObject = (Person) obj;
        return new EqualsBuilder().append(this.name, otherObject.name).isEquals();
    }

    @Override
    public String toString() {
        return this.name;
    }
}
