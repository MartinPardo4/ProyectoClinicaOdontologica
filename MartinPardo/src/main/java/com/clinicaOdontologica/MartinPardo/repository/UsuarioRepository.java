package com.clinicaOdontologica.MartinPardo.repository;
import org.springframework.data.jpa.repository.JpaRepository;

import com.clinicaOdontologica.MartinPardo.model.Usuario;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario,Long> {

    Optional<Usuario> findByEmail(String correo);
}
