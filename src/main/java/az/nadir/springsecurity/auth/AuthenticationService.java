package az.nadir.springsecurity.auth;

import az.nadir.springsecurity.model.auth.AuthenticationRequest;
import az.nadir.springsecurity.model.auth.AuthenticationResponse;
import az.nadir.springsecurity.model.auth.RegisterRequest;
import az.nadir.springsecurity.model.token.Token;
import az.nadir.springsecurity.model.token.TokenType;
import az.nadir.springsecurity.model.user.Role;
import az.nadir.springsecurity.model.user.User;
import az.nadir.springsecurity.repository.TokenRepository;
import az.nadir.springsecurity.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final TokenRepository tokenRepository;

    public AuthenticationResponse register(RegisterRequest request) {
        User user = User.builder()
                .firstname(request.getFirstName())
                .lastname(request.getLastName())
                .email(request.getEmail().toLowerCase(Locale.ROOT))
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .build();
        log.info("register user: {}", user);
        User savedUser = userRepository.save(user);
        String jwtToken = jwtService.generateToken(user);
        saveUserToken(savedUser, jwtToken);

        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }

    @SneakyThrows
    public AuthenticationResponse authenticate(AuthenticationRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found!"));
        log.info("authenticate user: {}", user);
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                request.getEmail(),
                request.getPassword())
        );
        String jwtToken = jwtService.generateToken(user);

        revokedAllUserTokens(user);
        saveUserToken(user, jwtToken);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }

    private void revokedAllUserTokens(User user) {
        List<Token> validUserTokens = tokenRepository.findAllValidTokensByUser(user.getId());
        log.info("revokedAllUserTokens validUserTokens: {}", validUserTokens);
        if (validUserTokens.isEmpty())
            return;
        validUserTokens.forEach(t -> {
            t.setExpired(true);
            t.setRevoked(true);
        });
        tokenRepository.saveAll(validUserTokens);
    }

    private void saveUserToken(User user, String jwtToken) {
        Token tokenModel = Token.builder()
                .user(user)
                .token(jwtToken)
                .tokenType(TokenType.BEARER)
                .expired(false)
                .revoked(false)
                .build();
        log.info("saveUserToken token: {}", tokenModel);
        tokenRepository.save(tokenModel);
    }
}
