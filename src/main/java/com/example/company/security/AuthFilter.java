package com.example.company.security;

import com.example.company.exception.InvalidCredentialsException;
import com.example.company.utils.StringUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Builder;
import lombok.SneakyThrows;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

@Builder
public class AuthFilter extends OncePerRequestFilter {

    private static final String BASIC_AUTH_SCHEME_NAME = "Basic";

    private final UserDetailsService userDetailsService;

    @Override
    @SneakyThrows
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) {

        var authorizationHeaderValue = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (!authHeaderValueValid(authorizationHeaderValue)) {
            chain.doFilter(request, response);
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
        var decodedCredentials = StringUtils.decodeBase64(base64Credentials);
        var credentialsArray = decodedCredentials.split(":", 2);
        if (credentialsArray.length != 2) {
            throw new InvalidCredentialsException();
        }
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
