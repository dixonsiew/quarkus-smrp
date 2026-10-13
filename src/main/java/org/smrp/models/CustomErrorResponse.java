package org.smrp.models;

import java.util.List;


public class CustomErrorResponse  {
    
    private String message;
    private List<FieldError> errors;

    public CustomErrorResponse(String message, List<FieldError> errors) {
        this.message = message;
        this.errors = errors;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public List<FieldError> getErrors() {
        return errors;
    }
    
    public void setErrors(List<FieldError> errors) {
        this.errors = errors;
    }

    public static class FieldError {

        private String field;
        private String message;

        public FieldError(String field, String message) {
            this.field = field;
            this.message = message;
        }
        
        public String getField() {
            return field;
        }
        
        public String getMessage() {
            return message;
        }
    }
}
