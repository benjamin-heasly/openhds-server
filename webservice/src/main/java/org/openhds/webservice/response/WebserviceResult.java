package org.openhds.webservice.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@JsonInclude(Include.NON_NULL)
@XmlRootElement
public class WebserviceResult implements Serializable {

    public final static int SUCCESS_STATUS = 1;
    public final static int CLIENT_ERROR_STATUS = 2;
    public final static int SERVER_ERROR_STATUS = 3;

	private String status = "";
	private String resultMessage = "";
	private int resultCode;
	private Map<Object, Object> data = new HashMap<Object, Object>();
	
	public void addDataElement(Object key, Object value) {
		data.put(key, value);
	}

	public String getStatus() {
		return status;
	}
	
	public void setStatus(String status) {
		this.status = status;
	}
	
	public String getResultMessage() {
		return resultMessage;
	}
	
	public void setResultMessage(String resultMessage) {
		this.resultMessage = resultMessage;
	}
	
	public int getResultCode() {
		return resultCode;
	}
	
	public void setResultCode(int resultCode) {
		this.resultCode = resultCode;
	}
	
	public Map<Object, Object> getData() {
		return data;
	}
	
	public void setData(Map<Object, Object> data) {
		this.data = data;
	}

}
