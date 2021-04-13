package de.smartmoney.gpeixoto.challenge.withdraw;

import java.time.Instant;
import java.util.Optional;

import javax.persistence.LockModeType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import de.smartmoney.gpeixoto.challenge.user.User;

@Repository
public interface WithdrawRepository extends JpaRepository<Withdraw, Long> {

	// TODO should be optimistic
	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("SELECT u FROM User u WHERE u.id = :id")
	public void lockByUser(@Param("id") Long id);

	public Long countByUser(User user);

	public Long countByUserAndCreatedDateAfter(User user, Instant after);

	public Optional<Withdraw> findByCode(Long code);

	//TODO the specific RDB SQL should be parameterized explicitly
	@Query(value = "CALL NEXT VALUE FOR code_generator_seq", nativeQuery = true)
	public Long generateNextCode();
}
