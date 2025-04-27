package com.home.codingassignment.task1.entity;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ResponseHandler {

    /**
     * Response handler for sending data back to the user
     * By default the responses are deemed successful with an HTTP status code of 200
     * Unsuccessful responses have a default HTTP status code of 400
     */

    private HttpStatus status = HttpStatus.OK;
    private boolean successful = true;
    private String message;
    private String warnings;
    private Object data;

    public ResponseHandler() {
    }

    public HttpStatus getStatus() {
        return status;
    }

    public ResponseHandler setStatus(HttpStatus status) {
        this.status = status;
        return this;
    }

    public boolean isSuccessful() {
        return successful;
    }

    public ResponseHandler setSuccessful(boolean successful) {
        this.successful = successful;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public ResponseHandler setMessage(String message) {
        this.message = message;
        return this;
    }

    public Object getData() {
        return data;
    }

    public ResponseHandler setData(Object data) {
        this.data = data;
        return this;
    }

    public String getWarnings() {
        return warnings;
    }

    public ResponseHandler setWarnings(String warnings) {
        this.warnings = warnings;
        return this;
    }


    public ResponseEntity<Object> generateResponse(){
        Map<String, Object> responseMap = new HashMap<>();

        responseMap.put("timestamp", new Date());
        responseMap.put("successful", this.successful);

        if(this.successful){
            responseMap.put("message", this.message);
        } else {
            responseMap.put("error", this.message);

            if (this.status == HttpStatus.OK) {
                this.status = HttpStatus.BAD_REQUEST;
            }
        }

        responseMap.put("status", this.status.value());


        if(this.warnings != null){
            responseMap.put("warnings", this.warnings);
        }

        if (this.data != null) {
            responseMap.put("data", this.data);
        }

        return new ResponseEntity<>(responseMap, this.status);
    }


}
