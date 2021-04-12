package de.smartmoney.gpeixoto.challenge;

import java.math.BigDecimal;
import java.math.RoundingMode;

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
	
	public static Withdraw newWithdraw(User user, String value, String fee) {
		Withdraw withdraw = new Withdraw();
		withdraw.setUser(user);
		withdraw.setValue(new BigDecimal(value));
		withdraw.setFee(new BigDecimal(fee));
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
			node.putRawValue("value", new RawValue(expectedDecimal(withdraw.getValue())));
		}
		if(withdraw.getFee() != null) {
			node.putRawValue("fee", new RawValue(expectedDecimal(withdraw.getFee())));
		}
	   
	    if(withdraw.getUser() != null) {
		    ObjectNode user = mapper.createObjectNode();
	
		    user.put("email", withdraw.getUser().getEmail());
		    user.put("name", withdraw.getUser().getName());
		    
		    node.set("user", user);
	    }
	    
	    return mapper.writeValueAsString(node);
	}
	
	private static String expectedDecimal(BigDecimal value) {
		return value.setScale(2, RoundingMode.DOWN).toString();
	}

	public static String expectedJson(User user) throws JsonProcessingException {
	    ObjectNode node = mapper.createObjectNode();

	    node.put("email", user.getEmail());
	    node.put("name", user.getName());

	    return mapper.writeValueAsString(node);
	}

}
