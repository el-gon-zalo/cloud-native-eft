package cl.duoc.asyncorders.producer.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

/**
 *
 * Usuarios de ejemplo:
 * - admin / admin123 (ROLE_ADMIN) -> acceso total
 * - transportista1 / trans123 (ROLE_TRANSPORTISTA) -> solo sus guías asignadas
 * - cliente1 / cliente123 (ROLE_CLIENTE) -> solo sus propias guías
 */
@Configuration
@EnableMethodSecurity
public class SecurityConfig {

	@Bean
	public PasswordEncoder passwordEncoder() {
		return PasswordEncoderFactories.createDelegatingPasswordEncoder();
	}

	@Bean
	public UserDetailsService userDetailsService(PasswordEncoder encoder) {
		InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();

		manager.createUser(User.withUsername("admin")
				.password(encoder.encode("admin123"))
				.roles("ADMIN")
				.build());

		manager.createUser(User.withUsername("transportista1")
				.password(encoder.encode("trans123"))
				.roles("TRANSPORTISTA")
				.build());

		manager.createUser(User.withUsername("cliente1")
				.password(encoder.encode("cliente123"))
				.roles("CLIENTE")
				.build());

		return manager;
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

		http
			.csrf(csrf -> csrf.disable())
			.sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			.authorizeHttpRequests(auth -> auth
				.requestMatchers("/api/ventas/**").permitAll()
				// Guías de despacho requieren autenticación
				.requestMatchers("/api/guias/**").authenticated()
				.anyRequest().permitAll())
			.oauth2ResourceServer(oauth2 ->
    			oauth2.jwt(jwt -> {})
			);
			//.httpBasic(basic -> {});

		return http.build();
	}
}
