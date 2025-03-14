package id.my.mrz.minimum.security;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.http.HttpHeaders;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import id.my.mrz.minimum.domain.session.service.ISessionService;
import id.my.mrz.minimum.domain.user.entity.Principal;
import id.my.mrz.minimum.exception.AuthorizationHeaderViolationException;

import io.jsonwebtoken.Claims;

@Component
public final class JwtAuthenticationFilter extends OncePerRequestFilter {

    private ISessionService jwtService;

    public JwtAuthenticationFilter(
        UserDetailsService userDetailsService, ISessionService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(
        HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException {

        try {
            String header = request.getHeader(HttpHeaders.AUTHORIZATION);
            if (header == null || header.isEmpty()) {
                filterChain.doFilter(request, response);
                return;
            }
            String[] sections = header.split(" ", 2);
            if (sections.length != 2) {
                throw new AuthorizationHeaderViolationException(
                    "Authorization header must have exactly two sections");
            }

            String token = sections[1];
            if (token != null) {
                Claims claims = jwtService.parseJwt(token).getPayload();
                if (claims.getExpiration().after(new Date())) {
                    Long principalId = Long.valueOf(claims.getId());
                    Principal principal = new Principal(claims.getSubject(), null, List.of());
                    principal.setId(principalId);

                    SecurityContextHolder.getContext().setAuthentication(principal);
                }
            }
        } catch (Exception e) {
            SecurityContextHolder.clearContext();
            throw e;
        }

        filterChain.doFilter(request, response);
    }
}
