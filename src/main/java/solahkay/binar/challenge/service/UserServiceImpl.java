package solahkay.binar.challenge.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import solahkay.binar.challenge.entity.Order;
import solahkay.binar.challenge.entity.User;
import solahkay.binar.challenge.model.CreateUserRequest;
import solahkay.binar.challenge.model.DeleteUserRequest;
import solahkay.binar.challenge.model.UpdateUserRequest;
import solahkay.binar.challenge.model.UserResponse;
import solahkay.binar.challenge.repository.OrderDetailRepository;
import solahkay.binar.challenge.repository.OrderRepository;
import solahkay.binar.challenge.repository.UserRepository;
import solahkay.binar.challenge.security.BCrypt;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final OrderRepository orderRepository;

    private final OrderDetailRepository orderDetailRepository;

    private final ValidationService validationService;

    private static final String USER_NOT_FOUND = "User not found";

    @Autowired
    public UserServiceImpl(UserRepository userRepository,
                           OrderRepository orderRepository,
                           OrderDetailRepository orderDetailRepository,
                           ValidationService validationService) {
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
        this.orderDetailRepository = orderDetailRepository;
        this.validationService = validationService;
    }

    @Override
    @Transactional
    public void createUser(CreateUserRequest request) {
        validationService.validate(request);

        if (userRepository.existsByUsernameOrEmail(request.getUsername(), request.getEmail())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User already exist");
        }

        Optional<String> name = Optional.ofNullable(request.getName());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedLocalDateTime = LocalDateTime.now().format(formatter);
        LocalDateTime localDateTime = LocalDateTime.parse(formattedLocalDateTime, formatter);

        User user = User.builder()
                .id(UUID.randomUUID().toString())
                .username(request.getUsername())
                .name(name.orElse(null))
                .email(request.getEmail())
                .password(BCrypt.hashpw(request.getPassword(), BCrypt.gensalt()))
                .createdAt(localDateTime)
                .updatedAt(localDateTime)
                .build();

        userRepository.save(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUser(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, USER_NOT_FOUND));

        return toUserResponse(user);
    }

    @Override
    @Transactional
    public UserResponse updateUser(String username, UpdateUserRequest request) {
        validationService.validate(request);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, USER_NOT_FOUND));

        if (Objects.nonNull(request.getUsername())) {
            user.setUsername(request.getUsername());
        }

        if (Objects.nonNull(request.getName())) {
            user.setName(request.getName());
        }

        if (Objects.nonNull(request.getPassword())) {
            user.setPassword(BCrypt.hashpw(request.getPassword(), BCrypt.gensalt()));
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedLocalDateTime = LocalDateTime.now().format(formatter);
        LocalDateTime localDateTime = LocalDateTime.parse(formattedLocalDateTime, formatter);

        user.setUpdatedAt(localDateTime);
        userRepository.save(user);

        return toUserResponse(user);
    }

    @Override
    @Transactional
    public void deleteUser(String username, DeleteUserRequest request) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, USER_NOT_FOUND));

        List<Order> orders = orderRepository.findAllByUser(user);

        if (!BCrypt.checkpw(request.getPassword(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid password");
        }

        orders.forEach(orderDetailRepository::deleteAllByOrder);
        orderRepository.deleteAll(orders);
        userRepository.delete(user);
    }

    private static UserResponse toUserResponse(User user) {
        return UserResponse.builder()
                .username(user.getUsername())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }

}
