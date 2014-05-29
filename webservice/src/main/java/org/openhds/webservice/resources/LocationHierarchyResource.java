package org.openhds.webservice.resources;

import static org.openhds.webservice.response.WebserviceResult.RESULT_CODE_FOUND;
import static org.openhds.webservice.response.WebserviceResult.STATUS_ERROR;
import static org.openhds.webservice.response.WebserviceResult.STATUS_SUCCESS;

import java.util.ArrayList;
import java.util.List;

import org.openhds.controller.service.LocationHierarchyService;
import org.openhds.domain.model.LocationHierarchy;
import org.openhds.domain.model.LocationHierarchyLevel;
import org.openhds.domain.model.wrappers.LocationHierarchies;
import org.openhds.webservice.response.WebserviceResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/locationhierarchies")
public class LocationHierarchyResource {

	private LocationHierarchyService locationHierarchyService;

	@Autowired
	public LocationHierarchyResource(LocationHierarchyService locationHierarchyService) {
		this.locationHierarchyService = locationHierarchyService;
	}

	@RequestMapping(method = RequestMethod.GET, produces = "application/xml")
	@ResponseBody
	public LocationHierarchies getEntireLocationHierarchyXml() {
		List<LocationHierarchy> allLocationHierarcies = locationHierarchyService.getAllLocationHierarchies();
		List<LocationHierarchy> copies = new ArrayList<LocationHierarchy>();
		
		for (LocationHierarchy lh : allLocationHierarcies) {
			LocationHierarchy copy = new LocationHierarchy();
			copy.setExtId(lh.getExtId());
			copy.setUuid(lh.getUuid());

			LocationHierarchyLevel level = new LocationHierarchyLevel();
			level.setName(lh.getLevel().getName());
			copy.setLevel(level);
			copy.setName(lh.getName());

			LocationHierarchy parent = new LocationHierarchy();
			parent.setExtId(lh.getParent().getExtId());
			copy.setParent(parent);

			copies.add(copy);
		}

		LocationHierarchies locationHierarcies = new LocationHierarchies();
		locationHierarcies.setLocationHierarchies(copies);
		
		return locationHierarcies;
	}
	
	@RequestMapping(method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public ResponseEntity<WebserviceResult> getEntireLocationHierarchyJson() {
		List<LocationHierarchy> allLocationHierarchies = locationHierarchyService.getAllLocationHierarchies();
		List<LocationHierarchy> copies = new ArrayList<LocationHierarchy>();
		
		for (LocationHierarchy lh : allLocationHierarchies) {
			LocationHierarchy copy = new LocationHierarchy();
			copy.setExtId(lh.getExtId());
			copy.setUuid(lh.getUuid());

			LocationHierarchyLevel level = new LocationHierarchyLevel();
			level.setName(lh.getLevel().getName());
			copy.setLevel(level);
			copy.setName(lh.getName());

			LocationHierarchy parent = new LocationHierarchy();
			parent.setExtId(lh.getParent().getExtId());
			copy.setParent(parent);

			copies.add(copy);
		}

		LocationHierarchies locationHierarcies = new LocationHierarchies();
		locationHierarcies.setLocationHierarchies(copies);
		
		WebserviceResult result = new WebserviceResult();
        result.addDataElement("locationHierarchies", copies);
        result.setResultCode(RESULT_CODE_FOUND);
        result.setStatus(STATUS_SUCCESS);
        result.setResultMessage(copies.size() + " location hierarchies found");
        
        return new ResponseEntity<WebserviceResult>(result, HttpStatus.OK);
	}
	
	
    @RequestMapping(value = "/{extId}", method = RequestMethod.GET)
    public ResponseEntity<WebserviceResult> getLocationHierarchyByExtId(@PathVariable String extId) {
        LocationHierarchy locationHierarchy = locationHierarchyService.findLocationHierarchyById(extId, null);
        
        WebserviceResult result = new WebserviceResult();
        if (locationHierarchy == null) {
        	result.setStatus(STATUS_ERROR);
        	result.setResultMessage("Location hierarchy not found for extId=" + extId);
            return new ResponseEntity<WebserviceResult>(HttpStatus.BAD_REQUEST);
        }

        result.addDataElement("locationHierarchy", locationHierarchy);
        result.setResultCode(RESULT_CODE_FOUND);
        result.setStatus(STATUS_SUCCESS);
        result.setResultMessage("Location hierarchy found for extId=" + extId);
		
        return new ResponseEntity<WebserviceResult>(result, HttpStatus.OK); 
    }
	
	@RequestMapping(value = "/levels", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public ResponseEntity<WebserviceResult> getAllLevels() {
		
		WebserviceResult result = new WebserviceResult();
		List<LocationHierarchyLevel> levels = locationHierarchyService.getAllLevels();
        result.addDataElement("locationLevels", levels);
        result.setResultCode(RESULT_CODE_FOUND);
        result.setStatus(STATUS_SUCCESS);
        result.setResultMessage(levels.size() + " location levels found");
		
        return new ResponseEntity<WebserviceResult>(result, HttpStatus.OK);
	}

}
