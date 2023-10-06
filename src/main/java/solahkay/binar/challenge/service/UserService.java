package solahkay.binar.challenge.service;

import solahkay.binar.challenge.entity.User;
import solahkay.binar.challenge.model.DeleteUserRequest;
import solahkay.binar.challenge.model.RegisterUserRequest;
import solahkay.binar.challenge.model.UpdateUserRequest;
import solahkay.binar.challenge.model.UserResponse;

public interface UserService {

    void register(RegisterUserRequest userRequest);

    UserResponse update(User user, UpdateUserRequest userRequest);

    void remove(DeleteUserRequest userRequest);

}
