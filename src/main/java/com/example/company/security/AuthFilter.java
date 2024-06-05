package com.example.company.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
public class AuthFilter extends OncePerRequestFilter {

    private static final String BASIC_AUTH_SCHEME_NAME = "Basic";

    private final UserDetailsService userDetailsService;

    @Override
    @SneakyThrows
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) {

        var authorizationHeaderValue = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (!authHeaderValueValid(authorizationHeaderValue)) {
            return;
        }

        var credentials = resolveCredentials(authorizationHeaderValue);

        if (SecurityContextHolder.getContext().getAuthentication() == null) {

            var userDetails = userDetailsService.loadUserByUsername(credentials.username());
            if (userDetails.getPassword().equals(credentials.password())) {

                var usernamePasswordAuthenticationToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                usernamePasswordAuthenticationToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            }
        }
        chain.doFilter(request, response);
    }

    private static Credentials resolveCredentials(String authorizationHeaderValue) {
        var base64Credentials = authorizationHeaderValue.substring(6);
        var decodedBytes = Base64.getDecoder().decode(base64Credentials);
        var decodedCredentials = new String(decodedBytes, StandardCharsets.UTF_8);
        var credentialsArray = decodedCredentials.split(":", 2);
        return Credentials.builder()
                .username(credentialsArray[0])
                .password(credentialsArray[1])
                .build();
    }

    @Builder
    private record Credentials(String username, String password) {}
    ;

    private boolean authHeaderValueValid(String headerValue) {
        return headerValue != null && headerValue.startsWith(BASIC_AUTH_SCHEME_NAME);
    }
}
