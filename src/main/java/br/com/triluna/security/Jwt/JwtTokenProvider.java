package br.com.triluna.security.Jwt;

import br.com.triluna.data.vo.v1.security.TokenVO;
import br.com.triluna.exception.InvalidJwtAuthenticationException;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.Base64;
import java.util.Date;
import java.util.List;

@Service
public class JwtTokenProvider {

    @Value("${security.jwt.token.secret-key:secret}")
    private String secretKey = "secret";
    @Value("${security.jwt.token.expire-length:3600000}")
    private Long validityInMilisseconds = 3600000L; // 1 hora

    @Autowired
    private UserDetailsService userDetailsService;

    Algorithm algorithm = null;

    @PostConstruct
    protected void init() {

        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());

        algorithm = Algorithm.HMAC256(secretKey.getBytes());
    }

    public TokenVO createAccessToken(String userName, List<String> roles) {

        Date now = new Date();
        Date validity = new Date(now.getTime() + validityInMilisseconds);

        var accessToken = getAccessToken(userName, roles, now, validity);
        var refreshToken = getRefreshToken(userName, roles, now);

        return new TokenVO(userName, true, now, validity, accessToken, refreshToken);
    }

    public TokenVO refreshToken(String refresh) {

        if (refresh.contains("Bearer ")) {

            refresh = refresh.substring("Bearer ".length());
        }

        JWTVerifier verifier = JWT.require(algorithm).build();

        DecodedJWT decodedJWT = verifier.verify(refresh);

        String userName = decodedJWT.getSubject();
        List<String> roles = decodedJWT.getClaim("roles").asList(String.class);

        return createAccessToken(userName, roles);
    }

    private String getRefreshToken(String userName, List<String> roles, Date now) {

        Date validity = new Date(now.getTime() + (validityInMilisseconds * 3L));

        return JWT.create()
                .withClaim("roles", roles)
                .withIssuedAt(now)
                .withExpiresAt(validity)
                .withSubject(userName)
                .sign(algorithm)
                .strip();
    }

    private String getAccessToken(String userName, List<String> roles, Date now, Date validity) {

        String issuerUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();

        return JWT.create()
                .withClaim("roles", roles)
                .withIssuedAt(now)
                .withExpiresAt(validity)
                .withSubject(userName)
                .withIssuer(issuerUrl)
                .sign(algorithm)
                .strip();
    }

    public Authentication getAuthentication(String token) {

        DecodedJWT decodedJWT = decodeToken(token);

        UserDetails userDetails = this.userDetailsService.loadUserByUsername(decodedJWT.getSubject());

        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    private DecodedJWT decodeToken(String token) {

        Algorithm alg = Algorithm.HMAC256(secretKey.getBytes());

        JWTVerifier verifier = JWT.require(alg).build();

        DecodedJWT decodedJWT = verifier.verify(token);

        return decodedJWT;
    }

    public String resolveToken(HttpServletRequest req) {

        String bearerToken = req.getHeader("Authorization");

        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {

            return bearerToken.substring("Bearer ".length());
        }

        return null;
    }

    public boolean validateToken(String token) {

        DecodedJWT decodedJWT = decodeToken(token);

        try {

            if (decodedJWT.getExpiresAt().before(new Date())) {
                return false;
            }

            return true;
        }
        catch (Exception ex) {
            throw new InvalidJwtAuthenticationException("Invalid or expired JWT Token.");
        }
    }
}
