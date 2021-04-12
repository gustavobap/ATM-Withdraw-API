package de.smartmoney.gpeixoto.challenge.tests.integration;

import java.io.IOException;

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

public class UserControllerTests extends IntegrationTest {

	@Autowired
	private UserRepository respository;

	private User validUser;

	public UserControllerTests() {

	}

	@BeforeEach
	public void setup() {
		validUser = new User();
		validUser.setName("Test");
		validUser.setEmail("test@test.com");
	}

	private MockHttpServletResponse find(User user) throws IOException, Exception {
		return mvc.perform(MockMvcRequestBuilders.get("/api/users/" + user.getId()).accept(MediaType.APPLICATION_JSON))
				.andReturn().getResponse();
	}

	@Test
	public void canGetUserById() throws Exception {
		User expected = respository.save(validUser);
		MockHttpServletResponse response = find(expected);
		
		Assertions.assertEquals(HttpStatus.OK.value(), response.getStatus());
		Assertions.assertEquals(TestHelper.expectedJson(expected), response.getContentAsString());
	}

	private MockHttpServletResponse create(User user) throws IOException, Exception {
		return mvc.perform(MockMvcRequestBuilders.post("/api/users").contentType(MediaType.APPLICATION_JSON)
				.content(TestHelper.expectedJson(user))).andReturn().getResponse();
	}

	@Test
	public void canCreateUser() throws Exception {
		MockHttpServletResponse response = create(validUser);
		Assertions.assertEquals(HttpStatus.CREATED.value(), response.getStatus());
	}

	@Test
	public void validateAttributesPresence() throws Exception {
		MockHttpServletResponse response = create(new User());
		Assertions.assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
		
		String expected = TestHelper.expectedJson("name", "A name must be specified", "email", "An e-mail must be specified");
		
		Assertions.assertEquals(expected, response.getContentAsString());
	}

	@Test
	public void validateAttributesNotBlank() throws Exception {

		User user = new User();
		user.setName("");
		user.setEmail("");

		MockHttpServletResponse response = create(user);
		Assertions.assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
		
		String expected = TestHelper.expectedJson("name", "A name must be specified", "email", "An e-mail must be specified");
		
		Assertions.assertEquals(expected, response.getContentAsString());
	}

	@Test
	public void validateAttributesEmail() throws Exception {

		validUser.setEmail("nonvalidemail");

		MockHttpServletResponse response = create(validUser);
		Assertions.assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
				
		Assertions.assertEquals(TestHelper.expectedJson("email", "must be a well-formed email address"), 
				response.getContentAsString());
	}

	@Test
	public void validateAttributesUnique() throws Exception {

		validUser = respository.save(validUser);

		User user = new User();
		user.setName(validUser.getName());
		user.setEmail(validUser.getEmail());

		MockHttpServletResponse response = create(user);
		Assertions.assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
		
		Assertions.assertEquals(TestHelper.expectedJson("email", "e-mail is already registered"), 
				response.getContentAsString());
	}
}