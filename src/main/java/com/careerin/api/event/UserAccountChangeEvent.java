package com.careerin.api.event;

import com.careerin.api.model.User;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

@Getter
@Setter
public class UserAccountChangeEvent extends ApplicationEvent {

    private static final long serialVersionUID = 3898681439653723595L;

    private User users;
    private String action;
    private String actionStatus;

    public UserAccountChangeEvent(final User users, final String action, final String actionStatus) {
        super(users);
        this.users = users;
        this.action = action;
        this.actionStatus = actionStatus;
    }
}
