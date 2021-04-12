package de.smartmoney.gpeixoto.challenge.tests.integration;

import java.io.IOException;
import java.math.BigDecimal;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import de.smartmoney.gpeixoto.challenge.IntegrationTest;
import de.smartmoney.gpeixoto.challenge.TestHelper;
import de.smartmoney.gpeixoto.challenge.user.User;
import de.smartmoney.gpeixoto.challenge.user.UserRepository;
import de.smartmoney.gpeixoto.challenge.withdraw.Withdraw;
import de.smartmoney.gpeixoto.challenge.withdraw.WithdrawRepository;

public class WithdrawControllerTests extends IntegrationTest {

	@Autowired
	private WithdrawRepository repository;

	@Autowired
	private UserRepository userRespository;

	private User validUser;

	private Withdraw validWithdraw;

	public WithdrawControllerTests() {

	}

	@BeforeEach
	public void setup() {
		validUser = new User();
		validUser.setName("Test");
		validUser.setEmail("test@test.com");

		validUser = userRespository.save(validUser);

		validWithdraw = new Withdraw();
		validWithdraw.setUser(validUser);
		validWithdraw.setValue(new BigDecimal("50.00"));
		validWithdraw.setFee(new BigDecimal("1.50"));
	}

	private MockHttpServletResponse find(Withdraw withdraw) throws IOException, Exception {
		return mvc.perform(
				MockMvcRequestBuilders.get("/api/withdrawals/" + withdraw.getId()).accept(MediaType.APPLICATION_JSON))
				.andReturn().getResponse();
	}

	@Test
	public void canGetWithdrawById() throws Exception {

		Withdraw expected = repository.save(validWithdraw);

		MockHttpServletResponse response = find(expected);

		Assertions.assertEquals(HttpStatus.OK.value(), response.getStatus());
		
		Assertions.assertEquals(TestHelper.expectedJson(expected), response.getContentAsString());
	}



	private MockHttpServletResponse create(Withdraw withdraw) throws IOException, Exception {
		return mvc.perform(
				MockMvcRequestBuilders.post("/api/withdrawals")
					.contentType(MediaType.APPLICATION_JSON)
					.content(TestHelper.expectedJson(withdraw))
				)
				.andReturn().getResponse();
	}

	@Test
	public void canCreateWithdraw() throws Exception {
		MockHttpServletResponse response = create(validWithdraw);
		Assertions.assertEquals(HttpStatus.CREATED.value(), response.getStatus());
	}

	@Test
	public void validateAttributesPresence() throws Exception {
		MockHttpServletResponse response = create(new Withdraw());
		Assertions.assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
		Assertions.assertEquals("{\"value\":\"A value must be specified\"}", response.getContentAsString());
	}

	@Test
	public void validateMinimumValue() throws Exception {
		User user = userRespository.save(validUser);

		Withdraw withdraw = TestHelper.newWithdraw(user, "0.99");

		MockHttpServletResponse response = create(withdraw);

		Assertions.assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
		Assertions.assertEquals("{\"value\":\"must be greater than or equal to $1.00\"}",
				response.getContentAsString());
	}

	@Test
	public void validateMaximumValue() throws Exception {

		validWithdraw.setValue(new BigDecimal("50.01"));

		MockHttpServletResponse response = create(validWithdraw);
		Assertions.assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
		Assertions.assertEquals("{\"value\":\"Your first withdraw is limited to $50.00\"}",
				response.getContentAsString());

		validWithdraw.setValue(new BigDecimal("50.00"));

		response = create(validWithdraw);
		Assertions.assertEquals(HttpStatus.CREATED.value(), response.getStatus());

		validWithdraw.setValue(new BigDecimal("300.01"));

		response = create(validWithdraw);
		Assertions.assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
		Assertions.assertEquals("{\"value\":\"Your withdrawals are limited to $300.00\"}",
				response.getContentAsString());

		validWithdraw.setValue(new BigDecimal("300.00"));

		response = create(validWithdraw);
		Assertions.assertEquals(HttpStatus.CREATED.value(), response.getStatus());

		Assertions.assertEquals(2, repository.count());
	}

}