package com.fatec.backend.configuration;
import com.fatec.backend.model.User;
import com.fatec.backend.model.UserDetailsImpl;
import com.fatec.backend.repository.UserRepository;

import com.fatec.backend.service.auth.JwtTokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
@AllArgsConstructor
@Component
public class UserAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenService jwtTokenService;

    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if(!verificaEndpointsPublicos(request)){
            String token = recuperaToken(request);
            if(token!=null){
                try {
                    String subject = jwtTokenService.pegarToken(token);
                    User modelUser = userRepository.findByUsername(subject)
                            .orElseThrow(() -> new RuntimeException("USUÁRIO NÃO ENCONTRADO!"));

                    UserDetailsImpl modelUserDetails = new UserDetailsImpl(modelUser);
                    Authentication authentication = new UsernamePasswordAuthenticationToken(
                            modelUserDetails,
                            null,
                            modelUserDetails.getAuthorities());

                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }catch (UsernameNotFoundException e){
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.getWriter().write("USUÁRIO NÃO ENCONTRADO");
                    return;
                }catch (Exception e){
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.getWriter().write("TOKEN INVÁLIDO OU INEXISTENTE");
                    return;
                }
            }else{
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("TOKEN INEXISTENTE");
                return;
            }
        }
        filterChain.doFilter(request,response);
    }

    private boolean verificaEndpointsPublicos(HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        return requestURI.startsWith("/swagger-ui") || requestURI.startsWith("/v3/api-docs")
                || requestURI.equals("/api/users/login") || requestURI.equals("/api/users/create-user") || requestURI.equals("api/users/recuperar-senha");
    }
    private String recuperaToken(HttpServletRequest request){
        String authHeader = request.getHeader("Authorization");
        if(authHeader != null && authHeader.startsWith("Bearer ")){
            return authHeader.replace("Bearer ","");
        }
        return null;
    }
}
