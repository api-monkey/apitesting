package com.skg.apimonkey.config;

import com.skg.apimonkey.service.UserService;
import com.skg.apimonkey.service.oauth2.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.sql.DataSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserService userService;
    @Autowired
    private OAuth2UserService oAuth2UserService;
    @Autowired
    private OAuth2OidcUserService oAuth2OidcUserService;
    @Autowired
    DataSource dataSource;

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder authenticationMgr) throws Exception {
        authenticationMgr.userDetailsService(userService).passwordEncoder(passwordEncoder());
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public PersistentTokenRepository persistentTokenRepository() {
        JdbcTokenRepositoryImpl db = new JdbcTokenRepositoryImpl();
        db.setDataSource(dataSource);
        return db;
    }

    @Bean
    public SavedRequestAwareAuthenticationSuccessHandler savedRequestAwareAuthenticationSuccessHandler() {
        SavedRequestAwareAuthenticationSuccessHandler auth = new SavedRequestAwareAuthenticationSuccessHandler();
        auth.setTargetUrlParameter("targetUrl");
        return auth;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                    .antMatchers("/**")
                        .permitAll()
//                    .antMatchers("/index").access("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
//                    .antMatchers("/admin/**").access("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
                    .antMatchers("/auth/**", "/oauth2/**")
                        .permitAll()
                    .anyRequest().authenticated()
                .and()
                    .logout()
                    .invalidateHttpSession(true)
                    .clearAuthentication(true)
                    .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                    .logoutSuccessUrl("/login?logout")
                    .permitAll()
                .and()
                    .formLogin().successHandler(savedRequestAwareAuthenticationSuccessHandler())
                    .defaultSuccessUrl("/")
                    .failureUrl("/login?error")
                    .loginPage("/login")
                    .permitAll()
                .and()
                    .exceptionHandling().accessDeniedPage("/access-denied")
                .and()
                    .csrf().disable().authorizeRequests()
                .and()
                    .rememberMe().tokenRepository(persistentTokenRepository())
                    .tokenValiditySeconds(1209600)
                .and()
                    .oauth2Login()
                    .loginPage("/sign-in")
                    .userInfoEndpoint()
                    .userService(oAuth2UserService)
                    .oidcUserService(oAuth2OidcUserService);
//                    .csrf().disable().cors();
    }
}