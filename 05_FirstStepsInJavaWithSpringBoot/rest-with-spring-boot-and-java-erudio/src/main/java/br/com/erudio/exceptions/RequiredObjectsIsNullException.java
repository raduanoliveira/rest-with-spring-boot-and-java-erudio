package br.com.erudio.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class RequiredObjectsIsNullException extends RuntimeException{
	
	private static final long serialVersionUID = 1L;
	
	public RequiredObjectsIsNullException() {
		super("It is not allowed to persist a null object");
	}
	
	public RequiredObjectsIsNullException(String ex) {
		super(ex);
	}
}