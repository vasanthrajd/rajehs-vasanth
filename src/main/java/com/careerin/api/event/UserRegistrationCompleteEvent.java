package com.careerin.api.event;

import com.careerin.api.dto.UsersDto;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;
import org.springframework.web.util.UriComponentsBuilder;

@Getter
@Setter
public class UserRegistrationCompleteEvent extends ApplicationEvent {

    private static final long serialVersionUID = -8779813944636822324L;

    private transient UriComponentsBuilder redirectUrl;
    private final UsersDto usersDto;

    public UserRegistrationCompleteEvent(final UsersDto usersDto, final UriComponentsBuilder redirectUrl) {
        super(usersDto);
        this.usersDto = usersDto;
        this.redirectUrl = redirectUrl;
    }
}
