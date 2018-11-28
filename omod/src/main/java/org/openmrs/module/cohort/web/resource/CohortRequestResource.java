package org.openmrs.module.cohort.web.resource;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.api.context.Context;
import org.openmrs.module.cohort.CohortLeader;
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

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@Resource(name = RestConstants.VERSION_1 + CohortRest.COHORT_NAMESPACE + "/cohort",
        supportedClass = CohortM.class,
        supportedOpenmrsVersions = {"1.8.*", "1.9.*", "1.10.*, 1.11.*", "1.12.*", "2.0.*", "2.1.*", "2.2.*"})
public class CohortRequestResource extends DataDelegatingCrudResource<CohortM> {

    @Override
    public DelegatingResourceDescription getRepresentationDescription(Representation rep) {
		if (Context.isAuthenticated()) {
	        DelegatingResourceDescription description = new DelegatingResourceDescription();
	        
	        if (rep instanceof DefaultRepresentation) {
	            description.addProperty("name");
	            description.addProperty("description");
	            description.addProperty("location");
	            description.addProperty("startDate");
	            description.addProperty("endDate");
	            description.addProperty("cohortType");
	            description.addProperty("cohortProgram");
	            description.addProperty("attributes");
	            description.addProperty("cohortLeaders");
	            description.addProperty("groupCohort");
	            description.addProperty("uuid");
	            description.addProperty("voided");
                description.addProperty("voidReason");
                description.addProperty("cohortMembers");
	            description.addSelfLink();
	        } 
	        else if (rep instanceof FullRepresentation) {
	            description.addProperty("name");
	            description.addProperty("description");
	            description.addProperty("location");
	            description.addProperty("startDate");
	            description.addProperty("endDate");
	            description.addProperty("cohortType");
	            description.addProperty("cohortProgram");
	            description.addProperty("cohortLeaders");
	            description.addProperty("attributes");
	            description.addProperty("groupCohort");
	            description.addProperty("cohortMembers");
                description.addProperty("voided");
                description.addProperty("voidReason");
	            description.addProperty("uuid");
				description.addProperty("auditInfo");
	            description.addSelfLink();
	        }
	        
	        return description;
		}
		return null;
    }

    @Override
    public DelegatingResourceDescription getCreatableProperties() {
        DelegatingResourceDescription description = new DelegatingResourceDescription();
        
        description.addRequiredProperty("name");
        description.addProperty("description");
        description.addRequiredProperty("location");
        description.addRequiredProperty("startDate");
        description.addProperty("endDate");
        description.addRequiredProperty("cohortType");
        description.addProperty("cohortProgram");	            
        description.addProperty("attributes");
        description.addProperty("cohortMembers");
        description.addProperty("voided");
        description.addProperty("groupCohort");
        return description;
    }
    
    @Override
    public DelegatingResourceDescription getUpdatableProperties() throws ResourceDoesNotSupportOperationException {
        DelegatingResourceDescription description = new DelegatingResourceDescription();

        description.addRequiredProperty("name");
        description.addProperty("description");
        description.addRequiredProperty("location");
        description.addRequiredProperty("startDate");
        description.addProperty("endDate");
        description.addRequiredProperty("cohortType");
        description.addProperty("cohortProgram");
        description.addProperty("attributes");
        description.addProperty("cohortMembers");
        description.addProperty("groupCohort");
        description.addProperty("voided");
        description.addProperty("voidReason");
        return description;
    }

    @Override
    public CohortM save(CohortM cohort) {
        if(cohort.getVoided()) {
            for(CohortLeader cohortLeader: cohort.getActiveCohortLeaders()) {
                cohortLeader.setVoided(true);
                cohortLeader.setVoidReason("Cohort Ended");
                cohortLeader.setEndDate(cohort.getEndDate());
            }
        // end memberships if cohort is voided
        if(cohort.getVoided()) {
            for(CohortMember cohortMember: cohort.getCohortMembers()) {
                cohortMember.setVoided(true);
                cohortMember.setVoidReason("Cohort Ended");
                cohortMember.setEndDate(cohort.getEndDate());
                }
            }
        }
        return Context.getService(CohortService.class).saveCohort(cohort);
    }

    @Override
    protected void delete(CohortM cohort, String reason, RequestContext request) throws ResponseException {
    	cohort.setVoided(true);
    	cohort.setVoidReason(reason);
        Context.getService(CohortService.class).saveCohort(cohort);
    }

    @Override
    public void purge(CohortM cohort, RequestContext request) throws ResponseException {
        Context.getService(CohortService.class).purgeCohort(cohort);
    }

    @Override
    public CohortM newDelegate() {
        return new CohortM();
    }

    @Override
    public CohortM getByUniqueId(String id) {
        CohortM obj = Context.getService(CohortService.class).getCohortByUuid(id);
        
        if(obj == null) {
        	// TODO add to API
        	// obj = Context.getService(CohortService.class).getCohortByName(id);
        }
        
        return obj;
    }

    @Override
    protected PageableResult doGetAll(RequestContext context) throws ResponseException {
    	List<CohortM> cohort = Context.getService(CohortService.class).getAllCohorts();
        return new NeedsPaging<CohortM>(cohort, context);
    }

    @Override
    protected PageableResult doSearch(RequestContext context) {
        String attributesStr = context.getParameter("attributes");
        Map<String, String> attributes = null;

        if(StringUtils.isNotBlank(attributesStr)){
        	try {
				attributes = new ObjectMapper().readValue("{"+attributesStr+"}", new TypeReference<Map<String, String>>() {});
			} catch (Exception e) {
				e.printStackTrace();
				throw new RuntimeException("Invalid format for parameter 'attributes'");
			}
        }

        List<CohortM> cohort = Context.getService(CohortService.class).findCohortsMatching(context.getParameter("q"), attributes);
        return new NeedsPaging<CohortM>(cohort, context);
    }
}
