package de.smartmoney.gpeixoto.challenge.withdraw;

import java.net.URI;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import de.smartmoney.gpeixoto.challenge.shared.ApplicationController;

@RestController
@RequestMapping("api/withdrawals")
public class WithdrawController extends ApplicationController {
    
	private final WithdrawService service;

	public WithdrawController(WithdrawService service) {
		this.service = service;
	}
	
    @GetMapping("{id}")
    public ResponseEntity<Withdraw> find(@PathVariable("id") Long id) {
        Optional<Withdraw> withdraw = service.findById(id);
        return ResponseEntity.of(withdraw);
    }
    
    @PostMapping
    public ResponseEntity<Withdraw> create(@Valid @RequestBody Withdraw withdraw) {
    	
    	withdraw = service.save(withdraw);
    	
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(withdraw.getId())
                .toUri();
        
        return ResponseEntity.created(location).body(withdraw);
	}

}