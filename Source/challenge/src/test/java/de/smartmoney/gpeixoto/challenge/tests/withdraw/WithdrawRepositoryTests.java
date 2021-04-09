package de.smartmoney.gpeixoto.challenge.tests.withdraw;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import de.smartmoney.gpeixoto.challenge.BaseTest;
import de.smartmoney.gpeixoto.challenge.TestHelper;
import de.smartmoney.gpeixoto.challenge.user.User;
import de.smartmoney.gpeixoto.challenge.user.UserRepository;
import de.smartmoney.gpeixoto.challenge.withdraw.Withdraw;
import de.smartmoney.gpeixoto.challenge.withdraw.WithdrawRepository;

public class WithdrawRepositoryTests extends BaseTest {

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

		repository.save(TestHelper.newWithdraw(userA));
		repository.save(TestHelper.newWithdraw(userA));
		repository.save(TestHelper.newWithdraw(userB));

		Assertions.assertEquals(2L, repository.countByUser(userA));
		Assertions.assertEquals(1L, repository.countByUser(userB));
	}

	@Test
	public void canCreateTimestamps() throws Exception {
		User user = TestHelper.newUser("a");
		userRespository.save(user);

		Withdraw withdraw = repository.save(TestHelper.newWithdraw(user));
		withdraw = repository.findById(withdraw.getId()).get();

		Assertions.assertNotNull(withdraw.getCreatedDate());
	}

}
