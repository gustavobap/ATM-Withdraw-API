package de.smartmoney.gpeixoto.challenge.user;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

@Service
public class UserService {

	private final UserRepository repository;
	
	public UserService(UserRepository repository) {
		this.repository = repository;
	}

	public Optional<User> find(Long code) {
		return repository.findByCode(code);
	}

	public User save(User user) {
		user.setCode(repository.generateNextCode());
		return repository.save(user);
	}
	
	public List<User> list() {
		return repository.findAll();
	}
}