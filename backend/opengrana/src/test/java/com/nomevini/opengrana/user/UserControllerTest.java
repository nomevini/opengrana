package com.nomevini.opengrana.user;

// ✅ Jackson 3 (Boot 4.x)
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import tools.jackson.databind.ObjectMapper;
import com.nomevini.opengrana.exception.GlobalExceptionHandler;
import com.nomevini.opengrana.user.dto.CreateUserRequest;
import com.nomevini.opengrana.user.dto.UserResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.web.method.annotation.AuthenticationPrincipalArgumentResolver;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@Import({GlobalExceptionHandler.class, UserControllerTest.ArgumentResolverConfig.class})
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;

    @TestConfiguration
    static class ArgumentResolverConfig implements WebMvcConfigurer {
        @Override
        public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
            resolvers.add(new AuthenticationPrincipalArgumentResolver());
        }
    }

    @Test
    void shouldReturn201WhenUserIsCreated() throws Exception {
        CreateUserRequest request = new CreateUserRequest("Test", "test@email.com", "password12345");
        UserResponse response = new UserResponse(1L, "Test", "test@email.com");

        when(userService.create(any(CreateUserRequest.class))).thenReturn(response);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.email").value("test@email.com"));
    }

    @Test
    void shouldReturn409WhenEmailAlreadyExists() throws Exception {
        CreateUserRequest request = new CreateUserRequest("Test", "test@email.com", "password12345");

        when(userService.create(any(CreateUserRequest.class)))
                .thenThrow(new EmailAlreadyExistsException("test@email.com"));

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.title").value("Email already in use"));
    }

    @Test
    void shouldReturn400WhenEmailIsBlank() throws Exception {
        CreateUserRequest request = new CreateUserRequest("Test", "", "password12345");

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.title").value("Validation failed"))
                .andExpect(jsonPath("$.errors[0].field").value("email"));
    }

    @Test
    void shouldReturn401WhenAuthenticatedUserNoLongerExists() throws Exception {
        when(userService.getCurrentUser("test@gmail.com"))
                .thenThrow(new UsernameNotFoundException("User not found: test@email.com"));

        mockMvc.perform(get("/users/me").with(withJwt("test@gmail.com")))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.title").value("Unauthorized"));
    }

    private static RequestPostProcessor withJwt(String subject) {
        return request -> {
            Jwt jwt = Jwt.withTokenValue("test-token")
                    .header("alg", "none")
                    .subject(subject)
                    .build();

            var context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(new JwtAuthenticationToken(jwt));
            SecurityContextHolder.setContext(context);
            return request;
        };
    }

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }
}
