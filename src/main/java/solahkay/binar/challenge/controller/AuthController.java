package solahkay.binar.challenge.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import solahkay.binar.challenge.model.LoginRequest;
import solahkay.binar.challenge.model.TokenResponse;
import solahkay.binar.challenge.model.WebResponse;
import solahkay.binar.challenge.service.AuthService;

@RestController
@RequestMapping("api/v1/auth")
public class AuthController {

    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping(
            path = "login",
            consumes = "application/json",
            produces = "application/json"
    )
    public WebResponse<TokenResponse> login(@RequestBody LoginRequest request) {
        TokenResponse tokenResponse = authService.login(request);
        return WebResponse.<TokenResponse>builder().data(tokenResponse).build();
    }

}
