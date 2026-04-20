package bloodmatch.application.usecase.auth;

import bloodmatch.domain.repositories.UserAccountRepositoryInterface;
import bloodmatch.domain.security.UserAccount;
import bloodmatch.domain.shared.valueObjects.Email;
import bloodmatch.infra.config.JwtProperties;
import bloodmatch.infra.security.JwtTokenProvider;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class AuthenticationService {

  private final UserAccountRepositoryInterface userAccountRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtTokenProvider jwtTokenProvider;
  private final JwtProperties jwtProperties;

  public AuthenticationService(
      UserAccountRepositoryInterface userAccountRepository,
      PasswordEncoder passwordEncoder,
      JwtTokenProvider jwtTokenProvider,
      JwtProperties jwtProperties) {

    if (userAccountRepository == null)
      throw new IllegalArgumentException("UserAccountRepository cannot be null");
    if (passwordEncoder == null)
      throw new IllegalArgumentException("PasswordEncoder cannot be null");
    if (jwtTokenProvider == null)
      throw new IllegalArgumentException("JwtTokenProvider cannot be null");
    if (jwtProperties == null)
      throw new IllegalArgumentException("JwtProperties cannot be null");

    this.userAccountRepository = userAccountRepository;
    this.passwordEncoder = passwordEncoder;
    this.jwtTokenProvider = jwtTokenProvider;
    this.jwtProperties = jwtProperties;
  }

  public AuthenticationResult authenticate(String email, String password) {
    if (email == null || email.isBlank())
      throw new IllegalArgumentException("email cannot be blank");
    if (password == null || password.isBlank())
      throw new IllegalArgumentException("password cannot be blank");

    UserAccount userAccount = userAccountRepository.findByEmail(new Email(email))
        .orElseThrow(() -> new IllegalArgumentException("Invalid credentials"));

    if (!userAccount.isEnabled())
      throw new IllegalStateException("User account is disabled");

    if (!passwordEncoder.matches(password, userAccount.getPasswordHash()))
      throw new IllegalArgumentException("Invalid credentials");

    String accessToken = jwtTokenProvider.generateAccessToken(userAccount);

    return new AuthenticationResult(
        accessToken,
        "Bearer",
        jwtProperties.getExpirationMs(),
        userAccount.getRoles(),
        userAccount.getPartyId().getValue().toString());
  }

  public record AuthenticationResult(
      String accessToken,
      String tokenType,
      long expiresIn,
      Set<bloodmatch.domain.security.SecurityRole> roles,
      String partyId) {
  }
}
