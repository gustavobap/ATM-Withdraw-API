package de.smartmoney.gpeixoto.challenge.shared;

public class BusinessException extends RuntimeException {

	private static final long serialVersionUID = -351281924125770385L;
	
	private String attributeName;
	
	public BusinessException(String attributeName, String message) {
		super(message);
		this.attributeName = attributeName;
	}
	
	public String getAttributeName() {
		return attributeName;
	}

}
