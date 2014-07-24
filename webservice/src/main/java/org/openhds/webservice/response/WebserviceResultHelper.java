package org.openhds.webservice.response;

import org.openhds.controller.exception.ConstraintViolations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class WebserviceResultHelper {

    public static ResponseEntity<WebserviceResult> foundResponse(String type, Object entity, String msg) {
        WebserviceResult result = new WebserviceResult();
        result.addDataElement(type, entity);
        result.setResultCode(WebserviceResult.SUCCESS_STATUS);
        result.setStatus("found");
        result.setResultMessage(msg);
        return new ResponseEntity<WebserviceResult>(result, HttpStatus.OK);
    }

    public static ResponseEntity<WebserviceResult> notFoundResponse(String msg) {
        WebserviceResult result = new WebserviceResult();
        result.setResultCode(WebserviceResult.CLIENT_ERROR_STATUS);
        result.setStatus("notFound");
        result.setResultMessage(msg);
        return new ResponseEntity<WebserviceResult>(result, HttpStatus.NOT_FOUND);
    }

    public static ResponseEntity<WebserviceResult> persistedResponse(String type, Object entity, String msg) {
        WebserviceResult result = new WebserviceResult();
        result.addDataElement(type, entity);
        result.setResultCode(WebserviceResult.SUCCESS_STATUS);
        result.setStatus("created");
        result.setResultMessage(msg);
        return new ResponseEntity<WebserviceResult>(result, HttpStatus.CREATED);
    }

    public static ResponseEntity<WebserviceResult> notPersistedResponse(String type, Object entity, String msg) {
        WebserviceResult result = new WebserviceResult();
        result.addDataElement(type, entity);
        result.setResultCode(WebserviceResult.CLIENT_ERROR_STATUS);
        result.setStatus("notCreated");
        result.setResultMessage(msg);
        return new ResponseEntity<WebserviceResult>(result, HttpStatus.BAD_REQUEST);
    }

    public static ResponseEntity<WebserviceResult> deletedResponse(String msg) {
        WebserviceResult result = new WebserviceResult();
        result.setResultCode(WebserviceResult.SUCCESS_STATUS);
        result.setStatus("deleted");
        result.setResultMessage(msg);
        return new ResponseEntity<WebserviceResult>(result, HttpStatus.OK);
    }

    public static ResponseEntity<WebserviceResult> notDeletedResponse(String msg) {
        WebserviceResult result = new WebserviceResult();
        result.setResultCode(WebserviceResult.CLIENT_ERROR_STATUS);
        result.setStatus("notDeleted");
        result.setResultMessage(msg);
        return new ResponseEntity<WebserviceResult>(result, HttpStatus.BAD_REQUEST);
    }

    public static ResponseEntity<WebserviceResult> constraintViolationResponse(ConstraintViolations cv, String msg) {
        WebserviceResult result = new WebserviceResult();
        result.addDataElement("constraintViolations", cv.getViolations());
        result.setResultCode(WebserviceResult.CLIENT_ERROR_STATUS);
        result.setStatus("error");
        result.setResultMessage(msg);
        return new ResponseEntity<WebserviceResult>(result, HttpStatus.BAD_REQUEST);
    }

    public static ResponseEntity<WebserviceResult> serverErrorResponse(Exception e) {
        WebserviceResult result = new WebserviceResult();
        result.addDataElement("internalException", e.getMessage());
        result.addDataElement("exceptionType", e.getClass().toString());
        result.addDataElement("stackTrace", e.getStackTrace().toString());
        result.setResultCode(WebserviceResult.SERVER_ERROR_STATUS);
        result.setStatus("error");
        result.setResultMessage(e.getMessage());
        return new ResponseEntity<WebserviceResult>(result, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
