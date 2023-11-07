package solahkay.binar.challenge.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import solahkay.binar.challenge.model.DeleteUserRequest;
import solahkay.binar.challenge.model.RegisterUserRequest;
import solahkay.binar.challenge.model.TokenResponse;
import solahkay.binar.challenge.model.UpdateUserRequest;
import solahkay.binar.challenge.model.UserResponse;
import solahkay.binar.challenge.model.WebResponse;
import solahkay.binar.challenge.service.UserService;

@RestController
@RequestMapping("api/v1/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping(
            path = "register",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<TokenResponse> registerUser(@RequestBody RegisterUserRequest request) {
        TokenResponse tokenResponse = userService.registerUser(request);
        return WebResponse.<TokenResponse>builder().data(tokenResponse).build();
    }

    @GetMapping(
            path = "{username}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<UserResponse> getUser(@PathVariable("username") String username) {
        UserResponse userResponse = userService.getUser(username);
        return WebResponse.<UserResponse>builder().data(userResponse).build();
    }

    @PatchMapping(
            path = "{username}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<UserResponse> updateUser(@PathVariable("username") String username,
                                                @RequestBody UpdateUserRequest request) {
        UserResponse userResponse = userService.updateUser(username, request);
        return WebResponse.<UserResponse>builder().data(userResponse).build();
    }

    @DeleteMapping(
            path = "delete/{username}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<String> deleteUser(@PathVariable("username") String username,
                                                @RequestBody DeleteUserRequest request) {
        userService.deleteUser(username, request);
        return WebResponse.<String>builder().data("OK").build();
    }

}
