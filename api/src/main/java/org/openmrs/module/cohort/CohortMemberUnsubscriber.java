package org.openmrs.module.cohort;

import org.openmrs.module.cohort.api.db.CohortDAO;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class CohortMemberUnsubscriber {


    public static final String VOID_REASON = "Subscribed to Cohort type that requires patient to be dismissed from past cohorts";

    /**
     * Unsubscribes patients from all other cohorts with exception of the {@code currentCohort}
     *
     * @param candidates the members to be dismissed from cohorts with exception of the {@code currentCohort}
     * @param currentCohort the current cohort
     * @param cohortDAO the data access object instance
     * @return dismissed cohort members
     */
    public static List<CohortMember> dismissFromPastCohorts(List<CohortMember> candidates, CohortM currentCohort, CohortDAO cohortDAO) {
        return candidates.stream()
                .map(member -> member.getPatient())
                .map(patient -> cohortDAO.getCohortMembersByPatientId(patient.getId()))
                .flatMap(Collection::stream)
                .filter(member -> !(member.getCohort().getUuid().equals(currentCohort.getUuid())))
                .map(dueMembership -> {
                    dueMembership.setVoided(true);
                    dueMembership.setVoidReason(VOID_REASON);
                    return cohortDAO.saveCPatient(dueMembership);
                }).collect(Collectors.toList());
    }

}
