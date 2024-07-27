package com.alkl1m.auth.web.controller;

import com.alkl1m.auth.domain.exception.ExceptionBody;
import com.alkl1m.auth.domain.exception.UserAlreadyExistsException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Глобальный обработчик исключений для контроллеров приложения.
 *
 * @author alkl1m
 */
@RestControllerAdvice
public class ControllerAdvice {

    /**
     * Обработчик исключения IllegalStateException.
     *
     * @param e исключение, которое нужно обработать
     * @return объект ExceptionBody с сообщением об ошибке
     */
    @ExceptionHandler(IllegalStateException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionBody handleIllegalState(
            final IllegalStateException e
    ) {
        return new ExceptionBody(e.getMessage());
    }

    /**
     * Обработчик исключений доступа (AccessDeniedException).
     *
     * @return объект ExceptionBody с сообщением о запрете доступа
     */
    @ExceptionHandler({
            AccessDeniedException.class,
            org.springframework.security.access.AccessDeniedException.class
    })
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ExceptionBody handleAccessDenied() {
        return new ExceptionBody("Access denied.");
    }

    /**
     * Обработчик исключения MethodArgumentNotValidException.
     *
     * @param e исключение, связанное с невалидными аргументами метода
     * @return объект ExceptionBody с сообщением о неудачной валидации и списком ошибок
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionBody handleMethodArgumentNotValid(
            final MethodArgumentNotValidException e
    ) {
        ExceptionBody exceptionBody = new ExceptionBody("Validation failed.");
        List<FieldError> errors = e.getBindingResult().getFieldErrors();
        exceptionBody.setErrors(errors.stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        FieldError::getDefaultMessage,
                        (existingMessage, newMessage) ->
                                existingMessage + " " + newMessage)
                ));
        return exceptionBody;
    }

    /**
     * Обработчик исключения ConstraintViolationException.
     *
     * @param e исключение, связанное с нарушением ограничений валидации
     * @return объект ExceptionBody с сообщением о неудачной валидации и списком ошибок
     */
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionBody handleConstraintViolation(
            final ConstraintViolationException e
    ) {
        ExceptionBody exceptionBody = new ExceptionBody("Validation failed.");
        exceptionBody.setErrors(e.getConstraintViolations().stream()
                .collect(Collectors.toMap(
                        violation -> violation.getPropertyPath().toString(),
                        ConstraintViolation::getMessage
                )));
        return exceptionBody;
    }

    /**
     * Обработчик исключения AuthenticationException.
     *
     * @param e исключение, связанное с ошибкой аутентификации
     * @return объект ExceptionBody с сообщением о неудачной аутентификации
     */
    @ExceptionHandler(AuthenticationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionBody handleAuthentication(
            final AuthenticationException e
    ) {
        return new ExceptionBody("Authentication failed.");
    }

    /**
     * Обработчик исключения UserAlreadyExistsException.
     *
     * @param e исключение, связанное с попыткой зарегистрировать уже существующего пользователя
     * @return объект ExceptionBody с сообщением о том, что пользователь уже существует
     */
    @ExceptionHandler(UserAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionBody handleUserAlreadyExists(
            final UserAlreadyExistsException e
    ) {
        return new ExceptionBody("User already exists.");
    }

}
