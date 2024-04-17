package br.com.natan.service;

import java.util.Optional;

import br.com.natan.dormain.entity.Pedido;
import br.com.natan.dormain.enums.StatusPedido;
import br.com.natan.rest.dto.PedidoDTO;

public interface PedidoService {
	
	Pedido salvar ( PedidoDTO dto );
	Optional<Pedido> obterPedidoCompleto(Integer id);
	void atualizaStatus(Integer id, StatusPedido statusPedido);
}