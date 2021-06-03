package de.timmi6790.mpstats.api.security;

import de.timmi6790.mpstats.api.apikey.ApiKeyService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

import java.util.function.Supplier;

@RequiredArgsConstructor
public class ApiKeyAuthenticationProvider implements AuthenticationProvider {
    private final ApiKeyService tokenService;

    @Override
    public Authentication authenticate(final Authentication authentication) throws AuthenticationException {
        final String token = (String) authentication.getPrincipal();
        return this.tokenService.getApiKey(token)
                .map(apiKey ->
                        new UsernamePasswordAuthenticationToken(
                                token,
                                null,
                                AuthorityUtils.createAuthorityList(apiKey.getAuthorities())
                        )
                ).orElseThrow((Supplier<RuntimeException>) () -> new BadCredentialsException("Invalid token"));
    }

    @Override
    public boolean supports(final Class<?> authentication) {
        return authentication.equals(PreAuthenticatedAuthenticationToken.class);
    }
}
