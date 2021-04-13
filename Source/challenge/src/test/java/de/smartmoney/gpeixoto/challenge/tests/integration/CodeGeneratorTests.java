package de.smartmoney.gpeixoto.challenge.tests.integration;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import de.smartmoney.gpeixoto.challenge.IntegrationTest;
import de.smartmoney.gpeixoto.challenge.user.UserRepository;
import de.smartmoney.gpeixoto.challenge.withdraw.WithdrawRepository;

public class CodeGeneratorTests extends IntegrationTest {

	@Autowired
	private WithdrawRepository withdrawRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	@Test
	public void generateNextCode() throws Exception {
		Assertions.assertEquals(1L, userRepository.generateNextCode());
		Assertions.assertEquals(2L, withdrawRepository.generateNextCode());
		Assertions.assertEquals(3L, userRepository.generateNextCode());
		Assertions.assertEquals(4L, withdrawRepository.generateNextCode());
	}	

}
