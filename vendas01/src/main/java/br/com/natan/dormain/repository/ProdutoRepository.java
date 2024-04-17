package br.com.natan.dormain.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.natan.dormain.entity.Produto;

public interface ProdutoRepository extends JpaRepository<Produto, Integer>{

}
