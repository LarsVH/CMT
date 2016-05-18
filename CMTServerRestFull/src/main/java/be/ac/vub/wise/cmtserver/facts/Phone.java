package be.ac.vub.wise.cmtserver.facts;

import be.ac.vub.wise.cmtserver.blocks.UriFactType;
import org.kie.api.definition.type.Role;
import org.kie.api.definition.type.Role.Type;
import java.io.Serializable;
import be.ac.vub.wise.cmtserver.blocks.IFactType;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

@Role(Type.FACT)
@UriFactType(id = "id")
public class Phone implements IFactType, Serializable {

    public java.lang.String id;

    public void setId(java.lang.String id) {
        this.id = id;
    }

    public java.lang.String getId() {
        return this.id;
    }
    public be.ac.vub.wise.cmtserver.facts.Person owner;

    public void setOwner(be.ac.vub.wise.cmtserver.facts.Person owner) {
        this.owner = owner;
    }

    public be.ac.vub.wise.cmtserver.facts.Person getOwner() {
        return this.owner;
    }
    public be.ac.vub.wise.cmtserver.facts.Location location;

    public void setLocation(be.ac.vub.wise.cmtserver.facts.Location location) {
        this.location = location;
    }

    public be.ac.vub.wise.cmtserver.facts.Location getLocation() {
        return this.location;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(this.id).toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Phone == false) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        final Phone otherObject = (Phone) obj;
        return new EqualsBuilder().append(this.id, otherObject.id).isEquals();
    }

    @Override
    public String toString() {
        return this.id;
    }
}
