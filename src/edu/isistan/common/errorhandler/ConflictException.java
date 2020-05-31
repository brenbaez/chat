package edu.isistan.common.errorhandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ConflictException extends RuntimeException{

    private List<String> errors = new ArrayList<>();

    /**
     * Constructor
     */
    public ConflictException() {
        super();
    }

    /**
     * Constructor
     *
     * @param error to add to errors
     */
    public ConflictException(String error) {
        super(error);
        this.errors.add(error);
    }

    /**
     * Constructor
     *
     * @param cause
     */
    public ConflictException(Throwable cause) {
        super(cause);
    }

    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }
}
