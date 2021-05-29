package com.ratnaafin.crm.common.exception;

import java.util.Date;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
@RestController
public class CustomizedResponseEntityExceptionHandler extends ResponseEntityExceptionHandler{
//	@ExceptionHandler(Exception.class)
//	public final ResponseEntity<Object> handleAllExceptions(Exception ex, WebRequest request) {
//		ErrorDetails errorDetails = new ErrorDetails(new Date(), ex.getMessage(),request.getDescription(false));
//		return new ResponseEntity<Object>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
//	}
//
//	@ExceptionHandler(UserNotFoundException.class)
//	public final ResponseEntity<Object> handleUserNotFoundException(UserNotFoundException ex, WebRequest request) {
//		ErrorDetails errorDetails = new ErrorDetails(new Date(), ex.getMessage(), request.getDescription(false));
//		return new ResponseEntity<Object>(errorDetails, HttpStatus.NOT_FOUND);
//	}
  
	@ExceptionHandler(UserAlreadyExistException.class)
	public final ResponseEntity<Object> handleUserAlreadyExistException(UserAlreadyExistException ex, WebRequest request) {
		ErrorDetails errorDetails = new ErrorDetails(ex.getStatus(),ex.getError_cd(),ex.getError_title(),ex.getError_msg(),ex.getError_detail());
		return new ResponseEntity<Object>(errorDetails, HttpStatus.NOT_FOUND);
	}
	
	@ExceptionHandler(TokenNotValidException.class)
    public final ResponseEntity<Object> handleTokenNotValidException(TokenNotValidException ex, WebRequest request) {
        ErrorDetails errorDetails = new ErrorDetails(ex.getStatus(),ex.getError_cd(),ex.getError_title(),ex.getError_msg(),ex.getError_detail());
        return new ResponseEntity<Object>(errorDetails, HttpStatus.NOT_FOUND);
    }
	
	@ExceptionHandler(UserNotAuthorizedException.class)
    public final ResponseEntity<Object> handleUserNotAuthorizedException(UserNotAuthorizedException ex, WebRequest request) {
        ErrorDetails errorDetails = new ErrorDetails(ex.getStatus(),ex.getError_cd(),ex.getError_title(),ex.getError_msg(),ex.getError_detail());
        return new ResponseEntity<Object>(errorDetails, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public final ResponseEntity<Object>  handleFileSizeLimitExceeded(MaxUploadSizeExceededException ex) {
        ErrorDetails errorDetails = new ErrorDetails("99","-99","MaxUploadSizeExceededException","File size too large",ex.getMessage());
        return new ResponseEntity<Object>(errorDetails, HttpStatus.BAD_REQUEST);
    }
  
//	@ExceptionHandler(RollbackTransactionException.class)
//	public final ResponseEntity<Object> handleRollback(RollbackTransactionException ex,WebRequest request){
//		ErrorDetails errorDetails = new ErrorDetails(new Date(), ex.getMessage(), request.getDescription(false));
//		return new ResponseEntity<Object>(errorDetails, HttpStatus.BAD_REQUEST); 
//	}
//
//	@Override
//	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,HttpHeaders headers, HttpStatus status, WebRequest request) {
//		ErrorDetails errorDetails = new ErrorDetails(new Date(), "Validation Failed", ex.getBindingResult().toString());
//		return new ResponseEntity<Object>(errorDetails, HttpStatus.BAD_REQUEST);
//	}	  
}
