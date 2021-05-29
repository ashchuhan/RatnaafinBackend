package com.ratnaafin.crm.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class RollbackTransactionException extends Exception{

	private static final long serialVersionUID = 1L;
	
	public RollbackTransactionException(String message) {
		super("Rollback Transaction "+message);
	}
	
}
