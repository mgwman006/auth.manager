
package tz.tante.auth.manager.utilities;

import java.time.Instant;
import java.util.Set;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Component;
import org.springframework.security.oauth2.jwt.JwtDecoder;

@Component
@RequiredArgsConstructor
public class JwtUtils {

  @Value("${app.url}")
  private String appUrl;

  private final JwtEncoder jwtEncoder;
  private final JwtDecoder jwtDecoder;

  public String generateToken(String username, Set<String> roles) {
    Instant now = Instant.now();

    JwtClaimsSet claims = JwtClaimsSet.builder()
      .issuer(appUrl)
      .subject(username)
      .issuedAt(now)
      .expiresAt(now.plusMillis(Constant.jwtExpirationMs))
      .claim("roles", roles)
      .build();

    return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
  }

  public boolean validateToken(String token) {
    try {
      getClaims(token);
      return true;
    } catch (Exception exception) {
      return false;
    }
  }

  public String getUserName(String token) {
    return getClaims(token).getSubject();
  }

  public Set<String> getRolesFromToken(String token) {
    Object roles = getClaims(token).getClaims().get("roles");

    if (!(roles instanceof java.util.Collection<?> values)) {
      return Set.of();
    }

    return values.stream()
      .filter(String.class::isInstance)
      .map(String.class::cast)
      .collect(java.util.stream.Collectors.toSet());
  }

  private Jwt getClaims(String token)
  {
    return jwtDecoder.decode(token);
  }
}