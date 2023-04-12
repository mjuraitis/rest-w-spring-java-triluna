package br.com.triluna.controller;

import br.com.triluna.data.vo.v1.security.AccountCredentialsVO;
import br.com.triluna.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Authentication Endpoint")
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    AuthService authService;

    @SuppressWarnings("rawtypes")
    @Operation(summary = "Authenticates a user and returns a token")
    @PostMapping(value = "/signin")
    public ResponseEntity signin(@RequestBody AccountCredentialsVO data) {

        if (checkIfParamIsNull(data)) {

            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid client request.");
        }

        var token = authService.signin(data);

        if (token == null) {

            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid token request.");
        }

        return token;
    }

    @SuppressWarnings("rawtypes")
    @Operation(summary = "Refresh token from an authenticated user and returns a new token")
    @PutMapping(value = "/refresh/{username}")
    public ResponseEntity refreshToken(@PathVariable("username") String username, @RequestHeader("Authorization") String refreshToken) {

        if (checkIfParamIsNull(username, refreshToken)) {

            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid client request.");
        }

        var token = authService.refreshToken(username, refreshToken);

        if (token == null) {

            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid token request.");
        }

        return token;
    }

    private boolean checkIfParamIsNull(AccountCredentialsVO data) {
        return data == null ||
                data.getUsername() == null || data.getUsername().isBlank() ||
                data.getPassword() == null || data.getPassword().isBlank();
    }

    private boolean checkIfParamIsNull(String username, String refreshToken) {
        return username == null || username.isBlank() ||
                refreshToken == null || refreshToken.isBlank();
    }
}
