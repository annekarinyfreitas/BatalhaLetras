package com.corba;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;

import org.omg.CORBA.ORB;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContext;
import org.omg.CosNaming.NamingContextHelper;

import PPDCorba.NoSuchPart;
import PPDCorba.Part;
import PPDCorba.PartInfo;
import PPDCorba.PartListItem;
import PPDCorba.PartRepository;
import PPDCorba.PartRepositoryHelper;

public class Client {
	PartRepository currentPartRepository;
	
	public static void main(String args[]) {
		Client c = new Client();
		
		try {
			while (true) {
				System.out.println("Terminal do Cliente");
				System.out.println("bind - Faz o cliente conectar-se ao servidor e pega a referência do PartRepository");
				System.out.println("inforep - Lista informações do repositório (nome e num de pecas)");
				System.out.println("listp - Lista as peças do repositório");
				System.out.println("getp - Busca a peça por código");
				System.out.println("showp - Mostra os atributos da peça");
				System.out.println("addp - Adiciona uma peça ao repositório corrente");
				System.out.println("quit - Encerrar");
				System.out.println("Digite um comando: ");
				
				BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
				String s = br.readLine();
				
				List<String> splited = Arrays.asList(s.split(" "));
				
				switch (splited.get(0)) {
				
				// Conectar a outro servidor
				case "bind":
					c.connectToServer(args, splited.get(1));
					break;
				
				// Listar as peças do repositório
				case "listp": 
					c.listRepository();
					break;
					
				// Buscar peça	
				case "getp":
					c.getPartByCode(splited.get(1));
					break;
				
				// Mostrar atributos da peça	
				case "showp":
					c.showPartInfoByCode(splited.get(1));
					break;
				
				// Adicionar peça
				case "addp":
					// Nome
					String name = splited.get(1);
					
					//Descricao
					String description = String.join(" ", splited.subList(2, splited.size()));
					
					c.addPartToRepository(name, description);
					break;
					
				case "inforep":
					c.printInfoFromRepository();
					break;
					
				case "quit":
					System.exit(0);
					break;
					
				default:
					System.out.println("Essa opcao não existe!");
					break;
				}
				
				System.out.println("\n");
			}

		} catch (Exception e) {
			System.out.println("Erro no Menu do Cliente : " + e);
			e.printStackTrace(System.out);
		}
	}
	
	// CONEXAO COM SERVIDOR
	private void connectToServer(String args[], String serverName) {
		try {
			ORB orb = ORB.init(args, null);
			org.omg.CORBA.Object obj = orb.resolve_initial_references("NameService");

			NamingContext naming = NamingContextHelper.narrow(obj);
			NameComponent[] name = { new NameComponent(serverName, "") };

			org.omg.CORBA.Object objRef = naming.resolve(name);
			currentPartRepository = PartRepositoryHelper.narrow(objRef);
			
			System.out.println("Repositório atual: "+ currentPartRepository.name() + "\n");
			
		} catch (Exception e) {
			System.out.println("Erro de conexão com o servidor!");
			e.printStackTrace(System.out);
		}
	}
	
	// LISTA AS PECAS NO REPOSITORIO
	private void listRepository() {
		PartListItem[] items = currentPartRepository.getPartList();
		
		System.out.println("Peças do Repositório: ");
		for (PartListItem item: items) {
			System.out.println(item.code + " " + item.name);
		}
	}
	
	// ADICIONAR PECA
	private void addPartToRepository(String name, String description) {
		Part part = currentPartRepository.addPart(name, description);
		System.out.println("Peça "+ part.name() + " adicionada!");
	}
	
	// BUSCAR PECA PELO CODIGO
	private void getPartByCode(String code) {
		try {
			Part part = currentPartRepository.getPart(code);
			System.out.println("Peça encontrada: " + part.code() + " " + part.name());
			
		} catch (NoSuchPart e) {
			System.out.println("A peça com o codigo " + e.partCode + " não existe no repositório!");
		}
	}
	
	// MOSTRAR ATRIBUTOS PECA DE PELO CODIGO
	private void showPartInfoByCode(String code) {
		try {
			PartInfo info = currentPartRepository.getPartInfo(code);
			
			System.out.println("Codigo: "+ info.code);
			System.out.println("Nome: "+ info.name);
			System.out.println("Descricao: "+ info.description);
			
		} catch (NoSuchPart e) {
			System.out.println("A peça com o codigo " + code + " não existe no repositório!");
		}	
	}
	
	// MOSTRA ATRIBUTOS DO REPOSITORIO
	private void printInfoFromRepository() {
		System.out.println("Nome: " + currentPartRepository.name() + " Número de peças: "+ currentPartRepository.numParts());
	}
}
