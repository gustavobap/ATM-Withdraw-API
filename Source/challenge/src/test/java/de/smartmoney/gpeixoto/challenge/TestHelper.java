package de.smartmoney.gpeixoto.challenge;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.InvalidParameterException;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.junit.jupiter.api.Assertions;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.util.RawValue;

import de.smartmoney.gpeixoto.challenge.user.User;
import de.smartmoney.gpeixoto.challenge.withdraw.Withdraw;

public class TestHelper {

	private static final ObjectMapper mapper = new ObjectMapper();
	
	public static Withdraw newWithdraw(User user) {
		return newWithdraw(user, "50.00");
	}

	public static Withdraw newWithdraw(User user, String value) {
		Withdraw withdraw = new Withdraw();
		withdraw.setUser(user);
		withdraw.setValue(new BigDecimal(value));
		return withdraw;
	}
	
	public static Withdraw newWithdraw(User user, String value, String fee, Long code) {
		Withdraw withdraw = new Withdraw();
		withdraw.setUser(user);
		withdraw.setValue(new BigDecimal(value));
		withdraw.setFee(new BigDecimal(fee));
		withdraw.setCode(code);
		return withdraw;
	}

	public static User newUser(String name) {
		User user = new User();
		user.setName(name);
		user.setEmail(name + "@email.com");
		return user;
	}
		
	public static String expectedJson(Withdraw withdraw) throws JsonProcessingException {
	    
		ObjectNode node = mapper.createObjectNode();

		if(withdraw.getCode() != null) {
			node.put("code", withdraw.getCode());
		}
		
		if(withdraw.getCreatedDate() != null) {
			//TODO specify date format explicitly
			node.put("createdDate", withdraw.getCreatedDate().toString());
		}
		if(withdraw.getValue() != null) {
			node.putRawValue("value", new RawValue(expectedWithdrawValue(withdraw)));
		}
		if(withdraw.getFee() != null) {
			node.putRawValue("fee", new RawValue(expectedFeeValue(withdraw)));
		}
	   
	    if(withdraw.getUser() != null) {
		    ObjectNode user = mapper.createObjectNode();
	
			if(withdraw.getUser().getCode() != null) {
				user.put("code", withdraw.getUser().getCode());
			}
					    
		    node.set("user", user);
	    }
	    
	    return write(node);
	}
	
	private static String expectedWithdrawValue(Withdraw withdraw) {
		return withdraw.getValue().setScale(2, RoundingMode.HALF_EVEN).toString();
	}

	private static String expectedFeeValue(Withdraw withdraw) {
		return withdraw.getFee().setScale(5, RoundingMode.HALF_EVEN).toString();
	}

	public static String expectedJson(User user) throws JsonProcessingException {
	    ObjectNode node = mapper.createObjectNode();

		if(user.getCode() != null) {
			node.put("code", user.getCode());
		}
		
	    node.put("email", user.getEmail());
	    node.put("name", user.getName());

	    return write(node);
	}
	
	public static String expectedJson(String ... nameValuePairs) throws JsonProcessingException {
	    ObjectNode node = mapper.createObjectNode();
	    
	    if(nameValuePairs.length % 2 != 0) {
	    	throw new InvalidParameterException();
	    }
	    
	    for(int i = 0; i < nameValuePairs.length; i+=2) {
	    	node.put(nameValuePairs[i], nameValuePairs[i+1]);
	    }
	    
	    return write(node);
	}
	
	private static String write(ObjectNode node) throws JsonProcessingException {
		return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(node);
	}

	public static void assertCloseToNow(Instant createdDate) {
		Assertions.assertNotNull(createdDate);
		Long seconds = Duration.between(createdDate, Instant.now()).get(ChronoUnit.SECONDS);
		Assertions.assertTrue(seconds >= 0 && seconds <= 1);
	}

}
