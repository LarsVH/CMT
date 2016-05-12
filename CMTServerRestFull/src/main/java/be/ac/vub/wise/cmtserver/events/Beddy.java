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
public class Beddy extends be.ac.vub.wise.cmtserver.blocks.Activity {

    public Beddy() {
        super.setCustom(true);
    }
}
