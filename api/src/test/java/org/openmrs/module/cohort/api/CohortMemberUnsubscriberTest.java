package org.openmrs.module.cohort.api;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.openmrs.Patient;
import org.openmrs.module.cohort.CohortM;
import org.openmrs.module.cohort.CohortMember;
import org.openmrs.module.cohort.CohortMemberUnsubscriber;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doAnswer;

@RunWith(MockitoJUnitRunner.class)
public class CohortMemberUnsubscriberTest {

    private List<CohortMember> cohortMembers;
    private Patient patient1;
    private Patient patient2;
    private Patient patient3;
    private CohortM cohort1;
    private CohortM cohort2;
    private CohortM cohort3;
    private List<CohortM> cohorts;
    private CohortMemberUnsubscriber cohortMemberUnsubscriber;
    @Mock
    private CohortService service;


    @Before
    public void setup() {
        // create cohorts
        cohort1 = newCohort("Today's clients", 1, "49458d51-dcf8-4663-8952-6058fdca4b21");
        cohort2 = newCohort("HIV Testing Services", 2, "0c68d8b8-e946-11eb-9a03-0242ac130003");
        cohort3 = newCohort("Counselling", 3, "9c68d8b8-e946-11eb-9a05-024aac13000v");

        // create patients
        patient1 = new Patient(1);
        patient2 = new Patient(2);
        patient3 = new Patient(3);
        // handle memberships
        CohortMember member1 = new CohortMember();
        member1.setPatient(patient1);
        member1.setCohort(cohort1);
        CohortMember member2 = new CohortMember();
        member2.setPatient(patient2);
        member2.setCohort(cohort1);
        CohortMember member3 = new CohortMember();
        member3.setPatient(patient3);
        member3.setCohort(cohort2);
        CohortMember member4 = new CohortMember();
        member4.setPatient(patient1);
        member4.setCohort(cohort3);
        CohortMember member5 = new CohortMember();
        member5.setPatient(patient3);
        member5.setCohort(cohort1);
        CohortMember member6 = new CohortMember();
        member6.setPatient(patient2);
        member6.setCohort(cohort3);

        cohort1.setCohortMembers(Arrays.asList(member1, member2, member5));
        cohort2.setCohortMembers(Arrays.asList(member3));
        cohort3.setCohortMembers(Arrays.asList(member4, member6));

        cohortMembers = Arrays.asList(member1, member2, member3, member4, member5, member6);
        cohortMemberUnsubscriber = new CohortMemberUnsubscriber(service);

        // Some boilerplate stubs
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                int patientId = invocationOnMock.getArgument(0);
                return cohortMembers.stream().filter(m -> m.getPatient().getId() == patientId).collect(Collectors.toList());
            }
        }).when(service).findCohortMembersByPatient(anyInt());

        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                CohortMember cohortMember = invocationOnMock.getArgument(0);
                return cohortMember;
            }
        }).when(service).saveCohortMember(any(CohortMember.class));
    }

    @Test
    public void dismissFromPastCohorts_shouldDismissPatientsFromAllOtherCohortsExceptCohort1() {
        // replay
        List<CohortMember> dismissedMembers = cohortMemberUnsubscriber.dismissFromPastCohorts(cohort1.getCohortMembers(), cohort1);

        // NOTES:
        // cohort1 had 3 members ie. member1, member2 and member5 associated to patient1, patient2 and patient3 respectively
        // patient1 - was part of cohort1 and cohort3, should have been dismissed from cohort3
        // patient2 - was only part of cohort1 and cohort3, should have been dismissed from cohort3
        // patient3 - was part of cohort1 and cohort2, should have been dismissed from cohort2

        // verify
        assertThat(dismissedMembers.size(), equalTo(3));
        // confirm that patient1's membership with cohort3 was revoked
        CohortMember dismissedMember1 = dismissedMembers.stream().filter(cohortMember -> cohortMember.getPatient().getId() == patient1.getId()).collect(Collectors.toList()).get(0);
        assertThat(dismissedMember1.getCohort().getUuid(), equalTo(cohort3.getUuid()));
        assertTrue(dismissedMember1.getVoided());
        assertThat(dismissedMember1.getVoidReason(), equalTo(cohortMemberUnsubscriber.VOID_REASON));
        // confirm that patient2's membership with cohort3 was revoked
        CohortMember dismissedMember2 = dismissedMembers.stream().filter(cohortMember -> cohortMember.getPatient().getId() == patient2.getId()).collect(Collectors.toList()).get(0);
        assertThat(dismissedMember2.getCohort().getUuid(), equalTo(cohort3.getUuid()));
        assertTrue(dismissedMember2.getVoided());
        assertThat(dismissedMember2.getVoidReason(), equalTo(cohortMemberUnsubscriber.VOID_REASON));
        // confirm that patient3's membership with cohort2 was revoked
        CohortMember dismissedMember3 = dismissedMembers.stream().filter(cohortMember -> cohortMember.getPatient().getId() == patient3.getId()).collect(Collectors.toList()).get(0);
        assertThat(dismissedMember3.getCohort().getUuid(), equalTo(cohort2.getUuid()));
        assertTrue(dismissedMember3.getVoided());
        assertThat(dismissedMember3.getVoidReason(), equalTo(cohortMemberUnsubscriber.VOID_REASON));
    }

    private CohortM newCohort(String name, Integer id, String uuid) {
        CohortM cohort = new CohortM();
        cohort.setName(name);
        cohort.setCohortId(id);
        cohort.setUuid(uuid);
        return cohort;
    }
}
