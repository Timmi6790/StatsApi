package de.timmi6790.mpstats.api.security;

import io.sentry.Sentry;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Log4j2
public class ApiKeyAuthenticationFilter extends GenericFilterBean {
    private final AuthenticationManager authenticationManager;

    public ApiKeyAuthenticationFilter(final AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @Override
    public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain) throws IOException, ServletException {
        final String token = ((HttpServletRequest) request).getHeader("X-Api-Key");

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
            ((HttpServletResponse) response).sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

            this.logger.error("Internal authentication service exception", exception);
            Sentry.captureException(exception);
        } catch (final AuthenticationException exception) {
            SecurityContextHolder.clearContext();
            ((HttpServletResponse) response).sendError(HttpServletResponse.SC_UNAUTHORIZED, exception.getMessage());
        }
    }
}
