package de.smartmoney.gpeixoto.challenge.tests.integration;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import de.smartmoney.gpeixoto.challenge.IntegrationTest;
import de.smartmoney.gpeixoto.challenge.user.UserRepository;
import de.smartmoney.gpeixoto.challenge.withdraw.WithdrawRepository;

public class WithdrawRepositoryTests extends IntegrationTest {

	@Autowired
	private WithdrawRepository repository;

	@Autowired
	private UserRepository userRespository;

	@Test
	public void generateNextCode() throws Exception {
		Assertions.assertEquals(1L, repository.generateNextCode());
		Assertions.assertEquals(2L, repository.generateNextCode());
		Assertions.assertEquals(3L, repository.generateNextCode());
	}	

}
