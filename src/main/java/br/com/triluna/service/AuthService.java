package br.com.triluna.service;

import br.com.triluna.data.vo.v1.security.AccountCredentialsVO;
import br.com.triluna.data.vo.v1.security.TokenVO;
import br.com.triluna.repository.UserRepository;
import br.com.triluna.security.Jwt.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private UserRepository repository;

    @SuppressWarnings("rawtypes")
    public ResponseEntity signin(AccountCredentialsVO data) {

        try {

            var userName = data.getUsername();
            var password = data.getPassword();

            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(userName, password));

            var user = repository.findByUsername(userName);

            var tokenResponse = new TokenVO();

            if (user != null) {

                tokenResponse = tokenProvider.createAccessToken(userName, user.getRoles());
            }
            else {
                throw new UsernameNotFoundException("Username: " + userName + " not found.");
            }

            return ResponseEntity.ok(tokenResponse);
        }
        catch (Exception ex) {

            throw new BadCredentialsException("Invalid username/password supplied.");
        }
    }

    @SuppressWarnings("rawtypes")
    public ResponseEntity refreshToken(String userName, String refreshTkn) {

        var user = repository.findByUsername(userName);

        var tokenResponse = new TokenVO();

        if (user != null) {

            tokenResponse = tokenProvider.refreshToken(refreshTkn);
        }
        else {
            throw new UsernameNotFoundException("Username: " + userName + " not found.");
        }

        return ResponseEntity.ok(tokenResponse);
    }
}
