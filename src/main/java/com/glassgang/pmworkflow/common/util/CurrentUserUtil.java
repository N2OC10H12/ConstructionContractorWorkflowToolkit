package com.glassgang.pmworkflow.common.util;

import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class CurrentUserUtil {

    // TEMP until JWT/security is implemented
    private static final UUID TEMP_USER_ID =
            UUID.fromString("3ecb4f1c-e784-4743-a0df-989a6b5cf844");

    public UUID getCurrentUserId() {
        return TEMP_USER_ID;
    }
}