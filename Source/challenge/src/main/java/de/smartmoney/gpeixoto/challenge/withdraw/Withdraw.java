package de.smartmoney.gpeixoto.challenge.withdraw;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.NaturalId;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import de.smartmoney.gpeixoto.challenge.user.User;

@Entity
@JsonPropertyOrder({ "code", "createdDate", "value", "fee" })
public class Withdraw {

	@Id
	@GeneratedValue
	@JsonIgnore
	private Long id;

	@NaturalId
	@Column(updatable = false, nullable = false, unique = true)
  	private Long code;

	@NotNull(message = "A value must be specified")
	@DecimalMin(value = "1", message = "must be greater than or equal to $1.00")
	@DecimalMax(value = "300", message = "Your withdrawals are limited to $300.00")
	@Digits(integer = 3, fraction = 2, message = "scale must be of 2 decimal places")
	@Column(nullable = false, precision = 5, scale = 2)
	private BigDecimal value;

	@Column(nullable = false, precision = 8, scale = 5)
	private BigDecimal fee;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@Column(nullable = false)
	private Instant createdDate;

	public Withdraw() {

	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getCode() {
		return code;
	}

	public void setCode(Long code) {
		this.code = code;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public BigDecimal getValue() {
		return value;
	}

	public void setValue(BigDecimal value) {
		this.value = value;
	}

	public BigDecimal getFee() {
		return fee;
	}

	public void setFee(BigDecimal fee) {
		this.fee = fee;
	}

	public Instant getCreatedDate() {
		return createdDate;
	}

	@PrePersist
	public void prePersist() {
		Instant now = Instant.now();
		// Database is limited to millisecond precision
		this.createdDate = now.truncatedTo(ChronoUnit.MILLIS);
	}

	public void setCreatedDate(Instant createdDate) {
		this.createdDate = createdDate;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((code == null) ? 0 : code.hashCode());
		result = prime * result + ((createdDate == null) ? 0 : createdDate.hashCode());
		result = prime * result + ((fee == null) ? 0 : fee.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((user == null) ? 0 : user.hashCode());
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof Withdraw))
			return false;
		Withdraw other = (Withdraw) obj;
		if (code == null) {
			if (other.code != null)
				return false;
		} else if (!code.equals(other.code))
			return false;
		if (createdDate == null) {
			if (other.createdDate != null)
				return false;
		} else if (!createdDate.equals(other.createdDate))
			return false;
		if (fee == null) {
			if (other.fee != null)
				return false;
		} else if (!fee.equals(other.fee))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (user == null) {
			if (other.user != null)
				return false;
		} else if (!user.equals(other.user))
			return false;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}

}
