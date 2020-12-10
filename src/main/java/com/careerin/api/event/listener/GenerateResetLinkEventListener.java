package com.careerin.api.event.listener;

import com.careerin.api.event.GenerateResetLinkEvent;
import com.careerin.api.model.PasswordReset;
import com.careerin.api.model.User;
import com.careerin.api.service.MailService;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class GenerateResetLinkEventListener implements ApplicationListener<GenerateResetLinkEvent> {

    private final MailService mailService;

    public GenerateResetLinkEventListener(final MailService mailService) {
        this.mailService = mailService;
    }


    @Override
    @Async
    public void onApplicationEvent(final GenerateResetLinkEvent generateResetLinkEvent) {
        sendRestLink(generateResetLinkEvent);
    }

    private void sendRestLink(final GenerateResetLinkEvent generateResetLinkEvent) {
        final PasswordReset passwordReset = generateResetLinkEvent.getPasswordReset();
        final User user = passwordReset.getUsers();
        final String recipientAddress = user.getEmail();
        final String mailConfirmationUrl = generateResetLinkEvent.getRedirectUrl()
                .queryParam("token", passwordReset.getToken()).toUriString();
        mailService.sendResetLink(mailConfirmationUrl, recipientAddress);
    }
}
