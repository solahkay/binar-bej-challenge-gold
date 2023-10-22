package solahkay.binar.challenge.service;

import solahkay.binar.challenge.model.CreateUserRequest;
import solahkay.binar.challenge.model.DeleteUserRequest;
import solahkay.binar.challenge.model.UpdateUserRequest;
import solahkay.binar.challenge.model.UserResponse;

public interface UserService {

    void createUser(CreateUserRequest request);

    UserResponse getUser(String username);

    UserResponse updateUser(String username, UpdateUserRequest request);

    void deleteUser(String username, DeleteUserRequest request);

}
