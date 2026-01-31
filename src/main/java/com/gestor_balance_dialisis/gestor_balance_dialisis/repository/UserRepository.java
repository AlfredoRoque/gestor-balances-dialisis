package com.gestor_balance_dialisis.gestor_balance_dialisis.repository;

import com.gestor_balance_dialisis.gestor_balance_dialisis.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByUsername(String username);

    boolean existsByUsername(String username);
}
