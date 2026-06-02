package gob.imss.mx.catalogs.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Configuración de seguridad de Spring Security para la aplicación.
 *
 * Esta clase define el filtro de seguridad y las reglas de autorización
 * para los endpoints de autenticación y productos.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .cors(cors -> cors.configure(http))
            .authorizeHttpRequests(auth -> auth
                // 1. Endpoint público para login
                .requestMatchers("/api/v1/auth/**").permitAll() 
                
                // 2. Reglas para Categorías (Lectura permitida para ambos roles)
                .requestMatchers(HttpMethod.GET, "/api/v1/categories/**").hasAnyRole("ADMIN", "USER")
                
                // 3. Reglas estrictas para Activos Tecnológicos (Assets)
                .requestMatchers(HttpMethod.GET, "/api/v1/assets/**").hasAnyRole("ADMIN", "USER")
                .requestMatchers(HttpMethod.POST, "/api/v1/assets/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/v1/assets/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PATCH, "/api/v1/assets/**").hasRole("ADMIN")
                
                // Cualquier otra ruta requiere firma obligatoria
                .anyRequest().authenticated()
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

}
