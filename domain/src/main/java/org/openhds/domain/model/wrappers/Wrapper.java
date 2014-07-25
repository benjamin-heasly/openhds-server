package org.openhds.domain.model.wrappers;


import org.openhds.domain.model.AuditableEntity;

import java.io.Serializable;
import java.util.List;

public abstract class Wrapper <T extends AuditableEntity> implements Serializable {

    private static final long serialVersionUID = 1L;

    protected List<T> entities;

    // subclasses should annotate this method with @XmlElement
    // say T is Membership, then annotate with  @XmlElement(name = "membership")
    public abstract List<T> getEntities();

    public void setEntities(List<T> entities) {
        this.entities = entities;
    }

    public int size() {
        return null == entities ? 0 : entities.size();
    }

}
