package gestion.model.collections;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import org.bson.types.ObjectId;
import org.jspecify.annotations.Nullable;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import gestion.model.enums.Rol;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection = "usuario")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class Usuario implements Serializable, UserDetails {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Id
	private ObjectId id;
	private String nombre;
	private String email;
	private String contrasena;
	private Rol rol;
	
	
	
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + rol.name()));
    }
	
	@Override
	public @Nullable String getPassword() {
		// TODO Auto-generated method stub
		return this.contrasena;
	}
	@Override
	public String getUsername() {
		// TODO Auto-generated method stub
		return this.email;
	}
	

	
}
