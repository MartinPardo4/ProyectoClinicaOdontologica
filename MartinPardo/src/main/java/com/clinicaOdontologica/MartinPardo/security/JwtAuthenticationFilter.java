package com.clinicaOdontologica.MartinPardo.security;

import com.clinicaOdontologica.MartinPardo.service.UsuarioService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UsuarioService usuarioService;

    public JwtAuthenticationFilter(JwtService jwtService, UsuarioService usuarioService) {
        this.jwtService = jwtService;
        this.usuarioService = usuarioService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String token = authHeader.substring(7);
        final String username;
        try {
            username = jwtService.extractUsername(token);
        } catch (Exception ex) {
            // Token inválido o malformado - dejar que Spring Security maneje la autenticación
            filterChain.doFilter(request, response);
            return;
        }

        if (username != null) {
            try {
                UserDetails userDetails = usuarioService.loadUserByUsername(username);
                if (jwtService.isTokenValid(token, userDetails)) {
                    // Token válido - establecer autenticación
                    UsernamePasswordAuthenticationToken authenticationToken =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                }
                // Si el token no es válido, no establecer autenticación y dejar que Spring Security maneje
            } catch (org.springframework.security.core.userdetails.UsernameNotFoundException ex) {
                // Usuario no encontrado - dejar que Spring Security maneje
            }
        }

        filterChain.doFilter(request, response);
    }
}

