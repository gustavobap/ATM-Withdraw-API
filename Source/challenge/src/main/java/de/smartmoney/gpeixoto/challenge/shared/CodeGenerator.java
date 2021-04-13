package de.smartmoney.gpeixoto.challenge.shared;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;

// This is a workaround to overcome a spring limitation, it does not support
// creating a database sequence to be queried programmatically. This Entity is declared
// only to trigger the sequence creation, therefore, the database table
// is unused.
@Entity
public class CodeGenerator {
	
	@Id
	@GeneratedValue(generator="code_generator_seq")
	@SequenceGenerator(name="code_generator_seq", sequenceName="code_generator_seq", allocationSize=1)
	@Column(insertable = false, updatable = false)
	private Long code;

	public Long getCode() {
		return code;
	}
	
	public void setCode(Long number) {
		this.code = number;
	}
}
