package com.clinicaOdontologica.MartinPardo.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;

public class RestOrFormAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final LoginUrlAuthenticationEntryPoint formEntryPoint;

    public RestOrFormAuthenticationEntryPoint(String loginPageUrl) {
        this.formEntryPoint = new LoginUrlAuthenticationEntryPoint(loginPageUrl);
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
            throws IOException, ServletException {
        String acceptHeader = request.getHeader(HttpHeaders.ACCEPT);
        String requestedWith = request.getHeader("X-Requested-With");

        boolean isAjax = requestedWith != null && "XMLHttpRequest".equalsIgnoreCase(requestedWith);
        boolean wantsHtml = acceptHeader == null || acceptHeader.contains(MediaType.TEXT_HTML_VALUE);

        if (isAjax || (!wantsHtml && acceptHeader != null)) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, authException.getMessage());
            return;
        }

        formEntryPoint.commence(request, response, authException);
    }
}
