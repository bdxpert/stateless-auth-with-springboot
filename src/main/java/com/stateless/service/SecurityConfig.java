package com.stateless.service;

import com.stateless.service.entities.User;
import com.stateless.service.security.*;
import com.stateless.service.security.AuthenticationExceptionEntryPoint;
import com.stateless.service.security.FVAuthenticationProvider;
import com.stateless.service.security.TokenHandler;
import com.stateless.service.security.UserAuthentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@ComponentScan
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@Order(1)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private FVAuthenticationProvider fvAuthenticationProvider;
    @Autowired
    private AuthenticationExceptionEntryPoint authenticationExceptionEntryPoint;
    @Autowired
    private TokenHandler tokenHandler;
    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.
                sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS).and().
                csrf().disable()
                .anonymous().and()
                .servletApi().and()
                .authorizeRequests().
                antMatchers(HttpMethod.POST, "/api/v1/users/register").permitAll().
                antMatchers(HttpMethod.POST, "/api/v1/users/forgot-password").permitAll().
                antMatchers(HttpMethod.PUT, "/api/v1/users/activate").permitAll().
                antMatchers(HttpMethod.GET, "/api/v1/users/{userId}/activation-email").permitAll().
                antMatchers(HttpMethod.POST, "/api/v1/users/login").permitAll().

                anyRequest().access("hasRole('USER')"). //.hasAnyRole("'USER','SUPERADMIN'")
                and().
                addFilterBefore(new StatelessAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class).
                exceptionHandling().authenticationEntryPoint(authenticationExceptionEntryPoint);

    }
    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Autowired
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(fvAuthenticationProvider);
    }

    class StatelessAuthenticationFilter extends GenericFilterBean {
        static final String ORIGIN = "Origin";


        @Override
        public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException,
                ServletException {

            HttpServletRequest httpServletRequest = (HttpServletRequest) req;
            HttpServletResponse response = (HttpServletResponse) res;
            response.addHeader("Access-Control-Allow-Origin", httpServletRequest.getHeader(ORIGIN));

            response.addHeader("Access-Control-Allow-Methods", "POST,PUT, GET, OPTIONS, DELETE");
            response.addHeader("Access-Control-Allow-Credentials", "true");
            response.addHeader("Access-Control-Allow-Headers",
                    " Origin, X-Requested-With, Content-Type, Accept, accessToken, advisorToken");

            if (httpServletRequest.getMethod().equalsIgnoreCase("OPTIONS")) {
                response.setStatus(200);
                response.flushBuffer();
            } else {
                final String token = ((HttpServletRequest) req).getHeader("accessToken");
                if(token !=null && token.length() > 0) {
                    final User user = tokenHandler.parseUserFromToken(token);
                    if (user == null) {
                        throw new BadCredentialsException("Forbidden");
                    }

                    Authentication authentication = new UserAuthentication(user);
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
                chain.doFilter(req, response); // always continue
            }
        }
    }
}