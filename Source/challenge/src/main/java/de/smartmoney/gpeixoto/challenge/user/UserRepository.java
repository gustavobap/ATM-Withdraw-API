package de.smartmoney.gpeixoto.challenge.user;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long>{

	public Optional<User> findByEmail(String email);

	public Optional<User> findByCode(Long code);

	//TODO the specific RDB SQL should be parameterized explicitly
	@Query(value = "CALL NEXT VALUE FOR code_generator_seq", nativeQuery = true)
	public Long generateNextCode();

}
