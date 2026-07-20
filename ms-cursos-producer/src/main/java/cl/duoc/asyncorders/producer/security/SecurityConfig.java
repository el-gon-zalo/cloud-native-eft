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
 * - estudiante1 / estudiante123 (ROLE_ESTUDIANTE)
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

		manager.createUser(User.withUsername("estudiante1")
				.password(encoder.encode("estudiante123"))
				.roles("ESTUDIANTE")
				.build());

		return manager;
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

		http
			.csrf(csrf -> csrf.disable())
			.sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			.authorizeHttpRequests(auth -> auth
				.requestMatchers("/api/cursos/health").permitAll()
				.requestMatchers("/api/ventas/**").permitAll()
				.requestMatchers("/api/cursos/**").authenticated()
				.anyRequest().permitAll())
			.oauth2ResourceServer(oauth2 ->
    			oauth2.jwt(jwt -> {})
			);
			//.httpBasic(basic -> {});

		return http.build();
	}
}
