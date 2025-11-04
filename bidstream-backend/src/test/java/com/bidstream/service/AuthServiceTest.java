package com.bidstream.service;

import com.bidstream.config.JwtUtil;
import com.bidstream.dto.LoginRequestDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AuthServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private CustomUserDetailsService userDetailsService;

    @InjectMocks
    private AuthService authService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAuthenticate() {
        LoginRequestDto request = new LoginRequestDto();
        request.setEmail("test@test.com");
        request.setPassword("password");

        UserDetails mockUserDetails = mock(UserDetails.class);
        
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(null);
        when(userDetailsService.loadUserByUsername("test@test.com")).thenReturn(mockUserDetails);
        when(jwtUtil.generateToken(mockUserDetails)).thenReturn("mock-jwt-token");

        String token = authService.authenticate(request);

        assertEquals("mock-jwt-token", token);
    }
}
