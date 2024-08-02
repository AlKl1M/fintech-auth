package com.alkl1m.auth.domain.exception;

/**
 * Исключение для случая, когда юзер уже существует.
 *
 * @author alkl1m
 */
public class UserAlreadyExistsException extends RuntimeException {

    public UserAlreadyExistsException(String message) {
        super(message);
    }

}
