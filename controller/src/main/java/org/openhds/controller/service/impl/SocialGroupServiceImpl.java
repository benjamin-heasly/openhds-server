package org.openhds.controller.service.impl;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.openhds.controller.exception.ConstraintViolations;
import org.openhds.controller.idgeneration.SocialGroupGenerator;
import org.openhds.controller.service.EntityService;
import org.openhds.controller.service.IndividualService;
import org.openhds.controller.service.ResidencyService;
import org.openhds.controller.service.SocialGroupService;
import org.openhds.dao.service.GenericDao;
import org.openhds.domain.annotations.Authorized;
import org.openhds.domain.model.Individual;
import org.openhds.domain.model.Location;
import org.openhds.domain.model.Membership;
import org.openhds.domain.model.SocialGroup;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SocialGroupServiceImpl implements SocialGroupService {
	
	private static Logger log = LoggerFactory.getLogger(SocialGroupServiceImpl.class);
	
	private EntityService service;
	private GenericDao genericDao;
	private IndividualService individualService;
	private ResidencyService residencyService;
	private SocialGroupGenerator generator;
	
	public SocialGroupServiceImpl(GenericDao genericDao, IndividualService individualService, ResidencyService residencyService, EntityService service, SocialGroupGenerator generator) {
		this.genericDao = genericDao;
		this.individualService = individualService;
		this.residencyService = residencyService;
		this.service = service;
		this.generator = generator;
	}
	
	public SocialGroup evaluateSocialGroup(SocialGroup entityItem) throws ConstraintViolations {
		if (entityItem.getGroupHead() != null && entityItem.getGroupHead().getExtId() == null) 
			entityItem.setGroupHead(null);
		
		if (entityItem.getGroupHead() != null) {
			if (individualService.getLatestEvent(entityItem.getGroupHead()).equals("Death")) {
				throw new ConstraintViolations("A Social Group cannot be created for an Individual who has a Death event.");	
			}      	
		}
		if (findSocialGroupById(entityItem.getExtId()) != null)
			throw new ConstraintViolations("The Id specified already exists");	
		
		generator.validateIdLength(entityItem.getExtId(), generator.getIdScheme());
		
		return entityItem;
	}
	
	public SocialGroup generateId(SocialGroup entityItem) throws ConstraintViolations {
		entityItem.setExtId(generator.generateId(entityItem));
		return entityItem;
	}
	
	public SocialGroup checkSocialGroup(SocialGroup persistedItem, SocialGroup entityItem) throws ConstraintViolations {
		if (!compareDeathInSocialGroup(persistedItem, entityItem))
			throw new ConstraintViolations("A Social Group cannot be saved because an attempt was made to set the Group Head on an Individual who has a Death event.");			
		return entityItem;
	}
		
    /**
     * Retrieves all Social Group extId's that contain the term provided.
     * Used in performing autocomplete.
     */
    public List<String> getSocialGroupExtIds(String term) {
        List<String> ids = new ArrayList<String>();
        List<SocialGroup> list = genericDao.findListByPropertyPrefix(SocialGroup.class, "extId", term, 10, true);
        for (SocialGroup sg : list) {
            ids.add(sg.getExtId());
        }

        return ids;
    }
    
	/**
	 * Compares the persisted and (soon to be persisted) SocialGroup items.
	 * If the persisted item and entity item has a mismatch of an end event type
	 * and the persisted item has a end type of death, the edit cannot be saved.
	 */
	public boolean compareDeathInSocialGroup(SocialGroup persistedItem, SocialGroup entityItem) {	
		if (individualService.getLatestEvent(persistedItem.getGroupHead()).equals("Death") ||
				individualService.getLatestEvent(entityItem.getGroupHead()).equals("Death"))		
			return false;

		return true;
	}
	
	@Transactional(rollbackFor=Exception.class)
	public void deleteSocialGroup(SocialGroup group) throws SQLException {
		
		if (group.getMemberships() != null) {
		
			Set<Membership> mems = group.getMemberships();		
			for (Membership item : mems) 
				service.delete(item);
		}
		service.delete(group);
	}
		
	public List<Individual> getAllIndividualsOfSocialGroup(SocialGroup group) {
		
		List<Individual> list = new ArrayList<Individual>();
		List<Membership> mems = genericDao.findListByProperty(Membership.class, "socialGroup", group);
		
		for (Membership item : mems) {
			if (item.getEndDate() == null && !item.isDeleted())
				list.add(item.getIndividual());
		}
		return list;
	}
	
	public List<SocialGroup> getAllSocialGroups(Individual individual) {
		List<SocialGroup> list = genericDao.findListByProperty(SocialGroup.class, "groupHead", individual, true);
		return list;
	}
	
	public SocialGroup getSocialGroupForIndividualByType(Individual individual, String groupType) {
        Set<Membership> memberships = individual.getAllMemberships();
 
        for (Membership membership : memberships) {
            if (membership.getSocialGroup().getGroupType() == null) {
            	return null;
            } else {
                if (membership.getSocialGroup().getGroupType().equals("FAM")) {
                    return membership.getSocialGroup();
                }
            }
        }
        return null;
 	}
		
	@Transactional(rollbackFor=Exception.class)
	public void modifySocialGroupHead(SocialGroup group, Individual selectedSuccessor, 
			List<Membership> memberships) throws ConstraintViolations, SQLException, Exception {

		group.setGroupHead(selectedSuccessor);
		
		// Remove all Memberships from the Social Group	
		Set<Membership> mems = group.getMemberships();  		
		Iterator<Membership> itr = mems.iterator();
		
		while(itr.hasNext()) {
			Membership item = itr.next();
			item.setDeleted(true);
			service.save(item);
		}
		
		// Create new Memberships 
		itr = memberships.iterator();
    	for (Membership item : memberships) 
    		service.create(item);
    	
    	service.save(group);
	}
	
    public SocialGroup findSocialGroupById(String socialGroupId, String msg) throws Exception {
        SocialGroup sg = genericDao.findByProperty(SocialGroup.class, "extId", socialGroupId);
        if (sg == null) {
            throw new Exception(msg);
        }
        return sg;
    }
    
    public SocialGroup findSocialGroupById(String sgExtId) {
        SocialGroup sg = genericDao.findByProperty(SocialGroup.class, "extId", sgExtId);
        return sg;
    }

	@Override
	public List<SocialGroup> getAllSocialGroups() {
		return genericDao.findAll(SocialGroup.class, true);
	}

	@Override
	public void createSocialGroup(SocialGroup socialGroup) throws ConstraintViolations {
		assignId(socialGroup);
	    evaluateSocialGroup(socialGroup);
		
		try {
			service.create(socialGroup);
		} catch (IllegalArgumentException e) {
		} catch (SQLException e) {
			throw new ConstraintViolations("There was a problem saving the social group to the database");
		}
	}

    private void assignId(SocialGroup socialGroup) throws ConstraintViolations {
    	log.info("assignId for socialGroup with extId=[" + socialGroup.getExtId() + "]");
        String id = socialGroup.getExtId() == null ? "" : socialGroup.getExtId();
        log.info("assignId for socialGroup=[" + id + "]");
        if (id.trim().isEmpty() && generator.generated) {
        	log.info("Generating id for social group");
            generateId(socialGroup);
        }
    }

    @Override
    @Authorized("VIEW_ENTITY")
    public List<SocialGroup> getAllSocialGroupsInRange(int i, int pageSize) {
        return genericDao.findPaged(SocialGroup.class, "extId", i, pageSize);
    }

    @Override
    @Authorized("VIEW_ENTITY")
    public long getTotalSocialGroupCount() {
        return genericDao.getTotalCount(SocialGroup.class);
    }
    
    @Override
    @Authorized("VIEW_ENTITY")
	public Set<SocialGroup> getSocialGroupsAssociatedWithLocation(Location location) {
    	Set<SocialGroup> socialGroups = new HashSet<SocialGroup>();
    	
    	List<Individual> individuals = residencyService.getIndividualsByLocation(location);
		for (Individual individual : individuals) {
			List<SocialGroup> socialGroupsForIndividual = getAllSocialGroups(individual);
			socialGroups.addAll(socialGroupsForIndividual);
		}

    	return socialGroups;
    }
}
