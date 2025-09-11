package com.nestenote.notenest.security;

import org.springframework.web.filter.OncePerRequestFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import com.nestenote.notenest.repository.UserRepository;
import com.nestenote.notenest.service.CustomUserDetailsService;
import com.nestenote.notenest.model.User;
import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService customUserDetailsService;

    public JwtAuthenticationFilter(JwtUtil jwtUtil, CustomUserDetailsService customUserDetailsService, UserRepository userRepository) {
        this.jwtUtil = jwtUtil;
        this.customUserDetailsService = customUserDetailsService;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filter) throws ServletException, IOException
    {
        final String authHeader = request.getHeader("Authorization");
        String jwt = null;
        Long userId = null;

        if(request.getServletPath().startsWith("/auth/login")) 
        {
            filter.doFilter(request, response);
            return;
        }

        //check if the JWT header exists
        if(authHeader != null && authHeader.startsWith("Bearer "))
        {
            //extract the token
           jwt = authHeader.substring(7);
           userId = jwtUtil.extractUserId(jwt);
        }
        if(userId != null && SecurityContextHolder.getContext().getAuthentication() == null)
        {
            User user = userRepository.findById(userId).orElse(null);
            if(user != null && jwtUtil.validateToken(jwt, user.getId()))
            {
                UserDetails userDetails = customUserDetailsService.loadUserByUsername(user.getEmail());
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    userDetails,
                    null,
                    userDetails.getAuthorities()
                );
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
      filter.doFilter(request, response);
    }
}
