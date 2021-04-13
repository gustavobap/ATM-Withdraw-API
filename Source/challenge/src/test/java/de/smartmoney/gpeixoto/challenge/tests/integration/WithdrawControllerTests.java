package de.smartmoney.gpeixoto.challenge.tests.integration;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.Instant;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.databind.node.ObjectNode;

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
	
	@Autowired
	private JacksonTester<Withdraw> jackson;

	private User validUser;

	public WithdrawControllerTests() {

	}

	@BeforeEach
	public void setup() {
		validUser = new User();
		validUser.setName("Test");
		validUser.setEmail("test@test.com");
		validUser.setCode(userRespository.generateNextCode());

		validUser = userRespository.save(validUser);
	}

	private MockHttpServletResponse find(Long code) throws IOException, Exception {
		return mvc.perform(
				MockMvcRequestBuilders.get("/api/withdrawals/" + code).accept(MediaType.APPLICATION_JSON))
				.andReturn().getResponse();
	}
	
	@Test
	public void canGetWithdrawByCode() throws Exception {

		Withdraw withdraw = TestHelper.newWithdraw(validUser, "50", "1.5", 101L);
		withdraw.setCreatedDate(Instant.now());
		withdraw = repository.save(withdraw);
				
		MockHttpServletResponse response = find(withdraw.getCode());

		Assertions.assertEquals(HttpStatus.OK.value(), response.getStatus());
		
		String actual = response.getContentAsString();
		Instant createdDate = jackson.parseObject(actual).getCreatedDate();
		
		TestHelper.assertCloseToNow(createdDate);
		
		//Skip comparison of CreatedDate
		withdraw.setCreatedDate(createdDate);
		
		Assertions.assertEquals(TestHelper.expectedJson(withdraw), actual);
	}
	
	private ObjectNode requestBody(String value, String userEmail) {
		return requestBody(value, userEmail, null);
	}
	
	private ObjectNode requestBody(String value, Long userCode) {
		return requestBody(value, null, userCode);
	}
	
	private ObjectNode requestBody(String value, String userEmail, Long userCode) {
		ObjectNode node = mapper.createObjectNode();

		if(value != null)
			node.put("value", value);

	    ObjectNode user = mapper.createObjectNode();
	    
	    if(userCode != null)
	    	user.put("code", userCode);
	    if(userEmail != null)
	    	user.put("email", userEmail);
				    
	    node.set("user", user);
	    
	    return node;
	}
	
	private MockHttpServletResponse create(ObjectNode node) throws Exception {		
		
		return mvc.perform(
				MockMvcRequestBuilders.post("/api/withdrawals")
					.contentType(MediaType.APPLICATION_JSON)
					.content(mapper.writeValueAsString(node))
				)
				.andReturn().getResponse();
	}
	
	@Test
	public void canCreateWithdrawWithUserCode() throws Exception {
		MockHttpServletResponse response = create(requestBody("50", validUser.getCode()));
		Assertions.assertEquals(HttpStatus.CREATED.value(), response.getStatus());
	}
	
	@Test
	public void canCreateWithdrawWithUserEmail() throws Exception {
		MockHttpServletResponse response = create(requestBody("50", validUser.getEmail()));
		Assertions.assertEquals(HttpStatus.CREATED.value(), response.getStatus());
	}
	
	@Test()
	public void setFeeAmount() throws Exception {
		MockHttpServletResponse response = create(requestBody("50.00", validUser.getCode()));
		Assertions.assertEquals(HttpStatus.CREATED.value(), response.getStatus());		

		Assertions.assertEquals(1, repository.count());		
		Withdraw withdraw = repository.findAll().get(0);
		Assertions.assertEquals(new BigDecimal("1.50000"), withdraw.getFee());
	}
	
	@Test()
	public void setFeeAmountWithBigPrecision() throws Exception {
		MockHttpServletResponse response = create(requestBody("43.99", validUser.getCode()));
		Assertions.assertEquals(HttpStatus.CREATED.value(), response.getStatus());		

		Assertions.assertEquals(1, repository.count());
		Withdraw withdraw = repository.findAll().get(0);
		Assertions.assertEquals(new BigDecimal("1.31970"), withdraw.getFee());
	}

	@Test
	public void validateAttributesPresence() throws Exception {
		MockHttpServletResponse response = create(requestBody(null, null, null));
		Assertions.assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
		
		Assertions.assertEquals(TestHelper.expectedJson("value", "A value must be specified"), 
				response.getContentAsString());
	}

	@Test
	public void validateMinimumValue() throws Exception {
		MockHttpServletResponse response = create(requestBody("0.99", validUser.getCode()));

		Assertions.assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
		
		Assertions.assertEquals(TestHelper.expectedJson("value", "must be greater than or equal to $1.00"), 
				response.getContentAsString());
	}
	
	@Test
	public void validateValuePrecision() throws Exception {
		MockHttpServletResponse response = create(requestBody("40.999", validUser.getCode()));

		Assertions.assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
		
		Assertions.assertEquals(TestHelper.expectedJson("value", "scale must be of 2 decimal places"), 
				response.getContentAsString());
	}

	@Test
	public void validateMaximumValue() throws Exception {

		MockHttpServletResponse response = create(requestBody("50.01", validUser.getCode()));
		Assertions.assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());

		Assertions.assertEquals(TestHelper.expectedJson("value", "Your first withdraw is limited to $50.00"), 
				response.getContentAsString());
		
		response = create(requestBody("50.00", validUser.getCode()));
		Assertions.assertEquals(HttpStatus.CREATED.value(), response.getStatus());

		response = create(requestBody("300.01", validUser.getCode()));
		Assertions.assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
		Assertions.assertEquals(TestHelper.expectedJson("value", "Your withdrawals are limited to $300.00"),
				response.getContentAsString());

		response = create(requestBody("300.00", validUser.getCode()));
		Assertions.assertEquals(HttpStatus.CREATED.value(), response.getStatus());

		Assertions.assertEquals(2, repository.count());
	}

}