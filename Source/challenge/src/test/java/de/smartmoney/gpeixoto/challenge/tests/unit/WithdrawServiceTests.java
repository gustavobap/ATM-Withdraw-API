package de.smartmoney.gpeixoto.challenge.tests.unit;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mock;
import org.mockito.Mockito;

import com.fasterxml.jackson.core.JsonProcessingException;

import de.smartmoney.gpeixoto.challenge.TestHelper;
import de.smartmoney.gpeixoto.challenge.UnitTest;
import de.smartmoney.gpeixoto.challenge.shared.BusinessException;
import de.smartmoney.gpeixoto.challenge.user.User;
import de.smartmoney.gpeixoto.challenge.user.UserRepository;
import de.smartmoney.gpeixoto.challenge.withdraw.Withdraw;
import de.smartmoney.gpeixoto.challenge.withdraw.WithdrawRepository;
import de.smartmoney.gpeixoto.challenge.withdraw.WithdrawService;

public class WithdrawServiceTests extends UnitTest {

	@Mock
	private UserRepository userRespository;

	@Mock
	private WithdrawRepository withdrawRepository;
	
	private WithdrawService service;
	
	private User validUser;
	private User nonExistantUser;
	
	@BeforeEach
	public void init() {
		service = new WithdrawService(withdrawRepository, userRespository);
		
		validUser = TestHelper.newUser("test");
		validUser.setId(123L);
		mockUserFinders(validUser, Optional.of(validUser));
		
		nonExistantUser = TestHelper.newUser("non.existant");
		nonExistantUser.setId(9999L);
		mockUserFinders(nonExistantUser, Optional.empty());
	}
	
	private void mockUserFinders(User user, Optional<User> mockResult) {
		Mockito.when(userRespository.findByEmail(user.getEmail())).thenReturn(mockResult);
		Mockito.when(userRespository.findById(user.getId())).thenReturn(mockResult);	
	}
	
	private void assertBusinessException(String message, Executable executable) {
		BusinessException exception = Assertions.assertThrows(BusinessException.class, () -> {
			executable.execute();
		});
		Assertions.assertEquals(message, exception.getMessage());
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
		
		Mockito.when(withdrawRepository.countByUserAndCreatedDateAfter(Mockito.any(User.class), Mockito.any(Instant.class)))
			.thenReturn(4L);

		service.save(TestHelper.newWithdraw(validUser));
		
		
		Mockito.when(withdrawRepository.countByUserAndCreatedDateAfter(Mockito.any(User.class), Mockito.any(Instant.class)))
			.thenReturn(5L);
		
		assertBusinessException("You have reached the daily withdrawals limit", () -> {
			service.save(TestHelper.newWithdraw(validUser));
		});
	}
	
	@Test
	public void validateUserEmailExists() {
		assertBusinessException("User not found", () -> {
			service.save(TestHelper.newWithdraw(nonExistantUser));
		});
	}
	
	private void checkFeeValue(String withdrawValue, String expectedFeeValue, Integer count) {
		Withdraw withdraw = TestHelper.newWithdraw(validUser, withdrawValue);
		Mockito.when(withdrawRepository.countByUser(validUser))
			.thenReturn(count.longValue());
		Mockito.when(withdrawRepository.countByUserAndCreatedDateAfter(Mockito.any(User.class), Mockito.any(Instant.class)))
			.thenReturn(count.longValue());
		Assertions.assertEquals(new BigDecimal(expectedFeeValue), service.save(withdraw).getFee());
	}

	@Test()
	public void setFeeAmount() {
		
		Mockito.when(withdrawRepository.save(Mockito.any(Withdraw.class))).thenAnswer(i -> i.getArguments()[0]);

		checkFeeValue("43.99", "1.3197", 1);
		checkFeeValue("50.00", "1.50", 1);
		checkFeeValue("100.99", "3.0297", 2);
		checkFeeValue("101.00", "2.02", 3);
		checkFeeValue("250.99", "5.0198", 4);
		checkFeeValue("251.00", "2.51", 4);
		checkFeeValue("300.00", "3.00", 4);
	}
	
   @Test
   public void registerCreatedDate() throws Exception {
       Withdraw withdraw = TestHelper.newWithdraw(validUser, "50.00");
       service.save(withdraw);
       TestHelper.assertCloseToNow(withdraw.getCreatedDate());
   }
	
	@Test
	public void validateMaximumFirstValue(){
	
		Mockito.when(withdrawRepository.countByUser(validUser)).thenReturn(0L);
		assertBusinessException("Your first withdraw is limited to $50.00", () -> {
			service.save(TestHelper.newWithdraw(validUser, "50.01"));
		});

		service.save(TestHelper.newWithdraw(validUser, "50.00"));
	}

}
