package com.careerin.api.service;

import com.careerin.api.dto.UsersDto;
import com.careerin.api.exception.BadResourceException;
import com.careerin.api.model.AuthorizeEmail;
import com.careerin.api.model.TokenStatus;
import com.careerin.api.model.User;
import com.careerin.api.repository.AuthorizeEmailRepository;
import com.careerin.api.repository.UserRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

@Service
@Log4j2
public class AuthorizeEmailServiceImpl implements AuthorizeEmailService {


    @Value("${app.token.email.verification.duration}")
    private Long emailVerificationTokenExpiryDuration;

    private final UserRepository userRepository;

    private final AuthorizeEmailRepository authorizeEmailRepository;

    public AuthorizeEmailServiceImpl(final UserRepository userRepository, final AuthorizeEmailRepository authorizeEmailRepository) {
        this.userRepository = userRepository;
        this.authorizeEmailRepository = authorizeEmailRepository;
    }

    @Override
    public String generateNewToken() {
        return generateOTP();
    }

     private String generateOTP () {
         final Random random = new Random();
         return String.valueOf(100000 + random.nextInt(1000));
     }


    @Override
    public AuthorizeEmail updateExistingTokenWithNameAndExpiry(final AuthorizeEmail authorizeEmail) {
        authorizeEmail.setTokenStatus(TokenStatus.STATUS_PENDING);
        authorizeEmail.setExpiryAt(LocalDateTime.now().plusSeconds(emailVerificationTokenExpiryDuration));
        return save(authorizeEmail);
    }

    @Override
    public void createVerificationToken(final UsersDto usersDto, final String token) {
        final Optional<User> users = userRepository.findById(usersDto.getId());
        if (users.isPresent()) {
            final AuthorizeEmail authorizeEmail = new AuthorizeEmail();
            authorizeEmail.setToken(token);
            authorizeEmail.setTokenStatus(TokenStatus.STATUS_PENDING);
            authorizeEmail.setUsers(users.get());
            authorizeEmail.setExpiryAt(LocalDateTime.now().plusSeconds(emailVerificationTokenExpiryDuration));
            authorizeEmailRepository.save(authorizeEmail);
            log.info("Generated Email verification token [" + usersDto.getEmail() + "]");
        } else {
            log.debug("Create Verificaiton Token Failure for the user " + usersDto.getEmail());
        }
    }

    @Override
    public Optional<AuthorizeEmail> findByToken(final String token) {
        return authorizeEmailRepository.findByToken(token);
    }

    @Override
    public void verifyExpiration(final AuthorizeEmail authorizeEmail) {
        if (authorizeEmail.getExpiryAt().compareTo(LocalDateTime.now()) < 0) {
            throw new BadResourceException( "Expired token. Please issue a new request", "Expired");
        }
    }

    @Override
    public AuthorizeEmail save(final AuthorizeEmail authorizeEmail) {
        return authorizeEmailRepository.save(authorizeEmail);
    }
}
