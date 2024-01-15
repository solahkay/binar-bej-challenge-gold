package solahkay.binar.challenge.service;

import solahkay.binar.challenge.model.LoginRequest;
import solahkay.binar.challenge.model.TokenResponse;

public interface AuthService {

    TokenResponse login(LoginRequest request);

}
