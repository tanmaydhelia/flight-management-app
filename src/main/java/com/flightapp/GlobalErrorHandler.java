package com.flightapp;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalErrorHandler {
	@ExceptionHandler
	public Map<String,String> handlerException(MethodArgumentNotValidException e) {
		Map<String, String> errorMap = new HashMap<>();
		
		List<ObjectError> errorList = e.getBindingResult().getAllErrors();
		
		errorList.forEach((error)->{
			String fieldName = ((FieldError)error).getField();
			String message = error.getDefaultMessage();
			errorMap.put(fieldName, message);
		});
		
		return errorMap;
	}
}
