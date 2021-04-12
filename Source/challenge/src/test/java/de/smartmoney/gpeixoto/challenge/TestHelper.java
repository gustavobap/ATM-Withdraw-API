package de.smartmoney.gpeixoto.challenge;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.InvalidParameterException;

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
		return expectedJson(withdraw, true);
	}
	
	public static String expectedJson(Withdraw withdraw, Boolean roundDecimals) throws JsonProcessingException {
	    
		ObjectNode node = mapper.createObjectNode();

		if(withdraw.getCode() != null) {
			node.put("code", withdraw.getCode());
		}
		
		if(withdraw.getCreatedDate() != null) {
			//TODO specify date format explicitly
			node.put("createdDate", withdraw.getCreatedDate().toString());
		}
		if(withdraw.getValue() != null) {
			node.putRawValue("value", new RawValue(expectedWithdrawValue(withdraw, roundDecimals)));
		}
		if(withdraw.getFee() != null) {
			node.putRawValue("fee", new RawValue(expectedFeeValue(withdraw, roundDecimals)));
		}
	   
	    if(withdraw.getUser() != null) {
		    ObjectNode user = mapper.createObjectNode();
	
		    user.put("email", withdraw.getUser().getEmail());
		    user.put("name", withdraw.getUser().getName());
		    
		    node.set("user", user);
	    }
	    
	    return write(node);
	}
	
	private static String expectedWithdrawValue(Withdraw withdraw, Boolean roundDecimals) {
		BigDecimal value = withdraw.getValue();
		if(roundDecimals)
			value = value.setScale(2, RoundingMode.HALF_EVEN);
		return value.toString();
	}

	private static String expectedFeeValue(Withdraw withdraw, Boolean roundDecimals) {
		BigDecimal value = withdraw.getFee();
		if(roundDecimals)
			value = value.setScale(5, RoundingMode.HALF_EVEN);
		return value.toString();
	}

	public static String expectedJson(User user) throws JsonProcessingException {
	    ObjectNode node = mapper.createObjectNode();

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

}
