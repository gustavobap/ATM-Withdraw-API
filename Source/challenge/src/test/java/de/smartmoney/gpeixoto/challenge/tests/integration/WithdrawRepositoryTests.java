package de.smartmoney.gpeixoto.challenge.tests.integration;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import de.smartmoney.gpeixoto.challenge.IntegrationTest;
import de.smartmoney.gpeixoto.challenge.TestHelper;
import de.smartmoney.gpeixoto.challenge.user.User;
import de.smartmoney.gpeixoto.challenge.user.UserRepository;
import de.smartmoney.gpeixoto.challenge.withdraw.Withdraw;
import de.smartmoney.gpeixoto.challenge.withdraw.WithdrawRepository;

public class WithdrawRepositoryTests extends IntegrationTest {

	@Autowired
	private WithdrawRepository repository;

	@Autowired
	private UserRepository userRespository;

	@Test
	public void canCountByUser() throws Exception {

		User userA = TestHelper.newUser("a");
		userRespository.save(userA);

		User userB = TestHelper.newUser("b");
		userRespository.save(userB);

		repository.save(TestHelper.newWithdraw(userA, "50.00", "1.50"));
		repository.save(TestHelper.newWithdraw(userA, "50.00", "1.50"));
		repository.save(TestHelper.newWithdraw(userB, "50.00", "1.50"));

		Assertions.assertEquals(2L, repository.countByUser(userA));
		Assertions.assertEquals(1L, repository.countByUser(userB));
	}

	@Test
	public void registerCreatedDate() throws Exception {
		User user = TestHelper.newUser("a");
		userRespository.save(user);

		Withdraw withdraw = repository.save(TestHelper.newWithdraw(user, "50.00", "1.50"));
		withdraw = repository.findById(withdraw.getId()).get();

		Assertions.assertNotNull(withdraw.getCreatedDate());
	}
	
	@Test
	public void registerCreatedCode() throws Exception {
		User user = TestHelper.newUser("a");
		userRespository.save(user);

		Withdraw withdraw = repository.save(TestHelper.newWithdraw(user, "50.00", "1.50"));
		withdraw = repository.findById(withdraw.getId()).get();

		Assertions.assertNotNull(withdraw.getCode());
	}	

}
