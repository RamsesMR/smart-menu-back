package gestion.security;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


import org.springframework.web.cors.CorsConfigurationSource;



import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
	

	@Autowired
	private CorsConfigurationSource corsConfigurationSource;

	@Autowired
	private JwtAuthenticationFilter jwtAuthenticationFilter;
	
	//Activar encriptación de password
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
	    .exceptionHandling(ex -> ex.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))
	    .authorizeHttpRequests(auth -> auth
	      .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

	      // auth público (login/register)
	      .requestMatchers("/auth/**").permitAll()

	      // público
	      .requestMatchers("/restaurante/**").permitAll()

	      // protegido
	      .requestMatchers("/pedido/**").hasAnyRole("EMPRESA","CLIENTE")
	      .requestMatchers("/categoria/**").hasAnyRole("EMPRESA","CLIENTE")
	      .requestMatchers("/producto/**").hasAnyRole("EMPRESA","CLIENTE")
	      .requestMatchers("/recommendations/**").hasAnyRole("EMPRESA","CLIENTE")

	      .anyRequest().authenticated()
	    );

	  http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

	  return http.build();
	}

}
