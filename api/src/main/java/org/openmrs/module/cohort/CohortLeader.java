
/**
This Source Code Form is subject to the terms of the Mozilla Public License, 
v. 2.0. If a copy of the MPL was not distributed with this file, You can 
obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under 
the terms of the Healthcare Disclaimer located at http://openmrs.org/license.

Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS 
graphic logo is a trademark of OpenMRS Inc.
**/

package org.openmrs.module.cohort;

import org.openmrs.BaseOpenmrsData;
import org.openmrs.Person;
import java.util.Date;

public class CohortLeader extends BaseOpenmrsData {
    private static final long serialVersionUID = 1L;
    private Date startDate;
    private Date endDate;
    private Person person;
    private Integer personId;
    private int cohortLeaderId;
    private CohortM cohort;

    public CohortM getCohort() {
        return cohort;
    }

    public void setCohort(CohortM cohort) {
        this.cohort = cohort;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public CohortLeader() {

    }
    public CohortLeader(Person person) {
        if (person != null) {
            this.personId = person.getPersonId();
            if (person.getUuid() != null) {
                this.setUuid(person.getUuid());
            }
        }
    }

    public int getCohortLeaderId() {
        return cohortLeaderId;
    }

    public void setCohortLeaderId(int cohortLeaderId) {
        this.cohortLeaderId = cohortLeaderId;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    @Override
    public Integer getId() {
        return cohortLeaderId;
    }

    @Override
    public void setId(Integer id) {
        this.cohortLeaderId = id;
    }
}
