package org.openmrs.module.cohort.web.resource;

import java.util.List;

import org.openmrs.api.context.Context;
import org.openmrs.module.cohort.CohortM;
import org.openmrs.module.cohort.CohortMember;
import org.openmrs.module.cohort.api.CohortService;
import org.openmrs.module.cohort.rest.v1_0.resource.CohortRest;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.representation.DefaultRepresentation;
import org.openmrs.module.webservices.rest.web.representation.FullRepresentation;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.api.PageableResult;
import org.openmrs.module.webservices.rest.web.resource.impl.DataDelegatingCrudResource;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.resource.impl.NeedsPaging;
import org.openmrs.module.webservices.rest.web.response.ResourceDoesNotSupportOperationException;
import org.openmrs.module.webservices.rest.web.response.ResponseException;

@Resource(name = RestConstants.VERSION_1 + CohortRest.COHORT_NAMESPACE + "/cohortmember", supportedClass = CohortMember.class, supportedOpenmrsVersions = {"1.8.*", "1.9.*", "1.10.*, 1.11.*", "1.12.*", "2.0.*", "2.1.*", "2.2.*"})
public class CohortMemberRequestResource extends DataDelegatingCrudResource<CohortMember> {

    @Override
    public DelegatingResourceDescription getRepresentationDescription(Representation rep) {

        DelegatingResourceDescription description = null;

        if (Context.isAuthenticated()) {
            description = new DelegatingResourceDescription();
            if (rep instanceof DefaultRepresentation) {
                description.addProperty("person");
                description.addProperty("cohort");
                description.addProperty("role");
                description.addProperty("startDate");
                description.addProperty("endDate");
                description.addProperty("head");
                description.addProperty("uuid");
                description.addSelfLink();
            } 
            else if (rep instanceof FullRepresentation) {
            	description.addProperty("person", Representation.FULL);
                description.addProperty("cohort");
                description.addProperty("role");
                description.addProperty("startDate");
                description.addProperty("endDate");
                description.addProperty("head");
                description.addProperty("uuid");
                description.addProperty("auditInfo");
                description.addSelfLink();
            }
        }
        return description;
    }

    @Override
    public DelegatingResourceDescription getCreatableProperties() {
        DelegatingResourceDescription description = new DelegatingResourceDescription();
        description.addRequiredProperty("person");
        description.addRequiredProperty("cohort");
        description.addRequiredProperty("role");
        description.addProperty("startDate");
        description.addProperty("endDate");
        description.addRequiredProperty("head");
        return description;
    }
    
    @Override
    public DelegatingResourceDescription getUpdatableProperties() throws ResourceDoesNotSupportOperationException {
    	DelegatingResourceDescription description = new DelegatingResourceDescription();
        description.addRequiredProperty("role");
        description.addProperty("startDate");
        description.addProperty("endDate");
        description.addRequiredProperty("head");
        return description;
    }

    @Override
    public CohortMember newDelegate() {
        return new CohortMember();
    }

    @Override
    public CohortMember save(CohortMember cohortMember) {
        return Context.getService(CohortService.class).saveCohortMember(cohortMember);
    }

    @Override
    protected void delete(CohortMember cohortMember, String reason, RequestContext context) throws ResponseException {
    	cohortMember.setVoided(true);
    	cohortMember.setVoidReason(reason);
        Context.getService(CohortService.class).saveCohortMember(cohortMember);
    }

    @Override
    public CohortMember getByUniqueId(String uuid) {
        return Context.getService(CohortService.class).getCohortMemberByUuid(uuid);
    }

    @Override
    public void purge(CohortMember cohortMember, RequestContext context) throws ResponseException {
    	throw new UnsupportedOperationException();
    }

    @Override
    protected PageableResult doSearch(RequestContext context) {
    	String cohort = context.getParameter("cohort");
    	
    	CohortM cohorto = Context.getService(CohortService.class).getCohortByName(cohort);
    	if(cohorto == null) {
    		cohorto = Context.getService(CohortService.class).getCohortByUuid(cohort);
    	}
    	
    	if(cohorto == null) {
    		throw new IllegalArgumentException("No valid value specified for param cohort");
    	}
    	
    	List<CohortMember> list = Context.getService(CohortService.class).findCohortMembersByCohort(cohorto.getCohortId());
    	return new NeedsPaging<CohortMember>(list, context);
    }
}
