package gestion.model.collections.DTO;

import lombok.Data;

@Data
public class RegisterRequestDto {
  private String nombre;
  private String email;
  private String password;
  private String rol; // "EMPRESA" o "CLIENTE"
}