package com.alkl1m.auth.filter;

import com.alkl1m.auth.service.impl.UserDetailsServiceImpl;
import com.alkl1m.auth.util.JwtUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Фильтрация HTTP-запросов и проверка наличия и валидности JWT в запросах.
 * Извлекает токен из куки, проверяет его и устанавливает аутентификацию для пользователя.
 *
 * @author alkl1m
 */
@Component
@RequiredArgsConstructor
public class AuthTokenFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;
    private final UserDetailsServiceImpl userDetailsService;

    private static final Logger logger = LogManager.getLogger(AuthTokenFilter.class);

    /**
     * Фильтр для запроса. Проверяет наличие JWT в запросе и,
     * если токен действителен, аутентифицирует его.
     *
     * @param request     запрос.
     * @param response    ответ.
     * @param filterChain цепочка фильтров.
     * @throws ServletException если ошибка при обработке запроса.
     * @throws IOException      если ошибка ввода-вывода.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            String jwt = parseJwt(request);
            if (jwt != null && jwtUtils.validateJwtToken(jwt)) {
                String login = jwtUtils.getLoginFromJwtToken(jwt);
                UserDetails userDetails = userDetailsService.loadUserByUsername(login);

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userDetails,
                                null,
                                userDetails.getAuthorities());

                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception e) {
            logger.error(String.format("Не получается установить аутентификацию юзеру: %s", e));
        }
        filterChain.doFilter(request, response);
    }

    private String parseJwt(HttpServletRequest request) {
        return jwtUtils.getJwtFromCookies(request);
    }
}
