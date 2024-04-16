package com.cgi.example.petstore.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.context.request.async.WebAsyncManagerIntegrationFilter;
import org.springframework.web.filter.ShallowEtagHeaderFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    return allowAllConnectionsFrom(http);
  }

  // TODO verify this
  @Autowired public ShallowEtagHeaderFilter shallowEtagHeaderFilter;

  private DefaultSecurityFilterChain allowAllConnectionsFrom(HttpSecurity http) throws Exception {
    http.authorizeHttpRequests(authorizeRequests -> authorizeRequests.anyRequest().permitAll())
        .addFilterBefore(shallowEtagHeaderFilter, WebAsyncManagerIntegrationFilter.class)
        .csrf(AbstractHttpConfigurer::disable);

    return http.build();
  }
}
