package com.ratnaafin.crm.common.exception;

import org.springframework.beans.ConversionNotSupportedException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.context.request.async.AsyncRequestTimeoutException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class CustomizedResponseEntityExceptionHandler extends ResponseEntityExceptionHandler{
    String globalError = "Something going wrong. Please try after sometime.";

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

    @Override
    protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        ErrorDetails errorDetails = new ErrorDetails("99","-99","Request Method not allowed.", globalError, ex.getMessage()+"  "+request.getDescription(false));
        return new ResponseEntity<Object>(errorDetails,HttpStatus.OK);
    }

    @Override
    protected ResponseEntity<Object> handleMissingPathVariable(MissingPathVariableException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        ErrorDetails errorDetails = new ErrorDetails("99","-99","Path Variable Missing.", globalError, ex.getMessage()+" Variable Name : "+ex.getVariableName()+"  "+request.getDescription(false));
        return new ResponseEntity<Object>(errorDetails,HttpStatus.OK);
    }

    @Override
    protected ResponseEntity<Object> handleNoHandlerFoundException(NoHandlerFoundException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        ErrorDetails errorDetails = new ErrorDetails("99","-99","No Handler found.", globalError,ex.getMessage()+"  "+request.getDescription(false));
        return new ResponseEntity<Object>(errorDetails,HttpStatus.OK);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMediaTypeNotSupported(HttpMediaTypeNotSupportedException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        ErrorDetails errorDetails = new ErrorDetails("99","-99","Http Media Type not supported.", globalError,ex.getMessage()+"  "+request.getDescription(false));
        return new ResponseEntity<Object>(errorDetails,HttpStatus.OK);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMediaTypeNotAcceptable(HttpMediaTypeNotAcceptableException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        ErrorDetails errorDetails = new ErrorDetails("99","-99","Http Media Type not acceptable.", globalError,ex.getMessage()+"  "+request.getDescription(false));
        return new ResponseEntity<Object>(errorDetails,HttpStatus.OK);
    }

    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(MissingServletRequestParameterException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        ErrorDetails errorDetails = new ErrorDetails("99","-99","Missing Servlet Request Parameter.", globalError,ex.getMessage()+"  "+request.getDescription(false));
        return new ResponseEntity<Object>(errorDetails,HttpStatus.OK);
    }

    @Override
    protected ResponseEntity<Object> handleServletRequestBindingException(ServletRequestBindingException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        ErrorDetails errorDetails = new ErrorDetails("99","-99","Servlet Request Binding Exception.", globalError,ex.getMessage()+"  "+request.getDescription(false));
        return new ResponseEntity<Object>(errorDetails,HttpStatus.OK);
    }

    @Override
    protected ResponseEntity<Object> handleConversionNotSupported(ConversionNotSupportedException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        ErrorDetails errorDetails = new ErrorDetails("99","-99","Conversion not supported.", globalError,ex.getMessage()+"  "+request.getDescription(false));
        return new ResponseEntity<Object>(errorDetails,HttpStatus.OK);
    }

    @Override
    protected ResponseEntity<Object> handleTypeMismatch(TypeMismatchException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        ErrorDetails errorDetails = new ErrorDetails("99","-99","Type Mismatch.", globalError,ex.getMessage()+"  "+request.getDescription(false));
        return new ResponseEntity<Object>(errorDetails,HttpStatus.OK);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        ErrorDetails errorDetails = new ErrorDetails("99","-99","Http Message not Readable.", globalError,ex.getMessage()+"  "+request.getDescription(false));
        return new ResponseEntity<Object>(errorDetails,HttpStatus.OK);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotWritable(HttpMessageNotWritableException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        ErrorDetails errorDetails = new ErrorDetails("99","-99","Http Message not Writable.", globalError,ex.getMessage()+"  "+request.getDescription(false));
        return new ResponseEntity<Object>(errorDetails,HttpStatus.OK);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        ErrorDetails errorDetails = new ErrorDetails("99","-99","Method Argument not valid.", globalError,ex.getMessage()+"  "+request.getDescription(false));
        return new ResponseEntity<Object>(errorDetails,HttpStatus.OK);
    }

    @Override
    protected ResponseEntity<Object> handleMissingServletRequestPart(MissingServletRequestPartException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        ErrorDetails errorDetails = new ErrorDetails("99","-99","Missing Servlet Request Part.", globalError,ex.getMessage()+"  "+request.getDescription(false));
        return new ResponseEntity<Object>(errorDetails,HttpStatus.OK);
    }

    @Override
    protected ResponseEntity<Object> handleBindException(BindException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        ErrorDetails errorDetails = new ErrorDetails("99","-99","Bind Exception.", globalError,ex.getMessage()+"  "+request.getDescription(false));
        return new ResponseEntity<Object>(errorDetails,HttpStatus.OK);
    }

    @Override
    protected ResponseEntity<Object> handleAsyncRequestTimeoutException(AsyncRequestTimeoutException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        ErrorDetails errorDetails = new ErrorDetails("99","-99","Async Request Timeout.", globalError,ex.getMessage()+"  "+request.getDescription(false));
        return new ResponseEntity<Object>(errorDetails,HttpStatus.OK);
    }

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body, HttpHeaders headers, HttpStatus status, WebRequest request) {
        ErrorDetails errorDetails = new ErrorDetails("99","-99","Exception Internal.", globalError,ex.getMessage()+"  "+request.getDescription(false));
        return new ResponseEntity<Object>(errorDetails,HttpStatus.OK);
    }
}
