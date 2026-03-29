package gestion.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CorsConfigurationSource corsConfigurationSource;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .cors(cors -> cors.configurationSource(corsConfigurationSource))
            .formLogin(AbstractHttpConfigurer::disable)
            .logout(AbstractHttpConfigurer::disable)
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                // Públicas
                .requestMatchers("/auth/**").permitAll()
                .requestMatchers("/restaurante/**").permitAll()
                // Solo EMPRESA puede modificar carta
                .requestMatchers(HttpMethod.POST,   "/producto/**").hasRole("EMPRESA")
                .requestMatchers(HttpMethod.PUT,    "/producto/**").hasRole("EMPRESA")
                .requestMatchers(HttpMethod.DELETE, "/producto/**").hasRole("EMPRESA")
                .requestMatchers(HttpMethod.POST,   "/categoria/**").hasRole("EMPRESA")
                .requestMatchers(HttpMethod.PUT,    "/categoria/**").hasRole("EMPRESA")
                .requestMatchers(HttpMethod.DELETE, "/categoria/**").hasRole("EMPRESA")
                // Lectura de carta: autenticado
                .requestMatchers(HttpMethod.GET, "/producto/**").hasAnyRole("EMPRESA", "CLIENTE")
                .requestMatchers(HttpMethod.GET, "/categoria/**").hasAnyRole("EMPRESA", "CLIENTE")
                // Pedidos y recomendaciones
                .requestMatchers(HttpMethod.PATCH, "/pedido/**").hasAnyRole("EMPRESA", "CLIENTE","EMPLEADO")
                .requestMatchers("/pedido/**").hasAnyRole("EMPRESA", "CLIENTE","EMPLEADO")
                .requestMatchers("/recommendations/**").hasAnyRole("EMPRESA", "CLIENTE","EMPLEADO")
                // Gestión usuarios: solo EMPRESA
                .requestMatchers("/usuario/**").hasRole("EMPRESA")
                .anyRequest().authenticated()
            );

        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}