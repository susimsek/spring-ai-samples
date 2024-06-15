package io.github.susimsek.springaisamples.security;

import java.util.Optional;
import lombok.experimental.UtilityClass;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

@UtilityClass
public class SecurityUtils {

    public Optional<String> getCurrentUserLogin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return Optional.empty();
        }

        String username = null;
        if (authentication.getPrincipal() instanceof UserDetails userDetails) {
            username = userDetails.getUsername();
        } else if (authentication instanceof JwtAuthenticationToken jwtAuthenticationToken) {
            username = jwtAuthenticationToken.getName();
        } else if (authentication.getPrincipal() instanceof String principal) {
            username = principal;
        }

        return Optional.ofNullable(username);
    }

    public boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null
            && authentication.isAuthenticated()
            && !(authentication instanceof JwtAuthenticationToken);
    }

    public boolean isCurrentUserInRole(String role) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return false;
        }

        return authentication.getAuthorities().stream()
            .anyMatch(authority -> authority.getAuthority().equals(role));
    }

    public boolean isJwtAuthentication() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication instanceof JwtAuthenticationToken;
    }
}