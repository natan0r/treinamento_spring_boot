package br.com.natan;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import br.com.natan.dormain.entity.Cliente;
import br.com.natan.dormain.repository.ClienteRepository;

@SpringBootApplication
public class VendasApplication {
 
	@Bean
	public CommandLineRunner commandLineRunner(@Autowired ClienteRepository clientes) {
		return args -> {
			Cliente cr = new Cliente(null, "Fulano");
			clientes.save(cr);
		};
	}
	
	public static void main(String[] args) {
		SpringApplication.run(VendasApplication.class, args);
	}
}