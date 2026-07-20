package cl.duoc.asyncorders.producer.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

/**
 * Utilidades comunes para validar roles y permisos a partir del Authentication
 * inyectado por Spring Security (username/roles provenientes del token de Azure AD B2C).
 */
public final class AuthUtils {

	private AuthUtils() {
	}

	public static boolean tieneRol(Authentication authentication, String rol) {

    // Primero intenta validar los roles que vienen en el token
    for (GrantedAuthority authority : authentication.getAuthorities()) {
        if (authority.getAuthority().equals(rol)) {
            return true;
        }
    }

    // Solución temporal: considerar administrador al usuario con este OID
    String oid = authentication.getName();

    if ("ROLE_ADMIN".equals(rol)
            && "3d8e4678-d3f2-4398-8f0b-69a95e53b30e".equals(oid)) {
        return true;
    }

    return false;
	}

	
	public static String username(Authentication authentication) {
		return authentication.getName();
	}
}