package br.com.natan.rest.controller;

import java.util.List;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import br.com.natan.dormain.entity.Produto;
import br.com.natan.dormain.repository.ProdutoRepository;

import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping("/api/produtos")
public class ProdutoController {

	private ProdutoRepository produtos;

	public ProdutoController(ProdutoRepository produtos) {
		this.produtos = produtos;
	}
	
	@GetMapping("{id}")
	public Produto getProdutoById(@PathVariable Integer id) {
		return produtos
				.findById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Produto não encontrado"));
	}
	
	@PostMapping
	@ResponseStatus(CREATED)
	public Produto saveProduto (@RequestBody Produto produto) {
		return produtos.save(produto);
	}
	
	@DeleteMapping("{id}")
	@ResponseStatus(NO_CONTENT)
	public void deleteProduto(@PathVariable Integer id) {
		produtos
			.findById(id)
			.map(produtoExistente -> {
				produtos.deleteById(id);
				return Void.TYPE;
			}).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Produto não encontrado"));
	}
	
	@PutMapping("{id}")
	@ResponseStatus(NO_CONTENT)
	public void updateProduto (@PathVariable Integer id,
								@RequestBody Produto produto) {
		produtos
			.findById(id)
			.map(produtoExistente -> {
				produto.setId(produtoExistente.getId());
				produtos.save(produto);
				return produto;
			}).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Produto não encontrado"));
	}
	
	@GetMapping
	public List<Produto> findProduto (Produto filtro){
		ExampleMatcher matcher = ExampleMatcher
									.matching()
									.withIgnoreCase()
									.withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING);
		
		Example example = Example.of(filtro, matcher);
		return produtos.findAll(example);
	}
}
