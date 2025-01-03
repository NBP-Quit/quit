package com.quit.store.infrastructure.configuration;

import jakarta.servlet.http.HttpServletRequest;
import lombok.NonNull;
import org.springframework.data.domain.AuditorAware;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Optional;

public class AuditorAwareImpl implements AuditorAware<String> {

    @Override
    @NonNull
    public Optional<String> getCurrentAuditor() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            String userId = request.getHeader("X-User-id");
            if (userId != null && !userId.isEmpty()) {
                return Optional.of(userId);
            }
        }
        return Optional.empty();
    }

}
