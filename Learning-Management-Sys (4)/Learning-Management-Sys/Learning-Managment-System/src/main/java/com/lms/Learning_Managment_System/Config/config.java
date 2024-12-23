package com.lms.Learning_Managment_System.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import static org.springframework.security.config.Customizer.withDefaults;
@Configuration
@EnableWebSecurity
public class config{
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers("/user/login", "/user/register").permitAll()
                        .requestMatchers("/user/instructor/**").hasRole("INSTRUCTOR")
                        .requestMatchers("/user/admin/**").hasRole("ADMIN")
                        .requestMatchers("/user/student/**").hasRole("STUDENT")
                        .requestMatchers("/user/course/create").hasAnyRole("INSTRUCTOR", "ADMIN")  // added for testing only
                        .anyRequest().authenticated())
                .httpBasic(withDefaults());
        return http.build();
    }
}
