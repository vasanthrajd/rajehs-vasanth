package com.careerin.api.event.listener;

import com.careerin.api.event.UserAccountChangeEvent;
import com.careerin.api.model.User;
import com.careerin.api.service.MailService;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Async;

@Log4j2
public class UserAccountChangeEventListener implements ApplicationListener<UserAccountChangeEvent> {



    private final MailService mailService;

    public UserAccountChangeEventListener(final MailService mailService) {
        this.mailService = mailService;
    }

    @Override
    @Async
    public void onApplicationEvent(final UserAccountChangeEvent userAccountChangeEvent) {
        sendAccountChangeEmail(userAccountChangeEvent);
    }

    private void sendAccountChangeEmail(final UserAccountChangeEvent userAccountChangeEvent) {
        final User user = userAccountChangeEvent.getUsers();
        final String action = userAccountChangeEvent.getAction();
        final String actionStatus = userAccountChangeEvent.getActionStatus();
        final String recipientAddress = user.getEmail();
        mailService.sendAccountChangeEmail(action, actionStatus, recipientAddress);
    }
}
