package com.lukbol.ProjectNoSQL.Configs;

import com.lukbol.ProjectNoSQL.Utils.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtRequestFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    private final CustomUserDetailsService customUserDetailsService;

    private static List<String> skipFilterUrls = Arrays.asList("/activate/**" , "/user/register/**", "/login", "/loginPage", "/",
            "/user/resetPasswordEmail/**", "/user/resetSite/**",
            "/user/resetPassword/**", "/activate/**",
            "/registerPage", "/h2-console/**", "/test", "/test/**", "/error");

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {

        return skipFilterUrls.stream().anyMatch(url -> new AntPathRequestMatcher(url).matches(request));
    }
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        String jwt = jwtUtil.extractJwtFromRequest(request);

        if (jwt == null || jwtUtil.isTokenBlacklisted(jwt)) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Brak tokena lub token został unieważniony");
            return;
        }

        String username = jwtUtil.extractUsername(jwt);

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);
            if (jwtUtil.validateToken(jwt, userDetails.getUsername())) {
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        chain.doFilter(request, response);
    }
}
