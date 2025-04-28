package com.home.codingassignment.task1.controller;

import com.home.codingassignment.task1.entity.ResponseHandler;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;


import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

@ControllerAdvice
public class GlobalExceptionHandler {

    // Handles path variable type mismatch
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Object> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        String message = "Invalid format for path variable: " + ex.getName();

        return new ResponseHandler()
                .setSuccessful(false)
                .setMessage(message)
                .generateResponse();

    }

    // Handles receiving request object validation
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            errors.put(fieldError.getField(), fieldError.getDefaultMessage());
        }

        return new ResponseHandler()
                .setSuccessful(false)
                .setStatus(HttpStatus.BAD_REQUEST)
                .setMessage("Parameter validation failed")
                .setData(errors)
                .generateResponse();

    }

    // Handles database constraint violations (duplicate key, etc.)
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Object> handleDatabaseConstraintViolation(DataIntegrityViolationException ex) {

        String rootMessage = ex.getRootCause() != null ? ex.getRootCause().getMessage() : ex.getMessage();
        String userFriendlyMessage = "Database constraint violation";

        if (rootMessage != null) {
            if (rootMessage.toLowerCase().contains("unique") || rootMessage.toLowerCase().contains("duplicate")) {
                userFriendlyMessage = "Entity already exists (duplicate entry)";
            } else if (rootMessage.toLowerCase().contains("foreign key")) {
                userFriendlyMessage = "Invalid reference to another entity (foreign key violation)";
            } else if (rootMessage.toLowerCase().contains("not null")) {
                userFriendlyMessage = "Required field is missing (null value not allowed)";
            } else if (rootMessage.toLowerCase().contains("already exists")) {
                userFriendlyMessage = rootMessage;
            }
        }

        return new ResponseHandler()
                .setSuccessful(false)
                .setStatus(HttpStatus.CONFLICT)
                .setMessage(userFriendlyMessage)
                .generateResponse();

    }

    //Handles requests for objects that don't exist in the db
    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<Object> handleNoSuchElementException(NoSuchElementException ex) {

        return new ResponseHandler()
                .setSuccessful(false)
                .setStatus(HttpStatus.NOT_FOUND)
                .setMessage(ex.getMessage())
                .generateResponse();

    }

    // Handles generic SQL exceptions
    @ExceptionHandler(SQLException.class)
    public ResponseEntity<Object> handleSqlException(SQLException ex) {

        return new ResponseHandler()
                .setSuccessful(false)
                .setStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                .setMessage("SQL error: " + ex.getMessage())
                .generateResponse();

    }

    // Handles everything else
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleAllExceptions(Exception ex) {

        return new ResponseHandler()
                .setSuccessful(false)
                .setStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                .setMessage("An unexpected error occurred: " + ex.getMessage())
                .generateResponse();

    }

}
