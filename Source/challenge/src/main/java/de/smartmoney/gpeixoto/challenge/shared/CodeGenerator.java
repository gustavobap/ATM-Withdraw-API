package de.smartmoney.gpeixoto.challenge.shared;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;

// This is a workaround for a spring boot limitation, it does not support generating 
// values for attributes that are not annotated with @Id neither creating a database 
// sequence to be queried programmatically.
//
// https://stackoverflow.com/questions/277630/hibernate-jpa-sequence-non-id
//
// Note that using specific RDB SQL is preferable over a solution running in the JVM 
// context (like AtomicLong), since ACID properties can be guaranteed in database level.
//
// This Entity is declared only to generate the sequence creation, therefore, the database 
// table is unused.
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
