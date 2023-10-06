package solahkay.binar.challenge.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import solahkay.binar.challenge.entity.User;
import solahkay.binar.challenge.exception.ApiException;
import solahkay.binar.challenge.exception.UserNotFoundException;
import solahkay.binar.challenge.model.DeleteUserRequest;
import solahkay.binar.challenge.model.RegisterUserRequest;
import solahkay.binar.challenge.model.UpdateUserRequest;
import solahkay.binar.challenge.model.UserResponse;
import solahkay.binar.challenge.repository.UserRepository;
import solahkay.binar.challenge.security.BCrypt;

import java.util.Objects;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final ValidationServiceImpl validationService;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, ValidationServiceImpl validationService) {
        this.userRepository = userRepository;
        this.validationService = validationService;
    }

    @Override
    @Transactional
    public void register(RegisterUserRequest userRequest) {
        validationService.validate(userRequest);

        if (userRepository.existsByUsername(userRequest.getUsername())) {
            throw new ApiException("User already exists");
        }

        Optional<String> name = Optional.ofNullable(userRequest.getName());

        User userRegister = User.builder()
                .username(userRequest.getUsername())
                .name(name.orElse(""))
                .email(userRequest.getEmail())
                .password(BCrypt.hashpw(userRequest.getPassword(), BCrypt.gensalt()))
                .build();

        userRepository.save(userRegister);
    }

    @Override
    @Transactional
    public UserResponse update(User user, UpdateUserRequest userRequest) {
        validationService.validate(userRequest);

        if (Objects.nonNull(userRequest.getName())) {
            user.setName(userRequest.getName());
        }

        if (Objects.nonNull(userRequest.getPassword())) {
            user.setPassword(BCrypt.hashpw(userRequest.getPassword(), BCrypt.gensalt()));
        }

        userRepository.save(user);

        return UserResponse.builder()
                .username(user.getUsername())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }

    @Override
    @Transactional
    public void remove(DeleteUserRequest userRequest) {
        validationService.validate(userRequest);

        User user = userRepository.findByUsername(userRequest.getUsername())
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        boolean isValid = BCrypt.checkpw(userRequest.getPassword(), user.getPassword());

        if (isValid) {
            userRepository.delete(user);
        }
    }

}


