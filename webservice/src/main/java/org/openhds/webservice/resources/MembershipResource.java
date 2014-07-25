package org.openhds.webservice.resources;

import org.openhds.controller.exception.ConstraintViolations;
import org.openhds.controller.service.EntityService;
import org.openhds.controller.service.IndividualService;
import org.openhds.controller.service.MembershipService;
import org.openhds.domain.model.Individual;
import org.openhds.domain.model.Membership;
import org.openhds.domain.model.wrappers.Memberships;
import org.openhds.domain.util.ShallowCopier;
import org.openhds.task.support.FileResolver;
import org.openhds.webservice.FieldBuilder;
import org.openhds.webservice.response.WebserviceResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.openhds.webservice.response.WebserviceResultHelper.foundResponse;
import static org.openhds.webservice.response.WebserviceResultHelper.notFoundResponse;


@Controller
@RequestMapping("/memberships")
public class MembershipResource extends ResourceTemplate<Membership, Memberships> {

    private static final Logger logger = LoggerFactory.getLogger(MembershipResource.class);

    private MembershipService membershipService;

    private IndividualService individualService;

    private EntityService entityService;

    private FieldBuilder fieldBuilder;

    private FileResolver fileResolver;


    @Autowired
    public MembershipResource(MembershipService membershipService, IndividualService individualService,
                              EntityService entityService, FieldBuilder fieldBuilder, FileResolver fileResolver) {
        this.membershipService = membershipService;
        this.individualService = individualService;
        this.entityService = entityService;
        this.fieldBuilder = fieldBuilder;
        this.fileResolver = fileResolver;
        this.entityName = "membership";
    }

    @Override
    protected Membership findById(String id) {
        return null;
    }

    @Override
    protected Memberships findAll() {
        List<Membership> memberships = membershipService.getAllMemberships();
        List<Membership> copies = new ArrayList<Membership>(memberships.size());
        for (Membership m : memberships) {
            Membership copy = ShallowCopier.shallowCopyMembership(m);
            copies.add(copy);
        }
        Memberships allMemberships = new Memberships();
        allMemberships.setEntities(copies);
        return allMemberships;
    }

    @Override
    protected File findAllCached() {
        return fileResolver.resolveMembershipXmlFile();
    }

    @Override
    protected boolean create(Membership membership) throws Exception {
        ConstraintViolations cv = new ConstraintViolations();
        membership.setCollectedBy(fieldBuilder.referenceField(membership.getCollectedBy(), cv));
        membership.setSocialGroup(fieldBuilder.referenceField(membership.getSocialGroup(), cv));
        membership.setIndividual(fieldBuilder.referenceField(membership.getIndividual(), cv));

        if (cv.hasViolations()) {
            throw cv;
        }

        membershipService.createMembership(membership);

        return true;
    }

    @Override
    protected boolean createOrUpdate(Membership membership) throws Exception {
        ConstraintViolations cv = new ConstraintViolations();
        membership.setCollectedBy(fieldBuilder.referenceField(membership.getCollectedBy(), cv));
        membership.setSocialGroup(fieldBuilder.referenceField(membership.getSocialGroup(), cv));
        membership.setIndividual(fieldBuilder.referenceField(membership.getIndividual(), cv));

        if (cv.hasViolations()) {
            throw cv;
        }

        if (membershipService.checkDuplicateMembership(membership.getIndividual(), membership.getSocialGroup())) {
            entityService.save(membership);
        } else {
            membershipService.createMembership(membership);
        }

        return true;
    }

    @Override
    protected boolean deleteById(String id) {
        return false;
    }

    @Override
    protected Membership normalize(Membership membership) {
        return ShallowCopier.shallowCopyMembership(membership);
    }


    @RequestMapping(value = "byIndividual/{extId}", method = RequestMethod.GET, produces = "application/xml")
    @ResponseBody
    public ResponseEntity<WebserviceResult> getAllMembershipsByIndividualId(@PathVariable String extId) {

        Individual individual = individualService.findIndivById(extId);
        if (individual == null) {
            return notFoundResponse("No such individual with extId " + extId);
        }

        List<Membership> memberships = membershipService.getAllMemberships();
        List<Membership> copies = new ArrayList<Membership>(memberships.size());
        for (Membership m : memberships) {
            Membership copy = ShallowCopier.shallowCopyMembership(m);
            copies.add(copy);
        }

        Memberships allMemberships = new Memberships();
        allMemberships.setEntities(copies);
        return foundResponse(entityName, allMemberships,
                "found " + allMemberships.size() + " memberships for individual " + extId);
    }
}
