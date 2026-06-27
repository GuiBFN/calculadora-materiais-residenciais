package com.obra.calculator.repository;

import com.obra.calculator.entity.Orcamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrcamentoRepository extends JpaRepository<Orcamento, Long> {

    Optional<Orcamento> findByNumeroOrcamento(String numeroOrcamento);

    List<Orcamento> findByNomeUsuarioContainingIgnoreCase(String nomeUsuario);
}
