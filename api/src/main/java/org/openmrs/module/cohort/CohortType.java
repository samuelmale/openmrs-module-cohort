package org.openmrs.module.cohort;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.openmrs.BaseOpenmrsData;

@Entity
@Table(name = "cohort_type")
public class CohortType extends BaseOpenmrsData {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "cohort_type_id")
	private int cohortTypeId;

	private String name;

	private String description;

	@Column(name = "dismiss_members_from_past_cohorts")
	private Boolean dismissMembersFromPastCohorts;

	public CohortType() {
		this.dismissMembersFromPastCohorts = Boolean.FALSE;
	}
	@Override
	public Integer getId() {
		return getCohortTypeId();
	}

	@Override
	public void setId(Integer id) {
		setCohortTypeId(id);
	}

	public int getCohortTypeId() {
		return cohortTypeId;
	}

	public void setCohortTypeId(int cohortTypeId) {
		this.cohortTypeId = cohortTypeId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Boolean shouldDismissMembersFromPastCohorts() {
		return dismissMembersFromPastCohorts;
	}

	public void setDismissMembersFromPastCohorts(Boolean dismissMembersFromPastCohorts) {
		this.dismissMembersFromPastCohorts = dismissMembersFromPastCohorts;
	}
}
