package org.openmrs.module.cohort;

import org.openmrs.api.context.Context;
import org.openmrs.module.cohort.api.CohortService;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class CohortMemberUnsubscriber {

    private CohortService cohortService;

    public final String VOID_REASON = "Subscribed to Cohort type that requires patient to be dismissed from past cohorts";

    public CohortMemberUnsubscriber() {

    }

    public CohortMemberUnsubscriber(CohortService cohortService) {
        this.cohortService = cohortService;
    }

    /**
     * Unsubscribes patients from all other cohorts with exception of the {@code currentCohort}
     *
     * @param candidates the members to be dismissed from cohorts with exception of the {@code currentCohort}
     * @param currentCohort the current cohort
     * @return dismissed cohort members
     */
    public List<CohortMember> dismissFromPastCohorts(List<CohortMember> candidates, CohortM currentCohort) {
        if (cohortService == null) {
            cohortService = Context.getService(CohortService.class);
        }
        return candidates.stream().map(member -> member.getPatient()).
                map(patient -> cohortService.findCohortMembersByPatient(patient.getId())).flatMap(Collection::stream).
                filter(member -> !(member.getCohort().getUuid().equals(currentCohort.getUuid()))).
                map(dueMembership -> {
                    dueMembership.setVoided(true);
                    dueMembership.setVoidReason(VOID_REASON);
                    return cohortService.saveCohortMember(dueMembership);
                }).collect(Collectors.toList());
    }

    public CohortService getCohortService() {
        return cohortService;
    }

    public void setCohortService(CohortService cohortService) {
        this.cohortService = cohortService;
    }
}
