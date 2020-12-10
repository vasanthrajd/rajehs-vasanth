package com.careerin.api.event.listener;

import com.careerin.api.dto.UsersDto;
import com.careerin.api.event.UserRegistrationCompleteEvent;
import com.careerin.api.service.AuthorizeEmailService;
import com.careerin.api.service.MailService;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class UserRegistrationCompleteListener implements ApplicationListener<UserRegistrationCompleteEvent> {

    private final AuthorizeEmailService authorizeEmailService;
    private final MailService mailService;

    public UserRegistrationCompleteListener(final AuthorizeEmailService authorizeEmailService,
                                            final MailService mailService) {
        this.authorizeEmailService = authorizeEmailService;
        this.mailService = mailService;
    }

    @Override
    @Async
    public void onApplicationEvent(final UserRegistrationCompleteEvent userRegistrationCompleteEvent) {
        sendEmailVerification(userRegistrationCompleteEvent);
    }

    private void sendEmailVerification(final UserRegistrationCompleteEvent userRegistrationCompleteEvent) {
        final UsersDto usersDto = userRegistrationCompleteEvent.getUsersDto();
        final String token = authorizeEmailService.generateNewToken();
        authorizeEmailService.createVerificationToken(usersDto, token);
        final String recipientAddress = usersDto.getEmail();
        final String emailConfirmationUrl = userRegistrationCompleteEvent.getRedirectUrl()
                .queryParam("token", token).toUriString();
        mailService.sendEmailVerification(emailConfirmationUrl, recipientAddress, token);
    }
}
