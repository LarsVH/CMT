package be.ac.vub.wise.cmtserver.facts;

import be.ac.vub.wise.cmtserver.blocks.UriFactType;
import org.kie.api.definition.type.Role;
import org.kie.api.definition.type.Role.Type;
import java.io.Serializable;
import be.ac.vub.wise.cmtserver.blocks.IFactType;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

@Role(Type.FACT)
@UriFactType(id = "room")
public class Location implements IFactType, Serializable {

    public java.lang.String room;

    public void setRoom(java.lang.String room) {
        this.room = room;
    }

    public java.lang.String getRoom() {
        return this.room;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(this.room).toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Location == false) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        final Location otherObject = (Location) obj;
        return new EqualsBuilder().append(this.room, otherObject.room).isEquals();
    }

    @Override
    public String toString() {
        return this.room;
    }
}
