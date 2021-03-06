package org.openhds.domain.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import org.openhds.domain.annotations.Description;
import org.openhds.domain.constraint.CheckFieldNotBlank;
import org.openhds.domain.constraint.Searchable;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Description(description="A Field Worker represents one who collects the data within " +
		"the study area. They can be identified by a uniquely generated " +
		"identifier which the system uses internally. Only the first and last names " +
		"are recorded.")
@Entity
@Table(name="fieldworker")
@JsonInclude(Include.NON_NULL)
public class FieldWorker extends AuditableEntity implements Serializable {

    private static final long serialVersionUID = 1898036206514199266L;
      
    @NotNull
    @CheckFieldNotBlank
    @Searchable
    @Description(description="External Id of the field worker. This id is used internally.")
    String extId;
    
    @CheckFieldNotBlank
    @Searchable
    @Description(description="First name of the field worker.")
    String firstName;
    
    @CheckFieldNotBlank
    @Searchable
    @Description(description="Last name of the field worker.")
    String lastName;

    public String getExtId() {
        return extId;
    }

    public void setExtId(String extId) {
        this.extId = extId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    
    public static FieldWorker makeStub(String extId) {
    	
    	FieldWorker stub = new FieldWorker();
    	stub.setExtId(extId);
    	return stub;
    	
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }

        if (!(other instanceof FieldWorker)) {
            return false;
        }

        final FieldWorker otherFieldWorker = (FieldWorker) other;

        if (!extId.equals(otherFieldWorker.getExtId())) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return extId.hashCode();
    }
}
