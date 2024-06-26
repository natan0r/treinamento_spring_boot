package br.com.natan.rest.controller;

import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import br.com.natan.dormain.entity.ItemPedido;
import br.com.natan.dormain.entity.Pedido;
import br.com.natan.dormain.enums.StatusPedido;
import br.com.natan.rest.dto.AtualizacaoStatusPedidoDTO;
import br.com.natan.rest.dto.InformacaoItemPedidoDTO;
import br.com.natan.rest.dto.InformacoesPedidoDTO;
import br.com.natan.rest.dto.PedidoDTO;
import br.com.natan.service.PedidoService;

import static org.springframework.http.HttpStatus.*;

import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

	private PedidoService service;
	
	public PedidoController(PedidoService service) {
		this.service = service;
	}
	
	@PostMapping
	@ResponseStatus(CREATED)
	public Integer save( @RequestBody PedidoDTO dto) {
		Pedido pedido = service.salvar(dto);
		return pedido.getId();
	}
	
	public void testarDevTools() {
		
	}
	
	@GetMapping("{id}")
	public InformacoesPedidoDTO getById(@PathVariable Integer id) {
		return service
					.obterPedidoCompleto(id)
					.map( p -> converter(p))
					.orElseThrow(() ->
							new ResponseStatusException(NOT_FOUND, "Pedido não encontrado."));
	}
	
	@PatchMapping("{id}")
	@ResponseStatus(NO_CONTENT)
	public void updateStatus( @PathVariable Integer id, @RequestBody AtualizacaoStatusPedidoDTO dto) {
		String novoStatus = dto.getNovoStatus();
		service.atualizaStatus(id, StatusPedido.valueOf(novoStatus));
	}
	
	private InformacoesPedidoDTO converter (Pedido pedido) {
		return InformacoesPedidoDTO
					.builder()
					.codigo(pedido.getId())
					.dataPedido(pedido.getDataPedido().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")))
					.cpf(pedido.getCliente().getCpf())
					.nomeCliente(pedido.getCliente().getNome())
					.total(pedido.getTotal())
					.status(pedido.getStatus().name())
					.itens(converter(pedido.getItens()))
					.build();
	}
	
	private List<InformacaoItemPedidoDTO> converter(List<ItemPedido> itens) {
		if(CollectionUtils.isEmpty(itens)) {
			return Collections.emptyList();
		}
		
		return itens.stream().map(
				item -> InformacaoItemPedidoDTO
											.builder().descricaoProduto(item.getProduto().getDescricao())
											.precoUnitario(item.getProduto().getPreco())
											.quantidade(item.getQuantidade())
											.build()
		).collect(Collectors.toList());
											
	}
}
