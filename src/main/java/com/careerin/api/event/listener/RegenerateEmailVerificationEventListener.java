package com.careerin.api.event.listener;

import com.careerin.api.event.RegenerateEmailVerificationEvent;
import com.careerin.api.model.AuthorizeEmail;
import com.careerin.api.model.User;
import com.careerin.api.service.MailService;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@Log4j2
public class RegenerateEmailVerificationEventListener implements ApplicationListener<RegenerateEmailVerificationEvent> {



    private final MailService mailService;

    public RegenerateEmailVerificationEventListener(final MailService mailService) {
        this.mailService = mailService;
    }


    @Override
    @Async
    public void onApplicationEvent(final RegenerateEmailVerificationEvent regenerateEmailVerificationEvent) {
        resendEmailVerification(regenerateEmailVerificationEvent);
    }

    private void resendEmailVerification(final RegenerateEmailVerificationEvent regenerateEmailVerificationEvent) {
        final User user = regenerateEmailVerificationEvent.getUsers();
        final AuthorizeEmail authorizeEmail = regenerateEmailVerificationEvent.getAuthorizeEmail();
        final String recipientAddress = user.getEmail();
        final String emailConfirmationUrl = regenerateEmailVerificationEvent.getRedirectUrl()
                        .queryParam("token", authorizeEmail.getToken()).toUriString();
        mailService.sendEmailVerification(emailConfirmationUrl, recipientAddress, authorizeEmail.getToken());
    }
}
