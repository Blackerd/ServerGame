package com.rental.rentalapplication.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter

public enum Error {
    UNCATEGORIZED_EXCEPTION("Uncategorized exception", "Uncategorized exception", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_KEY("Invalid message key", "Invalid message key", HttpStatus.BAD_REQUEST),
    USER_EXISTED("User existed", "Username already exists, change to another username", HttpStatus.BAD_REQUEST),
    USERNAME_INVALID("User name must be at least 3 characters", "User name must be at least 3 characters", HttpStatus.BAD_REQUEST),
    PASSWORD_INVALID("Password must be at least 8 characters", "Password must be at least 8 characters", HttpStatus.BAD_REQUEST),
    USER_NOT_EXISTED("User not existed", "User does not exist, please create a user", HttpStatus.NOT_FOUND),
    UNAUTHENTICATED("Unauthenticated", "Your token cannot be authenticated ", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED("You do not have permission", "You do not have permission", HttpStatus.FORBIDDEN),
    ROLE_NOT_FOUND("Role not found", "Not found role you want to find", HttpStatus.NOT_FOUND),
    ROLE_EXISTED("Role already exists", "Role already exists, change to another role", HttpStatus.BAD_REQUEST),
    HOUSE_EXISTED("House already exists", "House already exists, change to another house", HttpStatus.BAD_REQUEST),
    HOUSE_NOT_FOUND("House not found", "House not found", HttpStatus.NOT_FOUND),
    VEHICLE_NOT_FOUND("Vehicle not found", "Vehicle not found", HttpStatus.NOT_FOUND),
    VEHICLE_EXISTED("Vehicle already existed", "Vehicle already exists, change to another vehicle", HttpStatus.BAD_REQUEST),
    VEHICLE_RENTED("Vehicle rent", "This vehicle is rented", HttpStatus.BAD_REQUEST),
    HOUSE_RENTED("House rented", "This house is rented", HttpStatus.BAD_REQUEST),
    HOUSE_AND_VEHICEL_RENTED("House and vehicle is rented", "House and vehicle is rented", HttpStatus.BAD_REQUEST);
    private String message;
    private String violation;
    private HttpStatusCode statusCode;

    Error(String message, String violation, HttpStatusCode statusCode) {
        this.message = message;
        this.violation = violation;
        this.statusCode = statusCode;
    }
}
