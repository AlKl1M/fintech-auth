package com.alkl1m.auth.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Реализация AuthenticationEntryPoint.
 * Обрабатывает не авторизированные запросы и отправляет соответствующий ответ клиенту.
 *
 * @author alkl1m
 */
@Component
public class AuthEntryPointJwt implements AuthenticationEntryPoint {

    private static final Logger logger = LogManager.getLogger(AuthEntryPointJwt.class);

    /**
     * Вызывается, когда юзер пытается получить доступ к защищенному ресурсу
     * будучи без аутентификации. Генерирует объект с кодом 401 и возвращает
     * информацию об ошибке.
     *
     * @param request       запрос клиента.
     * @param response      ответ сервера.
     * @param authException исключение, связанное с аутентификацией.
     * @throws IOException если происходит ошибка ввода-вывода при записи ответа.
     */
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
            throws IOException {
        logger.error("Не удалось авторизировать: {}", authException.getMessage());

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        final Map<String, Object> body = new HashMap<>();
        body.put("status", HttpServletResponse.SC_UNAUTHORIZED);
        body.put("error", "Unauthorized");
        body.put("message", authException.getMessage());
        body.put("path", request.getServletPath());

        final ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(response.getOutputStream(), body);
    }

}