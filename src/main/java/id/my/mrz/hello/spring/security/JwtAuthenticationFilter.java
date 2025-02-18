package id.my.mrz.hello.spring.security;

import id.my.mrz.hello.spring.session.ISessionService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

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

    String header = request.getHeader(HttpHeaders.AUTHORIZATION);
    if (header == null || header.isEmpty()) {
      filterChain.doFilter(request, response);
      return;
    }

    String[] section = header.split(" ", 2);
    if (section.length != 2)
      throw new AuthorizationHeaderViolationException(
          "authorization header section is not exactly two");

    Claims claims = jwtService.parseJwt(section[1]).getPayload();
    if (claims.getExpiration().before(new Date())) throw new JwtException("token expired");

    UsernamePasswordAuthenticationToken auth =
        new UsernamePasswordAuthenticationToken(claims.getSubject(), null, List.of());
    SecurityContextHolder.getContext().setAuthentication(auth);

    filterChain.doFilter(request, response);
  }
}
