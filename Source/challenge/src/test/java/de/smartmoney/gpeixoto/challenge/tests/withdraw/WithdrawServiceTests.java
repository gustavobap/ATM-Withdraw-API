package de.smartmoney.gpeixoto.challenge.tests.withdraw;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import com.fasterxml.jackson.core.JsonProcessingException;

import de.smartmoney.gpeixoto.challenge.BaseTest;
import de.smartmoney.gpeixoto.challenge.TestHelper;
import de.smartmoney.gpeixoto.challenge.shared.BusinessException;
import de.smartmoney.gpeixoto.challenge.user.User;
import de.smartmoney.gpeixoto.challenge.user.UserRepository;
import de.smartmoney.gpeixoto.challenge.withdraw.Withdraw;
import de.smartmoney.gpeixoto.challenge.withdraw.WithdrawRepository;
import de.smartmoney.gpeixoto.challenge.withdraw.WithdrawService;

public class WithdrawServiceTests extends BaseTest {

	@Mock
	private UserRepository userRespository;

	@Mock
	private WithdrawRepository withdrawRepository;
	
	private WithdrawService service;
	
	@BeforeEach
	public void init() {
		service = new WithdrawService(withdrawRepository, userRespository);
	}

	@Test
	public void listWithdraws() throws JsonProcessingException {

		User userA = TestHelper.newUser("a");
		User userB = TestHelper.newUser("b");

		Withdraw a = TestHelper.newWithdraw(userA);
		Withdraw b = TestHelper.newWithdraw(userB);
		List<Withdraw> mockResult = new ArrayList<Withdraw>();
		mockResult.add(a);
		mockResult.add(b);
		
		Mockito.when(withdrawRepository.findAll()).thenReturn(mockResult);
		
		List<Withdraw> list = service.list();
		
		Assertions.assertEquals(2, list.size());
		Assertions.assertEquals(a, list.get(0));
		Assertions.assertEquals(b, list.get(1));
	}
	
	@Test()
	public void validateDailyLimit() {
		User user = TestHelper.newUser("a");
		Withdraw last = null;
		userRespository.save(user);

		for (int i = 0; i < 5; i++) {
			last = service.save(TestHelper.newWithdraw(user));
		}

		Assertions.assertEquals(5, withdrawRepository.countByUser(user));

		BusinessException exception = Assertions.assertThrows(BusinessException.class, () -> {
			service.save(TestHelper.newWithdraw(user));
		});

		Assertions.assertEquals("You have reached the daily withdrawals limit", exception.getMessage());

		last.setCreatedDate(Instant.now().minus(24, ChronoUnit.HOURS));

		withdrawRepository.save(last);

		service.save(TestHelper.newWithdraw(user));

		Assertions.assertEquals(6, withdrawRepository.countByUser(user));
	}
	
	@Test
	public void validateUserEmailExists() {
		Withdraw withdraw = TestHelper.newWithdraw(TestHelper.newUser("non.existant"));
		
		BusinessException exception = Assertions.assertThrows(BusinessException.class, () -> {
			service.save(withdraw);
		});
		
		Assertions.assertEquals("User not found", exception.getMessage());
	}

	private BigDecimal big(String value) {
		return new BigDecimal(value);
	}

	@Test()
	public void setFeeAmount() {

		User user = TestHelper.newUser("test");
		userRespository.save(user);

		Withdraw withdraw = TestHelper.newWithdraw(user, "50.00");
		withdraw = service.save(withdraw);
		Assertions.assertEquals(big("1.50"), withdraw.getFee());

		withdraw = TestHelper.newWithdraw(user, "100.99");
		withdraw = service.save(withdraw);
		Assertions.assertEquals(big("3.0297"), withdraw.getFee());

		withdraw = TestHelper.newWithdraw(user, "101.00");
		withdraw = service.save(withdraw);
		Assertions.assertEquals(big("2.02"), withdraw.getFee());

		withdraw = TestHelper.newWithdraw(user, "250.99");
		withdraw = service.save(withdraw);
		Assertions.assertEquals(big("5.0198"), withdraw.getFee());

		withdraw.setCreatedDate(Instant.now().minus(24, ChronoUnit.HOURS));
		service.save(withdraw);

		withdraw = TestHelper.newWithdraw(user, "251.00");
		withdraw = service.save(withdraw);
		Assertions.assertEquals(big("2.51"), withdraw.getFee());

		withdraw = TestHelper.newWithdraw(user, "300.00");
		withdraw = service.save(withdraw);
		Assertions.assertEquals(big("3.00"), withdraw.getFee());

	}

}
