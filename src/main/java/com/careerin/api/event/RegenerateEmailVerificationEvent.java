package com.careerin.api.event;

import com.careerin.api.model.AuthorizeEmail;
import com.careerin.api.model.User;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;
import org.springframework.web.util.UriComponentsBuilder;

@Getter
@Setter
public class RegenerateEmailVerificationEvent extends ApplicationEvent {

    private static final long serialVersionUID = 3758156251153232273L;

    private transient UriComponentsBuilder redirectUrl;
    private User users;
    private transient AuthorizeEmail authorizeEmail;

    public RegenerateEmailVerificationEvent(final User users, final UriComponentsBuilder redirectUrl, final AuthorizeEmail authorizeEmail) {
        super(users);
        this.users = users;
        this.redirectUrl = redirectUrl;
        this.authorizeEmail = authorizeEmail;
    }
}
