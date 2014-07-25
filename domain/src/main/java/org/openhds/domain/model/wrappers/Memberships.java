package org.openhds.domain.model.wrappers;

import org.openhds.domain.model.Membership;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement
public class Memberships extends Wrapper<Membership> {

    private static final long serialVersionUID = 1L;

    @XmlElement(name = "membership")
    public List<Membership> getEntities() {
        return entities;
    }
}
