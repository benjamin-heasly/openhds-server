package org.openhds.webservice.resources;

import java.util.ArrayList;
import java.util.List;

import org.openhds.controller.service.LocationHierarchyService;
import org.openhds.domain.model.LocationHierarchy;
import org.openhds.domain.model.LocationHierarchyLevel;
import org.openhds.domain.model.wrappers.LocationHierarchies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
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

	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	public LocationHierarchies getEntireLocationHierarchy() {
		List<LocationHierarchy> allLocationHierarcies = locationHierarchyService.getAllLocationHierarchies();
		List<LocationHierarchy> copies = new ArrayList<LocationHierarchy>();
		
		for (LocationHierarchy lh : allLocationHierarcies) {
			LocationHierarchy copy = new LocationHierarchy();
			copy.setExtId(lh.getExtId());

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
	
	@RequestMapping(value = "/levels", method = RequestMethod.GET)
	@ResponseBody
	public List<LocationHierarchyLevel> getAllLevels() {
		return locationHierarchyService.getAllLevels();
	}

}
