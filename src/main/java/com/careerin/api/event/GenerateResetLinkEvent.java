package com.careerin.api.event;

import com.careerin.api.model.PasswordReset;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;
import org.springframework.web.util.UriComponentsBuilder;

@Getter
@Setter
public class GenerateResetLinkEvent extends ApplicationEvent {

    private static final long serialVersionUID = -6515939231063443399L;

    private transient UriComponentsBuilder redirectUrl;

    private transient PasswordReset passwordReset;

    public GenerateResetLinkEvent(final UriComponentsBuilder redirectUrl, final PasswordReset passwordReset) {
        super(passwordReset);
        this.redirectUrl = redirectUrl;
        this.passwordReset = passwordReset;
    }
}
