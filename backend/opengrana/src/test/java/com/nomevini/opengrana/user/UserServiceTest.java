package com.nomevini.opengrana.user;

import com.nomevini.opengrana.user.dto.CreateUserRequest;
import com.nomevini.opengrana.user.dto.UserResponse;
import com.nomevini.opengrana.user.exception.EmailAlreadyExistsException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    void shouldCreateUserWithEncryptedPassword() {
        // arrange
        CreateUserRequest request = new CreateUserRequest("Test", "test@email.com", "password12345");

        when(userRepository.existsByEmail("test@email.com")).thenReturn(false);
        when(passwordEncoder.encode("password12345")).thenReturn("fake-hash");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User u = invocation.getArgument(0);
            return User.builder()
                    .id(1L)
                    .name(u.getName())
                    .email(u.getEmail())
                    .password(u.getPassword())
                    .role(u.getRole())
                    .build();
        });

        // act
        UserResponse response = userService.create(request);

        // assert
        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.email()).isEqualTo("test@email.com");
        verify(passwordEncoder).encode("password12345");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void shouldThrowExceptionWhenEmailAlreadyExists() {
        CreateUserRequest request = new CreateUserRequest("Test", "test@email.com", "password12345");

        when(userRepository.existsByEmail("test@email.com")).thenReturn(true);

        assertThrows(EmailAlreadyExistsException.class, () -> userService.create(request));

        verify(userRepository, never()).save(any());
    }
}
