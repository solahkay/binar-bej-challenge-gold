package solahkay.binar.challenge.service;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import solahkay.binar.challenge.entity.User;
import solahkay.binar.challenge.exception.ApiException;
import solahkay.binar.challenge.exception.UserNotFoundException;
import solahkay.binar.challenge.model.DeleteUserRequest;
import solahkay.binar.challenge.model.RegisterUserRequest;
import solahkay.binar.challenge.model.UpdateUserRequest;
import solahkay.binar.challenge.model.UserResponse;
import solahkay.binar.challenge.repository.UserRepository;
import solahkay.binar.challenge.security.BCrypt;

import javax.validation.ConstraintViolationException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserServiceTest {

    @Autowired
    UserService userService;

    @Autowired
    UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    void testRegisterSuccess() {
        RegisterUserRequest userRequest = new RegisterUserRequest();
        userRequest.setUsername("solahkay");
        userRequest.setEmail("solahkay@example.com");
        userRequest.setPassword("rahasia");

        userService.register(userRequest);

        User userRegister = userRepository.findByUsername("solahkay").orElse(null);

        User user = new User();
        user.setUsername("solahkay");
        user.setEmail("solahkay@example.com");
        user.setPassword("rahasia");
        user.setId(2L);

        assertNotNull(userRegister);
        assertEquals(user.getUsername(), userRegister.getUsername());
        assertEquals(user.getEmail(), userRegister.getEmail());
        assertTrue(BCrypt.checkpw(user.getPassword(), userRegister.getPassword()));
    }

    @Test
    void testRegisterFailed() {
        RegisterUserRequest user = new RegisterUserRequest();
        user.setUsername("test");
        user.setEmail("test@example.com");
        user.setPassword("rahasia");

        userService.register(user);

        RegisterUserRequest userRequest = new RegisterUserRequest();
        userRequest.setUsername("test");
        userRequest.setEmail("test@example.com");
        userRequest.setPassword("rahasia");

        assertThrows(ApiException.class, () -> userService.register(userRequest));
    }

    @Test
    void testRegisterFailedNull() {
        assertThrows(IllegalArgumentException.class, () -> userService.register(null));
    }

    @Test
    void testUpdateSuccess() {
        RegisterUserRequest registerUserRequest = new RegisterUserRequest();
        registerUserRequest.setUsername("testupdate");
        registerUserRequest.setEmail("testkadal@example.com");
        registerUserRequest.setPassword("rahasia");

        userService.register(registerUserRequest);

        User user = userRepository.findByUsername("testupdate").orElse(null);

        UpdateUserRequest userRequest = new UpdateUserRequest();
        userRequest.setName("Test Bro");
        userRequest.setPassword("Rahasia");

        UserResponse userResponse = userService.update(user, userRequest);

        assertNotNull(user);
        assertEquals(user.getUsername(), userResponse.getUsername());
        assertEquals(user.getName(), userResponse.getName());
        assertEquals(user.getEmail(), userResponse.getEmail());
    }

    @Test
    void testUpdateFailed() {
        RegisterUserRequest registerUserRequest = new RegisterUserRequest();
        registerUserRequest.setUsername("inicobaan");
        registerUserRequest.setPassword("rahasia");
        registerUserRequest.setEmail("example@example.com");

        userService.register(registerUserRequest);

        User user = userRepository.findByUsername("inicobaan").orElse(null);

        UpdateUserRequest userRequest = new UpdateUserRequest();
        userRequest.setName("sklfjskfasdfasdfsafasfsafsafasfsdafsdfdfkjdfkdfjdkfjdkjfkdfjkdfjdkfjdjfdkfdjfkdfjdkfjdkfjdkfjdkfjdkfjdkfjdkfjdkfjdfkjdfkdfjdkjfjskjf");
        userRequest.setPassword("120319101");

        assertThrows(ConstraintViolationException.class, () -> userService.update(user, userRequest));
    }

    @Test
    void testUpdateFailedNull() {
        assertThrows(IllegalArgumentException.class, () -> userService.update(null, null));
    }

    @Test
    void testDeleteSuccess() {
        User user = new User();
        user.setUsername("testhapus123");
        user.setPassword(BCrypt.hashpw("hapusdah", BCrypt.gensalt()));
        user.setName("Hapus dulu gak sih");
        user.setEmail("testhapus123@example.com");

        userRepository.save(user);

        DeleteUserRequest userRequest = new DeleteUserRequest();
        userRequest.setUsername("testhapus123");
        userRequest.setPassword("hapusdah");

        userService.remove(userRequest);

        assertFalse(userRepository.existsByUsername("testhapus123"));
    }

    @Test
    void testDeleteFailed() {
        DeleteUserRequest userRequest = new DeleteUserRequest();
        userRequest.setUsername("testhapus0812");
        userRequest.setPassword("hapusdah");

        assertThrows(UserNotFoundException.class, () -> userService.remove(userRequest));
    }

}
