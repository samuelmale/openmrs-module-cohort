package org.openmrs.module.cohort;

import java.util.Date;

import org.openmrs.BaseOpenmrsData;
import org.openmrs.Patient;


public class CohortMember extends BaseOpenmrsData {
	
	private static final long serialVersionUID = 1L;
	private Integer cohortMemberId;
	private Patient patient;
	private Integer patientId;
	private CohortM cohort;
	private CohortRole role;
	private Date startDate;
	private Date endDate;
	private Boolean head;
	
	public Boolean isHead() {
		return head;
	}
	
	public void setHead(Boolean head) {
		this.head = head;
	}
	public Boolean getHead() {
		return head;
	}

	public CohortMember() {
		
	}
	
	public CohortMember(Patient patient) {
		if (patient != null) {
			this.patientId = patient.getPatientId();
			if (patient.getUuid() != null) {
				this.setUuid(patient.getUuid());
			}
		}
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
		return getCohortMemberId();
	}
	
	@Override
	public void setId(Integer arg0) {
		setCohortMemberId(arg0);
	}
	
	public Integer getCohortMemberId() {
		return cohortMemberId;
	}
	
	public void setCohortMemberId(Integer cohortMemberId) {
		this.cohortMemberId = cohortMemberId;
	}
	
	public Patient getPatient() {
		return patient;
	}
	
	public void setPatient(Patient patient) {
		this.patient = patient;
	}
	
	public CohortM getCohort() {
		return cohort;
	}
	
	public void setCohort(CohortM cohort) {
		this.cohort = cohort;
	}
	
	public CohortRole getRole() {
		return role;
	}
	
	public void setRole(CohortRole role) {
		this.role = role;
	}
}
