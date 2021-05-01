package de.timmi6790.mpstats.api.security;

import de.timmi6790.mpstats.api.apikey.ApiKeyService;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

import java.util.function.Supplier;

public class ApiKeyAuthenticationProvider implements org.springframework.security.authentication.AuthenticationProvider {
    private final ApiKeyService tokenService;

    public ApiKeyAuthenticationProvider(final ApiKeyService tokenService) {
        this.tokenService = tokenService;
    }

    @Override
    public Authentication authenticate(final Authentication authentication) throws AuthenticationException {
        final String token = (String) authentication.getPrincipal();
        return this.tokenService.getApiKey(token)
                .map(apiKey ->
                        new UsernamePasswordAuthenticationToken(
                                token,
                                null,
                                AuthorityUtils.createAuthorityList(apiKey.getAuthorities().toArray(new String[0]))
                        )
                ).orElseThrow((Supplier<RuntimeException>) () -> new BadCredentialsException("Invalid token"));
    }

    @Override
    public boolean supports(final Class<?> authentication) {
        return authentication.equals(PreAuthenticatedAuthenticationToken.class);
    }
}
