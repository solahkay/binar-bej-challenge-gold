package solahkay.binar.challenge.service;

import solahkay.binar.challenge.model.DeleteUserRequest;
import solahkay.binar.challenge.model.RegisterUserRequest;
import solahkay.binar.challenge.model.TokenResponse;
import solahkay.binar.challenge.model.UpdateUserRequest;
import solahkay.binar.challenge.model.UserResponse;

public interface UserService {

    TokenResponse registerUser(RegisterUserRequest request);

    UserResponse getUser(String username);

    UserResponse updateUser(String username, UpdateUserRequest request);

    void deleteUser(String username, DeleteUserRequest request);

}
