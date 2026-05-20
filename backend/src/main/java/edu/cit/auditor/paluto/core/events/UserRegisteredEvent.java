package edu.cit.auditor.paluto.core.events;

import edu.cit.auditor.paluto.core.entities.User;
import lombok.Getter;

@Getter
public class UserRegisteredEvent {
    private final User user;

    public UserRegisteredEvent(User user) {
        this.user = user;
    }
}