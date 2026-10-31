/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.hotel.repository;

/**
 *
 * @author Elioth Artavia
 */
import com.hotel.domain.Usuario;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

    // Recupera los usuarios activos
    public List<Usuario> findByActivoTrue();

    // Busca un usuario por su correo electrónico
    public Usuario findByCorreo(String correo);

    // Verifica si ya existe un usuario con ese correo
    public boolean existsByCorreo(String correo);

    // Recupera los usuarios de un hotel específico
    public List<Usuario> findByEmpresaIdEmpresa(Integer idEmpresa);

    // Recupera los usuarios según su rol
    public List<Usuario> findByRolNombre(String nombre);

    // Recupera los clientes de un hotel específico
    public List<Usuario> findByEmpresaIdEmpresaAndRolNombre(
            Integer idEmpresa, String rol);

    // Busca clientes por nombre dentro de un hotel específico
    public List<Usuario> findByEmpresaIdEmpresaAndRolNombreAndNombreContainingIgnoreCase(
            Integer idEmpresa, String rol, String nombre);

}
