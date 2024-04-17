package br.com.natan.service.impl;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.natan.dormain.entity.Cliente;
import br.com.natan.dormain.entity.ItemPedido;
import br.com.natan.dormain.entity.Pedido;
import br.com.natan.dormain.entity.Produto;
import br.com.natan.dormain.enums.StatusPedido;
import br.com.natan.dormain.repository.ClienteRepository;
import br.com.natan.dormain.repository.ItemPedidoRepository;
import br.com.natan.dormain.repository.PedidoRepository;
import br.com.natan.dormain.repository.ProdutoRepository;
import br.com.natan.exception.PedidoNaoEncontradoException;
import br.com.natan.exception.RegraNegocioExceptio;
import br.com.natan.rest.dto.ItemPedidoDTO;
import br.com.natan.rest.dto.PedidoDTO;
import br.com.natan.service.PedidoService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PedidoServiceImpl implements PedidoService{
	
	private final PedidoRepository repository;
	private final ClienteRepository clienteRepository;
	private final ProdutoRepository produtoRepository;
	private final ItemPedidoRepository itemPedidoRepository;


//	public PedidoServiceImpl(PedidoRepository repository, ClienteRepository clienteRepository,
//			ProdutoRepository produtoRepository, ItemPedidoRepository itemPedidoRepository) {
//		super();
//		this.repository = repository;
//		this.clienteRepository = clienteRepository;
//		this.produtoRepository = produtoRepository;
//		this.itemPedidoRepository = itemPedidoRepository;
//	}



	@Override
	@Transactional
	public Pedido salvar(PedidoDTO dto) {
		Integer idCliente = dto.getCliente();
		Cliente cliente = clienteRepository
				.findById(idCliente)
				.orElseThrow(() -> new RegraNegocioExceptio("Código de cliente inválido!"));
		
		Pedido pedido = new Pedido();
		pedido.setTotal(dto.getTotal());
		pedido.setDataPedido(LocalDate.now());
		pedido.setCliente(cliente);
		pedido.setStatus(StatusPedido.REALIZADO);
		
		List<ItemPedido> itemPedido = converterItens(pedido, dto.getItens());
		repository.save(pedido);
		itemPedidoRepository.saveAll(itemPedido);
		pedido.setItens(itemPedido);
		
		return pedido;
	}
	
	private List<ItemPedido> converterItens(Pedido pedido, List<ItemPedidoDTO> itens) {
		if(itens.isEmpty()) {
			throw new RegraNegocioExceptio("Não é possível realizar um pedido sem itens.");
		}
		
		return itens
				.stream()
				.map(dto -> {
					Integer idProduto = dto.getProduto();
					Produto produto = produtoRepository
						.findById(idProduto)
						.orElseThrow(() -> new RegraNegocioExceptio("Código de produto inválido: " + idProduto));
					
					ItemPedido itemPedido = new ItemPedido();
					itemPedido.setQuantidade(dto.getQuant());
					itemPedido.setPedido(pedido);
					itemPedido.setProduto(produto);
					return itemPedido;
				}).collect(Collectors.toList());
	}



	@Override
	public Optional<Pedido> obterPedidoCompleto(Integer id) {
		return repository.findByIdFetchItens(id);
	}

	@Override
	@Transactional
	public void atualizaStatus(Integer id, StatusPedido statusPedido) {
		repository
				.findById(id)
				.map(pedido -> {
					pedido.setStatus(statusPedido);
					return repository.save(pedido);
				}).orElseThrow(() -> new PedidoNaoEncontradoException());
		
	}
}
