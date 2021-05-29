package com.ratnaafin.crm.user.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedStoredProcedureQuery;
import javax.persistence.ParameterMode;
import javax.persistence.StoredProcedureParameter;



@NamedStoredProcedureQuery(
		name= ProcedureEntity.TEST, 
		procedureName = "test", 
		parameters = {
					@StoredProcedureParameter(mode = ParameterMode.IN, name = "A_INPUT", type=String.class)
				})

@Entity
public class ProcedureEntity {
	public static final String TEST = "TEST";
	@Id
	@Column(name = "ID")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
}
