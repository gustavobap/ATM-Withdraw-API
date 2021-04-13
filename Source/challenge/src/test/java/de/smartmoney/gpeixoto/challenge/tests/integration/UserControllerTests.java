package de.smartmoney.gpeixoto.challenge.tests.integration;

import java.io.IOException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.databind.node.ObjectNode;

import de.smartmoney.gpeixoto.challenge.IntegrationTest;
import de.smartmoney.gpeixoto.challenge.TestHelper;
import de.smartmoney.gpeixoto.challenge.user.User;
import de.smartmoney.gpeixoto.challenge.user.UserRepository;

public class UserControllerTests extends IntegrationTest {

	@Autowired
	private UserRepository respository;

	public UserControllerTests() {

	}

	private MockHttpServletResponse find(Long code) throws IOException, Exception {
		return mvc.perform(MockMvcRequestBuilders.get("/api/users/" + code).accept(MediaType.APPLICATION_JSON))
				.andReturn().getResponse();
	}

	@Test
	public void canGetUserByCode() throws Exception {
		
		User expected = TestHelper.newUser("test");
		expected.setCode(555L);
		expected = respository.save(expected);
		
		MockHttpServletResponse response = find(555L);
		
		Assertions.assertEquals(HttpStatus.OK.value(), response.getStatus());
		Assertions.assertEquals(TestHelper.expectedJson(expected), response.getContentAsString());
	}

	private ObjectNode requestBody(Long code, String name, String email) {
		ObjectNode node = mapper.createObjectNode();
		if(code != null)
			node.put("code", code);
		if(email != null)
			node.put("email", email);
		if(name != null)
			node.put("name", name);
		return node;
	}
	
	private MockHttpServletResponse create(ObjectNode node) throws IOException, Exception {
		return mvc.perform(MockMvcRequestBuilders.post("/api/users").contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(node))).andReturn().getResponse();
	}

	@Test
	public void canCreateUser() throws Exception {
		MockHttpServletResponse response = create(requestBody(101L, "test", "test@test.com"));
		Assertions.assertEquals(HttpStatus.CREATED.value(), response.getStatus());
	}

	@Test
	public void validateAttributesPresence() throws Exception {
		MockHttpServletResponse response = create(requestBody(null, null, null));
		Assertions.assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
		
		String expected = TestHelper.expectedJson("name", "A name must be specified", "email", "An e-mail must be specified");
		
		Assertions.assertEquals(expected, response.getContentAsString());
	}

	@Test
	public void validateAttributesNotBlank() throws Exception {
		
		MockHttpServletResponse response = create(requestBody(101L, "", ""));
		Assertions.assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
		
		String expected = TestHelper.expectedJson("name", "A name must be specified", "email", "An e-mail must be specified");
		
		Assertions.assertEquals(expected, response.getContentAsString());
	}

	@Test
	public void validateAttributesEmail() throws Exception {
		MockHttpServletResponse response = create(requestBody(101L, "test", "nonvalidemail"));
		Assertions.assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
				
		Assertions.assertEquals(TestHelper.expectedJson("email", "must be a well-formed email address"), 
				response.getContentAsString());
	}

	@Test
	public void validateAttributesUnique() throws Exception {

		User user = TestHelper.newUser("test");
		user.setCode(101L);
		user = respository.save(user);

		MockHttpServletResponse response = create(requestBody(102L, "test", user.getEmail()));
		Assertions.assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
		
		Assertions.assertEquals(TestHelper.expectedJson("email", "e-mail is already registered"), 
				response.getContentAsString());
	}
}