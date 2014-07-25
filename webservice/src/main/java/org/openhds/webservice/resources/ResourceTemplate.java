package org.openhds.webservice.resources;

import org.openhds.controller.exception.ConstraintViolations;
import org.openhds.domain.model.wrappers.Wrapper;
import org.openhds.webservice.CacheResponseWriter;
import org.openhds.webservice.response.WebserviceResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.Serializable;

import static org.openhds.webservice.response.WebserviceResultHelper.constraintViolationResponse;
import static org.openhds.webservice.response.WebserviceResultHelper.deletedResponse;
import static org.openhds.webservice.response.WebserviceResultHelper.foundResponse;
import static org.openhds.webservice.response.WebserviceResultHelper.notDeletedResponse;
import static org.openhds.webservice.response.WebserviceResultHelper.notFoundResponse;
import static org.openhds.webservice.response.WebserviceResultHelper.notPersistedResponse;
import static org.openhds.webservice.response.WebserviceResultHelper.persistedResponse;
import static org.openhds.webservice.response.WebserviceResultHelper.serverErrorResponse;

@Controller
public abstract class ResourceTemplate <T extends Serializable, U extends Wrapper> {
    private static final Logger logger = LoggerFactory.getLogger(ResourceTemplate.class);

    protected String entityName = "entity";

    protected abstract T findById(String id);
    protected abstract U findAll();
    protected abstract File findAllCached();
    protected abstract boolean create(T entity) throws Exception;
    protected abstract boolean createOrUpdate(T entity) throws Exception;
    protected abstract boolean deleteById(String id);
    protected abstract T normalize(T entity);

    @RequestMapping(value = "/{extId}",
            method = RequestMethod.GET,
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<WebserviceResult> getByExtId(@PathVariable String extId) {
        T entity = findById(extId);
        if (null == entity) {
            return notFoundResponse("No such " + entityName + " with extId: " + extId);
        }
        return foundResponse(entityName, entity, entityName + " was found");
    }

    @RequestMapping(value = "",
            method = RequestMethod.GET,
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<WebserviceResult> getAll() {
        U allEntities = findAll();
        if (null == allEntities) {
            return notFoundResponse("No " + entityName + "(s) found.");
        }
        return foundResponse(entityName, allEntities, "found " + allEntities.size() + " " + entityName + "(s)");
    }

    @RequestMapping(value = "/cached",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_XML_VALUE)
    public void getAllCached(HttpServletResponse response) throws Exception {
        File cached = findAllCached();
        if (null != cached) {
            CacheResponseWriter.writeResponse(cached, response);
        } else {
            logger.error("Problem writing " + entityName +" xml file.");
        }
    }

    @RequestMapping(value = "",
            method = RequestMethod.POST,
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<WebserviceResult> insert(@RequestBody T entity) throws Exception {
        boolean success = create(entity);
        if (success) {
            return persistedResponse(entityName, normalize(entity), entityName + " created");
        } else {
            return notPersistedResponse(entityName, entity, "Could not create " + entityName + ".");
        }
    }

    @RequestMapping(value = "",
            method = RequestMethod.PUT,
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<WebserviceResult> addOrUpdate(@RequestBody T entity) throws Exception {
        boolean success = createOrUpdate(entity);
        if (success) {
            return persistedResponse(entityName, normalize(entity), entityName + " created");
        } else {
            return notPersistedResponse(entityName, entity, "Could not update " + entityName + ".");
        }
    }

    @RequestMapping(value = "/{extId}",
            method = RequestMethod.DELETE,
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<WebserviceResult> deleteByExtId(@PathVariable String extId) {
        boolean success = deleteById(extId);
        if (success) {
            return deletedResponse(entityName + " deleted");
        } else {
            return notDeletedResponse("Could not delete " + entityName + ".");
        }
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<WebserviceResult> handleServerError(Exception e) {
        return serverErrorResponse(e);
    }

    @ExceptionHandler(ConstraintViolations.class)
    public ResponseEntity<WebserviceResult> handleConstraintViolations(ConstraintViolations cv) {
        return constraintViolationResponse(cv, "There are " + cv.getViolations().size() + " constraint violations.");
    }

}
