package de.smartmoney.gpeixoto.challenge.user;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import de.smartmoney.gpeixoto.challenge.shared.ApplicationController;

@RestController
@RequestMapping("api/users")
public class UserController extends ApplicationController {

	private final UserRepository repository;

	public UserController(UserRepository repository) {
		this.repository = repository;
	}
	
    @GetMapping("{id}")
    public ResponseEntity<User> find(@PathVariable("id") Long id) {
        Optional<User> user= repository.findById(id);
        return ResponseEntity.of(user);
    }
    
    @PostMapping
    public ResponseEntity<User> create(@Valid @RequestBody User user) {
    	user = repository.save(user);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(user.getId())
                .toUri();
        return ResponseEntity.created(location).body(user);
    }
    
	@GetMapping()
	public ResponseEntity<List<User>> list() {
		return ResponseEntity.ok().body(repository.findAll());
	}

    @ExceptionHandler({DataIntegrityViolationException.class})
    public ResponseEntity<Map<String, String>> handleConstraintExceptions(DataIntegrityViolationException ex) {
        Map<String, String> map = new HashMap<>(1);
        if(ex.getMostSpecificCause().getMessage().contains(User.UNIQUE_EMAIL_CONSTRAINT_NAME)) {
        	map.put("email", "e-mail is already registered");
        }
        return ResponseEntity.badRequest().body(map);
    }
}


