package id.my.mrz.hello.spring.security;

import id.my.mrz.hello.spring.session.JwtService;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private JwtService jwtService;
  private UserDetailsService userDetailsService;

  public JwtAuthenticationFilter(UserDetailsService userDetailsService, JwtService jwtService) {
    this.userDetailsService = userDetailsService;
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

    var claims = jwtService.parseJwt(section[1]).getPayload();
    if (claims.getExpiration().before(new Date())) throw new JwtException("token expired");

    var userDetails = userDetailsService.loadUserByUsername(claims.getSubject());

    UsernamePasswordAuthenticationToken auth =
        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    SecurityContextHolder.getContext().setAuthentication(auth);

    filterChain.doFilter(request, response);
  }
}
