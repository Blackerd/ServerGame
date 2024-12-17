package com.rental.rentalapplication.exception;

public class CustomException extends RuntimeException {
    private Error error;

    public CustomException(Error error) {
        super(error.getMessage());
        this.error = error;
    }

    public Error getError() {
        return error;
    }

    public void setError(Error error) {
        this.error = error;
    }
}
