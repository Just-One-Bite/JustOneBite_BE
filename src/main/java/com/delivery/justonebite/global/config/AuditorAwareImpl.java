package com.delivery.justonebite.global.config;

import com.delivery.justonebite.global.common.security.UserDetailsImpl;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class AuditorAwareImpl implements AuditorAware<Long> {
    @Override
    public Optional<Long> getCurrentAuditor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (!(authentication == null || !authentication.isAuthenticated())) {
            Object principal = authentication.getPrincipal();

            if (principal instanceof UserDetailsImpl userDetails) {
                return Optional.of(userDetails.getUserId());
            }
        }

        return Optional.empty();
    }
}
