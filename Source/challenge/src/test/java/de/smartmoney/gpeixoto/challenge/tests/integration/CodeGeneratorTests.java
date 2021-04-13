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
		Long old = 101L;
		Long cur = userRepository.generateNextCode();
		Assertions.assertTrue(cur.equals(old));
		old = cur;
		cur = withdrawRepository.generateNextCode();
		Assertions.assertTrue(cur > old);
		old = cur;
		cur = userRepository.generateNextCode();
		Assertions.assertTrue(cur > old);
		old = cur;
		cur = withdrawRepository.generateNextCode();
		Assertions.assertTrue(cur > old);
	}	

}
