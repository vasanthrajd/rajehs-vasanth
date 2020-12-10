package com.careerin.api.event;

import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;
import java.time.Instant;
import java.util.Date;

@Getter
@Setter
public class UserLogoutSuccessEvent extends ApplicationEvent {

    private static final long serialVersionUID = 4040951650349685436L;
    private final String userEmail;
    private final String token;
    private final Date eventTime;

    public UserLogoutSuccessEvent(final String userEmail, final String token) {
        super(userEmail);
        this.userEmail = userEmail;
        this.token = token;
        this.eventTime = Date.from(Instant.now());
    }
}
