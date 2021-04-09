package de.smartmoney.gpeixoto.challenge.shared;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;

public class ApplicationController {

	@ExceptionHandler({ MethodArgumentNotValidException.class })
	public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
		List<ObjectError> errors = ex.getBindingResult().getAllErrors();
		Map<String, String> map = new HashMap<>(errors.size());
		errors.forEach((error) -> {
			String key = ((FieldError) error).getField();
			String val = error.getDefaultMessage();
			map.put(key, val);
		});
		return ResponseEntity.badRequest().body(map);
	}

	@ExceptionHandler({ BusinessException.class })
	public ResponseEntity<Map<String, String>> handleBusinessExceptions(BusinessException ex) {
		Map<String, String> map = new HashMap<>(1);
		map.put(ex.getAttributeName(), ex.getMessage());
		return ResponseEntity.badRequest().body(map);
	}
}
