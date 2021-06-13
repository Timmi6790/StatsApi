package de.timmi6790.mpstats.api.security;

import io.sentry.Sentry;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Log4j2
@RequiredArgsConstructor
public class ApiKeyAuthenticationFilter extends OncePerRequestFilter {
    private final AuthenticationManager authenticationManager;

    @Override
    protected void doFilterInternal(final HttpServletRequest request,
                                    @NotNull final HttpServletResponse response,
                                    @NotNull final FilterChain chain) throws ServletException, IOException {
        final String token = request.getHeader("X-Api-Key");
        try {
            if (token != null) {
                final PreAuthenticatedAuthenticationToken requestAuthentication = new PreAuthenticatedAuthenticationToken(token, null);

                final Authentication responseAuthentication = this.authenticationManager.authenticate(requestAuthentication);
                if (responseAuthentication == null || !responseAuthentication.isAuthenticated()) {
                    throw new InternalAuthenticationServiceException("Unable to authenticate with provided credentials");
                }
                SecurityContextHolder.getContext().setAuthentication(responseAuthentication);
            }

            chain.doFilter(request, response);
        } catch (final InternalAuthenticationServiceException exception) {
            SecurityContextHolder.clearContext();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

            this.logger.error("Internal authentication service exception", exception);
            Sentry.captureException(exception);
        } catch (final AuthenticationException exception) {
            SecurityContextHolder.clearContext();
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, exception.getMessage());
        }
    }
}
