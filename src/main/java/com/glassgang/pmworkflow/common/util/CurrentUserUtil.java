package com.glassgang.pmworkflow.common.util;

import com.glassgang.pmworkflow.user.entity.AppUser;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class CurrentUserUtil {

    public UUID getCurrentUserId() {
        Object principal = SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();

        if (principal instanceof AppUser user) {
            return user.getId();
        }

        throw new RuntimeException("No authenticated user");
    }
}