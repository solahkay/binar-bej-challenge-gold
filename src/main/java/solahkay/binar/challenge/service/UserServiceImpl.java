package solahkay.binar.challenge.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import solahkay.binar.challenge.entity.Merchant;
import solahkay.binar.challenge.entity.Order;
import solahkay.binar.challenge.entity.Role;
import solahkay.binar.challenge.entity.User;
import solahkay.binar.challenge.enums.UserRole;
import solahkay.binar.challenge.model.DeleteUserRequest;
import solahkay.binar.challenge.model.RegisterUserRequest;
import solahkay.binar.challenge.model.TokenResponse;
import solahkay.binar.challenge.model.UpdateUserRequest;
import solahkay.binar.challenge.model.UserResponse;
import solahkay.binar.challenge.repository.MerchantRepository;
import solahkay.binar.challenge.repository.OrderDetailRepository;
import solahkay.binar.challenge.repository.OrderRepository;
import solahkay.binar.challenge.repository.RoleRepository;
import solahkay.binar.challenge.repository.UserRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final ValidationService validationService;

    private final JwtService jwtService;

    private final PasswordEncoder passwordEncoder;

    private final RoleRepository roleRepository;

    private final OrderRepository orderRepository;

    private final OrderDetailRepository orderDetailRepository;

    private final MerchantRepository merchantRepository;

    private static final String USER_NOT_FOUND = "User not found!";

    @Autowired
    public UserServiceImpl(UserRepository userRepository,
                           ValidationService validationService,
                           JwtService jwtService,
                           PasswordEncoder passwordEncoder,
                           RoleRepository roleRepository,
                           OrderRepository orderRepository,
                           OrderDetailRepository orderDetailRepository,
                           MerchantRepository merchantRepository) {
        this.userRepository = userRepository;
        this.validationService = validationService;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
        this.orderRepository = orderRepository;
        this.orderDetailRepository = orderDetailRepository;
        this.merchantRepository = merchantRepository;
    }

    @Override
    @Transactional
    public TokenResponse registerUser(RegisterUserRequest request) {
        validationService.validate(request);

        if (userRepository.existsByUsernameOrEmail(request.getUsername(), request.getEmail())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User already exists!");
        }

        Optional<String> name = Optional.ofNullable(request.getName());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedLocalDateTime = LocalDateTime.now().format(formatter);
        LocalDateTime localDateTime = LocalDateTime.parse(formattedLocalDateTime, formatter);

        User user = User.builder()
                .username(request.getUsername())
                .name(name.orElse(null))
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .createdAt(localDateTime)
                .updatedAt(localDateTime)
                .build();

        Set<Role> roleSet = new HashSet<>();
        Role userRole = roleRepository.findByName(UserRole.USER);
        Role customerRole = roleRepository.findByName(UserRole.CUSTOMER);
        roleSet.add(userRole);
        roleSet.add(customerRole);
        user.setRoles(roleSet);

        userRepository.save(user);

        String token = jwtService.generateToken(user);
        return TokenResponse.builder()
                .token(token)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUser(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, USER_NOT_FOUND));

        return toUserResponse(user);
    }

    private static UserResponse toUserResponse(User user) {
        Optional<Merchant> merchant = Optional.ofNullable(user.getMerchant());
        return UserResponse.builder()
                .username(user.getUsername())
                .name(user.getName())
                .email(user.getEmail())
                .merchantUsername(merchant.map(Merchant::getUsername).orElse(""))
                .build();
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
            user.setPassword(passwordEncoder.encode(request.getPassword()));
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

        Merchant merchant = merchantRepository.findFirstByUser(user)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Merchant not found!"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid password");
        }

        orders.forEach(orderDetailRepository::deleteAllByOrder);
        orderRepository.deleteAll(orders);
        merchantRepository.delete(merchant);
        userRepository.delete(user);
    }

}
