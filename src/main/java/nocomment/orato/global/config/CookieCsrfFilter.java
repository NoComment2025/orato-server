package nocomment.orato.global.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.net.URI;
import java.util.Set;

@RequiredArgsConstructor
public class CookieCsrfFilter extends OncePerRequestFilter {

    private static final Set<String> SAFE_METHODS = Set.of("GET", "HEAD", "OPTIONS", "TRACE");

    private final OratoProperties oratoProperties;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        if (SAFE_METHODS.contains(request.getMethod()) || !hasAuthorizationCookie(request) || hasBearerToken(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        String origin = request.getHeader("Origin");
        String source = origin != null ? origin : request.getHeader("Referer");
        if (source == null || !isAllowedSource(source, request)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        filterChain.doFilter(request, response);
    }

    private boolean hasAuthorizationCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return false;
        }
        for (Cookie cookie : cookies) {
            if ("Authorization".equals(cookie.getName())) {
                return true;
            }
        }
        return false;
    }

    private boolean hasBearerToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        return header != null && header.startsWith("Bearer ");
    }

    private boolean isAllowedSource(String source, HttpServletRequest request) {
        try {
            URI sourceUri = URI.create(source);
            URI frontendUri = URI.create(oratoProperties.getFrontend().getRedirectUrl());
            if (!isHttpOrigin(sourceUri)) {
                return false;
            }

            boolean frontendOrigin = sameOrigin(sourceUri, frontendUri.getScheme(), frontendUri.getHost(), frontendUri.getPort());
            boolean serverOrigin = sameOrigin(sourceUri, request.getScheme(), request.getServerName(), request.getServerPort());
            return frontendOrigin || serverOrigin;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    private boolean isHttpOrigin(URI uri) {
        return uri.getHost() != null
                && uri.getUserInfo() == null
                && ("http".equalsIgnoreCase(uri.getScheme()) || "https".equalsIgnoreCase(uri.getScheme()));
    }

    private boolean sameOrigin(URI source, String scheme, String host, int port) {
        return scheme != null && host != null
                && source.getScheme().equalsIgnoreCase(scheme)
                && source.getHost().equalsIgnoreCase(host)
                && effectivePort(source.getScheme(), source.getPort()) == effectivePort(scheme, port);
    }

    private int effectivePort(String scheme, int port) {
        return port != -1 ? port : ("https".equalsIgnoreCase(scheme) ? 443 : 80);
    }
}
