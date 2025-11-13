package com.clinicaOdontologica.MartinPardo.security;

import com.clinicaOdontologica.MartinPardo.model.Usuario;
import com.clinicaOdontologica.MartinPardo.model.UsuarioRol;
import com.clinicaOdontologica.MartinPardo.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DatosIniciales implements ApplicationRunner {
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private BCryptPasswordEncoder codificador;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        String pass = "admin";
        String passCifrado = codificador.encode(pass);
        Usuario usuario = new Usuario("martin", "pardo", "martin@gmail.com", passCifrado, UsuarioRol.USER.name());
        System.out.println("pass sin cifrar: "+pass+ " pass cifrado: "+passCifrado);
        usuarioRepository.save(usuario);
    }
}
